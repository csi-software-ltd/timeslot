class Space {
  static mapping = { version false }

  Integer id
  Integer unit_id
  String name
  String description = ''
  Integer modstatus = 1
  Integer is_visible = 1

  def afterInsert(){
    Workhour.withNewSession{
      Workhour.findAllByPartner_idAndSpace_id(Unit.get(unit_id).partner_id,0).each{
        new Workhour().setData(it.properties).csiSetSpace_id(id).save(flush:true)
      }
    }
  }

  Space csiSetModstatus(iStatus){
    modstatus = iStatus?:0
    this
  }

  Space csiSetVisible(iStatus){
    is_visible = iStatus?:0
    this
  }

  Space updateMainData(_request){
    unit_id = _request.unit_id
    name = _request.name
    description = _request.description?:''
    this
  }
}