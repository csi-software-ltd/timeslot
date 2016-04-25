<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${searchresult.count}</div>
    <div class="fright">
      <g:paginate controller="company" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${searchresult.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>
<g:if test="${searchresult.records}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th rowspan="2">Начало</th>
          <th rowspan="2">Длительность</th>
          <th rowspan="2">Объект</th>
          <th rowspan="2">Название</th>
          <th rowspan="2">Цена</th>
          <th colspan="2">Статус</th>
        </tr>
        <tr>
          <th>Оплаты</th>
          <th>Подтверждения</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="left">
          <td>${String.format('%td.%<tm.%<tY %<tH:%<tM:%<tS',record.fromtime)}</td>
          <td>${record.toHourDurationString()}</td>
          <td>${record.name}</td>
          <td>${record.title}</td>
          <td>${number(value:record.price)}</td>
          <td align="center">
            <abbr title="${record.paidstatus?'да':'нет'}">
              <i class="icon-${record.paidstatus?'ok':'minus'}"></i>
            </abbr>
          </td>
          <td align="center">
            <abbr title="${record.modstatus==1?'Подтверждена':record.modstatus==0?'Не подтверждена':'Отменена'}">
              <i class="icon-${record.modstatus==1?'ok':record.modstatus==0?'minus':'ban-circle'}"></i>
            </abbr>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${searchresult.count}</span>
    <span class="fright">
      <g:paginate controller="company" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${searchresult.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>