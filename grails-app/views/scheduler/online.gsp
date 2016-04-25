<html>
  <head>
    <title>${infotext?.title?:'Timeslot приложение'}</title>
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />
    <meta name="layout" content="main" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'fullcalendar.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'scheduler.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="scheduler" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <style type="text/css">
      label{min-width:160px}
    </style>
    <g:javascript>
      function init(){
        jQuery('#calendar').fullCalendar({
          schedulerLicenseKey: 'CC-Attribution-NonCommercial-NoDerivatives',
          defaultView: 'timelineDay',
          aspectRatio: 1.8,
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
          editable: true,
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
          resources: { url:'${createLink(controller:'scheduler',action:'spaces')}', type:'POST' },
          eventSources: [
              {
                url: '${createLink(controller:'scheduler',action:'events')}',
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
            <g:remoteFunction controller='scheduler' action='preparebooking' onSuccess='processResponse(e)' params="'id='+resource.id+'&start='+start.format()+'&end='+end.format()" />
          },
          eventResize: function(event, delta, revertFunc) {
            <g:remoteFunction controller='scheduler' action='preparebooking' onSuccess='processEditResponse(e,revertFunc)' params="'id='+event.resourceId+'&event_id='+event.id+'&start='+event.start.format()+'&end='+event.end.format()" />
          },
          eventDrop: function(event, delta, revertFunc) {
            <g:remoteFunction controller='scheduler' action='preparebooking' onSuccess='processEditResponse(e,revertFunc)' params="'id='+event.resourceId+'&event_id='+event.id+'&start='+event.start.format()+'&end='+event.end.format()" />
          },
          eventClick: function(event) {
            <g:remoteFunction controller='scheduler' action='preparebooking' onSuccess='processEditResponse(e,null)' params="'id='+event.resourceId+'&event_id='+event.id+'&start='+event.start.format()+'&end='+event.end.format()" />
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
        $('message').hide();
        $('bookingForm').reset();
        if(e.responseJSON.errorcode.length){
          alert('На эти часы бронирование невозможно');
        }
        jQuery('#calendar').fullCalendar( 'unselect' );
        jQuery('#calendar').fullCalendar( 'refetchEvents' );
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
    </g:javascript>
  </head>
  <body onload="init()">
    <h3 class="fleft">${infotext?.header?:'Онлайн расписание'}</h3>
    <div class="clear"></div>
    <div class="grid_12" id="calendar"></div>
    <div class="clear"></div>
    <div id="message" class="grid_6 glossy messages" style="top: 50px;display:none">
      <div class="message-item-close" style="margin-top: -14px; margin-right: -14px;"/></div>
      <div class="text">
        <p>Бронирование объекта: <strong id="spacename"></strong></p>
        <p>Дата с: <strong id="startdate_text"></strong></p>
        <p>Дата по: <strong id="enddate_text"></strong></p>
      </div>
      <g:formRemote name="bookingForm" url="${[controller:'scheduler',action:'booking']}" method="post" onSuccess="processBookingResponse(e)" onreset="\$('recurrenceData').hide();\$('weeklytypedata').hide();">
        <g:csilabel for="title">Название брони:</g:csilabel>
        <input type="text" id="title" name="title" value=""/>
        <g:csilabel for="customer_id">Бронировать на:</g:csilabel>
        <g:select name="customer_id" from="${customers}" optionValue="${{it.toOptionValue()}}" optionKey="id" noSelection="${['0':'служебные цели']}"/>
        <g:csilabel for="price">К оплате:</g:csilabel>
        <input type="text" id="price" name="price" value=""/>
        <g:csilabel for="description">Описание:</g:csilabel>
        <label class="auto" for="is_recurrence">
          <input type="checkbox" id="is_recurrence" name="is_recurrence" value="1" onchange="jQuery('#recurrenceData').toggle()"/>
          Повторяющееся событие
        </label>
        <g:textArea name="description" id="description" value="" />
        <input type="hidden" id="booking_id" name="booking_id" value="0"/>
        <input type="hidden" id="space_id" name="space_id" value=""/>
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
        <div class="fright" style="padding-top:10px">
          <input type="submit" value="Сохранить" />
          <input type="button" class="reset button" value="Отмена" onclick="closeMessage()"/>
        </div>
      </g:formRemote>
    </div>
  </body>
</html>