class Unit {
  static mapping = { version false }

  Integer id
  Integer partner_id
  Integer modstatus = 1
  String name = 'Основная категория'
  String description = ''
  String contactperson = ''

  Unit csiSetModstatus(iStatus){
    modstatus = iStatus?:0
    this
  }

  Unit updateMainData(_request){
    name = _request.name
    description = _request.description?:''
    contactperson = _request.contactperson?:''
    this
  }
}