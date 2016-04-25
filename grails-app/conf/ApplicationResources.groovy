modules = {
    application {
      resource url:'js/application.js'
    }
    'prototype/prototype' {
      dependsOn 'jquery-1.10.1.min'
      dependsOn 'application'
      resource url:'js/prototype/prototype.js', disposition: 'head'
    }
    'prototype/autocomplete' {
      dependsOn 'prototype/prototype'
      resource url:'js/prototype/autocomplete.js', disposition: 'head'
    }
    'jquery-1.8.3' {
      resource url:'js/jquery-1.8.3.js', disposition: 'head'
    }
    'jquery-1.10.1.min' {
      resource url:'js/jquery-1.10.1.min.js', disposition: 'head'
    }
    'jquery.qtip.min' {
      dependsOn 'jquery-1.10.1.min'
      resource url:'js/jquery.qtip.min.js', disposition: 'head'
    }
    'html5' {
       resource url:'js/html5.js', disposition: 'head'
    }
    'kendo.culture.ru-RU.min' {
      dependsOn 'jquery-1.10.1.min'
      dependsOn 'kendo.web.min'
      resource url:'js/kendo.culture.ru-RU.min.js', disposition: 'head'
    }
    'kendo.web.min' {
      dependsOn 'jquery-1.10.1.min'
      dependsOn 'jquery.maskedinput.min'
      resource url:'js/kendo.web.min.js', disposition: 'head'
    }
    'jquery.maskedinput.min' {
      dependsOn 'jquery-1.10.1.min'
      resource url:'js/jquery.maskedinput.min.js', disposition: 'head'
    }
    'superfish.min' {
      dependsOn 'jquery-1.10.1.min'
      resource url:'js/superfish.min.js', disposition: 'head'
    }
    'moment.min' {
      dependsOn 'jquery-1.10.1.min'
      resource url:'js/moment.min.js', disposition: 'head'
    }
    'fullcalendar.min' {
      dependsOn 'moment.min'
      resource url:'js/fullcalendar.min.js', disposition: 'head'
    }
    'scheduler' {
      dependsOn 'fullcalendar.min'
      resource url:'js/scheduler.js', disposition: 'head'
    }
}