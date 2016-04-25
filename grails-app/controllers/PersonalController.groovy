import grails.converters.JSON
class PersonalController {
  def requestService
  def listingService
  def mailerService

  final Integer PARTNERID = 1
  final Integer UNITID = 2
  final Integer USERID = 3
  final Integer GROUPID = 4
  final Integer CUSTOMERID = 5
  final Integer SPACEID = 6
  final Integer BOOKINGID = 8

  final bookingGroupActions = ['accept','delete','confirm']

  def checkUser(hsRes) {
    if(!hsRes?.user){
      response.sendError(401)
      return false
    }
    if(!session.personalfilters) session.personalfilters = [:]
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
    hsRes.partner = Partner.get(hsRes.user.partner_id)
    true
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def sendverify = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)

    try {
      def oUser = User.get(hsRes.user.id).refreshScode().save(flush:true,failOnError:true)
      mailerService.sendActivationMail(oUser)
    } catch(Exception e) {
      log.debug("Error save data in Personal/sendverify\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Partner >>>/////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def organization = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)
    if (!checkAccess(hsRes,PARTNERID)) return

    return hsRes
  }

  def updateorgdetail = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkAccess(hsRes,PARTNERID)) return
    hsRes.result=[errorcode:[]]

    hsRes+=requestService.getParams(['business_id','lockin','gmt_id','timedelta_id','maxdays','minhours','is_privacy'],
                                    null,['pname','web','email','tel','country','city','address','description'])

    if(!hsRes.inrequest.pname)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.address)
      hsRes.result.errorcode<<2
    if(!hsRes.inrequest.city)
      hsRes.result.errorcode<<3
    if(!hsRes.inrequest.country)
      hsRes.result.errorcode<<4
    if(!hsRes.inrequest.business_id)
      hsRes.result.errorcode<<5

    if(!hsRes.result.errorcode){
      try {
        Partner.get(hsRes.user.partner_id)?.updateMainData(hsRes.inrequest)?.save(flush:true,failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Personal/updateorgdetail\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def partnerwhours = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkAccess(hsRes,PARTNERID)) return

    hsRes.whours = Workhour.findAllByPartner_idAndSpace_id(hsRes.user.partner_id,0)

    return hsRes
  }

  def workhours = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false,true)
    if (!checkAccess(hsRes,PARTNERID)) return

    hsRes.workhours = Workhour.findByPartner_idAndIdAndSpace_id(hsRes.partner.id?:0,requestService.getIntDef('id',0),0)
    hsRes.interval = Timedelta.get(hsRes.partner.timedelta_id).delta

    return hsRes
  }

  def addworkhours = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkAccess(hsRes,PARTNERID)) return
    hsRes.result=[errorcode:[]]

    def lId = requestService.getIntDef('id',0)
    hsRes.workhours = Workhour.findByIdAndPartner_idAndSpace_id(lId,hsRes.partner.id,0)
    if (!hsRes.workhours&&lId) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(['is_mon','is_tue','is_wen','is_thu','is_fra','is_sut','is_sun','is_hol'])
    hsRes.inrequest.time_open = requestService.getTime('workhours_time_open')
    hsRes.inrequest.time_close = requestService.getTime('workhours_time_close')

    if(!hsRes.inrequest.is_mon&&!hsRes.inrequest.is_tue&&!hsRes.inrequest.is_wen&&!hsRes.inrequest.is_thu&&!hsRes.inrequest.is_fra&&!hsRes.inrequest.is_sut&&!hsRes.inrequest.is_sun&&!hsRes.inrequest.is_hol)
      hsRes.result.errorcode<<1
    else if (listingService.assertWorkhours(hsRes.inrequest+[partner_id:hsRes.partner.id,id:lId]).size()>0)
      hsRes.result.errorcode<<2

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.workhours = new Workhour(partner_id:hsRes.partner.id)
        hsRes.workhours.updateData(hsRes.inrequest).save(failOnError:true,flush:true)
      } catch(Exception e) {
        log.debug("Error save data in Personal/addworkhours\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def deleteworkhours = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkAccess(hsRes,PARTNERID)) return

    try {
      Workhour.findByIdAndPartner_idAndSpace_id(requestService.getIntDef('id',0),hsRes.user.partner_id,0)?.delete(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Personal/deleteworkhours\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Partner >>>/////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Units >>>///////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def units = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)
    if (!checkAccess(hsRes,UNITID)) return

    return hsRes
  }

  def unitlist = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkAccess(hsRes,UNITID)) return

    hsRes.searchresult = listingService.partnerUnitsListing([partner_id:hsRes.partner.id],20,requestService.getOffset())

    return hsRes
  }

  def updateunitstatus = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkAccess(hsRes,UNITID)) return

    Unit.findByIdAndPartner_id(requestService.getIntDef('id',0),hsRes.user.partner_id)?.csiSetModstatus(requestService.getIntDef('status',0))?.save(flush:true,failOnError:true)

    render(contentType:"application/json"){[error:false]}
    return
  }

  def unit = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)
    if (!checkAccess(hsRes,UNITID)) return

    def lId = requestService.getIntDef('id',0)
    hsRes.unit = Unit.findByIdAndPartner_id(lId,hsRes.user.partner_id)
    if (!hsRes.unit&&lId) {
      response.sendError(404)
      return
    }

    return hsRes
  }

  def updateunitdetail = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkAccess(hsRes,UNITID)) return
    hsRes.result=[errorcode:[]]

    def lId = requestService.getIntDef('id',0)
    hsRes.unit = Unit.findByIdAndPartner_id(lId,hsRes.user.partner_id)
    if (!hsRes.unit&&lId) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(null,null,['name','contactperson','description'])

    if(!hsRes.inrequest.name)
      hsRes.result.errorcode<<1

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.unit = new Unit(partner_id:hsRes.user.partner_id)
        hsRes.result.unit = hsRes.unit.updateMainData(hsRes.inrequest).save(failOnError:true,flush:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Personal/updateunitdetail\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def unitprices = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkAccess(hsRes,UNITID)) return

    hsRes.unit = Unit.findByIdAndPartner_id(requestService.getIntDef('id',0),hsRes.user.partner_id)
    if (!hsRes.unit) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.prices = Unitprice.findAllByUnit_id(hsRes.unit.id)

    return hsRes
  }

  def unitprice = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false,true)
    if (!checkAccess(hsRes,UNITID)) return

    hsRes.unit = Unit.findByIdAndPartner_id(requestService.getIntDef('unit_id',0),hsRes.user.partner_id)
    if (!hsRes.unit) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes+=requestService.getParams(null,null,['id'])

    hsRes.unitprice = Unitprice.get(hsRes.inrequest.id)
    hsRes.is_exception = hsRes.unitprice?hsRes.unitprice?.is_exception:Unitprice.findByUnit_id(hsRes.unit.id)?true:false
    hsRes.interval = Timedelta.get(Partner.get(hsRes.user.partner_id)?.timedelta_id?:4).delta

    return hsRes
  }

  def addunitprice = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkAccess(hsRes,UNITID)) return
    hsRes.result=[errorcode:[]]

    def lId = requestService.getIntDef('id',0)
    hsRes.unit = Unit.findByIdAndPartner_id(requestService.getIntDef('unit_id',0),hsRes.user.partner_id)
    hsRes.unitprice = Unitprice.findByIdAndUnit_id(lId,hsRes.unit?.id?:0)
    if (!hsRes.unit||(!hsRes.unitprice&&lId)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(['forperiod','minperiod','is_mon','is_tue','is_wen','is_thu','is_fra','is_sut',
                                     'is_sun','is_hol'],null,null,null,['price'])
    hsRes.inrequest.time_start = requestService.getTime('unitprice_time_start')
    hsRes.inrequest.time_end = requestService.getTime('unitprice_time_end')
    hsRes.is_exception = hsRes.unitprice?hsRes.unitprice?.is_exception:Unitprice.findByUnit_id(hsRes.unit.id)?true:false
    hsRes.interval = Timedelta.get(Partner.get(hsRes.user.partner_id)?.timedelta_id?:4).delta

    if(!hsRes.inrequest.price)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.forperiod)
      hsRes.result.errorcode<<2
    else if(hsRes.inrequest.forperiod<0)
      hsRes.result.errorcode<<3
    else if (hsRes.inrequest.forperiod%hsRes.interval!=0)
      hsRes.result.errorcode<<3
    if(!hsRes.inrequest.minperiod)
      hsRes.result.errorcode<<4
    else if(hsRes.inrequest.minperiod<0)
      hsRes.result.errorcode<<5
    else if (hsRes.inrequest.minperiod%hsRes.interval!=0)
      hsRes.result.errorcode<<5
    if(hsRes.is_exception){
      if(!hsRes.inrequest.is_mon&&!hsRes.inrequest.is_tue&&!hsRes.inrequest.is_wen&&!hsRes.inrequest.is_thu&&!hsRes.inrequest.is_fra&&!hsRes.inrequest.is_sut&&!hsRes.inrequest.is_sun&&!hsRes.inrequest.is_hol)
        hsRes.result.errorcode<<6
      else if (listingService.assertUnitprice(hsRes.inrequest+[unit_id:hsRes.unit.id,id:lId]).size()>0)
        hsRes.result.errorcode<<7
    }

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.unitprice = new Unitprice(unit_id:hsRes.unit.id,is_exception:hsRes.is_exception?1:0)
        hsRes.unitprice.updateMainData(hsRes.inrequest).updateExceptionData(hsRes.inrequest).save(failOnError:true,flush:true)
      } catch(Exception e) {
        log.debug("Error save data in Personal/addunitprice\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def deleteunitprice = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkAccess(hsRes,UNITID)) return

    hsRes.unit = Unit.findByIdAndPartner_id(requestService.getIntDef('unit_id',0),hsRes.user.partner_id)
    if (!hsRes.unit) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      Unitprice.findByIdAndUnit_idAndIs_exception(requestService.getIntDef('id',0),hsRes.unit?.id?:0,1)?.delete(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Personal/deleteunitprice\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Units >>>///////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Users >>>///////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def users = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)
    if (!checkAccess(hsRes,USERID)) return

    return hsRes
  }

  def userlist = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkAccess(hsRes,USERID)) return

    hsRes.searchresult = listingService.partnerUsersListing([partner_id:hsRes.partner.id],20,requestService.getOffset())
    hsRes.groups = Usergroup.list().inject([:]){map, group -> map[group.id]=group.name;map}

    return hsRes
  }

  def updateuserstatus = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkAccess(hsRes,USERID)) return

    User.findByIdAndPartner_idAndIdNotEqual(requestService.getIntDef('id',0),hsRes.user.partner_id,hsRes.user.id)?.csiSetModstatus(requestService.getIntDef('status',0))?.save(flush:true,failOnError:true)

    render(contentType:"application/json"){[error:false]}
    return
  }

  def user = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)
    if (!checkAccess(hsRes,USERID)) return

    def lId = requestService.getIntDef('id',0)
    hsRes.useredit = User.findByIdAndPartner_id(lId,hsRes.user.partner_id)
    if (!hsRes.useredit&&lId) {
      response.sendError(404)
      return
    }
    hsRes.is_selfedit = hsRes.user.id==hsRes.useredit?.id

    return hsRes
  }

  def updateuserdetail = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkAccess(hsRes,USERID)) return
    hsRes.result=[errorcode:[]]

    def lId = requestService.getIntDef('id',0)
    hsRes.useredit = User.findByIdAndPartner_id(lId,hsRes.user.partner_id)
    if (!hsRes.useredit&&lId) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(['usergroup_id'],null,['email','fullname','mobile','password','password2'])
    if(hsRes.user.id==hsRes.useredit?.id) hsRes.inrequest.usergroup_id = hsRes.useredit.usergroup_id

    if(!lId){
      if(!hsRes.inrequest.email)
        hsRes.result.errorcode<<1
      else if(!Tools.checkEmailString(hsRes.inrequest.email))
        hsRes.result.errorcode<<2
      else if(User.findByLogin(hsRes.inrequest.email))
        hsRes.result.errorcode<<3
      if(!hsRes.inrequest.password)
        hsRes.result.errorcode<<4
    }
    if(!hsRes.inrequest.fullname)
      hsRes.result.errorcode<<5
    if(!hsRes.inrequest.usergroup_id)
      hsRes.result.errorcode<<6
    if(hsRes.inrequest.password){
      if(hsRes.inrequest.password.size()<Tools.getIntVal(Dynconfig.findByName('user.passwordlength')?.value,7))
        hsRes.result.errorcode<<7
      else if(!hsRes.inrequest.password.matches('.*(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?!.*[\\W_А-я]).*'))
        hsRes.result.errorcode<<7
      else if(hsRes.inrequest.password!=hsRes.inrequest.password2)
        hsRes.result.errorcode<<8
    }

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.useredit = new User(partner_id:hsRes.user.partner_id,login:hsRes.inrequest.email)
        hsRes.result.useredit = hsRes.useredit.updateMainData(hsRes.inrequest).save(failOnError:true,flush:true)?.id?:0
        if(!lId) mailerService.sendActivationMail(hsRes.useredit)
      } catch(Exception e) {
        log.debug("Error save data in Personal/updateuserdetail\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Users >>>///////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Partnergroups >>>///////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def groups = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)
    if (!checkAccess(hsRes,GROUPID)) return

    return hsRes
  }

  def grouplist = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkAccess(hsRes,GROUPID)) return

    hsRes.searchresult = listingService.partnerGroupsListing([partner_id:hsRes.partner.id],20,requestService.getOffset())

    return hsRes
  }

  def updategroupstatus = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkAccess(hsRes,GROUPID)) return

    Partnergroup.findByIdAndPartner_idAndIs_main(requestService.getIntDef('id',0,0),hsRes.user.partner_id)?.csiSetModstatus(requestService.getIntDef('status',0))?.save(flush:true,failOnError:true)

    render(contentType:"application/json"){[error:false]}
    return
  }

  def group = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)
    if (!checkAccess(hsRes,GROUPID)) return

    def lId = requestService.getIntDef('id',0)
    hsRes.partnergroup = Partnergroup.findByIdAndPartner_id(lId,hsRes.user.partner_id)
    if (!hsRes.partnergroup&&lId) {
      response.sendError(404)
      return
    }

    return hsRes
  }

  def updategroupdetail = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkAccess(hsRes,GROUPID)) return
    hsRes.result=[errorcode:[]]

    def lId = requestService.getIntDef('id',0)
    hsRes.partnergroup = Partnergroup.findByIdAndPartner_id(lId,hsRes.user.partner_id)
    if (!hsRes.partnergroup&&lId) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(null,null,['name','description'])

    if(!hsRes.inrequest.name)
      hsRes.result.errorcode<<1

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.partnergroup = new Partnergroup(partner_id:hsRes.user.partner_id)
        hsRes.result.partnergroup = hsRes.partnergroup.updateMainData(hsRes.inrequest).save(failOnError:true,flush:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Personal/updategroupdetail\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Partnergroups >>>///////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Customers >>>///////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def customers = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)
    if (!checkAccess(hsRes,CUSTOMERID)) return

    def fromDetails = requestService.getIntDef('fromDetails',0)

    if (fromDetails>0){
      session.personalfilters['fromDetails'] = fromDetails
      hsRes.inrequest = session.personalfilters."lastRequest_$fromDetails"
    } else {
      hsRes.inrequest=[:]
    }

    return hsRes
  }

  def customerlist = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkAccess(hsRes,CUSTOMERID)) return

    if (session.personalfilters?.fromDetails==1){
      hsRes.inrequest = session.personalfilters."lastRequest_$CUSTOMERID"
      session.personalfilters['fromDetails'] = 0
    } else {
      hsRes+=requestService.getParams(null,null,['searchname'])
      hsRes.inrequest.is_trust = requestService.getIntDef('is_trust',0)
      hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',0)
      hsRes.inrequest.offset = requestService.getOffset()
      session.personalfilters."lastRequest_$CUSTOMERID" = hsRes.inrequest
    }

    hsRes.searchresult = listingService.customerListing(hsRes.inrequest+[partner_id:hsRes.partner.id],20,hsRes.inrequest.offset)
    hsRes.groups = Partnergroup.findAllByPartner_id(hsRes.partner.id).inject([:]){map, group -> map[group.id]=group.name;map}

    return hsRes
  }

  def updatecustomerstatus = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkAccess(hsRes,CUSTOMERID)) return

    Customertopartner.findByCustomer_idAndPartner_id(requestService.getIntDef('id',0),hsRes.user.partner_id)?.csiSetModstatus(requestService.getIntDef('status',0))?.save(flush:true,failOnError:true)

    render(contentType:"application/json"){[error:false]}
    return
  }

  def customer = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)
    if (!checkAccess(hsRes,CUSTOMERID)) return

    def lId = requestService.getIntDef('id',0)
    hsRes.customer = listingService.customerListing([partner_id:hsRes.user.partner_id,customer_id:lId],1,0).records[0]
    if (!hsRes.customer&&lId) {
      response.sendError(404)
      return
    }

    return hsRes
  }

  def updatecustomerdetail = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkAccess(hsRes,CUSTOMERID)) return
    hsRes.result=[errorcode:[]]

    def lId = requestService.getIntDef('id',0)
    hsRes.customer = Customertopartner.findByCustomer_idAndPartner_id(lId,hsRes.user.partner_id)
    if (!hsRes.customer&&lId) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(['partnergroup_id','is_trust'],null,['email','firstname','lastname','company','phone'])

    if(!hsRes.customer){
      if(!hsRes.inrequest.email)
        hsRes.result.errorcode<<1
      else if(!Tools.checkEmailString(hsRes.inrequest.email))
        hsRes.result.errorcode<<2
      else if(Customertopartner.findByCustomer_idAndPartner_id(Customer.searchId(hsRes.inrequest.email),hsRes.user.partner_id))
        hsRes.result.errorcode<<3
    }
    if(!hsRes.inrequest.firstname)
      hsRes.result.errorcode<<4
    if(!hsRes.inrequest.partnergroup_id)
      hsRes.result.errorcode<<5

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.customer = new Customertopartner(partner_id:hsRes.user.partner_id,customer_id:Customer.getInstance(hsRes.inrequest.email).updateName(hsRes.inrequest.firstname).save(failOnError:true,flush:true)?.id)
        hsRes.result.customer = hsRes.customer.updateMainData(hsRes.inrequest).csiSetIsTrust(hsRes.inrequest.is_trust).save(failOnError:true,flush:true)?.customer_id?:0
        if(!lId) mailerService.sendClientInventationalMail(hsRes.customer)
      } catch(Exception e) {
        log.debug("Error save data in Personal/updatecustomerdetail\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Customers >>>///////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Spaces >>>//////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def spaces = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)
    if (!checkAccess(hsRes,SPACEID)) return

    return hsRes
  }

  def spacelist = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkAccess(hsRes,SPACEID)) return

    hsRes.searchresult = listingService.partnerSpacesListing([partner_id:hsRes.partner.id],20,requestService.getOffset())
    hsRes.units = Unit.findAllByPartner_id(hsRes.user.partner_id).inject([:]){map, unit -> map[unit.id]=unit.name;map}

    return hsRes
  }

  def updatespacestatus = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkAccess(hsRes,SPACEID)) return

    Space.findByIdAndUnit_idInList(requestService.getIntDef('id',0),Unit.findAllByPartner_id(hsRes.user.partner_id).collect{ it.id }?:[0])?.csiSetModstatus(requestService.getIntDef('status',0))?.save(flush:true,failOnError:true)

    render(contentType:"application/json"){[error:false]}
    return
  }

  def space = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)
    if (!checkAccess(hsRes,SPACEID)) return

    def lId = requestService.getIntDef('id',0)
    hsRes.space = Space.findByIdAndUnit_idInList(lId,Unit.findAllByPartner_id(hsRes.user.partner_id).collect{ it.id }?:[0])
    if (!hsRes.space&&lId) {
      response.sendError(404)
      return
    }
    hsRes.units = Unit.findAllByPartner_id(hsRes.user.partner_id)

    return hsRes
  }

  def updatespacedetail = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkAccess(hsRes,SPACEID)) return
    hsRes.result=[errorcode:[]]

    def lId = requestService.getIntDef('id',0)
    hsRes.space = Space.findByIdAndUnit_idInList(lId,Unit.findAllByPartner_id(hsRes.user.partner_id).collect{ it.id }?:[0])
    if (!hsRes.space&&lId) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(['unit_id','is_visible'],null,['name','description'])

    if(!hsRes.inrequest.name)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.unit_id)
      hsRes.result.errorcode<<2
    else if(!Unit.findByIdAndPartner_id(hsRes.inrequest.unit_id,hsRes.user.partner_id))
      hsRes.result.errorcode<<3

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.space = new Space(partner_id:hsRes.user.partner_id)
        hsRes.result.space = hsRes.space.updateMainData(hsRes.inrequest).csiSetVisible(hsRes.inrequest.is_visible).save(failOnError:true,flush:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Personal/updatespacedetail\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def spacewhours = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkAccess(hsRes,SPACEID)) return

    hsRes.space = Space.findByIdAndUnit_idInList(requestService.getIntDef('id',0),Unit.findAllByPartner_id(hsRes.user.partner_id).collect{ it.id }?:[0])
    if (!hsRes.space) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.whours = Workhour.findAllByPartner_idAndSpace_id(hsRes.user.partner_id,hsRes.space.id)

    return hsRes
  }

  def spworkhours = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false,true)
    if (!checkAccess(hsRes,SPACEID)) return

    hsRes.space = Space.findByIdAndUnit_idInList(requestService.getIntDef('space_id',0),Unit.findAllByPartner_id(hsRes.user.partner_id).collect{ it.id }?:[0])
    if (!hsRes.space) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes+=requestService.getParams(null,null,['id'])

    hsRes.workhours = Workhour.findByPartner_idAndIdAndSpace_id(hsRes.user.partner_id,hsRes.inrequest.id,hsRes.space.id)
    hsRes.interval = Timedelta.get(Partner.get(hsRes.user.partner_id)?.timedelta_id?:4).delta

    return hsRes
  }

  def addspworkhours = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkAccess(hsRes,SPACEID)) return
    hsRes.result=[errorcode:[]]

    def lId = requestService.getIntDef('id',0)
    hsRes.space = Space.findByIdAndUnit_idInList(requestService.getIntDef('space_id',0),Unit.findAllByPartner_id(hsRes.user.partner_id).collect{ it.id }?:[0])
    hsRes.workhours = Workhour.findByPartner_idAndIdAndSpace_id(hsRes.user.partner_id,lId,hsRes.space?.id?:-1)
    if (!hsRes.space||(!hsRes.workhours&&lId)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(['is_mon','is_tue','is_wen','is_thu','is_fra','is_sut','is_sun','is_hol'])
    hsRes.inrequest.time_open = requestService.getTime('workhours_time_open')
    hsRes.inrequest.time_close = requestService.getTime('workhours_time_close')
    hsRes.inrequest.date_start = requestService.getDate('workhours_date_start')
    hsRes.inrequest.date_end = requestService.getDate('workhours_date_end')

    if(!hsRes.inrequest.date_start)
      hsRes.result.errorcode<<3
    if(!hsRes.inrequest.date_end)
      hsRes.result.errorcode<<4
    if(!hsRes.inrequest.is_mon&&!hsRes.inrequest.is_tue&&!hsRes.inrequest.is_wen&&!hsRes.inrequest.is_thu&&!hsRes.inrequest.is_fra&&!hsRes.inrequest.is_sut&&!hsRes.inrequest.is_sun&&!hsRes.inrequest.is_hol)
      hsRes.result.errorcode<<1
    else if (listingService.assertWorkhours(hsRes.inrequest+[partner_id:hsRes.user.partner_id,id:lId,space_id:hsRes.space.id]).size()>0)
      hsRes.result.errorcode<<2

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.workhours = new Workhour(partner_id:hsRes.user.partner_id,space_id:hsRes.space.id)
        hsRes.workhours.updateData(hsRes.inrequest).updatePeriod(hsRes.inrequest).save(failOnError:true,flush:true)
      } catch(Exception e) {
        log.debug("Error save data in Personal/addspworkhours\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def deletespworkhours = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkAccess(hsRes,SPACEID)) return

    try {
      Workhour.findByIdAndPartner_idAndSpace_id(requestService.getIntDef('id',0),hsRes.user.partner_id,requestService.getIntDef('space_id',-1))?.delete(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Personal/deletespworkhours\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Spaces >>>//////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Bookings >>>////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def bookings = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)
    if (!checkAccess(hsRes,BOOKINGID)) return

    def fromDetails = requestService.getIntDef('fromDetails',0)

    if (fromDetails>0){
      session.personalfilters['fromDetails'] = fromDetails
      hsRes.inrequest = session.personalfilters."lastRequest_$fromDetails"
    } else {
      hsRes.inrequest=[:]
    }

    return hsRes
  }

  def customername_autocomplete = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkAccess(hsRes,BOOKINGID)) return

    hsRes.result = [:]
    hsRes.result.query = requestService.getStr('query')
    hsRes.result.suggestions = []
    hsRes.result.data = []
    if(hsRes.result.query?:''){
      listingService.customerListing([partner_id:hsRes.user.partner_id,customername:hsRes.result.query],10,0).records.each{
        hsRes.result.suggestions << it.toOptionValue()
      }
    }
    if(!hsRes.result.suggestions){
      response.sendError(404)
      return
    }
    render hsRes.result as JSON
  }

  def bookinglist = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkAccess(hsRes,BOOKINGID)) return

    if (session.personalfilters?.fromDetails==1){
      hsRes.inrequest = session.personalfilters."lastRequest_$BOOKINGID"
      session.personalfilters['fromDetails'] = 0
    } else {
      hsRes+=requestService.getParams(null,null,['customername','title'])
      hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',0)
      hsRes.inrequest.btype = requestService.getIntDef('btype',0)
      hsRes.inrequest.fromtime = requestService.getDate('fromtime')
      hsRes.inrequest.totime = requestService.getDate('totime')
      hsRes.inrequest.offset = requestService.getOffset()
      session.personalfilters."lastRequest_$BOOKINGID" = hsRes.inrequest
    }

    hsRes.searchresult = listingService.bookingListing(hsRes.inrequest+[partner_id:hsRes.partner.id],20,hsRes.inrequest.offset)

    return hsRes
  }

  def bookinggroupaction = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkAccess(hsRes,BOOKINGID)) return

    def bookingIds = requestService.getIds('booking')
    def groupaction = requestService.getStr('groupaction')

    if (!bookingIds||!bookingGroupActions.contains(groupaction)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.spaces = Space.findAllByUnit_idInList(Unit.findAllByPartner_id(hsRes.user.partner_id).collect{ it.id }?:[0]).collect{ it.id }?:[0]
    try {
      switch(groupaction){
        case 'accept': Booking.findAllByIdInListAndSpace_idInList(bookingIds,hsRes.spaces)?.each{ it.csiSetModstatus(1).save(flush:true) }; break
        case 'delete': Booking.findAllByIdInListAndSpace_idInList(bookingIds,hsRes.spaces)?.each{ it.csiSetModstatus(-1).save(flush:true) }; break
        case 'confirm': Booking.findAllByIdInListAndSpace_idInList(bookingIds,hsRes.spaces)?.each{ it.csiSetPaidstatus(1).save(flush:true) }; break
      }
    } catch(Exception e) {
      log.debug("Error save data in Personal/bookinggroupaction\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Bookings <<<////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
}