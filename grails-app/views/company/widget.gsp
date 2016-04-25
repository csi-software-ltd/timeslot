<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="ru" lang="ru">
  <head>
    <r:external uri="/js/jquery-1.8.3.js"/>
    <script type="text/javascript" src="http://img.yandex.net/webwidgets/1/WidgetApi_no_jquery.js"></script>
    <r:external uri="/js/prototype/prototype.js" />
    <r:external uri="/js/jquery.qtip.min.js" />
    <r:external uri="/js/moment.min.js" />
    <r:external uri="/js/fullcalendar.min.js" />
    <r:external uri="/js/scheduler.js" />
    <r:external uri="/js/kendo.web.min.js" />
    <r:external uri="/js/kendo.culture.ru-RU.min.js" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'reset.css')}" type="text/css" />
    <link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'grid.css')}" />
    <link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'style.css')}" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'fullcalendar.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'scheduler.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'qtip.css')}" />
    <style type="text/css">
      label{min-width:160px}
    </style>
    <script type="text/javascript">
      widget.onload = function(){
        widget.adjustIFrameHeight();
      }
      function init(){
        jQuery('#calendar').fullCalendar({
          schedulerLicenseKey: 'CC-Attribution-NonCommercial-NoDerivatives',
          defaultView: 'timelineDay',
          aspectRatio: 1.8,
          height: 'auto',
          dayNamesShort:['Вс','Пн','Вт','Ср','Чт','Пт','Сб'],
          monthNames:['Январь', 'Февраль', 'Март', 'Апрель', 'Май', 'Июнь', 'Июль', 'Август', 'Сентябрь', 'Октябрь', 'Ноябрь', 'Декабрь'],
          scrollTime: '00:00',
          resourceAreaWidth: '40%',
          selectable: true,
          selectOverlap: function(event) {
            return event.rendering === 'background';
          },
          minTime: "${partner.stringMinTime()}",
          maxTime: "${partner.stringMaxTime()}",
          slotDuration: '${"00:$interval"}',
          slotLabelFormat: [
            'H:mm'
          ],
          eventTextColor: '#777',
          displayEventTime: true,
          displayEventEnd: true,
          timeFormat: 'H(:mm)',
          titleFormat: 'D MMMM, YYYY',
          nowIndicator: true,
          //editable: true,
          header: {
            left: '',
            center: 'title',
            right: 'today prev,next'
          },
          resourceColumns: [
            {
              group: true,
              labelText: 'Подразделение',
              field: 'unit',
              width: '38%'
            },
            {
              labelText: 'Объект',
              field: 'space'
            }
          ],
          resources: { url:'${createLink(controller:'company',action:'spaces',params:[linkname:partner.linkname])}', type:'POST' },
          eventSources: [
              {
                url: '${createLink(controller:'company',action:'events',params:[linkname:partner.linkname])}',
                type:'POST'
              }
          ],
          eventOverlap: function(stillEvent, movingEvent) {
            return stillEvent.rendering === 'background';
          },
          eventAfterRender: function(event, element) {
            if ((event.description||event.title)&&event.rendering !== 'background')
              element.qtip({
                position: { my: "top center", at: "bottom center" },
                content: '<strong>Название: </strong>'+event.title+'<br/><strong>Описание: </strong>'+event.description
              });
          },
          select: function(start, end, jsEvent, view, resource) {
            <g:remoteFunction url="${[controller:'company',action:'preparebooking',params:[linkname:partner.linkname]]}" onSuccess='processResponse(e)' params="'space_id='+resource.id+'&start='+start.format()+'&end='+end.format()" />
          },
          eventResize: function(event, delta, revertFunc) {
            <g:remoteFunction url="${[controller:'company',action:'preparebooking',params:[linkname:partner.linkname]]}" onSuccess='processEditResponse(e,revertFunc)' params="'space_id='+event.resourceId+'&event_id='+event.id+'&start='+event.start.format()+'&end='+event.end.format()" />
          },
          eventDrop: function(event, delta, revertFunc) {
            <g:remoteFunction url="${[controller:'company',action:'preparebooking',params:[linkname:partner.linkname]]}" onSuccess='processEditResponse(e,revertFunc)' params="'space_id='+event.resourceId+'&event_id='+event.id+'&start='+event.start.format()+'&end='+event.end.format()" />
          },
          eventClick: function(event) {
            if (event.editable)
              <g:remoteFunction url="${[controller:'company',action:'preparebooking',params:[linkname:partner.linkname]]}" onSuccess='processEditResponse(e,null)' params="'space_id='+event.resourceId+'&event_id='+event.id+'&start='+event.start.format()+'&end='+event.end.format()" />
          }
        });
      }
      function processEditResponse(e,revertFunc){
        if(e.responseJSON.error){
          $('message').hide();
          alert('На эти часы бронирование невозможно');
          revertFunc();
        } else {
          jQuery('#spacename').html(e.responseJSON.spacename);
          jQuery('#startdate_text').html(e.responseJSON.startdate);
          jQuery('#enddate_text').html(e.responseJSON.enddate);
          jQuery('#price').val(e.responseJSON.price);
          jQuery('#space_id').val(e.responseJSON.bookdata.space_id);
          jQuery('#startdate').val(e.responseJSON.bookdata.startdate);
          jQuery('#enddate').val(e.responseJSON.bookdata.enddate);
          if (e.responseJSON.booking){
            jQuery('#title').val(e.responseJSON.booking.title);
            jQuery('#description').val(e.responseJSON.booking.notes);
            jQuery('#customer_id').val(e.responseJSON.booking.customer_id);
          }
          jQuery('#booking_id').val(e.responseJSON.bookdata.booking_id);
          $('message').show();
        }
      }
      function processResponse(e){
        if(e.responseJSON.error){
          $('message').hide();
          alert('На эти часы бронирование невозможно');
          jQuery('#calendar').fullCalendar( 'unselect' );
          jQuery('#calendar').fullCalendar( 'refetchEvents' );
        }else{
          $('bookingForm').reset();
          jQuery('#spacename').html(e.responseJSON.spacename);
          jQuery('#startdate_text').html(e.responseJSON.startdate);
          jQuery('#enddate_text').html(e.responseJSON.enddate);
          jQuery('#price').val(e.responseJSON.price);
          jQuery('#space_id').val(e.responseJSON.bookdata.space_id);
          jQuery('#startdate').val(e.responseJSON.bookdata.startdate);
          jQuery('#enddate').val(e.responseJSON.bookdata.enddate);
          jQuery('#booking_id').val('');
          $('message').show();
        }
      }
      function processBookingResponse(e){
        ['su_username','su_login'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length&&e.responseJSON.errorcode.indexOf(1)==-1){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 2: $('su_username').addClassName('red'); break;
              case 3: $('su_login').addClassName('red'); break;
            }
          });
        } else {
          $('message').hide();
          $('bookingForm').reset();
          if (e.responseJSON.errorcode.indexOf(1)>-1) alert('На эти часы бронирование невозможно');
          jQuery('#calendar').fullCalendar( 'unselect' );
          jQuery('#calendar').fullCalendar( 'refetchEvents' );
        }
        if(${!user}) location.reload(true)
      }
      function closeMessage(){
        $('message').hide();
        jQuery('#calendar').fullCalendar( 'unselect' );
        jQuery('#calendar').fullCalendar( 'refetchEvents' );
        $('bookingForm').reset();
      }
      function toggleadddata(sValue){
        if(sValue==1) $('weeklytypedata').show();
        else $('weeklytypedata').hide();
      }
    </script>
  </head>
  <body onload="init()" style="width:auto">
    <section id="content">
      <div class="container_12">
        <div class="grid_12" id="calendar"></div>
        <div class="clear"></div>
        <div id="message" class="grid_6 glossy messages" style="top: 50px;display:none">
          <div class="message-item-close" style="margin-top: -14px; margin-right: -14px;"/></div>
          <div class="text">
            <p>Бронирование объекта: <strong id="spacename"></strong></p>
            <p>Дата с: <strong id="startdate_text"></strong></p>
            <p>Дата по: <strong id="enddate_text"></strong></p>
          </div>
          <g:formRemote name="bookingForm" url="${[controller:'company',action:'booking',params:[linkname:partner.linkname]]}" method="post" onSuccess="processBookingResponse(e)" onreset="\$('recurrenceData').hide();\$('weeklytypedata').hide();">
            <g:csilabel for="title">Название брони:</g:csilabel>
            <input type="text" id="title" name="title" value=""/>
            <g:csilabel for="price">К оплате:</g:csilabel>
            <input type="text" id="price" name="price" value="" readonly/>
            <g:csilabel for="description">Описание:</g:csilabel>
            <label class="auto" for="is_recurrence">
              <input type="checkbox" id="is_recurrence" name="is_recurrence" value="1" onchange="jQuery('#recurrenceData').toggle()"/>
              Повторяющееся событие
            </label>
            <g:textArea name="description" id="description" value="" />
            <input type="hidden" id="booking_id" name="booking_id" value="0"/>
            <input type="hidden" id="space_id" name="space_id" value=""/>
            <input type="hidden" id="customer_id" name="customer_id" value="${customer?.id?:0}"/>
            <input type="hidden" id="startdate" name="startdate" value=""/>
            <input type="hidden" id="enddate" name="enddate" value=""/>
            <div id="recurrenceData" style="display:none">
              <g:csilabel for="recurrencetype">Тип:</g:csilabel>
              <g:select name="recurrencetype" from="['еженедельно','ежемесячно']" keys="[1,2]" noSelection="${['0':'ежедневно']}" onchange="toggleadddata(this.value)"/>
              <div id="weeklytypedata" style="display:none;width:460px;padding-bottom:10px">
                <label class="auto" for="weeklytypedata_is_mon">
                  <input type="checkbox" id="weeklytypedata_is_mon" name="is_mon" value="1" />
                  Понедельник
                </label>
                <label class="auto" for="weeklytypedata_is_tue">
                  <input type="checkbox" id="weeklytypedata_is_tue" name="is_tue" value="1" />
                  Вторник
                </label>
                <label class="auto" for="weeklytypedata_is_wen">
                  <input type="checkbox" id="weeklytypedata_is_wen" name="is_wen" value="1" />
                  Среда
                </label>
                <label class="auto" for="weeklytypedata_is_thu">
                  <input type="checkbox" id="weeklytypedata_is_thu" name="is_thu" value="1" />
                  Четверг
                </label>
                <label class="auto" for="weeklytypedata_is_fra">
                  <input type="checkbox" id="weeklytypedata_is_fra" name="is_fra" value="1" />
                  Пятница
                </label>
                <label class="auto" for="weeklytypedata_is_sut">
                  <input type="checkbox" id="weeklytypedata_is_sut" name="is_sut" value="1" />
                  Суббота
                </label>
                <label class="auto" for="weeklytypedata_is_sun">
                  <input type="checkbox" id="weeklytypedata_is_sun" name="is_sun" value="1" />
                  Воскресение
                </label>
              </div>
              <input id="endingcounttype" type="radio" name="endingtype" value="0" checked="checked" /><g:csilabel for="endingcounttype" style="min-width:139px">До повторений:</g:csilabel>
              <input type="text" id="endingcount" name="endingcount" value=""/>
              <br/><input id="endingdatetype" type="radio" name="endingtype" value="1" /><g:csilabel for="endingdatetype" style="min-width:139px">До даты:</g:csilabel>
              <g:datepicker class="normal nopad" name="endingdate" value="${String.format('%td.%<tm.%<tY',new Date())}"/>
            </div>
          <g:if test="${!user}">
            <hr class="admin">

            <g:csilabel for="su_username">Имя:</g:csilabel>
            <input type="text" id="su_username" name="username" value=""/>
            <g:csilabel for="su_login">Email:</g:csilabel>
            <input type="text" id="su_login" name="login" value=""/>

            <hr class="admin">
          </g:if>
            <div class="fright" style="padding-top:10px">
              <input type="submit" value="Сохранить" />
              <input type="button" class="reset button" value="Отмена" onclick="closeMessage()"/>
            </div>
          </g:formRemote>
        </div>
        <div class="clear"></div>
      </div>
    </section>
  </body>
</html>