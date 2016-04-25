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
          <th>Название</th>
          <th>Описание</th>
          <th>Статус</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="center">
          <td align="left" width="200">${record.name}</td>
          <td align="left">${record.description}</td>
          <td width="70">
            <abbr title="${record.modstatus?'активная':'неактивная'}">
              <i class="icon-${record.modstatus?'ok':'trash'}"></i>
            </abbr>
          </td>
          <td width="70">
            <a class="button" href="${g.createLink(controller:'personal',action:'group',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>
          <g:if test="${!record.is_main}">
            &nbsp;&nbsp;<g:remoteLink class="button" url="${[controller:'personal',action:'updategroupstatus',id:record.id,params:[status:(record.modstatus?0:1)]]}" title="${record.modstatus?'Деактивировать':'Активировать'}" onSuccess="\$('form_submit_button').click()"><i class="icon-${record.modstatus?'trash':'ok'}"></i></g:remoteLink>
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