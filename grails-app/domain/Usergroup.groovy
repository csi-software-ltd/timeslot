class Usergroup {
  def searchService
  static mapping = { version false }

  Integer id
  String name = ''
  String menu = ''
  String description = ''
  Integer is_superuser = 0

  def csiFindUsergroup(hsInrequest,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsString=[:]

    hsSql.select="*"
    hsSql.from='usergroup'
    hsSql.where="is_superuser!=1"+
      (hsInrequest?.name?' AND name like concat("%",:gname,"%")':'')
    hsSql.order="name asc"

    if(hsInrequest?.name)
      hsString['gname'] = hsInrequest.name

    searchService.fetchDataByPages(hsSql,null,null,null,hsString,null,null,iMax,iOffset,'id',true,Usergroup.class)
  }

  def csiSetData(hsInrequest){
    name = hsInrequest?.name
    description = hsInrequest?.description?:''
    this
  }
}