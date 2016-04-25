import grails.util.Holders
class RawHtmlTagLib {

  def rawHtml = { attrs, body ->
    if (attrs.noline)
      out << body().replaceAll('\r\n','').decodeHTML()
    else
      out << body().decodeHTML()
  }

  def descrToHtml = { attrs, body ->
    out << ('<p>'+body().replaceAll('\r\n\r\n\r\n','</p><br/><br/><p>').replaceAll('\r\n\r\n','</p><br/><p>').replaceAll('\r\n','</p><p>')+'</p>').decodeHTML()
  }

  def userName = { attrs, body ->
    out << body().replace('.','·').replace('@','<img class="favicon" src="'+
        Holders.config.grails.serverURL+'/images/favicon.gif" border="0">')
  }
  
  def shortString = { attrs ->
    String text = attrs.text
    int length  = attrs.length ? Integer.parseInt(attrs.length) : 100
    
    if ( text ) {
      if ( text.length() < length )
        out << text.encodeAsHTML()
      else {
        text = text[0..length-1]
        /*if ( text.lastIndexOf('. ') != -1 )
          out << text[0 .. text.lastIndexOf('. ') ]
        else if ( text.lastIndexOf(' ') != -1 )
          out << text[0 .. text.lastIndexOf(' ')] << '&hellip;'
        else*/
          out << text << '&hellip;'
      }
    }
  }

  def csilabel = { attrs, body ->
    String sfor = attrs.for

    String result = '<label'
    if (sfor) result += ' for="' + sfor + '"'
    if (attrs.class) result += ' class="' + attrs.class + '"'
    if (attrs.style) result += ' style="' + attrs.style + '"'
    if (attrs.disabled=='true'||attrs.disabled==true) result += ' disabled="disabled"'

    result += '>'

    result += body().decodeHTML()
    if (pageScope.infoforms?."$sfor"?.hover){
      result += '<a class="tooltip" href="javascript:void(0)" title="'+pageScope.infoforms?."$sfor"?.hover+'">'
      result += '<img alt="'+pageScope.infoforms?."$sfor"?.hover+'" src="'+g.resource(dir:'images',file:'question.png').toString()+'" hspace="10" valign="baseline" border="0"/>'
      result += '</a>'
    }

    result += '</label>'

    if (pageScope.infoforms?."$sfor"?.hover){
      result += '\n<script type="text/javascript">jQuery("a.tooltip[title]").qtip({\nposition: { my: "top center", at: "bottom center" },\nstyle: { classes: "qtip-shadow qtip-tipsy" }\n});\n</script>'
    }

    out << result
  }

  def datepicker = { attrs -> 
    String name = attrs.name
    String value = attrs.value   
    String result =''

    result = '<input id="'+name+'" name="'+name+'" value="'+value+'" '+(attrs.disabled=='true'?'disabled="disabled"':'')+(attrs.style?'style="'+attrs.style+'"':'')+'/>\n<script type="text/javascript">\nvar '+name+' = jQuery("#'+name+'").kendoDatePicker({\nculture: "ru-RU"'

    if(attrs.change=='1')
      result += ',\nchange: onChange_'+name
    else if(attrs.onchange)
      result += ',\nchange: function onchange() { '+attrs.onchange.replace('\'','"')+' }'
    if(attrs.max){
      def max = attrs.max.split('\\.')
      result += ',\nmax: new Date ('+max[2]+', '+(max[1].toInteger()-1)+', '+max[0]+')'
    }
    if(attrs.min){
      def min = attrs.min.split('\\.')
      result += ',\nmin: new Date ('+min[2]+', '+(min[1].toInteger()-1)+', '+min[0]+')'
    }

    result += '\n});\n</script>'

    out << result
  }

  def timepicker = { attrs -> 
    String name = attrs.name
    String value = attrs.value   
    String result =''

    result = '<input id="'+name+'" name="'+name+'" value="'+value+'" '+(attrs.disabled=='true'?'disabled="disabled"':'')+(attrs.style?'style="'+attrs.style+'"':'')+'/>\n<script type="text/javascript">\nvar '+name+' = jQuery("#'+name+'").kendoTimePicker({\nculture: "ru-RU",\nformat: "HH:mm"'

    if(attrs.onchange)
      result += ',\nchange: function onchange() { '+attrs.onchange.replace('\'','"')+' }'
    if(attrs.interval)
      result += ',\ninterval: '+attrs.interval
    if(attrs.max){
      def max = attrs.max.split(':')
      result += ',\nmax: new Date (0, 0, 0, '+max[0]+', '+max[1]+')'
    }
    if(attrs.min){
      def min = attrs.min.split(':')
      result += ',\nmin: new Date (0, 0, 0, '+min[0]+', '+min[1]+')'
    }

    result += '\n});\n</script>'

    out << result
  }

  def number = { attrs ->
    BigDecimal value = attrs.value
    Integer fdigs = attrs.fdigs ? attrs.fdigs : 2

    out << formatNumber(number:value,type:'currency',minFractionDigits:fdigs,currencySymbol:'').replace(' ','')
  }

  def intnumber = { attrs ->
    Long value = attrs.value

    out << formatNumber(number:value,format:"###,##0")
  }

  def account = { attrs ->
    String value = attrs.value

    out << (value?.size()>16?value[0..4]+'.'+value[5..7]+'.'+value[8]+'.'+value[9..15]+'.'+value[16..-1]:value)
  }

  def shortDate = { attrs ->
    Date curdate = attrs.date
    Date today = new Date()
    
    if(curdate.date==today.date)
      out << formatDate(date:curdate,type:'time',style:'SHORT')
    else if(curdate.year==today.year)
      out << formatDate(format:'dd MMM.',date:curdate)
    else
      out << formatDate(format:'dd.MM.yyyy',date:curdate)
  }  
  
  def shortDateNoTime = { attrs ->
    Date curdate = attrs.date
    Date today = new Date()    
   
    if(curdate.year==today.year)
      out << formatDate(format:'dd MMM.',date:curdate)
    else
      out << formatDate(format:'dd.MM.yyyy',date:curdate)
  }
}
