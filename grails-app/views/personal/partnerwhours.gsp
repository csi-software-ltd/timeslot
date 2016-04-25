<g:formRemote id="workhoursAddForm" name="workhoursAddForm" url="[action:'addworkhours']" method="post" onSuccess="processAddworkhoursResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="errorworkhourslist">
      <li></li>
    </ul>
  </div>
  <div id="workhours"></div>
</g:formRemote>
<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th colspan="9">Часы работы</th>
          <th rowspan="2"></th>
        </tr>
        <tr>
          <th width="50">ПН</th>
          <th width="50">ВТ</th>
          <th width="50">СР</th>
          <th width="50">ЧТ</th>
          <th width="50">ПТ</th>
          <th width="50">СБ</th>
          <th width="50">ВС</th>
          <th width="50">ПР</th>
          <th>Время</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${whours}" status="i" var="record">
        <tr align="center">
          <td><abbr><i class="icon-${record.is_mon?'ok':'minus'}"></i></abbr></td>
          <td><abbr><i class="icon-${record.is_tue?'ok':'minus'}"></i></abbr></td>
          <td><abbr><i class="icon-${record.is_wen?'ok':'minus'}"></i></abbr></td>
          <td><abbr><i class="icon-${record.is_thu?'ok':'minus'}"></i></abbr></td>
          <td><abbr><i class="icon-${record.is_fra?'ok':'minus'}"></i></abbr></td>
          <td><abbr><i class="icon-${record.is_sut?'ok':'minus'}"></i></abbr></td>
          <td><abbr><i class="icon-${record.is_sun?'ok':'minus'}"></i></abbr></td>
          <td><abbr><i class="icon-${record.is_hol?'ok':'minus'}"></i></abbr></td>
          <td>${String.format('%tH:%<tM',record.time_open)} &mdash; ${String.format('%tH:%<tM',record.time_close)}</td>
          <td width="60">
            <g:remoteLink class="button" style="z-index:1" url="${[controller:controllerName, action:'deleteworkhours', id:record.id]}" title="Удалить" onSuccess="getWorkhours()"><i class="icon-trash"></i></g:remoteLink>&nbsp;&nbsp;
            <a class="button" style="z-index:1" href="javascript:void(0)" title="Редактировать" onclick="$('workhour_id').value=${record.id};$('workhours_submit_button').click();"><i class="icon-pencil"></i></a>
          </td>
        </tr>
      </g:each>
        <tr>
          <td colspan="10" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)" onclick="$('workhour_id').value=0;$('workhours_submit_button').click();">
              Добавить часы работы &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="workhoursForm" url="[action:'workhours']" update="workhours" onComplete="\$('errorworkhourslist').up('div').hide();jQuery('#workhoursAddForm').slideDown();" style="display:none">
  <input type="hidden" id="workhour_id" name="id" value="0"/>
  <input type="submit" class="button" id="workhours_submit_button" value="Показать"/>
</g:formRemote>