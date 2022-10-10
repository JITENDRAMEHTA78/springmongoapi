
package com.sixsprints.eclinic.util;

import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PhoneNumberUtil {

  public static String cleanNumber(String number) {
    String original = number;
    log.debug("Number before = " + number);
    if (StringUtils.isEmpty(number)) {
      return null;
    }
    number = number.replace(" ", "").replace("(", "").replace(")", "").replace("-", "");
    if (number.length() == 12 && allNumeric(number)) {
      number = "+" + number;
    }
    if (number.length() > 10 && number.startsWith("0")) {
      number = number.substring(1);
    }
    if (!number.startsWith("+")) {
      number = "+91" + number;
    }
    if (!allNumeric(number.substring(1))) {
      number = original;
    }
    log.debug("Number after = " + number);
    return number;
  }
  
  public static String cleanNumberWithoutCode(String number) {
	    String original = number;
	    log.debug("Number before = " + number);
	    if (StringUtils.isEmpty(number)) {
	      return null;
	    }
	    number = number.replace(" ", "").replace("(", "").replace(")", "").replace("-", "");
	    if (number.length() > 10 && number.startsWith("0")) {
	      number = number.substring(1);
	    }

	    if (!allNumeric(number.substring(1))) {
	      number = original;
	    }
	    log.debug("Number after = " + number);
	    return number;
	  }

  public static Boolean isPhoneNumber(String string) {
    string = cleanNumber(string);
    return string.length() == 13 && allNumeric(string.substring(1));
  }

  public static String mask(String phoneNumber) {
    phoneNumber = cleanNumber(phoneNumber);
    return phoneNumber.substring(0, 5) + "XXXX" + phoneNumber.substring(9);
  }

  private static boolean allNumeric(String string) {
    for (char c : string.toCharArray()) {
      if (c < '0' || c > '9') {
        return false;
      }
    }
    return true;
  }

}
