class Adminmenu {
  static mapping = { version false }
  def searchService

  Integer id
  String name

  def csiGetMenu(iGroupId){
    def lsMenuItemIds = Admingroup.get(iGroupId).menu.tokenize(',')
    def hsSql = [select :'*',
                 from   :'adminmenu',
                 where  :'id in (:ids)',
                 order  :'id']

    searchService.fetchData(hsSql,null,null,null,[ids:lsMenuItemIds],Adminmenu.class)
  }
}