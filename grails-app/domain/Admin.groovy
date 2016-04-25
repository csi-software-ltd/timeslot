class Admin {
  static mapping = { version false }

  Long id
  String login
  String password
  Integer accesslevel
  Integer admingroup_id
  Integer modstatus
  Integer partner_id
  String tel

  Admin csiSetPassword(_request){
    password = _request?.password?Tools.hidePsw(_request.password):password
    this
  }
}