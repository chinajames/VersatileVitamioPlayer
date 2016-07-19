package com.app;

/**
 * Created by hanjiahu on 16/7/19.
 */
import android.text.TextUtils;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StringUtils {
  protected static final DecimalFormat dFormat = new DecimalFormat("#0.0");
  private static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

  public StringUtils() {
  }

  public static String encoding(String str) {
    return isEmpty(str)?"":URLEncoder.encode(str);
  }

  public static String decoding(String str) {
    return isEmpty(str)?"":URLDecoder.decode(str);
  }

  public static String maskNull(String str) {
    return isEmpty(str)?"":str;
  }

  public static final String maskUrl(String strUrl) {
    if(TextUtils.isEmpty(strUrl)) {
      return "";
    } else {
      String url = strUrl.trim().replaceAll("&amp;", "&");
      url = url.replaceAll(" ", "%20").trim();
      if(TextUtils.isEmpty(url)) {
        return "";
      } else if(!url.startsWith("http://") && !url.startsWith("https://")) {
        url = "http://" + url;
        return url;
      } else {
        return url;
      }
    }
  }

  public static boolean isEmpty(String str) {
    return null != str && !"".equals(str) && !"null".equals(str)?(str.length() > 4?false:str.equalsIgnoreCase("null")):true;
  }

  public static boolean isEmpty(int num) {
    return isEmpty(num, 0);
  }

  public static boolean isEmpty(int num, int defauleNum) {
    return num == defauleNum;
  }


  public static int toInt(Object _obj, int _defaultValue) {
    if(TextUtils.isEmpty(String.valueOf(_obj))) {
      return _defaultValue;
    } else {
      try {
        return Integer.parseInt(String.valueOf(_obj));
      } catch (Exception var3) {
        return _defaultValue;
      }
    }
  }

  public static boolean toBoolean(Object _obj, boolean _defaultValue) {
    if(TextUtils.isEmpty(String.valueOf(_obj))) {
      return _defaultValue;
    } else {
      try {
        return Boolean.parseBoolean(String.valueOf(_obj));
      } catch (Exception var3) {
        return _defaultValue;
      }
    }
  }

  public static int getInt(String intString, int defaultValue) {
    try {
      if(!isEmpty(intString)) {
        defaultValue = Integer.parseInt(intString);
      }
    } catch (Exception var3) {
      var3.printStackTrace();
    }

    return defaultValue;
  }

  public static final float toFloat(Object _obj, float _defaultValue) {
    if(isEmpty(String.valueOf(_obj))) {
      return _defaultValue;
    } else {
      try {
        return Float.parseFloat(String.valueOf(_obj));
      } catch (Exception var3) {
        return _defaultValue;
      }
    }
  }

  public static final double toDouble(Object _obj, double _defaultValue) {
    if(isEmpty(String.valueOf(_obj))) {
      return _defaultValue;
    } else {
      try {
        return Double.parseDouble(String.valueOf(_obj));
      } catch (Exception var4) {
        return _defaultValue;
      }
    }
  }

  public static final String decimalFormat(Object _obj, double _defaultValue) {
    return dFormat.format(toDouble(_obj, _defaultValue));
  }

  public static final long toLong(Object _obj, long _defaultValue) {
    if(isEmpty(String.valueOf(_obj))) {
      return _defaultValue;
    } else {
      try {
        return Long.parseLong(String.valueOf(_obj));
      } catch (Exception var4) {
        return _defaultValue;
      }
    }
  }

  public static boolean isEmptyList(List<?> list) {
    return null == list || list.size() == 0;
  }

  public static boolean isEmpty(Collection<?> list) {
    return isEmpty((Collection)list, 1);
  }

  public static boolean isEmpty(Map<?, ?> map) {
    return isEmpty((Map)map, 1);
  }

  public static boolean isEmpty(Collection<?> list, int len) {
    return null == list || list.size() < len;
  }

  public static boolean isEmpty(Map<?, ?> map, int len) {
    return null == map || map.size() < len;
  }

  public static boolean isEmptyList(List<?> list, int len) {
    return null == list || list.size() < len;
  }

  public static boolean isEmptyMap(Map<?, ?> map) {
    return null == map || map.size() == 0;
  }

  public static boolean isEmptyArray(Object[] array) {
    return isEmptyArray(array, 1);
  }

  public static boolean isEmptyArray(Object array) {
    return null == array;
  }

  public static boolean isEmptyArray(Object[] array, int len) {
    return null == array || array.length < len;
  }

  public static String dateString2String(String str, String format) {
    try {
      return dataFormat((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(str), format);
    } catch (ParseException var3) {
      var3.printStackTrace();
      return "";
    }
  }

  public static Date string2Date(String date) throws ParseException {
    return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(date);
  }

  public static Date string2Date(String date, String format) throws ParseException {
    return isEmpty(format)?string2Date(date):(new SimpleDateFormat(format)).parse(date);
  }

  public static String dataFormat(Date date, String format) {
    return null == date?"":(new SimpleDateFormat(format)).format(date);
  }

  public static String dateFormat(Date date) {
    return dataFormat(date, "yyyy-MM-dd HH:mm:ss");
  }

  public static Double string2DoubleScale(String str, int scale) {
    return Double.valueOf(isEmpty(str)?0.0D:(new BigDecimal(str)).setScale(2, 4).doubleValue() / 100.0D);
  }

  public static Integer toDoubleScale(String str, int scale) {
    if(!isEmpty(str) && scale >= 1) {
      Double _double = Double.valueOf((new BigDecimal(str)).setScale(scale, 4).doubleValue() * 100.0D);
      return Integer.valueOf(_double.intValue());
    } else {
      return Integer.valueOf(0);
    }
  }

  public static String byte2XB(long b) {
    long i = 1024L;
    if(b < i) {
      return b + "B";
    } else {
      i = 1048576L;
      if(b < i) {
        return calXB(1.0F * (float)b / 1024.0F) + "K";
      } else {
        i = 1073741824L;
        if(b < i) {
          return calXB(1.0F * (float)b / 1048576.0F) + "M";
        } else {
          i = 1099511627776L;
          return b < i?calXB(1.0F * (float)b / 1.07374182E9F) + "G":(b < i?calXB(1.0F * (float)b / 1.09951163E12F) + "T":b + "B");
        }
      }
    }
  }

  public static String cleanperiod(String str) {
    return str != null & str.length() >= 1?(str.endsWith("期")?(String)str.subSequence(0, str.length() - 1):str):str;
  }

  public static String getDate(String str) {
    if(str != null && !str.equals("")) {
      String[] subStr = str.split(" ");
      return subStr[0];
    } else {
      return str;
    }
  }

  public static String calXB(float r) {
    String result = r + "";
    int index = result.indexOf(".");
    String s = result.substring(0, index + 1);
    String n = result.substring(index + 1);
    if(n.length() >= 1) {
      n = n.substring(0, 1);
    }

    return s + n;
  }

  public static String stringForTime(int timeMs) {
    StringBuilder formatBuilder = new StringBuilder();
    Formatter formatter = new Formatter(formatBuilder, Locale.getDefault());
    String result = null;
    int totalSeconds = timeMs / 1000;
    int seconds = totalSeconds % 60;
    int minutes = totalSeconds / 60 % 60;
    int hours = totalSeconds / 3600;
    if(hours > 0) {
      result = formatter.format("%02d:%02d:%02d", new Object[]{Integer.valueOf(hours), Integer.valueOf(minutes), Integer.valueOf(seconds)}).toString();
    } else {
      result = formatter.format("%02d:%02d", new Object[]{Integer.valueOf(minutes), Integer.valueOf(seconds)}).toString();
    }

    formatter.close();
    return result;
  }

  public static String removeBlankAndN(String str) {
    if(!isEmpty(str)) {
      str = str.replace("\n", "").replace("\t", "").trim();
    }

    return str;
  }

  public static boolean isInteger(String s) {
    if(isEmpty(s)) {
      return false;
    } else {
      for(int i = 0; i < s.length(); ++i) {
        if(i == 0 && s.charAt(i) == 45) {
          if(s.length() == 1) {
            return false;
          }
        } else if(Character.digit(s.charAt(i), 10) < 0) {
          return false;
        }
      }

      return true;
    }
  }

}
