import grails.converters.JSON
import java.text.SimpleDateFormat
class SchedulerController {
  def requestService
  def listingService
  def schedulerService
  def mailerService

  final Integer ONLINEID = 7

  def checkUser(hsRes) {
    if(!hsRes?.user){
      response.sendError(401)
      return false
    }
	  def oTemp_notification=Temp_notification.findWhere(id:1,status:1)
	  session.attention_message=oTemp_notification?oTemp_notification.text:null
    return true
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def checkAccess(hsRes,iActionId){
    if (!checkUser(hsRes)) return false

    def bDenied = true
    hsRes.user.menu.each{
      if (iActionId==it.id) bDenied = false
    }
    if (bDenied) {
      redirect(controller:'index',action:'index')
      return false
    }
    hsRes.action_id=iActionId
    true
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Scheduler >>>///////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def online = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)
    if (!checkAccess(hsRes,ONLINEID)) return

    hsRes.partner = Partner.get(hsRes.user.partner_id)
    hsRes.interval = Timedelta.get(hsRes.partner.timedelta_id).delta
    hsRes.customers = listingService.customerListing([partner_id:hsRes.partner.id,modstatus:1],-1,0).records << [id:-1,toOptionValue:{'недоступно'}]

    return hsRes
  }

  def spaces = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkAccess(hsRes,ONLINEID)) return

    render (listingService.partnerSchedulerListing(partner_id:hsRes.user.partner_id,onlyVisible:true).collect{ [id:it.id,unit:it.unitname,space:it.name] } as JSON)
    return
  }

  def events = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkAccess(hsRes,ONLINEID)) return

    Date basedate = requestService.getISO8601Date('start')
    if (!basedate){
      render ([] as JSON)
      return
    }

    render ((listingService.workhourSchedulerListing(partner_id:hsRes.user.partner_id,onlyVisible:true,basedate:basedate)
            +listingService.bookingSchedulerListing(partner_id:hsRes.user.partner_id,onlyVisible:true,basedate:basedate)).collect{ it.prepareJSON(basedate) } as JSON)
    return
  }

  def preparebooking = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkAccess(hsRes,ONLINEID)) return
    hsRes.result=[error:false,bookdata:[:]]

    Date start = requestService.getISO8601Date('start')
    Date end = requestService.getISO8601Date('end')
    Space oSpace = Space.findByIdAndUnit_idInList(requestService.getIntDef('id',0),Unit.findAllByPartner_id(hsRes.user.partner_id).collect{ it.id }?:[0])
    Integer iSpaceId = requestService.getIntDef('event_id',0)
    Booking oBooking = Booking.get(iSpaceId)

    if (!oSpace||!start||!end){
      hsRes.result.error = true
    } else if (!oBooking&&iSpaceId){
      hsRes.result.error = true
    } else if(listingService.bookingSchedulerListing(space_id:oSpace.id,time_start:start,time_end:end,partner_id:hsRes.user.partner_id,exclude_id:oBooking?.id?:0).size()>0){
      hsRes.result.error = true
    } else if(!schedulerService.checkBookingWorkhour(space_id:oSpace.id,start:start,end:end,partner_id:hsRes.user.partner_id,onlyVisible:true)){
      hsRes.result.error = true
    } else if(!schedulerService.checkBookingDimention(unit_id:oSpace.unit_id,start:start,end:end)){
      hsRes.result.error = true
    } else {
      hsRes.result.price = oBooking?.price?:schedulerService.computeBookingPrice(unit_id:oSpace.unit_id,start:start,end:end)
      hsRes.result.spacename = oSpace.name
      hsRes.result.startdate = String.format('%td.%<tm.%<tY %<tH:%<tM',start)
      hsRes.result.enddate = String.format('%td.%<tm.%<tY %<tH:%<tM',end)
      hsRes.result.bookdata.startdate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(start)
      hsRes.result.bookdata.enddate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(end)
      hsRes.result.bookdata.space_id = oSpace.id
      hsRes.result.booking = oBooking
      hsRes.result.bookdata.booking_id = oBooking?.id?:''
    }

    render hsRes.result as JSON
    return
  }

  def booking = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkAccess(hsRes,ONLINEID)) return
    hsRes.result=[errorcode:[]]

    hsRes+=requestService.getParams(['space_id','customer_id','booking_id','is_recurrence','recurrencetype',
                                     'is_mon','is_tue','is_wen','is_thu','is_fra','is_sut','is_sun',
                                     'endingtype','endingcount'],null,['title','description'],null,['price'])
    hsRes.inrequest.fromtime = requestService.getISO8601Date('startdate')
    hsRes.inrequest.totime = requestService.getISO8601Date('enddate')
    hsRes.inrequest.endingdate = requestService.getDate('endingdate')
    Space oSpace = Space.findByIdAndUnit_idInList(hsRes.inrequest.space_id?:0,Unit.findAllByPartner_id(hsRes.user.partner_id).collect{ it.id }?:[0])
    Booking oBooking = Booking.get(hsRes.inrequest.booking_id)
    if (!oSpace||!hsRes.inrequest.fromtime||!hsRes.inrequest.totime||(!oBooking&&hsRes.inrequest.booking_id)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if (hsRes.inrequest.is_recurrence) {
      if (!schedulerService.checkRecurrenceAvailability(hsRes.inrequest+[space_id:oSpace.id,exclude_id:oBooking?.id?:0,partner_id:hsRes.user.partner_id]))
        hsRes.result.errorcode << 1
    } else if(listingService.bookingSchedulerListing(space_id:oSpace.id,time_start:hsRes.inrequest.fromtime,time_end:hsRes.inrequest.totime,partner_id:hsRes.user.partner_id,exclude_id:oBooking?.id?:0).size()>0){
      hsRes.result.errorcode << 1
    } else if(!schedulerService.checkBookingWorkhour(space_id:oSpace.id,start:hsRes.inrequest.fromtime,end:hsRes.inrequest.totime,partner_id:hsRes.user.partner_id,onlyVisible:true)){
      hsRes.result.errorcode << 1
    } else if(!schedulerService.checkBookingDimention(unit_id:oSpace.unit_id,start:hsRes.inrequest.fromtime,end:hsRes.inrequest.totime)){
      hsRes.result.errorcode << 1
    }

    if(!hsRes.result.errorcode){
      try {
        schedulerService.updateBooking(hsRes.inrequest+[space_id:oSpace.id,booking:oBooking])
        if (!oBooking&&hsRes.inrequest.customer_id>0){
          def oCustomer = Customer.get(Customertopartner.get(hsRes.inrequest.customer_id)?.customer_id)
          if (oCustomer?.is_emailconfirmed) mailerService.sendBookingNotificationMail(oCustomer)
        }
      } catch(Exception e) {
        log.debug("Error save data in Scheduler/booking\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Scheduler >>>///////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
}