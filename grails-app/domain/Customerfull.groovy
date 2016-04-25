class Customerfull {
  static mapping = { version false }

/////////Customertopartner///
  Integer id
  Integer customer_id
  Integer partner_id
  String firstname
  String lastname
  Integer partnergroup_id
  String company
  String phone
  Integer is_trust
  Integer modstatus
  String searchname
  Date inputdate
/////////Customer////////////
  String email
  String custname
  String custphone

  String toOptionValue(){
    "$firstname $lastname"
  }
}