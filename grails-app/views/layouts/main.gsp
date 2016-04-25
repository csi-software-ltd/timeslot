<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="ru" lang="ru">
  <head>
    <title><g:layoutTitle default="Timeslot" /></title>
    <meta http-equiv="content-language" content="ru" />
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
    <meta name="copyright" content="LinkDevelopment" />
    <meta name="resource-type" content="document" />
    <meta name="document-state" content="dynamic" />
    <meta name="revisit" content="1" />
    <meta name="viewport" content="width=1000,maximum-scale=1.0" />
    <meta name="robots" content="noindex,nofollow" />
    <link rel="shortcut icon" href="${resource(file:'favicon.ico',absolute:true)}" type="image/x-icon" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'reset.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'grid.css')}" type="text/css" />  
    <link rel="stylesheet" href="${resource(dir:'css',file:'superfish.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'style.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'font-awesome.min.css')}" type="text/css" />
    <link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'qtip.css')}" />
    <g:layoutHead />
    <g:javascript library="jquery-1.10.1.min" />
    <g:javascript library="superfish.min" />
    <g:javascript library="jquery.qtip.min" />
    <g:javascript library="application" />
    <g:javascript library="prototype/prototype" />
    <!--[if lt IE 7]>
  		<div class='aligncenter'><a href="http://www.microsoft.com/windows/internet-explorer/default.aspx?ocid=ie6_countdown_bannercode"><img src="http://storage.ie6countdown.com/assets/100/images/banners/warning_bar_0000_us.jpg" border="0"></a></div>  
    <![endif]-->
    <!--[if lt IE 9]>    
      <g:javascript library="html5" />
      <link rel="stylesheet" href="${resource(dir:'css',file:'ie.css')}" type="text/css" />   		  		
    <![endif]-->    
    <r:layoutResources/>
  </head>
  <body onload="${pageProperty(name:'body.onload')}">    
    <header>
      <div class="main">
        <a class="logo" title="Timeslot — главная страница" href="${createLink(controller:'index',action:'index')}">Timeslot</a>
        <g:if test="${!user}"><h1 align="center" class="fleft">Timeslot приложение</h1></g:if>
        <g:else><h1 align="center" class="fleft">${partner.name}</h1></g:else>
        <div class="user inline fright">
        <g:if test="${user}">
          <span class="icon-lock icon-light"></span> <span class="user-login" id="user">${user.name?:user.login?:''}</span>
          <a class="icon-bell icon-large icon-light" title="Новые брони" href="${createLink(controller:'personal', action:'bookings')}">
          <g:if test="${notice.newbookings_count}">
            <div class="new">${notice.newbookings_count}</div>
          </g:if>
          </a>
          <a class="icon-signout icon-1x icon-light" title="Выход" href="${g.createLink(controller:'index',action:'logout')}"> </a>
        </g:if><g:else>&nbsp;</g:else>
        </div>
        <div class="clear"></div>
      </div>
    </header>
  <g:if test="${user}">
    <nav>
      <div class="main">
        <ul class="sf-menu">
        <g:each in="${user.menu}" var="item">
        <g:if test="${item.is_main}">
          <li class="${action_id==item.id?'current':''}">
            <g:link controller="${item.controller}" action="${item.action}">${item.name}</g:link>
          </li>
        </g:if>
        </g:each>
          <li class="${user.addit.contains(action_id)?'current':''}">
            <a href="javascript:void(0)">${user.addit.contains(action_id)?user.menu.find{it.id==action_id}.name:'Доп. меню'}</a>
            <ul style="display:none">
            <g:each in="${user.menu}" var="item">
            <g:if test="${!item.is_main}">
              <li><g:link controller="${item.controller}" action="${item.action}">${item.name}</g:link></li>
            </g:if>
            </g:each>
            </ul>
          </li>
        </ul>
      </div>
    </nav>
    <div class="clear"></div>
  </g:if>
    <section id="content">
      <div class="container_12">
        <g:if test="${session.attention_message!='' && session.attention_message!=null}"> 
          <div class="info-box">
            <span class="icon icon-info-sign icon-3x"></span>
            ${session.attention_message}
          </div>    
        </g:if>
        <g:if test="${user?.is_emailconfirmed==0}"> 
          <div class="error-box">
            <span class="icon icon-warning-sign icon-3x"></span>
            Подтвердите, пожалуйста, email адрес вашего аккаунта. Если вы не получили письмо с инструкцией для подтверждения email, нажмите на <g:remoteLink controller="personal" action="sendverify" onSuccess="alert('Письмо отправлено')">ссылку</g:remoteLink>.
          </div>    
        </g:if>
        <g:layoutBody />
        <div class="clear"></div>
      </div>
    </section>
    <r:layoutResources/>
  <g:if test="${user}">
    <script type="text/javascript">
      jQuery(window).scroll(function(){
        if(jQuery(this).scrollTop()>44)
          jQuery("nav").css({position:'fixed',top:0,width:'100%'});
        else if(jQuery(this).scrollTop()==0)
          jQuery("nav").css('position','relative');        
      });
      jQuery(document).ready(function(){
        jQuery('ul.sf-menu').superfish({
          hoverClass:'sfHover',
          pathClass:'active',
          delay:300,
          animation:{height:'show'},
          speed:'def',
          cssArrows:false,
          autoArrows:false,
          dropShadows:1
        });
      });
    </script>
  </g:if>
  </body>
</html>