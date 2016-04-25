import java.sql.Time
class Partner {
  static mapping = { version false }

  Integer id
  String name
  String linkname = ''
  String description = ''
  String web = ''
  String email = ''
  String tel = ''
  String address = ''
  String city = ''
  String country = ''
  Integer business_id = 0
  Integer gmt_id = 16
  Integer timedelta_id = 3
  Time time_open = new Time(9,0,0)
  Time time_end = new Time(18,0,0)
  Integer maxdays = 0
  Integer minhours = 0
  Integer lockin = 0
  Integer modstatus = 1
  Integer stype = 1
  Integer payway = 0
  Integer is_privacy = 0

  def afterInsert(){
    Unit.withNewSession{
      new Unit(partner_id:id).save(flush:true)
      new Workhour(partner_id:id).save(flush:true)
      new Partnergroup(partner_id:id,is_main:1).save(flush:true)
    }
  }

  def beforeInsert(){
    linkname = csiGetUniqueLinkname(name)
  }

  def csiGetUniqueLinkname(sName){
    def tempResult = Tools.transliterate(sName)
    int i = 0
    if (tempResult.matches("[0-9-]+")||tempResult in ['restore','passrestore','confirm','admin','company','index','personal','scheduler'])
      tempResult = "ts_"+tempResult

    def result = tempResult
    while (Partner.findByLinknameAndIdNotEqual(result,id?:0)){
      i++
      result = tempResult + '_' + i
    }

    return result
  }

  String stringMinTime(){
    String.format('%tH:%<tM',time_open?:new java.sql.Time(0,0,0))
  }

  String stringMaxTime(){
    time_end!=new Time(0,0,0)?String.format('%tH:%<tM',time_end):'24:00'
  }

  Partner csiSetModstatus(iStatus){
    modstatus = iStatus?:0
    this
  }

  Partner updateAdminData(_request){
    stype = _request.stype?:1
    payway = _request.payway?:0
    this
  }

  Partner updateMainData(_request){
    name = _request.pname
    description = _request.description?:''
    web = _request.web?:''
    email = _request.email?:''
    tel = _request.tel?:''
    address = _request.address
    city = _request.city
    country = _request.country
    business_id = _request.business_id
    gmt_id = _request.gmt_id?:16
    timedelta_id = _request.timedelta_id?:3
    maxdays = _request.maxdays?:0
    minhours = _request.minhours?:0
    lockin = _request.lockin?:0
    is_privacy = _request.is_privacy?:0
    this
  }

  Partner updateWorkhours(){
    time_open = Workhour.findByPartner_idAndSpace_id(id,0,[sort:'time_open',order:'asc'])?.time_open?:new Time(9,0,0)
    time_end = Workhour.findByPartner_idAndSpace_idAndTime_close(id,0,new Time(24,0,0))?new Time(24,0,0):Workhour.findByPartner_idAndSpace_id(id,0,[sort:'time_close',order:'desc'])?.time_close?:new Time(18,0,0)
    this
  }

  Integer csiGetCommonGroupId(){
    Partnergroup.findByPartner_idAndIs_main(id,1)?.id
  }
}