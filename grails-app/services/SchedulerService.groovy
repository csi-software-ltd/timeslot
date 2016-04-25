import groovy.time.*
class SchedulerService {
  def listingService

  Boolean checkBookingWorkhour(_request){
    def startdate = _request.start.clone()
    while(!Tools.checkSameDate(_request.start,_request.end+(_request.end.getHours()==0&&_request.end.getMinutes()==0?0:1))) {
      Boolean isSameDay = Tools.checkSameDate(_request.start,_request.end-(_request.end.getHours()==0&&_request.end.getMinutes()==0?1:0))
      Boolean isFirstDay = Tools.checkSameDate(_request.start,startdate)
      def workhours = listingService.workhourSchedulerListing(partner_id:_request.partner_id,space_id:_request.space_id,onlyVisible:_request.onlyVisible,basedate:_request.start,time_open:isFirstDay?_request.start:new java.sql.Time(0,0,0),time_close:isSameDay?_request.end:new java.sql.Time(23,59,0))

      Boolean errors = workhours.size()==0
      workhours.eachWithIndex { it, index ->
        if (index==0&&String.format('%tH:%<tM:%<tS',isFirstDay?_request.start:new java.sql.Time(0,0,0))<String.format('%tH:%<tM:%<tS',it.time_open)) errors = true
        if (index==workhours.size()-1&&String.format('%tH:%<tM:%<tS',isSameDay?_request.end:new java.sql.Time(23,59,0))>String.format('%tH:%<tM:%<tS',it.time_close)) errors = true
        if (index!=workhours.size()-1&&workhours[index+1].time_open!=it.time_close) errors = true
      }
      if(errors) return false
      _request.start++
    }
    return true
  }

  Boolean checkBookingDimention(_request){
    def baseprice = Unitprice.findByUnit_idAndIs_exception(_request.unit_id,0)

    if(!baseprice) return true

    def startdate = _request.start.clone()
    while(!Tools.checkSameDate(_request.start,_request.end+(_request.end.getHours()==0&&_request.end.getMinutes()==0?0:1))) {
      Boolean isSameDay = Tools.checkSameDate(_request.start,_request.end-(_request.end.getHours()==0&&_request.end.getMinutes()==0?1:0))
      Boolean isFirstDay = Tools.checkSameDate(_request.start,startdate)
      def prices = listingService.unitpriceBookingListing(unit_id:_request.unit_id,basedate:_request.start,time_start:isFirstDay?_request.start:new java.sql.Time(0,0,0),time_end:isSameDay?_request.end:new java.sql.Time(23,59,0))

      Boolean errors = false
      prices.eachWithIndex { it, index ->
        if (index==0&&String.format('%tH:%<tM:%<tS',isFirstDay?_request.start:new java.sql.Time(0,0,0))<String.format('%tH:%<tM:%<tS',it.time_start)&&!checkDimention(computeMinutesDiff(isFirstDay?_request.start:new java.sql.Time(0,0,0),it.time_start),baseprice.forperiod,baseprice.minperiod)) errors = true
        if(!checkDimention(computeMinutesDiff(String.format('%tH:%<tM:%<tS',isFirstDay?_request.start:new java.sql.Time(0,0,0))<String.format('%tH:%<tM:%<tS',it.time_start)?it.time_start:isFirstDay?_request.start:new java.sql.Time(0,0,0),String.format('%tH:%<tM:%<tS',it.time_end)<String.format('%tH:%<tM:%<tS',isSameDay?_request.end:new java.sql.Time(23,59,0))?it.time_end:isSameDay?_request.end:new java.sql.Time(0,0,0)),it.forperiod,it.minperiod)) errors = true
        if (index!=prices.size()-1&&prices[index+1].time_start!=it.time_end&&!checkDimention(computeMinutesDiff(it.time_end,prices[index+1].time_start),baseprice.forperiod,baseprice.minperiod)) errors = true
        if (index==prices.size()-1&&String.format('%tH:%<tM:%<tS',isSameDay?_request.end:new java.sql.Time(23,59,0))>String.format('%tH:%<tM:%<tS',it.time_end)&&!checkDimention(computeMinutesDiff(it.time_end,isSameDay?_request.end:new java.sql.Time(0,0,0)),baseprice.forperiod,baseprice.minperiod)) errors = true
      }
      if(prices.size()==0) if(!checkDimention(computeMinutesDiff(isFirstDay?_request.start:new java.sql.Time(0,0,0),isSameDay?_request.end:new java.sql.Time(0,0,0)),baseprice.forperiod,baseprice.minperiod)) errors = true

      if(errors) return false
      _request.start++
    }

    return true
  }

  Boolean checkRecurrenceAvailability(_request){
    if(listingService.bookingSchedulerListing(space_id:_request.space_id,time_start:_request.fromtime,time_end:_request.totime,partner_id:_request.partner_id,exclude_id:_request.exclude_id).size()>0) return false
    else if(!checkBookingWorkhour(space_id:_request.space_id,start:_request.fromtime,end:_request.totime,partner_id:_request.partner_id,onlyVisible:true)) return false

    if (!_request.endingtype) return checkByCountRecurrenceAvailability(_request)
    else return checkByDateRecurrenceAvailability(_request)
  }

  private Boolean checkByCountRecurrenceAvailability(_request){
    if (!_request.endingcount||_request.endingcount<0) return true
    if (_request.recurrencetype==1&&!(_request.is_sun||_request.is_mon||_request.is_tue||_request.is_wen||_request.is_thu||_request.is_fra||_request.is_sut)) return true

    def bookcount = 0
    def startcal = Calendar.getInstance()
    startcal.setTime(_request.fromtime)
    def endcal = Calendar.getInstance()
    endcal.setTime(_request.totime)
    while(bookcount < _request.endingcount) {
      startcal.add(_request.recurrencetype==2?Calendar.MONTH:Calendar.DATE,1)
      endcal.add(_request.recurrencetype==2?Calendar.MONTH:Calendar.DATE,1)
      while(_request.recurrencetype==1&&!checkDayOfWeek(startcal.getTime(),_request)) {
        startcal.add(Calendar.DATE,1)
        endcal.add(Calendar.DATE,1)
      }

      if(listingService.bookingSchedulerListing(space_id:_request.space_id,time_start:startcal.getTime(),time_end:endcal.getTime(),partner_id:_request.partner_id,exclude_id:_request.exclude_id).size()>0) return false
      else if(!checkBookingWorkhour(space_id:_request.space_id,start:startcal.getTime(),end:endcal.getTime(),partner_id:_request.partner_id,onlyVisible:true)) return false
      else if(!checkBookingDimention(unit_id:Space.get(_request.space_id).unit_id,start:startcal.getTime(),end:endcal.getTime())) return false

      bookcount++
    }

    return true
  }

  private Boolean checkByDateRecurrenceAvailability(_request){
    if (!_request.endingdate) return true
    if (_request.recurrencetype==1&&!(_request.is_sun||_request.is_mon||_request.is_tue||_request.is_wen||_request.is_thu||_request.is_fra||_request.is_sut)) return true

    def startcal = Calendar.getInstance()
    startcal.setTime(_request.fromtime)
    def endcal = Calendar.getInstance()
    endcal.setTime(_request.totime)
    startcal.add(_request.recurrencetype==2?Calendar.MONTH:Calendar.DATE,1)
    endcal.add(_request.recurrencetype==2?Calendar.MONTH:Calendar.DATE,1)
    while(_request.recurrencetype==1&&!checkDayOfWeek(startcal.getTime(),_request)) {
      startcal.add(Calendar.DATE,1)
      endcal.add(Calendar.DATE,1)
    }
    while(startcal.getTime().clearTime() <= _request.endingdate) {
      if(listingService.bookingSchedulerListing(space_id:_request.space_id,time_start:startcal.getTime(),time_end:endcal.getTime(),partner_id:_request.partner_id,exclude_id:_request.exclude_id).size()>0) return false
      else if(!checkBookingWorkhour(space_id:_request.space_id,start:startcal.getTime(),end:endcal.getTime(),partner_id:_request.partner_id,onlyVisible:true)) return false
      else if(!checkBookingDimention(unit_id:Space.get(_request.space_id).unit_id,start:startcal.getTime(),end:endcal.getTime())) return false

      startcal.add(_request.recurrencetype==2?Calendar.MONTH:Calendar.DATE,1)
      endcal.add(_request.recurrencetype==2?Calendar.MONTH:Calendar.DATE,1)
      while(_request.recurrencetype==1&&!checkDayOfWeek(startcal.getTime(),_request)) {
        startcal.add(Calendar.DATE,1)
        endcal.add(Calendar.DATE,1)
      }
    }

    return true
  }

  void updateBooking(_request){
    if(!_request.booking_id) _request.booking = new Booking()
    _request.booking.csiSetSpace_id(_request.space_id).updateMainData(_request).updatePeriod(_request).save(failOnError:true,flush:true)

    if(!_request.is_recurrence) return

    if (!_request.endingtype) doBookingRecurrenceByCount(_request)
    else doBookingRecurrenceByDate(_request)
  }

  private void doBookingRecurrenceByCount(_request){
    if (!_request.endingcount||_request.endingcount<0) return
    if (_request.recurrencetype==1&&!(_request.is_sun||_request.is_mon||_request.is_tue||_request.is_wen||_request.is_thu||_request.is_fra||_request.is_sut)) return

    def bookcount = 0
    def startcal = Calendar.getInstance()
    startcal.setTime(_request.fromtime)
    def endcal = Calendar.getInstance()
    endcal.setTime(_request.totime)
    while(bookcount < _request.endingcount) {
      startcal.add(_request.recurrencetype==2?Calendar.MONTH:Calendar.DATE,1)
      endcal.add(_request.recurrencetype==2?Calendar.MONTH:Calendar.DATE,1)
      while(_request.recurrencetype==1&&!checkDayOfWeek(startcal.getTime(),_request)) {
        startcal.add(Calendar.DATE,1)
        endcal.add(Calendar.DATE,1)
      }

      new Booking().csiSetSpace_id(_request.space_id).updateMainData(_request).updatePeriod(fromtime:startcal.getTime(),totime:endcal.getTime()).save(failOnError:true,flush:true)

      bookcount++
    }
  }

  private void doBookingRecurrenceByDate(_request){
    if (!_request.endingdate) return
    if (_request.recurrencetype==1&&!(_request.is_sun||_request.is_mon||_request.is_tue||_request.is_wen||_request.is_thu||_request.is_fra||_request.is_sut)) return

    def startcal = Calendar.getInstance()
    startcal.setTime(_request.fromtime)
    def endcal = Calendar.getInstance()
    endcal.setTime(_request.totime)
    startcal.add(_request.recurrencetype==2?Calendar.MONTH:Calendar.DATE,1)
    endcal.add(_request.recurrencetype==2?Calendar.MONTH:Calendar.DATE,1)
    while(_request.recurrencetype==1&&!checkDayOfWeek(startcal.getTime(),_request)) {
      startcal.add(Calendar.DATE,1)
      endcal.add(Calendar.DATE,1)
    }
    while(startcal.getTime().clearTime() <= _request.endingdate) {
      new Booking().csiSetSpace_id(_request.space_id).updateMainData(_request).updatePeriod(fromtime:startcal.getTime(),totime:endcal.getTime()).save(failOnError:true,flush:true)

      startcal.add(_request.recurrencetype==2?Calendar.MONTH:Calendar.DATE,1)
      endcal.add(_request.recurrencetype==2?Calendar.MONTH:Calendar.DATE,1)
      while(_request.recurrencetype==1&&!checkDayOfWeek(startcal.getTime(),_request)) {
        startcal.add(Calendar.DATE,1)
        endcal.add(Calendar.DATE,1)
      }
    }
  }

  BigDecimal computeBookingPrice(_request){
    BigDecimal result = 0.0g
    def baseprice = Unitprice.findByUnit_idAndIs_exception(_request.unit_id,0)

    if(baseprice){
      def startdate = _request.start.clone()
      while(!Tools.checkSameDate(_request.start,_request.end+(_request.end.getHours()==0&&_request.end.getMinutes()==0?0:1))) {
        Boolean isSameDay = Tools.checkSameDate(_request.start,_request.end-(_request.end.getHours()==0&&_request.end.getMinutes()==0?1:0))
        Boolean isFirstDay = Tools.checkSameDate(_request.start,startdate)
        def prices = listingService.unitpriceBookingListing(unit_id:_request.unit_id,basedate:_request.start,time_start:isFirstDay?_request.start:new java.sql.Time(0,0,0),time_end:isSameDay?_request.end:new java.sql.Time(23,59,0))

        prices.eachWithIndex { it, index ->
          if (index==0&&String.format('%tH:%<tM:%<tS',isFirstDay?_request.start:new java.sql.Time(0,0,0))<String.format('%tH:%<tM:%<tS',it.time_start)) result += computeMinutesDiff(isFirstDay?_request.start:new java.sql.Time(0,0,0),it.time_start) * baseprice.price / baseprice.forperiod
          result += computeMinutesDiff(String.format('%tH:%<tM:%<tS',isFirstDay?_request.start:new java.sql.Time(0,0,0))<String.format('%tH:%<tM:%<tS',it.time_start)?it.time_start:isFirstDay?_request.start:new java.sql.Time(0,0,0),String.format('%tH:%<tM:%<tS',it.time_end)<String.format('%tH:%<tM:%<tS',isSameDay?_request.end:new java.sql.Time(23,59,0))?it.time_end:isSameDay?_request.end:new java.sql.Time(0,0,0)) * it.price / it.forperiod
          if (index!=prices.size()-1&&prices[index+1].time_start!=it.time_end) result += computeMinutesDiff(it.time_end,prices[index+1].time_start) * baseprice.price / baseprice.forperiod
          if (index==prices.size()-1&&String.format('%tH:%<tM:%<tS',isSameDay?_request.end:new java.sql.Time(23,59,0))>String.format('%tH:%<tM:%<tS',it.time_end)) result += computeMinutesDiff(it.time_end,isSameDay?_request.end:new java.sql.Time(0,0,0)) * baseprice.price / baseprice.forperiod
        }
        if(prices.size()==0) result += computeMinutesDiff(isFirstDay?_request.start:new java.sql.Time(0,0,0),isSameDay?_request.end:new java.sql.Time(0,0,0)) * baseprice.price / baseprice.forperiod

        _request.start++
      }
    }

    return result
  }

  private Boolean checkDayOfWeek (_date, _request){
    switch(_date.getDay()) {
      case 0: !!_request.is_sun; break;
      case 1: !!_request.is_mon; break;
      case 2: !!_request.is_tue; break;
      case 3: !!_request.is_wen; break;
      case 4: !!_request.is_thu; break;
      case 5: !!_request.is_fra; break;
      case 6: !!_request.is_sut; break;
    }
  }

  private Boolean checkDimention (Integer _booklength, _forperiod, _minperiod){
    !(_booklength%_forperiod)&&(_booklength>=_minperiod)
  }

  private Integer computeMinutesDiff (_timestart, _timeend){
    Integer startminutes = _timestart.getHours()*60 + _timestart.getMinutes()
    Integer endminutes = _timeend.getHours()*60 + _timeend.getMinutes() ?: 1440
    endminutes - startminutes > 0 ? endminutes - startminutes : 0
  }
}