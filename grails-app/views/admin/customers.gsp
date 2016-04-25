<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${customers.count}</div>
    <div class="fright">
      <g:paginate controller="admin" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${customers.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>
<g:if test="${customers.records}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th rowspan="2">Имя</th>
          <th rowspan="2">Фамилия</th>
          <th rowspan="2">Компания</th>
          <th rowspan="2">Email</th>
          <th rowspan="2">Телефон</th>
          <th rowspan="2">Группа</th>
          <th colspan="2">Статус</th>
        </tr>
        <tr>
          <th>Доверия</th>
          <th>Активности</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${customers.records}" status="i" var="record">
        <tr align="left">
          <td>${record.firstname?:record.custname}</td>
          <td>${record.lastname}</td>
          <td>${record.company?:'нет'}</td>
          <td>${record.email}</td>
          <td>${record.phone?:record.custphone}</td>
          <td>${groups[record.partnergroup_id?:'0']}</td>
          <td align="center">
          <g:if test="${record.id>0}">
            <abbr title="${record.is_trust?'да':'нет'}">
              <i class="icon-${record.is_trust?'ok':'minus'}"></i>
            </abbr>
          </g:if>
          </td>
          <td align="center">
          <g:if test="${record.id>0}">
            <abbr title="${record.modstatus?'активный':'неактивный'}">
              <i class="icon-${record.modstatus?'ok':'trash'}"></i>
            </abbr>
          </g:if>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${customers.count}</span>
    <span class="fright">
      <g:paginate controller="admin" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${customers.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>