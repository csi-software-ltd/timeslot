<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${searchresult.count}</div>
    <div class="fright">
      <g:paginate controller="personal" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${searchresult.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>
<g:if test="${searchresult.records}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>ФИО</th>
          <th>Email</th>
          <th>Группа</th>
          <th>Статус</th>
          <th>Подтверждение</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="center">
          <td align="left">${record.fullname}</td>
          <td align="left">${record.login}</td>         
          <td align="left">${groups[record.usergroup_id]}</td>
          <td>
            <abbr title="${record.modstatus?'активный':'неактивный'}">
              <i class="icon-${record.modstatus?'ok':'trash'}"></i>
            </abbr>
          </td>
          <td>
            <abbr title="${record.is_emailconfirmed?'да':'нет'}">
              <i class="icon-${record.is_emailconfirmed?'ok':'minus'}"></i>
            </abbr>
          </td>
          <td>
            <a class="button" href="${g.createLink(controller:'personal',action:'user',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>
          <g:if test="${record.id!=user.id}">
            &nbsp;&nbsp;<g:remoteLink class="button" url="${[controller:'personal',action:'updateuserstatus',id:record.id,params:[status:(record.modstatus?0:1)]]}" title="${record.modstatus?'Деактивировать':'Активировать'}" onSuccess="\$('form_submit_button').click()"><i class="icon-${record.modstatus?'ban':'ok'}"></i></g:remoteLink>
          </g:if>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${searchresult.count}</span>
    <span class="fright">
      <g:paginate controller="personal" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${searchresult.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>