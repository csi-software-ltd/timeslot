class Customer {
  def searchService
  static mapping = { version false }

  Integer id
  String email
  String password = ''
  String name
  String phone = ''
  Integer is_block = 0
  Integer is_emailconfirmed = 0
  String scode = ''
  Date inputdate = new Date()

  def beforeInsert(){
    scode = java.util.UUID.randomUUID().toString()
    password = password?:Tools.hidePsw(Tools.generatePassword(Tools.getIntVal(Dynconfig.findByName('customer.passwordlength')?.value,8)))
  }

  def beforeUpdate(){
    if(isDirty('is_block')&&is_block==0) Customerlog.withNewSession{ new Customerlog().resetSuccessDuration(id) }
  }

  def restorySession(sGuid){
    def hsSql = [
      select:"*, customer.id as id",
      from:"customer,customersession",
      where:"(customer.id=customersession.users_id) AND (customersession.guid=:guid)"
    ]

    return searchService.fetchData(hsSql,null,null,[guid:sGuid],null,Customer.class)
  }

  static Customer getInstance(String _email){
    Customer.findOrCreateByEmail(_email)
  }

  static Customer searchInstance(String _email){
    Customer.findByEmail(_email)
  }

  static Integer searchId(String _email){
    Customer.searchInstance(_email)?.id?:0
  }

  Customer refreshScode(){
    scode = scode?:java.util.UUID.randomUUID().toString()
    this
  }

  Customer clearScode(){
    scode = ''
    this
  }

  Customer mailConfirm(){
    is_emailconfirmed = 1
    this
  }

  Customer csiSetPassword(_request){
    password = _request?.password?Tools.hidePsw(_request.password):password
    this
  }

  Customer updateName(String _name){
    name = name?:_name
    this
  }

  Customer csiSetCustomer(hsInrequest){
    email = hsInrequest.login
    name = hsInrequest.username
    password = hsInrequest?.password?Tools.hidePsw(hsInrequest?.password):password
    is_block = hsInrequest?.is_block?:0
    this
  }

  Customer updateMainData(hsInrequest){
    name = hsInrequest.name
    password = hsInrequest?.password?Tools.hidePsw(hsInrequest?.password):password
    this
  }
}