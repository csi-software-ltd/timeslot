import org.springframework.context.i18n.LocaleContextHolder as LCH
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.PostMethod

class MailerService {
  def grailsApplication
  LinkGenerator grailsLinkGenerator
  static transactional = false

  private def collectContext() {
    [
      is_dev:(Tools.getIntVal(grailsApplication.config.isdev,0)==1),
      serverURL:grailsApplication.config.grails.serverURL,
      appname:grailsApplication.config.grails.serverApp,
      lang:''
    ]
  }

  void sendMailGAE(htmlContent,from,sender,to,subject,isTemplate=0){
    HttpClient client = new HttpClient()  
    String url = "http://mail-st.appspot.com/mail_sender"
    PostMethod method = new PostMethod(url)
    
    method.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
    
    method.addParameter("email", htmlContent);
    method.addParameter("from", from);
    method.addParameter("to", to);
    method.addParameter("sender", sender);
    method.addParameter("subject",subject);
    method.addParameter("key","msg_sender_ST");
    method.addParameter("template",isTemplate.toString());
    
    int returnCode = client.executeMethod(method) //Response Code: 200, 302, 304 etc.
    def response = method.getResponseBodyAsString() // Actual response
  }

  def sendActivationMail(oUser){
    if (!oUser) return
    def th=new Thread()
    th.start{
      synchronized(this) {
        User.withNewSession {
          def lsText = Email_template.findWhere(action:'#activation')
          def sText = '[@EMAIL], for activation of your account use follow link [@URL]'
          def sHeader = "Sign Up to Timeslot"
          if(lsText){
            sText = lsText.itext
            sHeader = lsText.title
          }
          sText = sText.replace('[@NICKNAME]',oUser.fullname)
            .replace('[@EMAIL]',oUser.email)
            .replace('[@URL]',grailsLinkGenerator.link(controller: 'index', action: 'confirm', params:[id:oUser.scode], absolute: true))
          sHeader = sHeader.replace('[@EMAIL]',oUser.email)
            .replace('[@URL]',grailsLinkGenerator.link(controller: 'index', action: 'confirm', params:[id:oUser.scode], absolute: true))

          try{
            if(Tools.getIntVal(grailsApplication.config.mail_gae,0))
              sendMailGAE(sText,grailsApplication.config.grails.mail.default.from,grailsApplication.config.grails.mail.username,oUser.email,sHeader,1)
            else{
              sendMail{
                to oUser.email
                subject sHeader
                body( view:"/_mail",
                model:[mail_body:sText])
              }
            }
          } catch(Exception e) {
            log.debug("Cannot sent email \n"+e.toString()+' in sendActivationMail')
          }
        }
      }
    }
  }

  def sendRestorePasswordMail(oUser){
    if (!oUser) return
    def th=new Thread()
    th.start{
      synchronized(this) {
        User.withNewSession {
          def lsText = Email_template.findWhere(action:'#restore')
          def sText = '[@EMAIL], for restore of your password use follow link [@URL]'
          def sHeader = "Restore password"
          if(lsText){
            sText = lsText.itext
            sHeader = lsText.title
          }
          sText = sText.replace('[@NICKNAME]',oUser.fullname)
            .replace('[@EMAIL]',oUser.email)
            .replace('[@URL]',grailsLinkGenerator.link(controller: 'index', action: 'passrestore', params:[id:oUser.scode], absolute: true))
          sHeader = sHeader.replace('[@EMAIL]',oUser.email)
            .replace('[@URL]',grailsLinkGenerator.link(controller: 'index', action: 'passrestore', params:[id:oUser.scode], absolute: true))

          try{
            if(Tools.getIntVal(grailsApplication.config.mail_gae,0))
              sendMailGAE(sText,grailsApplication.config.grails.mail.default.from,grailsApplication.config.grails.mail.username,oUser.email,sHeader,1)
            else{
              sendMail{
                to oUser.email
                subject sHeader
                body( view:"/_mail",
                model:[mail_body:sText])
              }
            }
          } catch(Exception e) {
            log.debug("Cannot sent email \n"+e.toString()+' in sendRestorePasswordMail')
          }
        }
      }
    }
  }

  def sendSuccessConfirmationMail(oUser){
    if (!oUser) return
    def th=new Thread()
    th.start{
      synchronized(this) {
        User.withNewSession {
          def lsText = Email_template.findWhere(action:'#greeting')
          def sText = '[@EMAIL] Registration at Timeslot'
          def sHeader = "Registration at Timeslot"
          if(lsText){
            sText = lsText.itext
            sHeader = lsText.title
          }
          sText = sText.replace('[@NICKNAME]',oUser.fullname)
            .replace('[@EMAIL]',oUser.email)
          sHeader = sHeader.replace('[@EMAIL]',oUser.email)

          try{
            if(Tools.getIntVal(grailsApplication.config.mail_gae,0))
              sendMailGAE(sText,grailsApplication.config.grails.mail.default.from,grailsApplication.config.grails.mail.username,oUser.email,sHeader,1)
            else{
              sendMail{
                to oUser.email
                subject sHeader
                body( view:"/_mail",
                model:[mail_body:sText])
              }
            }
          } catch(Exception e) {
            log.debug("Cannot sent email \n"+e.toString()+' in sendSuccessConfirmationMail')
          }
        }
      }
    }
  }

  def sendClientInventationalMail(Customertopartner oCustomertopartner){
    if (!oCustomertopartner) return
    def th=new Thread()
    th.start{
      synchronized(this) {
        Customertopartner.withNewSession {
          def lsText = Email_template.findWhere(action:'#clinventation')
          def sText = '[@NAME], you have been invited by [@PARTNERNAME]'
          def sHeader = "Inventation to Timeslot"
          if(lsText){
            sText = lsText.itext
            sHeader = lsText.title
          }
          def oCustomer = Customer.get(oCustomertopartner.customer_id)
          sText = sText.replace('[@NAME]',oCustomer.name)
            .replace('[@PARTNERNAME]',Partner.get(oCustomertopartner.partner_id)?.name?:'')
          sHeader = sHeader.replace('[@NAME]',oCustomer.name)

          try{
            if(Tools.getIntVal(grailsApplication.config.mail_gae,0))
              sendMailGAE(sText,grailsApplication.config.grails.mail.default.from,grailsApplication.config.grails.mail.username,oCustomer.email,sHeader,1)
            else{
              sendMail{
                to oCustomer.email
                subject sHeader
                body( view:"/_mail",
                model:[mail_body:sText])
              }
            }
          } catch(Exception e) {
            log.debug("Cannot sent email \n"+e.toString()+' in sendClientInventationalMail')
          }
        }
      }
    }
  }

  def sendRestoreCustomerPasswordMail(oUser,oPartner){
    if (!oUser) return
    def th=new Thread()
    th.start{
      synchronized(this) {
        User.withNewSession {
          def lsText = Email_template.findWhere(action:'#restoreCustomer')
          def sText = '[@EMAIL], for restore of your password use follow link [@URL]'
          def sHeader = "Restore password"
          if(lsText){
            sText = lsText.itext
            sHeader = lsText.title
          }
          sText = sText.replace('[@NICKNAME]',oUser.name)
            .replace('[@EMAIL]',oUser.email)
            .replace('[@URL]',grailsLinkGenerator.link(controller: 'company', action: 'passrestore', params:[linkname:oPartner.linkname,code:oUser.scode], absolute: true))
          sHeader = sHeader.replace('[@EMAIL]',oUser.email)
            .replace('[@URL]',grailsLinkGenerator.link(controller: 'company', action: 'passrestore', params:[linkname:oPartner.linkname,code:oUser.scode], absolute: true))

          try{
            if(Tools.getIntVal(grailsApplication.config.mail_gae,0))
              sendMailGAE(sText,grailsApplication.config.grails.mail.default.from,grailsApplication.config.grails.mail.username,oUser.email,sHeader,1)
            else{
              sendMail{
                to oUser.email
                subject sHeader
                body( view:"/_mail",
                model:[mail_body:sText])
              }
            }
          } catch(Exception e) {
            log.debug("Cannot sent email \n"+e.toString()+' in sendRestoreCustomerPasswordMail')
          }
        }
      }
    }
  }

  def sendCustomerActivationMail(oUser,oPartner){
    if (!oUser) return
    def th=new Thread()
    th.start{
      synchronized(this) {
        User.withNewSession {
          def lsText = Email_template.findWhere(action:'#activationCustomer')
          def sText = '[@EMAIL], for activation of your account use follow link [@URL]'
          def sHeader = "Sign Up to Timeslot"
          if(lsText){
            sText = lsText.itext
            sHeader = lsText.title
          }
          sText = sText.replace('[@NICKNAME]',oUser.name)
            .replace('[@EMAIL]',oUser.email)
            .replace('[@URL]',grailsLinkGenerator.link(controller: 'company', action: 'confirm', params:[linkname:oPartner.linkname,code:oUser.scode], absolute: true))
          sHeader = sHeader.replace('[@EMAIL]',oUser.email)
            .replace('[@URL]',grailsLinkGenerator.link(controller: 'company', action: 'confirm', params:[linkname:oPartner.linkname,code:oUser.scode], absolute: true))

          try{
            if(Tools.getIntVal(grailsApplication.config.mail_gae,0))
              sendMailGAE(sText,grailsApplication.config.grails.mail.default.from,grailsApplication.config.grails.mail.username,oUser.email,sHeader,1)
            else{
              sendMail{
                to oUser.email
                subject sHeader
                body( view:"/_mail",
                model:[mail_body:sText])
              }
            }
          } catch(Exception e) {
            log.debug("Cannot sent email \n"+e.toString()+' in sendCustomerActivationMail')
          }
        }
      }
    }
  }

  def sendSuccessCustomerConfirmationMail(oUser){
    if (!oUser) return
    def th=new Thread()
    th.start{
      synchronized(this) {
        User.withNewSession {
          def lsText = Email_template.findWhere(action:'#greetingCustomer')
          def sText = '[@EMAIL] Registration at Timeslot'
          def sHeader = "Registration at Timeslot"
          if(lsText){
            sText = lsText.itext
            sHeader = lsText.title
          }
          sText = sText.replace('[@NICKNAME]',oUser.name)
            .replace('[@EMAIL]',oUser.email)
          sHeader = sHeader.replace('[@EMAIL]',oUser.email)

          try{
            if(Tools.getIntVal(grailsApplication.config.mail_gae,0))
              sendMailGAE(sText,grailsApplication.config.grails.mail.default.from,grailsApplication.config.grails.mail.username,oUser.email,sHeader,1)
            else{
              sendMail{
                to oUser.email
                subject sHeader
                body( view:"/_mail",
                model:[mail_body:sText])
              }
            }
          } catch(Exception e) {
            log.debug("Cannot sent email \n"+e.toString()+' in sendSuccessCustomerConfirmationMail')
          }
        }
      }
    }
  }

  def sendBookingNotificationMail(oUser){
    if (!oUser) return
    def th=new Thread()
    th.start{
      synchronized(this) {
        User.withNewSession {
          def lsText = Email_template.findWhere(action:'#booknotify')
          def sText = '[@NICKNAME], your booking at Timeslot'
          def sHeader = "[@EMAIL], your booking at Timeslot"
          if(lsText){
            sText = lsText.itext
            sHeader = lsText.title
          }
          sText = sText.replace('[@NICKNAME]',oUser.name)
            .replace('[@EMAIL]',oUser.email)
          sHeader = sHeader.replace('[@EMAIL]',oUser.email)

          try{
            if(Tools.getIntVal(grailsApplication.config.mail_gae,0))
              sendMailGAE(sText,grailsApplication.config.grails.mail.default.from,grailsApplication.config.grails.mail.username,oUser.email,sHeader,1)
            else{
              sendMail{
                to oUser.email
                subject sHeader
                body( view:"/_mail",
                model:[mail_body:sText])
              }
            }
          } catch(Exception e) {
            log.debug("Cannot sent email \n"+e.toString()+' in sendBookingNotificationMail')
          }
        }
      }
    }
  }
}