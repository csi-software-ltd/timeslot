<html>
  <head>
    <title>${infotext?.title?:'Timeslot приложение'}</title>
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />
    <meta name="layout" content="main" />
    <style type="text/css">
      label{min-width:160px}
    </style>
    <g:javascript>
      function returnToList(){
        $("returnToListForm").submit();
      }
      function processResponse(e){
        $("saveinfo").up('div').hide();
        $("errorlist").up('div').hide();
        var sErrorMsg = '';
        ['uname'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Название"])}</li>'; $('uname').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
          jQuery('html,body').animate({scrollTop: 0},'slow');
        } else if(${partnergroup?1:0}){
          $("saveinfo").up('div').show();
          jQuery('html,body').animate({scrollTop: 0},'slow');
        } else if(e.responseJSON.partnergroup){
          location.assign('${createLink(controller:controllerName,action:'group')}'+'/'+e.responseJSON.partnergroup);
        } else
          location.assign('${createLink(controller:controllerName,action:'groups')}');
      }
      function submitForm(){
        $('submit_button').click();
      }
    </g:javascript>
  </head>
  <body>
    <h3 class="fleft">${infotext?.header?:'Группа клиентов'}</h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку групп</a>
    <div class="clear"></div>
    <g:formRemote name="partnergroupDetailForm" url="${[action:'updategroupdetail',id:partnergroup?.id?:0]}" method="post" onSuccess="processResponse(e)">

      <div class="info-box" style="display:none;margin-top:0">
        <span class="icon icon-info-sign icon-3x"></span>
        <ul id="saveinfo">
          <li>Изменения сохранены!</li>
        </ul>
      </div>
      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
        </ul>
      </div>

      <g:csilabel for="uname">Название:</g:csilabel>
      <input type="text" id="uname" name="name" value="${partnergroup?.name}"/>

      <br/><g:csilabel for="description">Описание:</g:csilabel>
      <g:textArea name="description" id="description" value="${partnergroup?.description}" />

      <input style="display:none" type="submit" id="submit_button" value="Сохранить" />
      <div class="fright" id="btns" style="padding-top:10px">
        <input type="button" class="spacing" value="Сохранить" onclick="submitForm()" />
      </div>
    </g:formRemote>
    <div class="clear"></div>
    <g:form id="returnToListForm" name="returnToListForm" url="${[controller:'personal', action:'groups', params:[fromDetails:action_id]]}"></g:form>
  </body>
</html>