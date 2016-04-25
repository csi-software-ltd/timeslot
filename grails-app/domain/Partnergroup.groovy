class Partnergroup {
  static mapping = { version false }

  Integer id
  Integer partner_id
  Integer modstatus = 1
  String name = 'Общая группа'
  String description = ''
  Integer is_main = 0

  Partnergroup csiSetModstatus(iStatus){
  	modstatus = iStatus?:0
  	this
  }

  Partnergroup updateMainData(_request){
    name = _request.name
    description = _request.description?:''
    this
  }
}