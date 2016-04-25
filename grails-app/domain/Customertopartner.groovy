class Customertopartner {
  static mapping = { version false }

  Integer id
  Integer customer_id
  Integer partner_id
  String firstname
  String lastname = ''
  Integer partnergroup_id
  String company = ''
  String phone = ''
  Integer is_trust = 0
  Integer modstatus = 1
  String searchname = ''
  Date inputdate = new Date()

  def beforeInsert(){
    searchname = company + ' ' + phone + ' ' + (Customer.get(customer_id)?.email?:'')
  }

  def beforeUpdate(){
    searchname = company + ' ' + phone + ' ' + (Customer.get(customer_id)?.email?:'')
  }

  Customertopartner updateMainData(_request){
    firstname = _request.firstname
    lastname = _request.lastname?:''
    partnergroup_id = _request.partnergroup_id
    company = _request.company?:''
    phone = _request.phone?:''
    this
  }

  Customertopartner csiSetModstatus(iStatus){
    modstatus = iStatus?:0
    this
  }

  Customertopartner csiSetIsTrust(iStatus){
    is_trust = iStatus?:0
    this
  }
}