<g:formRemote id="unitpriceAddForm" name="unitpriceAddForm" url="[action:'addunitprice']" method="post" onSuccess="processAddunitpriceResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="errorunitpricelist">
      <li></li>
    </ul>
  </div>
  <div id="unitprice"></div>
</g:formRemote>
<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th rowspan="2">Цена</th>
          <th rowspan="2">Размерность, мин</th>
          <th rowspan="2">Мин. резерв, мин</th>
          <th colspan="9">Цена действительна</th>
          <th rowspan="2"></th>
        </tr>
        <tr>
          <th width="20">ПН</th>
          <th width="20">ВТ</th>
          <th width="20">СР</th>
          <th width="20">ЧТ</th>
          <th width="20">ПТ</th>
          <th width="20">СБ</th>
          <th width="20">ВС</th>
          <th width="20">ПР</th>
          <th width="60">Время</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${prices}" status="i" var="record">
        <tr align="center">
          <td>${number(value:record.price)}</td>
          <td>${record.forperiod}</td>
          <td>${record.minperiod}</td>
        <g:if test="${!record.is_exception}">
          <td colspan="9">Базовая цена</td>
        </g:if><g:else>
          <td><abbr><i class="icon-${record.is_mon?'ok':'minus'}"></i></abbr></td>
          <td><abbr><i class="icon-${record.is_tue?'ok':'minus'}"></i></abbr></td>
          <td><abbr><i class="icon-${record.is_wen?'ok':'minus'}"></i></abbr></td>
          <td><abbr><i class="icon-${record.is_thu?'ok':'minus'}"></i></abbr></td>
          <td><abbr><i class="icon-${record.is_fra?'ok':'minus'}"></i></abbr></td>
          <td><abbr><i class="icon-${record.is_sut?'ok':'minus'}"></i></abbr></td>
          <td><abbr><i class="icon-${record.is_sun?'ok':'minus'}"></i></abbr></td>
          <td><abbr><i class="icon-${record.is_hol?'ok':'minus'}"></i></abbr></td>
          <td>${String.format('%tH:%<tM',record.time_start)}<br/>${String.format('%tH:%<tM',record.time_end)}</td>
        </g:else>
          <td>
          <g:if test="${record.is_exception}">
            <g:remoteLink class="button" style="z-index:1" url="${[controller:controllerName, action:'deleteunitprice', id:record.id, params:[unit_id:unit.id]]}" title="Удалить" onSuccess="getPrices()"><i class="icon-trash"></i></g:remoteLink>&nbsp;&nbsp;
          </g:if>
            <a class="button" style="z-index:1" href="javascript:void(0)" title="Редактировать" onclick="$('unitprice_id').value=${record.id};$('unitprice_submit_button').click();"><i class="icon-pencil"></i></a>
          </td>
        </tr>
      </g:each>
        <tr>
          <td colspan="13" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)" onclick="$('unitprice_id').value=0;$('unitprice_submit_button').click();">
              Добавить настройку цены &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="unitpriceForm" url="[action:'unitprice', params:[unit_id:unit.id]]" update="unitprice" onComplete="\$('errorunitpricelist').up('div').hide();jQuery('#unitpriceAddForm').slideDown();" style="display:none">
  <input type="hidden" id="unitprice_id" name="id" value="0"/>
  <input type="submit" class="button" id="unitprice_submit_button" value="Показать"/>
</g:formRemote>