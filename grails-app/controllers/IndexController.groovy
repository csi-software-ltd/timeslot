import grails.converters.*

class IndexController {
  def requestService
  def usersService
  def mailerService

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
  def index = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    if (hsRes.user?.id){
      redirect(controller:'personal',action:'organization')
      return
    } else return params
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Login >>>///////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////  
  def login = {
    if(Temp_ipblock.findWhere(userip:request.remoteAddr,status:1)){
      redirect(controller:'index',action:'index')
      return
    }
    requestService.init(this)

    def sUser = requestService.getStr('login')
    def sPassword = requestService.getStr('password')
    def iRemember = requestService.getIntDef('is_remember',0)
    if (sUser==''){
      flash.error = 1 // set login
      redirect(controller:'index',action:'index')
      return
    }
    def oUserlog = new Userlog()
    def blocktime = Tools.getIntVal(Dynconfig.findByName('user.blocktime')?.value,900)
    def unsuccess_log_limit = Tools.getIntVal(Dynconfig.findByName('user.unsuccess_log_limit')?.value,3)
    sPassword = Tools.hidePsw(sPassword)
    def oUser = User.find('from User where (login=:login or email=:login) and modstatus=1',[login:sUser.toLowerCase()])
    if(!oUser){
      flash.error = 2 // Wrong password or User does not exists
      redirect(controller:'index',action:'index')
      return
    } else if (oUser.is_block || oUserlog.csiCountUnsuccessDurationLogs(oUser.id)[0]>=Tools.getIntVal(Dynconfig.findByName('user.unsuccess_duration_log_limit')?.value,30)){
      flash.error = 5 // User blocked
      oUserlog = new Userlog(user_id:oUser.id,logtime:new Date(),ip:request.remoteAddr,success:3,success_duration:-1)
      if (!oUserlog.save(flush:true)){
        log.debug('error on save Userlog in User:login')
        oUserlog.errors.each{log.debug(it)}
      }
      if(!oUser.is_block){
        oUser.is_block = 1
        if (!oUser.save(flush:true)){
          log.debug('error on save User in User:login')
          oUser.errors.each{log.debug(it)}
        }
      }
      redirect(controller:'index',action:'index')
      return	
    } else if (oUserlog.csiCountUnsuccessLogs(oUser.id, new Date(System.currentTimeMillis()-blocktime*1000))[0]>=unsuccess_log_limit){
      flash.error = 3 // User blocked
      oUserlog = new Userlog(user_id:oUser.id,logtime:new Date(),ip:request.remoteAddr,success:2,success_duration:-1)
      if (!oUserlog.save(flush:true)){
        log.debug('error on save Userlog in User:login')
        oUserlog.errors.each{log.debug(it)}
      }
      redirect(controller:'index',action:'index')
      return
    } else if (oUser.password != sPassword) {
      flash.error = 2 // Wrong password or User does not exists
      oUserlog = new Userlog(user_id:oUser.id,logtime:new Date(),ip:request.remoteAddr,success:0,success_duration:0)
      if (!oUserlog.save(flush:true)){
        log.debug('error on save Userlog in User:login')
        oUserlog.errors.each{log.debug(it)}
      }
      redirect(controller:'index',action:'index')
      return
    }

    if(Tools.checkIpRange(request.remoteAddr) || oUser.is_remote){
      oUserlog = new Userlog(user_id:oUser.id,logtime:new Date(),ip:request.remoteAddr,success:1,success_duration:1)
      if (!oUserlog.save(flush:true)){
        log.debug('error on save Userlog in User:login')
        oUserlog.errors.each{log.debug(it)}
      }

      usersService.loginInternalUser(oUser,requestService,iRemember)
      redirect(controller:'personal',action:'organization')
      return
    } else {
      flash.error = 4
      redirect(controller:'index',action:'index')
      return
    }
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def logout = {
    requestService.init(this)
    usersService.logoutUser(requestService)
    redirect(controller:'index',action:'index')
  }

  def signup = {
    if(Temp_ipblock.findWhere(userip:request.remoteAddr,status:1)){
      redirect(controller:'index',action:'index')
      return
    }
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.result=[errorcode:[]]

    hsRes+=requestService.getParams(['is_remember'],null,['orgname','username','name2','login','password','password2'])

    if(!hsRes.inrequest.orgname)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.username)
      hsRes.result.errorcode<<2
    if(!hsRes.inrequest.name2)
      hsRes.result.errorcode<<3
    if(!hsRes.inrequest.login)
      hsRes.result.errorcode<<4
    else if(!Tools.checkEmailString(hsRes.inrequest.login))
      hsRes.result.errorcode<<9
    else if(User.findByLogin(hsRes.inrequest.login))
      hsRes.result.errorcode<<10
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
        def oUser = new User(partner_id:new Partner(name:hsRes.inrequest.orgname).save(flush:true,failOnError:true)?.id).csiSetUser(hsRes.inrequest).save(flush:true,failOnError:true)
        usersService.loginInternalUser(oUser,requestService,hsRes.inrequest.is_remember?:0)
        mailerService.sendActivationMail(oUser)
      } catch(Exception e) {
        log.debug("Error save data in Index/signup\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def confirm = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)

    hsRes.confirm_user = User.findByScodeAndScodeNotEqual(requestService.getStr('id'),'')

    if(hsRes.confirm_user){
      try {
        hsRes.confirm_user.mailConfirm().clearScode().save(flush:true,failOnError:true)
        mailerService.sendSuccessConfirmationMail(hsRes.confirm_user)
        if (!hsRes.user) hsRes.user = usersService.loginInternalUser(hsRes.confirm_user,requestService,1)
        else hsRes.user.is_emailconfirmed = 1
      } catch(Exception e) {
        log.debug("Error save data in Index/confirm\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    hsRes.partner = Partner.get(hsRes.user?.partner_id?:0)

    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Login <<<///////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Restore >>>/////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def restore = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
    if(hsRes.user!=null){
      redirect(controller:'personal',action:'organization')
      return
    }

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
    else if(!User.findByLogin(hsRes.inrequest.login))
      hsRes.result.errorcode<<3

    if(!hsRes.result.errorcode){
      try {
        def oUser = User.findByLogin(hsRes.inrequest.login).refreshScode().save(flush:true,failOnError:true)
        mailerService.sendRestorePasswordMail(oUser)
      } catch(Exception e) {
        log.debug("Error save data in Index/rest\n"+e.toString())
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
      redirect(controller:'personal',action:'organization')
      return
    }

    hsRes.restore_user = User.findByScodeAndScodeNotEqual(requestService.getStr('id'),'')

    return hsRes
  }

  def passrest = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.result=[errorcode:[]]

    hsRes+=requestService.getParams(null,null,['scode','password','password2'])

    hsRes.restore_user = User.findByScodeAndScodeNotEqual(hsRes.inrequest.scode,'')

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
        usersService.loginInternalUser(hsRes.restore_user,requestService,1)
      } catch(Exception e) {
        log.debug("Error save data in Index/passrest\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Restore <<</////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def justtest = {
    return
  }
}