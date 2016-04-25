<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${partners.count}</div>
    <div class="fright">
      <g:paginate controller="user" action="${actionName}" params="${inrequest}"
        prev="&lt;" next="&gt;" max="20" total="${partners.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>
<g:if test="${partners.records}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Код</th>
          <th>Название</th>
          <th>Тип бизнеса</th>
          <th>Тип обслуживания</th>
          <th>Статус</th>
          <th width="50"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${partners.records}" status="i" var="record">
        <tr align="left">
          <td align="center">${record.id}</td>
          <td>${record.name}</td>
          <td>${business[record.business_id]}</td>
          <td>${record.stype==1?'стандарт':'премиум'}</td>
          <td align="center"><i class="icon-${record.modstatus?'ok':'minus'}" title="${record.modstatus?'активный':'неактивный'}"></i></td>
          <td align="center">
            <a class="button" href="${createLink(action:'partner', id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${partners.count}</span>
    <span class="fright">
      <g:paginate controller="user" action="${actionName}" params="${inrequest}"
        prev="&lt;" next="&gt;" max="20" total="${partners.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>