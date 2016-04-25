import java.sql.Time
class Unitprice {
  static mapping = { version false }

  Integer id
  Integer unit_id
  BigDecimal price
  Integer valuta_id = 857
  Integer forperiod
  Integer minperiod
  Integer is_exception = 0
  Integer is_mon = 0
  Integer is_tue = 0
  Integer is_wen = 0
  Integer is_thu = 0
  Integer is_fra = 0
  Integer is_sut = 0
  Integer is_sun = 0
  Integer is_hol = 0
  Time time_start = new Time(0,0,0)
  Time time_end = new Time(24,0,0)

  Unitprice updateMainData(_request){
    price = _request.price
    forperiod = _request.forperiod
    minperiod = _request.minperiod
    this
  }

  Unitprice updateExceptionData(_request){
    if (is_exception){
      is_mon = _request.is_mon?:0
      is_tue = _request.is_tue?:0
      is_wen = _request.is_wen?:0
      is_thu = _request.is_thu?:0
      is_fra = _request.is_fra?:0
      is_sut = _request.is_sut?:0
      is_sun = _request.is_sun?:0
      is_hol = _request.is_hol?:0
      time_start = _request.time_start?:new Time(0,0,0)
      time_end = _request.time_end?:new Time(24,0,0)
    }
    this
  }
}