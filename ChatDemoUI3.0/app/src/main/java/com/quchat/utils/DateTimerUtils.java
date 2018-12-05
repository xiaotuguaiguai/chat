package com.quchat.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class DateTimerUtils {

	public static String getTimer(long datatimer) {
		int hour = (int) (datatimer / (60 * 60));
		if (hour >= 24) {
			int days = hour / 24;
			int i = hour % 24;
			if (i > 0) {
				days = days + 1;
			}

			return   days + "天";
		} else {
			return  (hour > 0 ? hour : 1) + "小时";
		}
	}

	public static String  getDateUseFormat(long times,String format){
		String date = new SimpleDateFormat(format).format(times);
		return date;
	}
	public static String strToDateLong(String birthday, String format) {
		if(birthday==null){
			return "未知";
		}
		if("0".equals(birthday)){
			return "未知";
		}
		Long timestamp = Long.parseLong(birthday) * 1000;
		String date = new SimpleDateFormat(format)
				.format(new java.util.Date(timestamp));
		return date;
	}

	private static final int SECONDS_IN_DAY = 60 * 60 * 24;
	private static final long MILLIS_IN_DAY = SECONDS_IN_DAY;

	public static boolean isSameDayOfMillis(final long ms1, final long ms2) {
		final long interval = ms1 - ms2;
		return interval < MILLIS_IN_DAY && interval > -1L * MILLIS_IN_DAY;
//				&& toDay(ms1) == toDay(ms2);
	}

	private static long toDay(long millis) {
		return (millis + TimeZone.getDefault().getOffset(millis))
				/ MILLIS_IN_DAY;
	}

	/**
	 * 当天24小时的时间
	 * @return
     */
	public static int getTimesnight(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 24);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return (int) (cal.getTimeInMillis()/1000);
	}

	/***
	 * 获得多长时间之前发布的商品
	 * @param timer
	 * @return
	 */
	public static String getTimerForGuissue(long timer){
		String str ;
		if(timer<=0){
			str = "0分钟前";
		}else{
			int minute = (int) (timer/60);
			int hour = (int) (timer/60/60);
			int day = (int) (timer/60/60/24);
			if(day>0){
				str = day+"天前";
			}else if(hour>0){
				str = hour+"小时前";
			}else{
				str = minute+"分钟前";
			}
		}
		return str;
	};

public static String getWhatTime(long time1){
	long time = System.currentTimeMillis()/1000 - time1;
	if(time<60){
		return "刚刚";
	}else if(time<60*60){
		return (time/60+"分钟前");
	}else if(time<60*60*24){
		return(time/60/60+"小时前");
	}else if(time<60*60*48){
		return("昨天");
	}else {
		String nowyear = DateTimerUtils.getDateUseFormat(System.currentTimeMillis(),"yyyy");
		String dateyear = DateTimerUtils.getDateUseFormat(time1*1000,"yyyy");
		if(nowyear.equals(dateyear)) {
			return (DateTimerUtils.strToDateLong(String.valueOf(time1), "M月d日"));
		}else {
			return (DateTimerUtils.strToDateLong(String.valueOf(time1), "yyyy年M月d日"));
		}
	}
}

}
