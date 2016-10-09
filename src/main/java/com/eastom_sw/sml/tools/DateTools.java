package com.eastom_sw.sml.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * 
 * 时间封装类，用于时间的处理  
 * parse  对常用时间条件的处理基本可以解析适配常用的格式
 * @author hw
 *
 */
public class DateTools {
	public static final long DAY_TIME_MILLS = 24 * 60 * 60 * 1000;
	public static final long HOUR_TIME_MILLS = 60 * 60 * 1000;
	public static final SimpleDateFormat sdf = new SimpleDateFormat();
	public static final SimpleDateFormat sdf_mi = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat sdf_mis = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
	public static final SimpleDateFormat sdf_mi2 = new SimpleDateFormat("yyyyMMddHHmmss");
	public static final SimpleDateFormat sdf_dd = new SimpleDateFormat("yyyyMMdd");
	public static final SimpleDateFormat sdf_hh = new SimpleDateFormat("yyyyMMddHH");

	/** 格式化取整时间 */
	public static Date trunc(Date date, int type) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if (type == Calendar.DAY_OF_MONTH) {
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.MILLISECOND, 0);
		} else if (type == Calendar.HOUR_OF_DAY) {
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.MILLISECOND, 0);
			cal.set(Calendar.SECOND, 0);
		} else if (type == Calendar.MINUTE) {
			cal.set(Calendar.MILLISECOND, 0);
			cal.set(Calendar.SECOND, 0);
			cal.add(Calendar.MINUTE,-cal.get(Calendar.MINUTE)%10);//对分钟进行整十处理
		} else if (type == Calendar.MONTH) {
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.MILLISECOND, 0);
		}else if(type==Calendar.DAY_OF_WEEK){
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.MILLISECOND, 0);
			cal.set(Calendar.DAY_OF_WEEK,2);
		}
		return cal.getTime();
	}

	/** 增加天数 */
	public static Date addDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, days);
		return cal.getTime();
	}

	/** 增加小时数 */
	public static Date addHours(Date date, int hours) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR_OF_DAY, hours);
		return cal.getTime();
	}

	/** 增加月数 */
	public static Date addMonths(Date date, int months) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, months);
		return cal.getTime();
	}
	/**增加年数*/
	public static Date addYears(Date date,int years){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.YEAR, years);
		return cal.getTime();
	}
	/** 增加分钟数 */
	public static Date addMinutes(Date date, int minutes) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, minutes);
		return cal.getTime();
	}
	public static Date addSeconds(Date date, int seconds) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.SECOND, seconds);
		return cal.getTime();
	}
	public static Date addMills(Date date,int mills){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MILLISECOND,mills);
		return cal.getTime();
	}

	/** 将指定格式的时间字符串转化成时间格式 */
	public static Date parseDate(String timestamp, String... format) {
		String[] fms = format;
		SimpleDateFormat sdf = null;
		for (String fm : fms) {
			if (timestamp.length() == fm.length()) {
				sdf = new SimpleDateFormat(fm);
				try {
					return sdf.parse(timestamp);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	/**默认值的读取*/
	@SuppressWarnings("deprecation")
	public static String getDefaultStarttime(String type,int ... integer){
		if("week".equalsIgnoreCase(type)){
			return sdf_dd.format(addDays(trunc(new Date(), Calendar.DAY_OF_MONTH), -new Date().getDay()-6));
		}else if("day".equalsIgnoreCase(type)){
			return sdf_dd.format(addDays(new Date(),-1));
		}else if("minute".equalsIgnoreCase(type)){
			return sdf_mi2.format(addHours(trunc(new Date(), Calendar.MINUTE),-1));
		}else if("hour".equalsIgnoreCase(type)){
			return sdf_hh.format(addHours(trunc(new Date(), Calendar.HOUR),-2));
		}
		return "";
	}
	public static int getYear(Date date){
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
	}
	public static int getDay(Date date){
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_MONTH);
	}
	public static int getDayOfWeek(Date date){
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_WEEK);
	}
	public static int getMonth(Date date){
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.MONTH)+1;
	}
	@SuppressWarnings("deprecation")
	public static String getDefaultEndtime(String type){
		if("week".equalsIgnoreCase(type)){
			return sdf_dd.format(addDays(trunc(new Date(), Calendar.DAY_OF_MONTH), -new Date().getDay()));
		}else if("day".equalsIgnoreCase(type)){
			return sdf_dd.format(addDays(new Date(),-1));
		}else if("minute".equalsIgnoreCase(type)){
			return sdf_mi2.format(trunc(new Date(), Calendar.MINUTE));
		}else if("hour".equalsIgnoreCase(type)){
			return sdf_hh.format(addHours(trunc(new Date(), Calendar.HOUR),-1));
		}
		return "";
	}
	
	public static Date parse(String timestamp){
		if(timestamp==null||timestamp.trim().length()==0){
			return null;
		}
		if(timestamp.matches("\\d{13}")){
			return new Date(Long.parseLong(timestamp));
		}
		if(timestamp.matches("\\d{4}/\\d{1,2}/\\d{1,2}")){
			try {
				return new SimpleDateFormat("yyyy/MM/dd").parse(timestamp);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if(timestamp.matches("\\d{4}/\\d{1,2}/\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}")){
			try {
				return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(timestamp);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		timestamp=trim(timestamp," ","-",":","_","年","月","日","时","分","/",".","T");
		int length=timestamp.length();
		
		Date date=null;
		try {
			switch(length){
				case 5:date=new SimpleDateFormat("yyyyMM").parse(timestamp);break;
				case 6:date=new SimpleDateFormat("yyyyMM").parse(timestamp);break;
				case 7:date=new SimpleDateFormat("yyyyMMdd").parse(timestamp);break;
				case 8:date=new SimpleDateFormat("yyyyMMdd").parse(timestamp);break;
				case 9:date=new SimpleDateFormat("yyyyMMddHH").parse(timestamp);break;
				case 10:date=new SimpleDateFormat("yyyyMMddHH").parse(timestamp);break;
				case 11:date=new SimpleDateFormat("yyyyMMddHHmm").parse(timestamp);break;
				case 12:date=new SimpleDateFormat("yyyyMMddHHmm").parse(timestamp);break;
				case 13:date=new SimpleDateFormat("yyyyMMddHHmmss").parse(timestamp);break;
				case 14:date=new SimpleDateFormat("yyyyMMddHHmmss").parse(timestamp);break;
				case 15:date=new SimpleDateFormat("yyyyMMddHHmmssS").parse(timestamp);break;
				case 16:date=new SimpleDateFormat("yyyyMMddHHmmssS").parse(timestamp);break;
				case 17:date=new SimpleDateFormat("yyyyMMddHHmmssS").parse(timestamp);break;
		    }
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	public static Date getFormatTime(String time,String format){
		try {
			return new SimpleDateFormat(format).parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String getFormatTime(Date time,String format){
		if(time==null){
			return null;
		}
		return new SimpleDateFormat(format).format(time);
	}

	public static String trim(String datefomat,String ...strings){
		for(String str:strings){
			datefomat=datefomat.replace(str, "");
		}
		return datefomat;
	}
	public static List<String> buildTime(Date startTime,Date endTime,String format,int minute){
		List<String> result=new ArrayList<String>();
		Date temp=startTime;
		for(int i=0;i<10000;i++){
			result.add(getFormatTime(temp,format));
			temp=addMinutes(temp,minute);
			if(temp.after(endTime)) break;
		}
		return result;
	}
	public static Date parseFromString(String target,String mather,int index){
		List<String> mathers=RegexUtils.matchGroup(mather,target);
		if(mathers.size()<index){
			return null;
		}else{
			return parse(mathers.get(index));
		}
	}
	
	
}
