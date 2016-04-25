class Adminlog {
  static mapping = { version false }
  def searchService

  Long id
  Long admin_id
  Date logtime
  String ip
  Integer success
  Integer success_duration

  def csiGetLogs(lId){
    def hsSql = [select :'distinct *',
                 from   :'adminlog',
                 where  :'admin_id = :id AND success=1',
                 order  :'logtime desc']

    searchService.fetchData(hsSql,[id: lId],null,null,null,Adminlog.class)
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def csiCountUnsuccessLogs(lId, dDateFrom){
    def sDateFrom = String.format('%tY-%<tm-%<td %<tH:%<tM:%<tS', dDateFrom)
    def hsSql = [select :'count(id)',
                 from   :'adminlog',
                 where  :"admin_id = :id AND success=0 AND logtime>'${sDateFrom}'"]

    searchService.fetchData(hsSql,[id: lId],null,null,null)
  }

  def resetSuccessDuration(lAdminId){
    def sSql = "UPDATE adminlog SET success_duration=1 WHERE admin_id=:id AND success_duration=0"
    searchService.updateData(sSql,['id':lAdminId])
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def csiCountUnsuccessDurationLogs(lId){
    def hsSql = [select :'count(id)',
                 from   :'adminlog',
                 where  :"admin_id = :id AND success_duration=0"]

    searchService.fetchData(hsSql,[id: lId],null,null,null)
  }
}