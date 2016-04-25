<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="ru" lang="ru">
  <head>
    <title><g:layoutTitle default="Prisma" /></title>
    <meta http-equiv="content-language" content="ru" />
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />      
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />               
    <meta name="copyright" content="Prisma" />    
    <meta name="resource-type" content="document" />
    <meta name="document-state" content="dynamic" />
    <meta name="revisit" content="1" />
    <meta name="viewport" content="width=1000,maximum-scale=1.0" />     
    <meta name="robots" content="noindex,nofollow" />
    <!--<meta name="cmsmagazine" content="55af4ed6d7e3fafc627c933de458fa04" />-->
    <link rel="shortcut icon" href="${resource(file:'favicon.ico',absolute:true)}" type="image/x-icon" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'reset.css')}" type="text/css" />    
    <link rel="stylesheet" href="${resource(dir:'css',file:'grid.css')}" type="text/css" />  
    <link rel="stylesheet" href="${resource(dir:'css',file:'style.css')}" type="text/css" />
    <g:layoutHead />
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
        <a class="logo" title="Prisma — главная страница" href="${createLink(controller:'user',action:'panel')}">Prisma</a>
        <h1 align="center" class="fleft">Prisma приложение</h1>      
        <div class="clear"></div>
      </div>
    </header>
    <section id="content">
      <div class="container_12">
        <g:layoutBody />
        <div class="clear"></div>
      </div>
    </section>
    <r:layoutResources/>
  </body>
</html>