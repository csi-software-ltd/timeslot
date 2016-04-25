class User {
  def searchService
  static mapping = { version false }

  Long id
  String login
  Integer partner_id
  String fullname
  String password
  Integer is_block = 0
  Integer is_remote = 1
  String phone = ''
  String mobile
  String email
  String scode = ''
  Integer usergroup_id
  Integer modstatus = 1
  Integer is_emailconfirmed = 0
  Date inputdate = new Date()
  /////////////////////////////////////////////////////////////////////////////
  def beforeInsert(){
    scode = java.util.UUID.randomUUID().toString()
  }

  def beforeUpdate(){
    if(isDirty('is_block')&&is_block==0) Userlog.withNewSession{ new Userlog().resetSuccessDuration(id) }
  }

  def csiGetUsersList(hsInrequest,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*"
    hsSql.from="user, usergroup"
    hsSql.where="user.usergroup_id=usergroup.id AND usergroup.is_superuser!=1"+
      (hsInrequest?.usergroup_id>0?' AND usergroup_id=:UGId':'')+
      (hsInrequest?.user_id>0?' AND user.id=:uid':'')+
      (hsInrequest?.login?' AND login like CONCAT(:login,"%")':'')+
      (hsInrequest?.modstatus>-100?' AND modstatus =:status':'')+
      (hsInrequest?.is_block?' AND is_block=1':'')
    hsSql.order="user.id desc"

    if(hsInrequest?.user_id>0)
      hsLong['uid'] = hsInrequest.user_id
    if(hsInrequest?.login)
      hsString['login'] = hsInrequest.login
    if(hsInrequest?.modstatus>-100)
      hsLong['status'] = hsInrequest.modstatus
    if(hsInrequest?.usergroup_id>0)
      hsLong['UGId'] = hsInrequest.usergroup_id

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,null,null,iMax,iOffset,'user.id',true,User.class)
  }

  def restorySession(sGuid){
    def hsSql = [
      select:"*, user.id as id",
      from:"user,usession",
      where:"(user.id=usession.users_id) AND (user.modstatus=1) AND (usession.guid=:guid)"]

    return searchService.fetchData(hsSql,null,null,[guid:sGuid],null,User.class)
  }

  User csiSetUser(hsInrequest){
    login = hsInrequest.login
    fullname = hsInrequest.username+' '+hsInrequest.name2
    password = hsInrequest?.password?Tools.hidePsw(hsInrequest?.password):password
    is_block = hsInrequest?.is_block?:0
    is_remote = hsInrequest?.is_remote?:0
    mobile = hsInrequest?.mobile?:''
    email = hsInrequest?.login?:''
    usergroup_id = hsInrequest?.usergroup_id?:1
    this
  }

  User updateMainData(hsInrequest){
    fullname = hsInrequest.fullname
    password = hsInrequest?.password?Tools.hidePsw(hsInrequest?.password):password
    mobile = hsInrequest?.mobile?:''
    email = hsInrequest?.email?:login
    usergroup_id = hsInrequest?.usergroup_id?:1
    this
  }

  User refreshScode(){
    scode = scode?:java.util.UUID.randomUUID().toString()
    this
  }

  User clearScode(){
    scode = ''
    this
  }

  User csiSetPassword(_request){
    password = _request?.password?Tools.hidePsw(_request.password):password
    this
  }

  User csiSetModstatus(iMod){
    modstatus = iMod?:0
    this
  }

  User mailConfirm(){
    is_emailconfirmed = 1
    this
  }
}