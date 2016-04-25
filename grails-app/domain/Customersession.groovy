class Customersession {
  static mapping = { cache false }
  def searchService

  String guid
  Integer users_id

  def createSession(iUserId){
    def sGuid = ''
    def bExists = true
    try{
      while(bExists){
        //Моя параноидальная проверка :-))). D.M
        sGuid=java.util.UUID.randomUUID().toString()
        def lsTmp=searchService.fetchData([select:'count(*)',from:'customersession',
            where:'guid=:guid'],null,null,[guid:sGuid],null)
        bExists=lsTmp[0]>0
      }
    }catch(Exception e){
      log.debug("EXCEPTION Customersession:createSession: generate guid (java-sucks) \n"+
        e.toString())
      return ''
    }

    def sSql = "INSERT INTO customersession(guid,users_id) VALUES(:guid,:uid)"
    searchService.updateData(sSql,['uid':iUserId.toLong()],null,['guid':sGuid])

    return sGuid
  }

  def deleteSession(sGuid){
    def sSql = "DELETE FROM customersession WHERE guid=:guid"
    searchService.updateData(sSql,null,null,['guid':sGuid])
  }
}