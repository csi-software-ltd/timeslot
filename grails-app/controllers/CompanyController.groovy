import grails.converters.JSON
import java.text.SimpleDateFormat
class CompanyController {
  def requestService
  def listingService
  def schedulerService
  def customersService
  def mailerService

  def transient m_oPartner = null

  final Integer INDEXID = 1
  final Integer PROFILEID = 2
  final Integer RESERVATIONID = 3

  def beforeInterceptor = [action:this.&checkPartner]

  def checkPartner() {
    m_oPartner = Partner.get(params.id?:0)
    if (!m_oPartner) {
      response.sendError(404)
      return false
    }
  }

  def checkUser(hsRes,iActionId) {
    hsRes.partner = m_oPartner
    if(!hsRes?.user){
      response.sendError(401)
      return false
    }
    if(!session.companyfilters) session.companyfilters = [:]
	  def oTemp_notification=Temp_notification.findWhere(id:1,status:1)
	  session.attention_message=oTemp_notification?oTemp_notification.text:null
    hsRes.action_id = iActionId
    return true
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Scheduler >>>///////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def index = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)

    hsRes.partner = m_oPartner
    hsRes.action_id = INDEXID

    hsRes.interval = Timedelta.get(hsRes.partner.timedelta_id).delta
    hsRes.customer = Customertopartner.findByPartner_idAndCustomer_id(m_oPartner.id,hsRes.user?.id?:0)

    return hsRes
  }

  def spaces = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)

    render (listingService.partnerSchedulerListing(partner_id:m_oPartner.id,onlyVisible:true).collect{ [id:it.id,unit:it.unitname,space:it.name] } as JSON)
    return
  }

  def events = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)

    Date basedate = requestService.getISO8601Date('start')
    if (!basedate){
      render ([] as JSON)
      return
    }

    def custId = Customertopartner.findByPartner_idAndCustomer_id(m_oPartner.id,hsRes.user?.id?:0)?.id?:0
    render ((listingService.workhourSchedulerListing(partner_id:m_oPartner.id,onlyVisible:true,basedate:basedate)
            +listingService.bookingSchedulerListing(partner_id:m_oPartner.id,onlyVisible:true,basedate:basedate)).collect{ it.prepareJSON(basedate, custId) } as JSON)
    return
  }

  def preparebooking = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    hsRes.result=[error:false,bookdata:[:]]

    Date start = requestService.getISO8601Date('start')
    Date end = requestService.getISO8601Date('end')
    Space oSpace = Space.findByIdAndUnit_idInList(requestService.getIntDef('space_id',0),Unit.findAllByPartner_id(m_oPartner.id).collect{ it.id }?:[0])
    Integer iBookingId = requestService.getIntDef('event_id',0)
    Booking oBooking = Booking.get(iBookingId)

    if (!oSpace||!start||!end){
      hsRes.result.error = true
    } else if (!oBooking&&iBookingId){
      hsRes.result.error = true
    } else if(listingService.bookingSchedulerListing(space_id:oSpace.id,time_start:start,time_end:end,partner_id:m_oPartner.id,exclude_id:oBooking?.id?:0).size()>0){
      hsRes.result.error = true
    } else if(!schedulerService.checkBookingWorkhour(space_id:oSpace.id,start:start,end:end,partner_id:m_oPartner.id,onlyVisible:true)){
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
    hsRes.result=[errorcode:[]]

    hsRes+=requestService.getParams(['space_id','customer_id','booking_id','is_recurrence','recurrencetype',
                                     'is_mon','is_tue','is_wen','is_thu','is_fra','is_sut','is_sun',
                                     'endingtype','endingcount'],null,['title','description','username','login'],null,['price'])
    hsRes.inrequest.fromtime = requestService.getISO8601Date('startdate')
    hsRes.inrequest.totime = requestService.getISO8601Date('enddate')
    hsRes.inrequest.endingdate = requestService.getDate('endingdate')
    Space oSpace = Space.findByIdAndUnit_idInList(hsRes.inrequest.space_id?:0,Unit.findAllByPartner_id(m_oPartner.id).collect{ it.id }?:[0])
    Booking oBooking = Booking.get(hsRes.inrequest.booking_id)
    if (!oSpace||!hsRes.inrequest.fromtime||!hsRes.inrequest.totime||(!oBooking&&hsRes.inrequest.booking_id)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if (hsRes.inrequest.is_recurrence) {
      if (!schedulerService.checkRecurrenceAvailability(hsRes.inrequest+[space_id:oSpace.id,exclude_id:oBooking?.id?:0,partner_id:m_oPartner.id]))
        hsRes.result.errorcode << 1
    } else if(listingService.bookingSchedulerListing(space_id:oSpace.id,time_start:hsRes.inrequest.fromtime,time_end:hsRes.inrequest.totime,partner_id:m_oPartner.id,exclude_id:oBooking?.id?:0).size()>0){
      hsRes.result.errorcode << 1
    } else if(!schedulerService.checkBookingWorkhour(space_id:oSpace.id,start:hsRes.inrequest.fromtime,end:hsRes.inrequest.totime,partner_id:m_oPartner.id,onlyVisible:true)){
      hsRes.result.errorcode << 1
    } else if(!schedulerService.checkBookingDimention(unit_id:oSpace.unit_id,start:hsRes.inrequest.fromtime,end:hsRes.inrequest.totime)){
      hsRes.result.errorcode << 1
    }
    if(!hsRes.user){
      if(!hsRes.inrequest.username)
        hsRes.result.errorcode << 2
      if(!hsRes.inrequest.login)
        hsRes.result.errorcode << 3
      else if(!Tools.checkEmailString(hsRes.inrequest.login))
        hsRes.result.errorcode<<3
      else if(Customer.findByEmail(hsRes.inrequest.login))
        hsRes.result.errorcode<<3
    }

    if(!hsRes.result.errorcode){
      try {
        if (!hsRes.user) hsRes.user = customersService.loginInternalUser(new Customer().csiSetCustomer(hsRes.inrequest).save(flush:true,failOnError:true),requestService,1)
        if (!hsRes.inrequest.customer_id) hsRes.inrequest.customer_id = new Customertopartner(customer_id:hsRes.user.id,partner_id:m_oPartner.id,firstname:hsRes.user.name,partnergroup_id:m_oPartner.csiGetCommonGroupId()).save(flush:true,failOnError:true).id
        schedulerService.updateBooking(hsRes.inrequest+[space_id:oSpace.id,booking:oBooking])
        if (!oBooking&&hsRes.user.is_emailconfirmed) mailerService.sendBookingNotificationMail(hsRes.user)
      } catch(Exception e) {
        log.debug("Error save data in Company/booking\n"+e.toString())
        hsRes.result.errorcode << 1
      }
    }
    render hsRes.result as JSON
    return
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Scheduler >>>///////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Auth >>>////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def auth = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false,true)

    hsRes.partner = m_oPartner

    if (hsRes.user?.id){
      redirect(controller:'company', action:'index', params:[linkname:m_oPartner.linkname])
      return
    } else return hsRes
  }

  def login = {
    if(Temp_ipblock.findWhere(userip:request.remoteAddr,status:1)){
      redirect(controller:'company', action:'index', params:[linkname:m_oPartner.linkname])
      return
    }
    requestService.init(this)

    def sUser = requestService.getStr('login')
    def sPassword = requestService.getStr('password')
    def iRemember = requestService.getIntDef('is_remember',0)
    if (sUser==''){
      flash.error = 1 // set login
      redirect(controller:'company', action:'auth', params:[linkname:m_oPartner.linkname])
      return
    }
    def oUserlog = new Customerlog()
    def blocktime = Tools.getIntVal(Dynconfig.findByName('customer.blocktime')?.value,900)
    def unsuccess_log_limit = Tools.getIntVal(Dynconfig.findByName('customer.unsuccess_log_limit')?.value,3)
    sPassword = Tools.hidePsw(sPassword)
    def oUser = Customer.find('from Customer where email=:login',[login:sUser.toLowerCase()])
    if(!oUser){
      flash.error = 2 // Wrong password or User does not exists
      redirect(controller:'company', action:'auth', params:[linkname:m_oPartner.linkname])
      return
    } else if (oUser.is_block || oUserlog.csiCountUnsuccessDurationLogs(oUser.id)[0]>=Tools.getIntVal(Dynconfig.findByName('customer.unsuccess_duration_log_limit')?.value,30)){
      flash.error = 5 // User blocked
      oUserlog = new Customerlog(user_id:oUser.id,logtime:new Date(),ip:request.remoteAddr,success:3,success_duration:-1)
      if (!oUserlog.save(flush:true)){
        log.debug('error on save Customerlog in User:login')
        oUserlog.errors.each{log.debug(it)}
      }
      if(!oUser.is_block){
        oUser.is_block = 1
        if (!oUser.save(flush:true)){
          log.debug('error on save User in User:login')
          oUser.errors.each{log.debug(it)}
        }
      }
      redirect(controller:'company', action:'auth', params:[linkname:m_oPartner.linkname])
      return  
    } else if (oUserlog.csiCountUnsuccessLogs(oUser.id, new Date(System.currentTimeMillis()-blocktime*1000))[0]>=unsuccess_log_limit){
      flash.error = 3 // User blocked
      oUserlog = new Customerlog(user_id:oUser.id,logtime:new Date(),ip:request.remoteAddr,success:2,success_duration:-1)
      if (!oUserlog.save(flush:true)){
        log.debug('error on save Customerlog in User:login')
        oUserlog.errors.each{log.debug(it)}
      }
      redirect(controller:'company', action:'auth', params:[linkname:m_oPartner.linkname])
      return
    } else if (oUser.password != sPassword) {
      flash.error = 2 // Wrong password or User does not exists
      oUserlog = new Customerlog(user_id:oUser.id,logtime:new Date(),ip:request.remoteAddr,success:0,success_duration:0)
      if (!oUserlog.save(flush:true)){
        log.debug('error on save Customerlog in User:login')
        oUserlog.errors.each{log.debug(it)}
      }
      redirect(controller:'company', action:'auth', params:[linkname:m_oPartner.linkname])
      return
    }

    oUserlog = new Customerlog(user_id:oUser.id,logtime:new Date(),ip:request.remoteAddr,success:1,success_duration:1)
    if (!oUserlog.save(flush:true)){
      log.debug('error on save Customerlog in User:login')
      oUserlog.errors.each{log.debug(it)}
    }

    customersService.loginInternalUser(oUser,requestService,iRemember)
    redirect(controller:'company', action:'index', params:[linkname:m_oPartner.linkname])
    return
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def logout = {
    requestService.init(this)
    customersService.logoutUser(requestService)
    redirect(controller:'company', action:'index', params:[linkname:m_oPartner.linkname])
  }

  def signup = {
    if(Temp_ipblock.findWhere(userip:request.remoteAddr,status:1)){
      redirect(controller:'company', action:'index', params:[linkname:m_oPartner.linkname])
      return
    }
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.result=[errorcode:[]]

    hsRes+=requestService.getParams(['is_remember'],null,['username','login','password','password2'])

    if(!hsRes.inrequest.username)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.login)
      hsRes.result.errorcode<<2
    else if(!Tools.checkEmailString(hsRes.inrequest.login))
      hsRes.result.errorcode<<3
    else if(Customer.findByEmail(hsRes.inrequest.login))
      hsRes.result.errorcode<<4
    if(!hsRes.inrequest.password)
      hsRes.result.errorcode<<5
    else if(hsRes.inrequest.password.size()<Tools.getIntVal(Dynconfig.findByName('user.passwordlength')?.value,7))
      hsRes.result.errorcode<<7
    else if(!hsRes.inrequest.password.matches('.*(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?!.*[\\W_А-я]).*'))
      hsRes.result.errorcode<<7
    else if(hsRes.inrequest.password!=hsRes.inrequest.password2)
      hsRes.result.errorcode<<8
    if(!hsRes.inrequest.password2)
      hsRes.result.errorcode<<6

    if(!hsRes.result.errorcode){
      try {
        def oUser = new Customer().csiSetCustomer(hsRes.inrequest).save(flush:true,failOnError:true)
        customersService.loginInternalUser(oUser,requestService,hsRes.inrequest.is_remember?:0)
        mailerService.sendCustomerActivationMail(oUser,m_oPartner)
      } catch(Exception e) {
        log.debug("Error save data in Customer/signup\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def confirm = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)

    hsRes.partner = m_oPartner
    hsRes.confirm_user = Customer.findByScodeAndScodeNotEqual(requestService.getStr('code'),'')

    if(hsRes.confirm_user){
      try {
        hsRes.confirm_user.mailConfirm().clearScode().save(flush:true,failOnError:true)
        mailerService.sendSuccessCustomerConfirmationMail(hsRes.confirm_user,m_oPartner)
        if (!hsRes.user) hsRes.user = customersService.loginInternalUser(hsRes.confirm_user,requestService,1)
      } catch(Exception e) {
        log.debug("Error save data in Company/confirm\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }

    return hsRes
  }

  def restore = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
    if(hsRes.user!=null){
      redirect(controller:'company', action:'index', params:[linkname:m_oPartner.linkname])
      return
    }

    hsRes.partner = m_oPartner

    return hsRes
  }

  def rest = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.result=[errorcode:[]]

    hsRes+=requestService.getParams(null,null,['login'])

    if(!hsRes.inrequest.login)
      hsRes.result.errorcode<<1
    else if(!Tools.checkEmailString(hsRes.inrequest.login))
      hsRes.result.errorcode<<2
    else if(!Customer.findByEmail(hsRes.inrequest.login))
      hsRes.result.errorcode<<3

    if(!hsRes.result.errorcode){
      try {
        def oUser = Customer.findByEmail(hsRes.inrequest.login).refreshScode().save(flush:true,failOnError:true)
        mailerService.sendRestoreCustomerPasswordMail(oUser,m_oPartner)
      } catch(Exception e) {
        log.debug("Error save data in Company/rest\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def passrestore = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
    if(hsRes.user!=null){
      redirect(controller:'company', action:'index', params:[linkname:m_oPartner.linkname])
      return
    }

    hsRes.partner = m_oPartner
    hsRes.restore_user = Customer.findByScodeAndScodeNotEqual(requestService.getStr('code'),'')

    return hsRes
  }

  def passrest = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.result=[errorcode:[]]

    hsRes+=requestService.getParams(null,null,['scode','password','password2'])

    hsRes.restore_user = Customer.findByScodeAndScodeNotEqual(hsRes.inrequest.scode,'')

    if(!hsRes.restore_user)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.password)
      hsRes.result.errorcode<<2
    else if(hsRes.inrequest.password.size()<Tools.getIntVal(Dynconfig.findByName('user.passwordlength')?.value,7))
      hsRes.result.errorcode<<3
    else if(!hsRes.inrequest.password.matches('.*(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?!.*[\\W_А-я]).*'))
      hsRes.result.errorcode<<3
    else if(hsRes.inrequest.password!=hsRes.inrequest.password2)
      hsRes.result.errorcode<<4
    if(!hsRes.inrequest.password2)
      hsRes.result.errorcode<<5

    if(!hsRes.result.errorcode){
      try {
        hsRes.restore_user.csiSetPassword(hsRes.inrequest).mailConfirm().clearScode().save(flush:true,failOnError:true)
        customersService.loginInternalUser(hsRes.restore_user,requestService,1)
      } catch(Exception e) {
        log.debug("Error save data in Company/passrest\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Auth <<<////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Profile >>>/////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def profile = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false,true)
    if (!checkUser(hsRes,PROFILEID)) return

    hsRes.profile = Customer.get(hsRes.user.id)

    return hsRes
  }

  def updateprofile = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkUser(hsRes,PROFILEID)) return
    hsRes.result=[errorcode:[]]

    hsRes+=requestService.getParams(null,null,['name','phone','password','password2'])

    if(!hsRes.inrequest.name)
      hsRes.result.errorcode<<1
    if(hsRes.inrequest.password){
      if(hsRes.inrequest.password.size()<Tools.getIntVal(Dynconfig.findByName('customer.passwordlength')?.value,7))
        hsRes.result.errorcode<<2
      else if(!hsRes.inrequest.password.matches('.*(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?!.*[\\W_А-я]).*'))
        hsRes.result.errorcode<<2
      else if(hsRes.inrequest.password!=hsRes.inrequest.password2)
        hsRes.result.errorcode<<3
    }

    if(!hsRes.result.errorcode){
      try {
        Customer.get(hsRes.user.id)?.updateMainData(hsRes.inrequest)?.save(flush:true,failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Company/updateprofile\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Profile <<</////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Reservations >>>////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def reservations = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false,true)
    if (!checkUser(hsRes,RESERVATIONID)) return

    def fromDetails = requestService.getIntDef('fromDetails',0)

    if (fromDetails>0){
      session.companyfilters['fromDetails'] = fromDetails
      hsRes.inrequest = session.companyfilters."lastRequest_$fromDetails"
    } else {
      hsRes.inrequest=[:]
    }

    return hsRes
  }

  def reservationlist = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkUser(hsRes,RESERVATIONID)) return

    if (session.companyfilters?.fromDetails==1){
      hsRes.inrequest = session.companyfilters."lastRequest_$RESERVATIONID"
      session.companyfilters['fromDetails'] = 0
    } else {
      hsRes+=requestService.getParams(null,null,['title'])
      hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',0)
      hsRes.inrequest.fromtime = requestService.getDate('fromtime')
      hsRes.inrequest.totime = requestService.getDate('totime')
      hsRes.inrequest.offset = requestService.getOffset()
      session.companyfilters."lastRequest_$RESERVATIONID" = hsRes.inrequest
    }

    hsRes.searchresult = listingService.bookingListing(hsRes.inrequest+[customer_id:hsRes.user.id],20,hsRes.inrequest.offset)

    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Reservations <<<////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Widget >>>//////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def widget = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)

    hsRes.partner = m_oPartner

    hsRes.interval = Timedelta.get(hsRes.partner.timedelta_id).delta
    hsRes.customer = Customertopartner.findByPartner_idAndCustomer_id(m_oPartner.id,hsRes.user?.id?:0)

    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Widget <<<//////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
}