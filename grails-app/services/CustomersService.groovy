class CustomersService implements Serializable{
  static boolean transactional = false
  static scope = "session"
  def transient m_hsUser=null
  static final COOKIENAME='customer'
  private static final long serialVersionUID = 1L;

  def saveSession(requestService,iRemember=0){
    if((m_hsUser.id?:0)==0)
      return
    def sGuid = new Customersession().createSession(m_hsUser.id)
    requestService.setCookie(COOKIENAME,sGuid,iRemember?Tools.getIntVal(Dynconfig.findByName('customer.remembertime')?.value,2592000):-1)
  }
  ///////////////////////////////////////////////////////////////////
  def deleteSession(requestService){
    new Customersession().deleteSession(requestService.getCookie(COOKIENAME))
    requestService.setCookie(COOKIENAME,'',Tools.getIntVal(Dynconfig.findByName('customer.timeout')?.value,259200))
    m_hsUser = null
  }
 ///////////////////////////////////////////////////////////////////
  void restoreSession(requestService){
    def sGuid = requestService.getCookie(COOKIENAME)
    if(sGuid=='') return

    def hsUser = new Customer().restorySession(sGuid)

    if(hsUser!=null&&hsUser.size()>0)
      m_hsUser = [
                  id     : hsUser[0].id,
                  login  : hsUser[0].email,
                  email  : hsUser[0].email,
                  name   : hsUser[0].name,
                  is_emailconfirmed   : hsUser[0].is_emailconfirmed,
                  menu   : new Customermenu().csiGetMenu()
                 ]
    else
      deleteSession(requestService)
  }
  ///////////////////////////////////////////////////////////////////
  def loginInternalUser(Customer oUser,requestService,iRemember){
    m_hsUser = null

    if(!oUser)
      return false
    m_hsUser = [
                id     : oUser.id,
                login  : oUser.email,
                email  : oUser.email,
                name   : oUser.name,
                is_emailconfirmed   : oUser.is_emailconfirmed,
                menu   : new Customermenu().csiGetMenu()
               ]
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