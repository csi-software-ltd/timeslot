class ListingService {
  def searchService

  def partnerUnitsListing(hsInrequest,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*"
    hsSql.from='unit'
    hsSql.where="1=1"+
      (hsInrequest?.partner_id>0?' AND partner_id=:partner_id':'')
    hsSql.order="name asc"

    if(hsInrequest?.partner_id>0)
      hsLong['partner_id'] = hsInrequest.partner_id

    searchService.fetchDataByPages(hsSql,null,hsLong,null,null,null,null,iMax,iOffset,'id',true,Unit.class)
  }

  def partnerAdminListing(hsInrequest,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*"
    hsSql.from='partner'
    hsSql.where="1=1"+
      (hsInrequest?.partner_id>0?' AND partner_id=:partner_id':'')
    hsSql.order="name asc"

    if(hsInrequest?.partner_id>0)
      hsLong['partner_id'] = hsInrequest.partner_id

    searchService.fetchDataByPages(hsSql,null,hsLong,null,null,null,null,iMax,iOffset,'id',true,Partner.class)
  }

  def assertUnitprice(hsInrequest){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[uid:hsInrequest.unit_id,upid:hsInrequest.id?:0]
    def hsString=[start:String.format('%tH:%<tM:%<tS',hsInrequest.time_start?:new java.sql.Time(0,0,0)),end:String.format('%tH:%<tM:%<tS',hsInrequest.time_end?:new java.sql.Time(23,59,0))]

    hsSql.select="*"
    hsSql.from='unitprice'
    hsSql.where="unit_id=:uid and is_exception=1 and id!=:upid and time_start<:end and if(time_end!='00:00',time_end,'24:00:00')>:start and (0=1"+
      (hsInrequest?.is_mon>0?' OR is_mon=1':'')+
      (hsInrequest?.is_tue>0?' OR is_tue=1':'')+
      (hsInrequest?.is_wen>0?' OR is_wen=1':'')+
      (hsInrequest?.is_thu>0?' OR is_thu=1':'')+
      (hsInrequest?.is_fra>0?' OR is_fra=1':'')+
      (hsInrequest?.is_sut>0?' OR is_sut=1':'')+
      (hsInrequest?.is_sun>0?' OR is_sun=1':'')+
      (hsInrequest?.is_hol>0?' OR is_hol=1':'')+')'
    hsSql.order="id asc"

    searchService.fetchData(hsSql,hsLong,null,hsString,null,Unitprice.class)
  }

  def assertWorkhours(hsInrequest){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[pid:hsInrequest.partner_id,whid:hsInrequest.id?:0,spid:hsInrequest.space_id?:0]
    def hsString=[start:String.format('%tH:%<tM:%<tS',hsInrequest.time_open?:new java.sql.Time(0,0,0)),end:String.format('%tH:%<tM:%<tS',hsInrequest.time_close?:new java.sql.Time(23,59,0))]

    hsSql.select="*"
    hsSql.from='workhour'
    hsSql.where="partner_id=:pid and space_id=:spid and id!=:whid and time_open<:end and if(time_close!='00:00',time_close,'24:00:00')>:start and (0=1"+
      (hsInrequest?.is_mon>0?' OR is_mon=1':'')+
      (hsInrequest?.is_tue>0?' OR is_tue=1':'')+
      (hsInrequest?.is_wen>0?' OR is_wen=1':'')+
      (hsInrequest?.is_thu>0?' OR is_thu=1':'')+
      (hsInrequest?.is_fra>0?' OR is_fra=1':'')+
      (hsInrequest?.is_sut>0?' OR is_sut=1':'')+
      (hsInrequest?.is_sun>0?' OR is_sun=1':'')+
      (hsInrequest?.is_hol>0?' OR is_hol=1':'')+')'+
      (hsInrequest?.date_start&&hsInrequest?.date_end?' and date_start<=:date_end and date_end>=:date_start':'')
    hsSql.order="id asc"

    if(hsInrequest?.date_start&&hsInrequest?.date_end){
      hsString['date_start'] = String.format('%tF',hsInrequest.date_start)
      hsString['date_end'] = String.format('%tF',hsInrequest.date_end)
    }

    searchService.fetchData(hsSql,hsLong,null,hsString,null,Workhour.class)
  }

  def partnerUsersListing(hsInrequest,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*"
    hsSql.from='user'
    hsSql.where="1=1"+
      (hsInrequest?.partner_id>0?' AND partner_id=:partner_id':'')
    hsSql.order="fullname asc"

    if(hsInrequest?.partner_id>0)
      hsLong['partner_id'] = hsInrequest.partner_id

    searchService.fetchDataByPages(hsSql,null,hsLong,null,null,null,null,iMax,iOffset,'id',true,User.class)
  }

  def partnerGroupsListing(hsInrequest,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*"
    hsSql.from='partnergroup'
    hsSql.where="1=1"+
      (hsInrequest?.partner_id>0?' AND partner_id=:partner_id':'')
    hsSql.order="name asc"

    if(hsInrequest?.partner_id>0)
      hsLong['partner_id'] = hsInrequest.partner_id

    searchService.fetchDataByPages(hsSql,null,hsLong,null,null,null,null,iMax,iOffset,'id',true,Partnergroup.class)
  }

  def customerListing(hsInrequest,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, customer.name as custname, customer.phone as custphone"
    hsSql.from='customertopartner join customer on (customertopartner.customer_id=customer.id)'
    hsSql.where="1=1"+
      (hsInrequest?.partner_id>0?' AND customertopartner.partner_id=:partner_id':'')+
      (hsInrequest?.customer_id>=0?' AND customertopartner.customer_id=:customer_id':'')+
      (hsInrequest?.customername?' AND concat(customertopartner.firstname," ",customertopartner.lastname) like concat("%",:customername,"%")':'')+
      (hsInrequest?.is_trust>-100?' AND customertopartner.is_trust=:is_trust':'')+
      (hsInrequest?.modstatus>-100?' AND customertopartner.modstatus=:status':'')+
      (hsInrequest?.searchname?' AND customertopartner.searchname like concat("%",:searchname,"%")':'')
    hsSql.order="customertopartner.firstname asc, customertopartner.lastname asc"

    if(hsInrequest?.partner_id>0)
      hsLong['partner_id'] = hsInrequest.partner_id
    if(hsInrequest?.customer_id>=0)
      hsLong['customer_id'] = hsInrequest.customer_id
    if(hsInrequest?.is_trust>-100)
      hsLong['is_trust'] = hsInrequest?.is_trust
    if(hsInrequest?.modstatus>-100)
      hsLong['status'] = hsInrequest?.modstatus
    if(hsInrequest?.searchname)
      hsString['searchname'] = hsInrequest.searchname
    if(hsInrequest?.customername)
      hsString['customername'] = hsInrequest.customername

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,null,null,iMax,iOffset,'customertopartner.id',true,Customerfull.class)
  }

  def customerAdminListing(hsInrequest,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="ifnull(customertopartner.id,-customer.id) as id, customertopartner.*, customer.name as custname, customer.email as email, customer.phone as custphone"
    hsSql.from='customertopartner right join customer on (customertopartner.customer_id=customer.id)'
    hsSql.where="1=1"+
      (hsInrequest?.modstatus>-100?' AND customertopartner.modstatus=:status':'')+
      (hsInrequest?.companyname?' AND customertopartner.company like concat("%",:companyname,"%")':'')+
      (hsInrequest?.email?' AND customer.email like concat("%",:email,"%")':'')
    hsSql.order="customer.id asc, customertopartner.id asc"

    if(hsInrequest?.companyname)
      hsString['companyname'] = hsInrequest.companyname
    if(hsInrequest?.email)
      hsString['email'] = hsInrequest.email


    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,null,null,iMax,iOffset,'*',true,Customerfull.class)
  }

  def bookingListing(hsInrequest,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, '' as partnername"
    hsSql.from='booking join space on (booking.space_id=space.id) join unit on (space.unit_id=unit.id) left join customertopartner on (booking.customer_id=customertopartner.id)'
    hsSql.where="1=1"+
      (hsInrequest?.partner_id>0?' AND unit.partner_id=:pid':'')+
      (hsInrequest?.customer_id>0?' AND customertopartner.customer_id=:cid':'')+
      (hsInrequest?.customername?' AND concat(customertopartner.firstname," ",customertopartner.lastname) like concat("%",:customername,"%")':'')+
      (hsInrequest?.title?' AND booking.title like concat("%",:title,"%")':'')+
      (hsInrequest?.modstatus>-100?' AND booking.modstatus=:status':'')+
      (hsInrequest?.btype>-100?' AND booking.btype=:btype':'')+
      (hsInrequest?.fromtime?' and date(totime)>=:fromtime':'')+
      (hsInrequest?.totime?' and date(fromtime)<=:totime':'')
    hsSql.order="booking.id desc"

    if(hsInrequest?.partner_id>0)
      hsLong['pid'] = hsInrequest.partner_id
    if(hsInrequest?.customer_id>0)
      hsLong['cid'] = hsInrequest.customer_id
    if(hsInrequest?.fromtime)
      hsString['fromtime'] = String.format('%tF',hsInrequest.fromtime)
    if(hsInrequest?.totime)
      hsString['totime'] = String.format('%tF',hsInrequest.totime)
    if(hsInrequest?.modstatus>-100)
      hsLong['status'] = hsInrequest.modstatus
    if(hsInrequest?.btype>-100)
      hsLong['btype'] = hsInrequest.btype
    if(hsInrequest?.title)
      hsString['title'] = hsInrequest.title
    if(hsInrequest?.customername)
      hsString['customername'] = hsInrequest.customername

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,null,null,iMax,iOffset,'booking.id',true,Bookingfull.class)
  }

  def bookingAdminListing(hsInrequest,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, partner.name as partnername"
    hsSql.from='booking join space on (booking.space_id=space.id) join unit on (space.unit_id=unit.id) left join customertopartner on (booking.customer_id=customertopartner.id) join partner on (unit.partner_id=partner.id)'
    hsSql.where="1=1"+
      (hsInrequest?.companyname?' AND partner.name like concat("%",:companyname,"%")':'')+
      (hsInrequest?.customername?' AND concat(customertopartner.firstname," ",customertopartner.lastname) like concat("%",:customername,"%")':'')+
      (hsInrequest?.modstatus>-100?' AND booking.modstatus=:status':'')+
      (hsInrequest?.fromtime?' and date(totime)>=:fromtime':'')+
      (hsInrequest?.totime?' and date(fromtime)<=:totime':'')
    hsSql.order="booking.fromtime desc"

    if(hsInrequest?.fromtime)
      hsString['fromtime'] = String.format('%tF',hsInrequest.fromtime)
    if(hsInrequest?.totime)
      hsString['totime'] = String.format('%tF',hsInrequest.totime)
    if(hsInrequest?.modstatus>-100)
      hsLong['status'] = hsInrequest.modstatus
    if(hsInrequest?.customername)
      hsString['customername'] = hsInrequest.customername
    if(hsInrequest?.companyname)
      hsString['companyname'] = hsInrequest.companyname

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,null,null,iMax,iOffset,'booking.id',true,Bookingfull.class)
  }

  def partnerSpacesListing(hsInrequest,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*"
    hsSql.from='space join unit on (space.unit_id=unit.id)'
    hsSql.where="1=1"+
      (hsInrequest?.partner_id>0?' AND unit.partner_id=:partner_id':'')
    hsSql.order="space.name asc"

    if(hsInrequest?.partner_id>0)
      hsLong['partner_id'] = hsInrequest.partner_id

    searchService.fetchDataByPages(hsSql,null,hsLong,null,null,null,null,iMax,iOffset,'space.id',true,Space.class)
  }

  def partnerSchedulerListing(hsInrequest){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, unit.name as unitname, unit.modstatus as unitstatus"
    hsSql.from='space join unit on (space.unit_id=unit.id)'
    hsSql.where="unit.modstatus=1 and space.modstatus=1"+
      (hsInrequest?.partner_id>0?' AND unit.partner_id=:partner_id':'')+
      (hsInrequest?.onlyVisible?' AND space.is_visible=1':'')
    hsSql.order="unit.name asc, space.name asc"

    if(hsInrequest?.partner_id>0)
      hsLong['partner_id'] = hsInrequest.partner_id

    searchService.fetchData(hsSql,hsLong,null,null,null,Spacefull.class)
  }

  def workhourSchedulerListing(hsInrequest){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[pid:hsInrequest.partner_id]
    def hsString=[:]

    hsSql.select="*"
    hsSql.from='workhour join space on (workhour.space_id=space.id) join unit on (space.unit_id=unit.id)'
    hsSql.where="workhour.partner_id=:pid and unit.modstatus=1 and space.modstatus=1"+
      getDayOfWeekCondition(hsInrequest.basedate)+
      (hsInrequest?.onlyVisible?' AND space.is_visible=1':'')+
      (hsInrequest?.space_id>0?' AND space.id=:space_id':'')+
      (hsInrequest?.basedate?' and date_start<=:basedate and date_end>=:basedate':'')+
      (hsInrequest?.time_open?" and time_open<:end and if(time_close!='00:00',time_close,'24:00:00')>:start":'')
    hsSql.order="workhour.time_open asc"

    if(hsInrequest?.basedate){
      hsString['basedate'] = String.format('%tF',hsInrequest.basedate)
    }
    if(hsInrequest?.space_id>0)
      hsLong['space_id'] = hsInrequest.space_id
    if(hsInrequest?.time_open){
      hsString['start'] = String.format('%tH:%<tM:%<tS',hsInrequest.time_open)
      hsString['end'] = String.format('%tH:%<tM:%<tS',hsInrequest.time_close)
    }

    searchService.fetchData(hsSql,hsLong,null,hsString,null,Workhour.class)
  }

  def bookingSchedulerListing(hsInrequest){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[pid:hsInrequest.partner_id]
    def hsString=[:]

    hsSql.select="*"
    hsSql.from='booking join space on (booking.space_id=space.id) join unit on (space.unit_id=unit.id)'
    hsSql.where="unit.partner_id=:pid and unit.modstatus=1 and space.modstatus=1 and booking.modstatus>=0"+
      (hsInrequest?.onlyVisible?' AND space.is_visible=1':'')+
      (hsInrequest?.space_id>0?' AND space.id=:space_id':'')+
      (hsInrequest?.exclude_id>0?' AND booking.id!=:exclude_id':'')+
      (hsInrequest?.basedate?' and (date(fromtime)=:basedate or date(totime)=:basedate)':'')+
      (hsInrequest?.time_start?" and fromtime<:end and totime>:start":'')
    hsSql.order="booking.id asc"

    if(hsInrequest?.basedate){
      hsString['basedate'] = String.format('%tF',hsInrequest.basedate)
    }
    if(hsInrequest?.space_id>0)
      hsLong['space_id'] = hsInrequest.space_id
    if(hsInrequest?.exclude_id>0)
      hsLong['exclude_id'] = hsInrequest.exclude_id
    if(hsInrequest?.time_start){
      hsString['start'] = String.format('%tF %<tH:%<tM:%<tS',hsInrequest.time_start)
      hsString['end'] = String.format('%tF %<tH:%<tM:%<tS',hsInrequest.time_end)
    }

    searchService.fetchData(hsSql,hsLong,null,hsString,null,Booking.class)
  }

  def unitpriceBookingListing(hsInrequest){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[uid:hsInrequest.unit_id]
    def hsString=[:]

    hsSql.select="*"
    hsSql.from='unitprice'
    hsSql.where="unitprice.unit_id=:uid and unitprice.is_exception=1"+
      getDayOfWeekCondition(hsInrequest.basedate)+
      (hsInrequest?.time_start?" and time_start<:end and if(time_end!='00:00',time_end,'24:00:00')>:start":'')
    hsSql.order="unitprice.time_start asc"

    if(hsInrequest?.time_start){
      hsString['start'] = String.format('%tH:%<tM:%<tS',hsInrequest.time_start)
      hsString['end'] = String.format('%tH:%<tM:%<tS',hsInrequest.time_end)
    }

    searchService.fetchData(hsSql,hsLong,null,hsString,null,Unitprice.class)
  }

  def bookingNoticeListing(hsInrequest){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[pid:hsInrequest.partner_id]
    def hsString=[:]

    hsSql.select="*"
    hsSql.from='booking join space on (booking.space_id=space.id) join unit on (space.unit_id=unit.id)'
    hsSql.where="unit.partner_id=:pid and unit.modstatus=1 and space.modstatus=1 and booking.modstatus=0"+
      (hsInrequest?.time_start?" and fromtime>:start":'')
    hsSql.order="booking.id asc"

    if(hsInrequest?.time_start){
      hsString['start'] = String.format('%tF %<tH:%<tM:%<tS',hsInrequest.time_start)
    }

    searchService.fetchData(hsSql,hsLong,null,hsString,null,Booking.class)
  }

  private String getDayOfWeekCondition(Date _date){
    if(!_date) return ""
    switch(_date.getDay()) {
      case 0: " and is_sun=1"; break;
      case 1: " and is_mon=1"; break;
      case 2: " and is_tue=1"; break;
      case 3: " and is_wen=1"; break;
      case 4: " and is_thu=1"; break;
      case 5: " and is_fra=1"; break;
      case 6: " and is_sut=1"; break;
      default: ""; break;
    }
  }
}