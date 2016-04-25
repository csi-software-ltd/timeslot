<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th rowspan="2">Название</th>
          <th rowspan="2">Категория</th>
          <th colspan="2">Статус</th>
        </tr>
        <tr>
          <th>Видимости</th>
          <th>Активности</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${spaces.records}" status="i" var="record">
        <tr align="center">
          <td align="left">${record.name}</td>
          <td align="left">${units[record.unit_id]}</td>
          <td>
            <abbr title="${record.is_visible?'да':'нет'}">
              <i class="icon-${record.is_visible?'ok':'minus'}"></i>
            </abbr>
          </td>
          <td>
            <abbr title="${record.modstatus?'активный':'неактивный'}">
              <i class="icon-${record.modstatus?'ok':'trash'}"></i>
            </abbr>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</div>