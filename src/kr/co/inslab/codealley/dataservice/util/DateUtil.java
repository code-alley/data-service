package kr.co.inslab.codealley.dataservice.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 현재 날짜, 시간 추출 클래스
 *
 * @author  jdkim
 */
public class DateUtil {

	/**
	 * 현재 시간 리턴 (형식: yyyy-MM-dd HH:mm)
	 * @return
	 */
	public static String currentTime(){
		DateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date nowDate = new Date();
		return sdFormat.format(nowDate);
	}
	
	/**
	 * 현재 시간 리턴
	 * @param fotmat
	 * @return
	 */
	public static String currentTime(String fotmat){
		DateFormat sdFormat = new SimpleDateFormat(fotmat);
		Date nowDate = new Date();
		return sdFormat.format(nowDate);
	}
	
}
