class Usermenu implements Serializable {
  def searchService
  static mapping = { version false }
  private static final long serialVersionUID = 1L

  Integer id
  String name
  String controller
  String action
  Integer is_main

  def csiGetMenu(iGroupId){
    def oUsergroup = Usergroup.get(iGroupId)
    if(oUsergroup.menu.size()){
      def hsSql = [select :'*',
                   from   :'usermenu',
                   where  :'id in (:ids)',
                   order  :'id']
      def hsList = [ids:oUsergroup.menu.tokenize(',')]

      return searchService.fetchData(hsSql,null,null,null,hsList,Usermenu.class)
    } else
      return []
  }
}