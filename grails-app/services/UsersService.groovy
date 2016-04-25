class UsersService implements Serializable{
  static boolean transactional = false
  static scope = "session"
  def transient m_hsUser=null
  static final COOKIENAME='user'
  private static final long serialVersionUID = 1L;
  def grailsApplication

  def saveSession(requestService,iRemember=0){
    if((m_hsUser.id?:0)==0)
      return
    def sGuid = new Usession().createSession(m_hsUser.id)
    requestService.setCookie(COOKIENAME,sGuid,iRemember?Tools.getIntVal(Dynconfig.findByName('user.remembertime')?.value,2592000):-1)
  }
  ///////////////////////////////////////////////////////////////////
  def deleteSession(requestService){
    new Usession().deleteSession(requestService.getCookie(COOKIENAME))
    requestService.setCookie(COOKIENAME,'',Tools.getIntVal(Dynconfig.findByName('user.timeout')?.value,259200))
    m_hsUser = null
  }
 ///////////////////////////////////////////////////////////////////
  void restoreSession(requestService){
    def sGuid = requestService.getCookie(COOKIENAME)
    if(sGuid=='') return

    def hsUser = new User().restorySession(sGuid)

    if(hsUser!=null&&hsUser.size()>0){
      m_hsUser = [
                  id     : hsUser[0].id,
                  login  : hsUser[0].login,
                  email  : hsUser[0].email,
                  name   : hsUser[0].fullname,
                  partner_id   : hsUser[0].partner_id,
                  is_emailconfirmed   : hsUser[0].is_emailconfirmed,
                  group  : Usergroup.get(hsUser[0].usergroup_id),
                  menu   : new Usermenu().csiGetMenu(hsUser[0].usergroup_id),
                  addit  : []
                 ]
      for(menu in m_hsUser.menu){
        if(!menu.is_main)
          m_hsUser.addit << menu.id
      }
    } else
      deleteSession(requestService)
  }
  ///////////////////////////////////////////////////////////////////
  def loginInternalUser(User oUser,requestService,iRemember){
    m_hsUser = null

    if(!oUser)
      return false
    m_hsUser = [
                id     : oUser.id,
                login  : oUser.login,
                email  : oUser.email,
                name   : oUser.fullname,
                partner_id   : oUser.partner_id,
                is_emailconfirmed   : oUser.is_emailconfirmed,
                group  : Usergroup.get(oUser.usergroup_id),
                menu   : new Usermenu().csiGetMenu(oUser.usergroup_id),
                addit  : []
               ]
    for(menu in m_hsUser.menu){
      if(!menu.is_main)
        m_hsUser.addit << menu.id
    }

    saveSession(requestService,iRemember)
    new Userlog().resetSuccessDuration(oUser.id)
    return m_hsUser
  }
  ////////////////////////////////////////////////////////////////////
  def getCurrentUser(requestService){
    if (!checkSession(requestService)){
	    m_hsUser = null
	  } else {
	    restoreSession(requestService)
	  }
    return m_hsUser
  }
  ///////////////////////////////////////////////////////////////////
  def logoutUser(requestService){
    m_hsUser = null
    deleteSession(requestService)
    return true
  }
  ///////////////////////////////////////////////////////////////////
  def checkSession(requestService){
    if (!requestService) return false
    requestService.getCookie(COOKIENAME)?true:false
  }
}