<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${searchresult.count}</div>
    <div class="fright">
      <g:paginate controller="personal" action="${actionName}" params="${inrequest}" 
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
          <th rowspan="2">Имя</th>
          <th rowspan="2">Фамилия</th>
          <th rowspan="2">Компания</th>
          <th rowspan="2">Email</th>
          <th rowspan="2">Телефон</th>
          <th rowspan="2">Группа</th>
          <th colspan="2">Статус</th>
          <th rowspan="2"></th>
        </tr>
        <tr>
          <th>Доверия</th>
          <th>Активности</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="left">
          <td>${record.firstname}</td>
          <td>${record.lastname}</td>
          <td>${record.company}</td>
          <td>${record.email}</td>
          <td>${record.phone}</td>
          <td>${groups[record.partnergroup_id]}</td>
          <td align="center">
            <abbr title="${record.is_trust?'да':'нет'}">
              <i class="icon-${record.is_trust?'ok':'minus'}"></i>
            </abbr>
          </td>
          <td align="center">
            <abbr title="${record.modstatus?'активный':'неактивный'}">
              <i class="icon-${record.modstatus?'ok':'trash'}"></i>
            </abbr>
          </td>
          <td width="60" align="center">
            <a class="button" href="${g.createLink(controller:'personal',action:'customer',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a> &nbsp;&nbsp;
            <g:remoteLink class="button" url="${[controller:'personal',action:'updatecustomerstatus',id:record.customer_id, params:[status:(record.modstatus?0:1)]]}" title="${record.modstatus?'Деактивировать':'Активировать'}" onSuccess="\$('form_submit_button').click()"><i class="icon-${record.modstatus?'trash':'ok'}"></i></g:remoteLink>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${searchresult.count}</span>
    <span class="fright">
      <g:paginate controller="personal" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${searchresult.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>