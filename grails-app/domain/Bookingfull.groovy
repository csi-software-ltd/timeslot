class Bookingfull {
  static mapping = { version false }

/////////Booking/////////////
  Integer id
  Integer space_id
  Date bdate
  Integer customer_id
  String title
  Integer btype
  Date fromtime
  Date totime
  Integer duration
  BigDecimal price
  Integer valuta_id
  Integer modstatus
  Integer paidstatus
  String notes
/////////Customer////////////
  String firstname
  String lastname
/////////Space///////////////
  String name
/////////Partner/////////////
  String partnername

  String toCustomerString(){
    customer_id>0 ? "$firstname $lastname" : ""
  }

  String toHourDurationString(){
    (duration / 60).toInteger() + ":" + (duration % 60 > 10 ? duration % 60 : duration % 60 > 0 ? "0" + duration % 60 : "00")
  }
}