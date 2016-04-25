import grails.util.Holders
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.security.MessageDigest
import java.security.SecureRandom
import org.apache.commons.lang.time.DateUtils as DU

class Tools  {     
  ///////////////////////////////////////////////////////////////////////////////
  static prepareEmailString(sEmail){
    // remove -,.,@
    if(sEmail==null)
      return ''
    return sEmail.replace("@", '').replace('-','').replace('.','')
  }
  ///////////////////////////////////////////////////////////////////////////////
  static checkEmailString(sEmail){
    return sEmail ==~ /^[_A-Za-z0-9](([_\.\-]?[a-zA-Z0-9]+)*)[_]*@([A-Za-z0-9]+)(([\.\-]?[a-zA-Z0-9]+)*)\.([A-Za-z]{2,})$/
  }
  /////////////////////////////////////////////////////////////////////////////
  static generateMD5(sText) {
    MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
    digest.update(sText.getBytes());
    def mdRes=digest.digest()
    def sOut=''
    for (i in mdRes) 
      sOut+=Integer.toHexString(0xFF & i)
    return sOut;  
  }
  /////////////////////////////////////////////////////////////////////////////
  static hidePsw(sPsw) {
    if (!sPsw) return null
    return generateMD5('_yellcat'+sPsw+'yellcat-')
	/*def psw='_yellcat'+sPsw+'yellcat-'
	return psw.encodeAsMD5()*/
  }
  /////////////////////////////////////////////////////////////////////////////
  static getIntVal(sValue,iDefault=0){
    if(sValue==null)
      return iDefault
    try{
      iDefault=sValue.toInteger()
    }catch(Exception e){
      //do nothing
    }
    return iDefault
  }
  /////////////////////////////////////////////////////////////////////////////
  static getLongVal(sValue,iDefault=0){
    if(sValue==null)
      return iDefault
    try{
      iDefault=sValue.toLong()
    }catch(Exception e){
      //do nothing
    }
    return iDefault
  }
  /////////////////////////////////////////////////////////////////////////////
  static Float getFloatVal(sValue,fDefault=0f){
    if(sValue==null)
      return fDefault
    try{
      fDefault=sValue.toFloat()
    }catch(Exception e){
      //do nothing
    }
    return fDefault
  }
  static Date getDate(sName){
    if(!sName)
      return null
    try{
      return Date.parse('dd.MM.yyyy', sName)
    }catch(Exception e){
      return null
    }
  }
  static Date getDateShort(sName){
    if(!sName)
      return null
    try{
      return Date.parse('dd.MM.yy', sName)
    }catch(Exception e){
      return null
    }
  }
  ///////////////////////////////////////////////////////////////////////////
  static String arrayToString(sValue,separator) {
    if(((sValue!=null)?sValue:[]).size()==0)
      return ''
    StringBuffer result = new StringBuffer();
    if (sValue.size() > 0) {
      result.append(sValue[0]);
      for (int i=1; i<sValue.size(); i++) {
        result.append(separator);
        result.append(sValue[i]);
      }
    }
    return result.toString();
  }
  ///////////////////////////////////////////////////////////////////////////
  static String escape(sValue){
    return sValue.replace("'","\\'").replace('"','\\"')
  }
  static fixHtml(sText,sFrom){
    if(!(sText?:'').size())
	  return ''
    def start=false
	def lsTags=[]
	switch(sFrom){
	  case 'admin': start=true;
        lsTags=['u','i','em','b','ol','ul','li','s','sub','sup','address','pre','p',
        'h1','h2','h3','h4','h5','h6','strong']
		break		
	  case 'personal':	
        if(Tools.getIntVal(Holders.config.editor.fixHtml)) start=true
        lsTags=['u','i','em','b','ol','ul','li','s','sub','sup','address','pre','p',
        'h1','h2','h3','h4','h5','h6','strong']
		break
	}	
	sText=sText.replace("\r",' ').replace("\n",' ').replace("'",'"')
	if(start){      
      sText=sText.replace("[YELLclose]",'').replace("[YELLspan]",'')
      sText=sText.replace('<br />','[YELLbr]')
      sText=sText.replace('<br>','[YELLbr]')
      sText=sText.replace('</span>','[/YELLspan]')
    
      sText=sText.replaceAll( /(<span )(style="[^\">]*?;")(>)/,'[YELLspan] $2[YELLclose]')
    
      for(sTag in lsTags) //TODO? change pre into p?
        sText=sText.replace('<'+sTag+'>','[YELL'+sTag+']').replace('</'+sTag+'>','[/YELL'+sTag+']')  
    
      sText=sText.replace('<',' &lt; ').replace('>',' &gt; ')
    
      for(sTag in lsTags) //TODO? change pre into p?
        sText=sText.replace('[YELL'+sTag+']','<'+sTag+'>').replace('[/YELL'+sTag+']','</'+sTag+'>')  
    
      sText=sText.replace('[YELLspan]','<span').replace('[YELLclose]','>').replace('[/YELLspan]','</span>')
      sText=sText.replace('[YELLbr]','<br />')      
    }
    return sText
  }  

  static String generateSMScode() {
    Random rand = new Random(System.currentTimeMillis())
    return (rand.nextInt().abs() % 89999 + 10000).toString() //10000..99999
  }

  static String generatePassword(passwordlength){
    Random RANDOM = new SecureRandom()
    def letters = "abcdefghjkmnpqrstuvwxyz"
    def firstLetters="ABCDEFGHJKMNPQRSTUVWXYZ"
    def digits="1234567890"

    def pw = ""
    def digitsPw = ""
    def firstLettersPw = ""
    //def passwordlength=Tools.getIntVal(Dynconfig.findByName('user.passwordlength')?.value,8)

    def firstLettersLength=1+RANDOM.nextInt(2)   
    def digitsLength=1+RANDOM.nextInt(2)    
    def lsFirst=[]
    
    for (int i=0; i<passwordlength; i++){
        int index = (int)(RANDOM.nextDouble()*letters.length());
        pw += letters.substring(index, index+1);
    }
    for (int i=0; i<firstLettersLength; i++){ 
        int index = (int)(RANDOM.nextDouble()*firstLetters.length());
        firstLettersPw += firstLetters.substring(index, index+1);
    }
    for (int i=0; i<digitsLength; i++){
        int index = (int)(RANDOM.nextDouble()*digits.length());
        digitsPw += digits.substring(index, index+1);
    }    
    
    for(int i=0;i<firstLettersLength;i++){
      def number=(int)(RANDOM.nextDouble()*pw.length())
      pw=Tools.replaceCharAt(pw,number,firstLettersPw.charAt(i))      
      lsFirst<<number
    }
    
    for(int i=0;i<digitsLength;i++){
      def number
      
      for(;;){ 
        number=(int)(RANDOM.nextDouble()*pw.length())
        if(!lsFirst.contains(number)) //condition to break, oppossite to while 
          break        
      }           
      pw=Tools.replaceCharAt(pw,number,digitsPw.charAt(i)) 
    }
    
    return pw;
  }
  
  public static String replaceCharAt(String s, int pos, char c) {
    StringBuffer buf = new StringBuffer( s );
    buf.setCharAt( pos, c );
    return buf.toString( );
  }

  static String generateModeParam(lId,lCId) {
    def i = 0
    return generateMD5('_yellcat'+lId+'somesalt'+lCId+'yellcat-').toCharArray().collect{++i; i%4?'':it}.join()
  }
  static String generateModeParam(lId) {
    return generateModeParam(lId,0)
  }

  static String generateSnils(lId,iType) {
    def snils = []
    lId.toString().reverse().eachWithIndex { it, index -> snils << it; index%3!=2?null:(snils << '-') }
    while(snils.size<11){
        snils.size%4==3? snils << '-' : null
        snils << '0'
    }
    snils.join().reverse()+' 0' + iType
  }

  static boolean checkIpRange(sIp){   
    def lsIp=sIp.split('\\.')
    if(lsIp.size()==4){  
      def ip_last=lsIp[3]
      def ipTmp=lsIp[0]+'.'+lsIp[1]+'.'+lsIp[2]      
                      
      def sUips=Dynconfig.findByName('allowed.ips')?.value?:''
      
      for(oUip in sUips.split(',')){      
        def sUip=oUip.split('\\.')
        if(sUip.size()==4){
          def sUip_main_ip_part=sUip[0]+'.'+sUip[1]+'.'+sUip[2]
          def sUip_start=sUip[3].split('-')[0]
          def sUip_end=sUip[3].split('-')[1]
       
          if(sUip_main_ip_part==ipTmp && sUip_start<=ip_last && sUip_end>=ip_last)
            return true
        }  
      }
    }          
    return false    
  }

  static String prepareUrl (sPhrase) {
    sPhrase.replace("–", '-').replace(":", '').replace("'", '').replace(')','').replace('(','').replace('\\','')
    .replace("\$", '').replace("^", '').replace("&", '').replace("<", '').replace(">", '').replace("|", '')
    .replace("=", '').replace("[", '').replace("]", '').replace("{", '').replace("}", '').replace('+','_')
    .replace('№','').replace('@','').replace('#','').replace('"','').replace(',','').replace(';','')
    .replace('.','').replace('%','').replace('?','').replace('!','').replace('/','').trim().replace(' ','_')
  }

  static String transliterate (sPhrase,bUrl=1) {
    def alpha = "абвгдеёжзиыйклмнопрстуфхцчшщьэюяъ"
    def _alpha = ["a","b","v","g","d","e","yo","zh","z","i","y","j",
                  "k","l","m","n","o","p","r","s","t","u",
                  "f","h","c","ch","sh","shh","","e","yu","ya",""]
    if(!bUrl){
      alpha+=alpha.toUpperCase()
      def _alphaTmp=[]
      for(_a in _alpha){
        _alphaTmp<<_a.capitalize()
      }
      for(_a in _alphaTmp){
          _alpha<<_a
      }
    }
    int k
    def result = ""
    def sPrepPhrase = (bUrl)?prepareUrl(sPhrase.toLowerCase()):sPhrase
    for(int i=0; i<(sPrepPhrase?:'').size();i++){
      k = alpha.indexOf(sPrepPhrase[i])
      if(k != -1)
        result += _alpha[k]
      else
        result += sPrepPhrase[i]
    }
    if(bUrl && result=='') result = "ts_"
    return result
  }

  static Integer computeMonthDiff(dStartdate, dEnddate){
    if (dEnddate<=dStartdate) return 0
    return ((dEnddate.getYear() - dStartdate.getYear())*12+(dEnddate.getMonth() - dStartdate.getMonth()))+1
  }

  static BigDecimal toFixed(BigDecimal _value, Integer precision){
    Long divider = Math.pow(10d,precision.toDouble()).toLong()
    return new BigDecimal(Math.rint(_value * divider).toBigInteger(),precision)
  }

  static Date parseISO8601date(String _date){
    try{
      return DU.parseDateStrictly(_date,["yyyy-MM-dd'T'HH:mm:ssXXX","yyyy-MM-dd'T'HH:mm:ss","yyyy-MM-dd"] as String[])
    } catch(Exception e) {
      return null
    }
  }

  static boolean checkSameDate(Date _date1, Date _date2){
    return DU.isSameDay(_date1,_date2)
  }
}