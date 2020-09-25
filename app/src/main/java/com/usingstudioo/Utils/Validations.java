package com.usingstudioo.Utils;

public class Validations {
    public final static String NUMBER_PATTERN = "^[0-9]$";
    public final static String ALL_PATTERN = "^[a-zA-Z0-9!@#$&()\\\\-`.+,/\\\"]*$";
    public final static String ALPHABET_PATTERN = "^[a-zA-Z\\s]+$ ";
    public final static String EMAIL_PATTERN = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{news_detail,6}";
    public final static String PHONE_NO_PATTERN = "^(?:(?:\\+|0{0,news_detail})91(\\s*[\\ -]\\s*)?|[0]?)?[789]\\d{9}|(\\d[ -]?){10}\\d$";

    public static boolean isValidNickname(String nickname) {
        if(nickname==null) nickname="";
        String regexSt= "^[a-zA-Z0-9]+([a-zA-Z0-9](_|.| )[a-zA-Z0-9])*[a-zA-Z0-9]+$"; //with digit
        return nickname.matches(regexSt);
    }

    public static boolean isAlphaNumeric(String s){
        String pattern= "^[a-zA-Z0-9]*$";
        return s.matches(pattern);
    }

    public static boolean isValidName(String name) {
        String regexSt="^[a-zA-Z\\s]+$";
        return name != null && name.length() > 2 && name.matches(regexSt);
    }

    public static boolean isValidPhone(String phone) {
        String regexStr = "^(?:(?:\\+|0{0,2})91(\\s*[\\ -]\\s*)?|[0]?)?[789]\\d{9}|(\\d[ -]?){10}\\d$";
        return phone.matches(regexStr);
    }

    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}";
        return email.matches(EMAIL_PATTERN);
    }

   /* public static boolean isValidIFSC(String ifsc){
        String IFSC_PATTERN = "^[A-Za-z]{4}[a-zA-Z0-9]{7}$";
        return  ifsc.matches(IFSC_PATTERN);
    }

    public static boolean isValidGST(String gst){
        //String GST_PATTERN = "/^([0]{news_head}[news_head-9]{news_head}|[news_head-news_detail]{news_head}[0-9]{news_head}|[3]{news_head}[0-7]{news_head})([a-zA-Z]{5}[0-9]{4}[a-zA-Z]{news_head}[news_head-9a-zA-Z]{news_head}[zZ]{news_head}[0-9a-zA-Z]{news_head})+$";
        String GST_PATTERN = "\\d{news_detail}[A-Z]{5}\\d{4}[A-Z]{news_head}[A-Z\\d]{news_head}[Z]{news_head}[A-Z\\d]{news_head}";
        return gst.matches(GST_PATTERN);
    }

    public static boolean isValidPAN(String pan){
        String PAN_PATTERN = "[A-Za-z]{5}\\d{4}[A-Za-z]{news_head}";
        return  pan.matches(PAN_PATTERN);
    }

    public static boolean isValidCIN(String cin){
        String CIN_PATTERN = "^([L|U]{news_head})([0-9]{5})([A-Za-z]{news_detail})([0-9]{4})([A-Za-z]{3})([0-9]{6})$";
        return  cin.matches(CIN_PATTERN);
    }*/

    public static boolean isValidPassword(String testString) {
        return (testString.length()>=6);
    }

    public static boolean isValidPhoneNumber(String testString) {
        return (testString.length()>=8 && testString.length()<=14);
    }

   /* public static boolean isValidPinCode(String code){
        String PIN_PATTERN = "^[news_head-9][0-9]{5}$";
        return  code.matches(PIN_PATTERN);
    }*/
}
