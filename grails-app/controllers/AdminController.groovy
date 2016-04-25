import grails.converters.*

class AdminController {
  def requestService
  def listingService
  def usersService
  
  def beforeInterceptor = [action:this.&checkAdmin,except:['login','index']]

  final Integer PROFILEID = 1
  final Integer PARTNERID = 2
  final Integer CUSTOMERID = 3
  final Integer BOOKINGID = 4

  def checkAdmin() {
    if(session?.admin?.id!=null){
      if(!session.adminfilters) session.adminfilters = [:]
      def oTemp_notification=Temp_notification.findWhere(id:1,status:1)
      session.attention_message=oTemp_notification?oTemp_notification.text:null
    }else{
      redirect(controller:'admin', action:'index', params:[redir:1])
      return false;
    }
  }
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////
  def checkAccess(iActionId){
    def bDenied = true
    session.admin.menu.each{
	    if (iActionId==it.id) bDenied = false
	  }
    if (bDenied) {
	    redirect(action:'profile');
	    return
	  }
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def index = {
    if (session?.admin?.id){
      redirect(action:'profile')
      return
    } else return params
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def login = {
    requestService.init(this)
    def sAdmin=requestService.getStr('login')
    def sPassword=requestService.getStr('password')	    
    if (sAdmin==''){
      flash.error = 1 // set login
      redirect(controller:'admin',action:'index')//TODO change action
      return
    }
    def oAdminlog = new Adminlog()
    def blocktime = Tools.getIntVal(Dynconfig.findByName('admin.blocktime')?.value,1800)
    def unsuccess_log_limit = Tools.getIntVal(Dynconfig.findByName('admin.unsuccess_log_limit')?.value,5)
    sPassword=Tools.hidePsw(sPassword)
    def oAdmin=Admin.find('from Admin where login=:login and modstatus=1',
                             [login:sAdmin.toLowerCase()])
    if(!oAdmin){
      flash.error = 2 // Wrong password or admin does not exists
      redirect(controller:'admin',action:'index')
      return
    } else if (oAdminlog.csiCountUnsuccessDurationLogs(oAdmin.id)[0]>=Tools.getIntVal(Dynconfig.findByName('admin.unsuccess_duration_log_limit')?.value,30)){
      flash.error = 3 // Admin blocked
      oAdminlog = new Adminlog(admin_id:oAdmin.id,logtime:new Date(),ip:request.remoteAddr,success:3,success_duration:-1)
      if (!oAdminlog.save(flush:true)){
        log.debug('error on save Adminlog in Admin:login')
        oAdminlog.errors.each{log.debug(it)}
      }
      redirect(controller:'admin',action:'index')
      return	
    } else if (oAdminlog.csiCountUnsuccessLogs(oAdmin.id, new Date(System.currentTimeMillis()-blocktime*1000))[0]>=unsuccess_log_limit){
      flash.error = 3 // Admin blocked
      oAdminlog = new Adminlog(admin_id:oAdmin.id,logtime:new Date(),ip:request.remoteAddr,success:2,success_duration:-1)
      if (!oAdminlog.save(flush:true)){
        log.debug('error on save Adminlog in Admin:login')
        oAdminlog.errors.each{log.debug(it)}
      }
      redirect(controller:'admin',action:'index')
      return
    }else if (oAdmin.password != sPassword) {
      flash.error = 2 // Wrong password or admin does not exists
      oAdminlog = new Adminlog(admin_id:oAdmin.id,logtime:new Date(),ip:request.remoteAddr,success:0,success_duration:0)
      if (!oAdminlog.save(flush:true)){
        log.debug('error on save Adminlog in Admin:login')
        oAdminlog.errors.each{log.debug(it)}
      }
      redirect(controller:'admin',action:'index')
      return
    }
    def oAdminmenu = new Adminmenu()
    session.admin = [id            : oAdmin.id,
                     login         : oAdmin.login,
                     group         : oAdmin.admingroup_id,
                     menu          : oAdminmenu.csiGetMenu(oAdmin.admingroup_id),
                     accesslevel   : oAdmin.accesslevel
                    ]
    oAdminlog = new Adminlog(admin_id:oAdmin.id,logtime:new Date(),ip:request.remoteAddr,success:1,success_duration:1)
    if (!oAdminlog.save(flush:true)){
      log.debug('error on save Adminlog in Admin:login')
      oAdminlog.errors.each{log.debug(it)}
    }
    oAdminlog.resetSuccessDuration(oAdmin.id)
    redirect(action:'partnerfilter',params:[ext:1])
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def logout = {
    requestService.init(this)
    session.admin = null
    redirect(controller:'admin',action: 'index')
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def menu = {
    requestService.init(this)
    def iPage = requestService.getIntDef('id',1)
    switch (iPage){	
      case PROFILEID: redirect(action:'profile'); return
      case PARTNERID: redirect(action:'partnerfilter'); return
      case CUSTOMERID: redirect(action:'customerfilter'); return
      case BOOKINGID: redirect(action:'bookingfilter'); return
      default: redirect(action:'partnerfilter'); return
    }
    return [admin:session.admin,action_id:iPage]
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Administrator`s profile >>>/////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def profile = {
    checkAccess(PROFILEID)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes+=[administrator:Admin.get(session.admin.id),action_id:PROFILEID]
    hsRes.admin = session.admin
    def oAdminlog = new Adminlog()
    def lsLogs = oAdminlog.csiGetLogs(hsRes.admin.id)
    if (lsLogs.size()>0){
      hsRes.lastlog = lsLogs[0]
      hsRes.unsuccess_log_amount = oAdminlog.csiCountUnsuccessLogs(hsRes.admin.id, new Date()-7)[0]
      hsRes.unsuccess_limit = Tools.getIntVal(Dynconfig.findByName('admin.unsuccess_log_limit')?.value,5)
    }
    hsRes.partner = Partner.get(1)
    hsRes.interval = Timedelta.get(hsRes.partner.timedelta_id).delta
    hsRes.spaces = listingService.partnerSchedulerListing(partner_id:hsRes.partner.id,onlyVisible:true)

    return hsRes
  }

  def updateProfile = {
    checkAccess(PROFILEID)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.result=[errorcode:[]]

    hsRes+=requestService.getParams(null,null,['pass','confirm_pass'])

    if(!hsRes.inrequest.pass)
      hsRes.result.errorcode<<1
    else {
      if(hsRes.inrequest.pass.size()<Tools.getIntVal(Dynconfig.findByName('user.passwordlength')?.value,7))
        hsRes.result.errorcode<<2
      else if(!hsRes.inrequest.pass.matches('.*(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?!.*[\\W_А-я]).*'))
        hsRes.result.errorcode<<2
      else if(hsRes.inrequest.pass!=hsRes.inrequest.confirm_pass)
        hsRes.result.errorcode<<3
    }

    if(!hsRes.result.errorcode){
      try {
        Admin.get(session.admin.id)?.csiSetPassword(password:hsRes.inrequest.pass)?.save(flush:true,failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Admin/updateProfile\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Administrator`s profile <<</////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Partners >>>////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def partnerfilter = {
    checkAccess(PARTNERID)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes+=[administrator:Admin.get(session.admin.id),action_id:PARTNERID]
    hsRes.admin = session.admin

    def fromDetails = requestService.getIntDef('fromDetails',0)

    if (fromDetails>0){
      session.adminfilters['fromDetails'] = fromDetails
      hsRes.inrequest = session.adminfilters."lastRequest_$fromDetails"
    } else {
      hsRes.inrequest=[:]
    }

    return hsRes
  }

  def partners = {
    checkAccess(PARTNERID)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)

    if (session.adminfilters?.fromDetails==1){
      hsRes.inrequest = session.adminfilters."lastRequest_$PARTNERID"
      session.adminfilters['fromDetails'] = 0
    } else {
      hsRes+=requestService.getParams(null)
      hsRes.inrequest.offset = requestService.getOffset()
      session.adminfilters."lastRequest_$PARTNERID" = hsRes.inrequest
    }

    hsRes.partners = listingService.partnerAdminListing(hsRes.inrequest,20,hsRes.inrequest?.offset?:0)
    hsRes.business = Business.list().inject([0:'не указан']){map, business -> map[business.id]=business.name;map}

    return hsRes
  }

  def partner = {
    checkAccess(PARTNERID)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes+=[administrator:Admin.get(session.admin.id),action_id:PARTNERID]
    hsRes.admin = session.admin

    hsRes.partner = Partner.get(requestService.getLongDef('id',0))
    if (!hsRes.partner) {
      response.sendError(404)
      return
    }

    return hsRes
  }

  def updatepartner = {
    checkAccess(PARTNERID)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.result=[errorcode:[]]

    hsRes.partner = Partner.get(requestService.getIntDef('id',0))
    if (!hsRes.partner) {
      response.sendError(404)
      return
    }

    hsRes+=requestService.getParams(['business_id','lockin','gmt_id','timedelta_id','maxdays','minhours','is_privacy',
                                     'stype','payway','modstatus'],null,['pname','web','email','tel','country','city',
                                     'address','description'])

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
        hsRes.partner.updateMainData(hsRes.inrequest).updateAdminData(hsRes.inrequest).csiSetModstatus(hsRes.inrequest.modstatus).save(flush:true,failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Admin/updatepartner\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def partnerusers = {
    checkAccess(PARTNERID)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)

    hsRes.partner = Partner.get(requestService.getIntDef('id',0))
    if (!hsRes.partner) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.users = User.findAllByPartner_id(hsRes.partner.id)
    hsRes.usergroups = Usergroup.list().inject([:]){map, group -> map[group.id]=group.name;map}

    return hsRes
  }

  def loginAsUser = {
    checkAccess(PARTNERID)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)

    usersService.loginInternalUser(User.get(requestService.getLongDef('id',0)),requestService,0)

    render(contentType:"application/json"){[error:false]}
    return
  }

  def partnerspaces = {
    checkAccess(PARTNERID)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)

    hsRes.partner = Partner.get(requestService.getIntDef('id',0))
    if (!hsRes.partner) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.spaces = listingService.partnerSpacesListing([partner_id:hsRes.partner.id],-1,0)
    hsRes.units = Unit.findAllByPartner_id(hsRes.partner.id).inject([:]){map, unit -> map[unit.id]=unit.name;map}

    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Partners <<<////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Customers >>>///////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def customerfilter = {
    checkAccess(CUSTOMERID)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes+=[administrator:Admin.get(session.admin.id),action_id:CUSTOMERID]
    hsRes.admin = session.admin

    def fromDetails = requestService.getIntDef('fromDetails',0)

    if (fromDetails>0){
      session.adminfilters['fromDetails'] = fromDetails
      hsRes.inrequest = session.adminfilters."lastRequest_$fromDetails"
    } else {
      hsRes.inrequest=[:]
    }

    return hsRes
  }

  def customers = {
    checkAccess(CUSTOMERID)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)

    if (session.adminfilters?.fromDetails==1){
      hsRes.inrequest = session.adminfilters."lastRequest_$CUSTOMERID"
      session.adminfilters['fromDetails'] = 0
    } else {
      hsRes+=requestService.getParams(null,null,['email','companyname'])
      hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',0)
      hsRes.inrequest.offset = requestService.getOffset()
      session.adminfilters."lastRequest_$CUSTOMERID" = hsRes.inrequest
    }

    hsRes.customers = listingService.customerAdminListing(hsRes.inrequest,20,hsRes.inrequest?.offset?:0)
    hsRes.groups = Partnergroup.list().inject(['0':'нет']){map, group -> map[group.id]=group.name;map}

    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Customers <<<///////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Bookings >>>////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def bookingfilter = {
    checkAccess(BOOKINGID)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes+=[administrator:Admin.get(session.admin.id),action_id:BOOKINGID]
    hsRes.admin = session.admin

    def fromDetails = requestService.getIntDef('fromDetails',0)

    if (fromDetails>0){
      session.adminfilters['fromDetails'] = fromDetails
      hsRes.inrequest = session.adminfilters."lastRequest_$fromDetails"
    } else {
      hsRes.inrequest=[:]
    }

    return hsRes
  }

  def bookings = {
    checkAccess(BOOKINGID)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)

    if (session.adminfilters?.fromDetails==1){
      hsRes.inrequest = session.adminfilters."lastRequest_$BOOKINGID"
      session.adminfilters['fromDetails'] = 0
    } else {
      hsRes+=requestService.getParams(null,null,['customername','companyname'])
      hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',0)
      hsRes.inrequest.fromtime = requestService.getDate('fromtime')
      hsRes.inrequest.totime = requestService.getDate('totime')
      hsRes.inrequest.offset = requestService.getOffset()
      session.adminfilters."lastRequest_$BOOKINGID" = hsRes.inrequest
    }

    hsRes.bookings = listingService.bookingAdminListing(hsRes.inrequest,20,hsRes.inrequest.offset)

    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Bookings <<<////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
