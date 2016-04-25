class Spacefull {
  static mapping = { version false }

/////////Space///////////////
  Integer id
  Integer unit_id
  String name
  String description
  Integer modstatus
  Integer is_visible
/////////Unit////////////////
  Integer partner_id
  Integer unitstatus
  String unitname

  String toJsonString(){
    "{ id: '$id', unit: '$unitname', space: '$name' }"
  }
}