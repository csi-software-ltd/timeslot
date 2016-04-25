import groovy.time.*
import java.text.SimpleDateFormat
class Booking {
  static mapping = { version false }
  def mailerService

  Integer id
  Integer space_id
  Date bdate = new Date()
  Integer customer_id
  String title = ''
  Integer btype = 1
  Date fromtime
  Date totime
  Integer duration = 0
  BigDecimal price = 0.0g
  Integer valuta_id = 857
  Integer modstatus = 0
  Integer paidstatus = 0
  String notes = ''

  def beforeInsert(){
    btype = customer_id > 0 ? 1 : customer_id < 0 ? 0 : 2
    use( TimeCategory ) {
      def tmpDuration = totime - fromtime
      duration = tmpDuration.days*60*24 + tmpDuration.hours*60 + tmpDuration.minutes
    }
  }

  def beforeUpdate(){
    btype = customer_id > 0 ? 1 : customer_id < 0 ? 0 : 2
    use( TimeCategory ) {
      def tmpDuration = totime - fromtime
      duration = tmpDuration.days*60*24 + tmpDuration.hours*60 + tmpDuration.minutes
    }
    if (isDirty('modstatus')||isDirty('paidstatus')){
      def oCustomer = Customer.get(Customertopartner.get(customer_id)?.customer_id)
      if (oCustomer?.is_emailconfirmed) mailerService.sendBookingNotificationMail(oCustomer)
    }
  }

  def prepareJSON(Date _date){
    prepareJSON(_date,-1)
  }

  def prepareJSON(Date _date, Integer _custId){
    def mapJSON = [id: id, resourceId: space_id, title: title, description: notes, editable: _custId==-1?:customer_id==_custId&&_custId>0 ]
    mapJSON.color = btype == 1 ? 'lightblue' : btype == 2 ? 'lightyellow' : 'pink'
    mapJSON.start = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(fromtime)
    mapJSON.end = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(totime)
    mapJSON
  }

  Booking updatePeriod(_request){
    fromtime = _request.fromtime
    totime = _request.totime
    this
  }

  Booking updateMainData(_request){
    customer_id = _request.customer_id?:0
    title = _request.title?:''
    price = _request.price?:0.0g
    notes = _request.description?:''
    this
  }

  Booking csiSetSpace_id(iSpaceId){
    space_id = iSpaceId
    this
  }

  Booking csiSetModstatus(iStatus){
    modstatus = iStatus?:0
    this
  }

  Booking csiSetPaidstatus(iStatus){
    paidstatus = iStatus?:0
    this
  }
}