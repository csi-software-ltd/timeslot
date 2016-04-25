<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Логин</th>
          <th>ФИО</th>
          <th>Группа</th>
          <th>Телефон</th>
          <th>Статус email</th>
          <th>Статус</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${users}" status="i" var="record">
        <tr align="center">
          <td>${record.login}</td>
          <td>${record.fullname}</td>
          <td>${usergroups[record.usergroup_id]}</td>
          <td>${record.mobile}</td>
          <td>
            <abbr title="${record.is_emailconfirmed?'подтвержден':'не подтвержден'}">
              <i class="icon-${record.is_emailconfirmed?'ok':'minus'}"></i>
            </abbr>
          </td>
          <td>
            <abbr title="${record.modstatus?'активный':'неактивный'}">
              <i class="icon-${record.modstatus?'ok':'trash'}"></i>
            </abbr>
          </td>
          <td width="40">
            <a class="button" onclick="loginAsUser(${record.id})" title="Войти под именем"><i class="icon-signin"></i></a>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</div>