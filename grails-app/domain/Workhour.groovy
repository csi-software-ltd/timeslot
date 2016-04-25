import java.sql.Time
import java.text.SimpleDateFormat
class Workhour {
  static mapping = { version false }
  static constraints = {
    date_start(nullable:true)
    date_end(nullable:true)
  }

  Integer id
  Integer partner_id
  Integer space_id = 0
  Integer is_mon = 1
  Integer is_tue = 1
  Integer is_wen = 1
  Integer is_thu = 1
  Integer is_fra = 1
  Integer is_sut = 0
  Integer is_sun = 0
  Integer is_hol = 0
  Time time_open = new Time(9,0,0)
  Time time_close = new Time(18,0,0)
  Date date_start
  Date date_end

  def afterInsert(){
    Partner.withNewSession{
      if (space_id==0) Partner.get(partner_id)?.updateWorkhours().save(flush:true)
    }
  }

  def afterDelete(){
    Partner.withNewSession{
      if (space_id==0) Partner.get(partner_id)?.updateWorkhours().save(flush:true)
    }
  }

  def afterUpdate(){
    Partner.withNewSession{
      if (space_id==0) Partner.get(partner_id)?.updateWorkhours().save(flush:true)
    }
  }

  def prepareJSON(Date _date, Integer _custId){
    prepareJSON(_date)
  }

  def prepareJSON(Date _date){
    def mapJSON = [id: 'w'+id.toString(), resourceId: space_id, title: 'Рабочие часы', backgroundColor: 'green', rendering: 'background']
    _date.set(hourOfDay:time_open[Calendar.HOUR_OF_DAY],minute:time_open[Calendar.MINUTE],second:time_open[Calendar.SECOND])
    mapJSON.start = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(_date)
    _date.set(hourOfDay:time_close[Calendar.HOUR_OF_DAY]!=0?time_close[Calendar.HOUR_OF_DAY]:24,minute:time_close[Calendar.MINUTE],second:time_close[Calendar.SECOND])
    mapJSON.end = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(_date)
    mapJSON
  }

  Workhour updatePeriod(_request){
    date_start = _request.date_start
    date_end = _request.date_end
    this
  }

  Workhour updateData(_request){
    is_mon = _request.is_mon?:0
    is_tue = _request.is_tue?:0
    is_wen = _request.is_wen?:0
    is_thu = _request.is_thu?:0
    is_fra = _request.is_fra?:0
    is_sut = _request.is_sut?:0
    is_sun = _request.is_sun?:0
    is_hol = _request.is_hol?:0
    time_open = _request.time_open?:new Time(0,0,0)
    time_close = _request.time_close?:new Time(24,0,0)
    this
  }

  Workhour setData(_prop){
    properties = _prop
    date_start = new Date()
    date_end = new Date()+365
    this
  }

  Workhour csiSetSpace_id(iSpaceId){
    space_id = iSpaceId?:0
    this
  }
}