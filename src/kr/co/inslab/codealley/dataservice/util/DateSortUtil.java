package kr.co.inslab.codealley.dataservice.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import kr.co.inslab.codealley.dataservice.log.SLog;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 날짜, 시간별 정렬 처리 관련 클래스
 *
 * @author  jdkim
 */
public class DateSortUtil {

	ArrayList<JSONObject> list = new ArrayList<JSONObject>();
	public static final String ASC = "asc";		//오름순
	public static final String DES = "dec";		//내림순
	
	/**
	 * Json 객체를 날짜,시간별로 정렬(오름차순)
	 * @param testsResult
	 * @param dateKey
	 * @param dateFormat
	 */
	public void sort(JSONArray testsResult, String dateKey, String dateFormat) {
		
		if(dateFormat == null)
			dateFormat = "yyyy-MM-dd HH:mm:ss";
		
		SLog.d(">>>>>>>>>>> JsonArray length : " + testsResult.length());
		
		try{
			
			for( int index=0 ; index<testsResult.length() ; index++ )
			{
				JSONObject obj = (JSONObject) testsResult.get(index);
				
				if(list.size() == 0)
				{	
					list.add(obj);
					continue;
				}
				
				String cmpDate = obj.getString(dateKey);
				
				calcAboutTime(cmpDate, dateFormat);
				
				for( int j=0; j<list.size() ; j++ )
				{
					JSONObject cmpObj = list.get(j);
					
					String srcDate = (String) cmpObj.get(dateKey);
					
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
					
					Date sdate = simpleDateFormat.parse(srcDate);
		            Date cdate = simpleDateFormat.parse(cmpDate);
		            
		            int compare = sdate.compareTo(cdate);
		            if(compare > 0)
		            {	
//		            	SLog.d(" sdate > cdate " + simpleDateFormat.format(sdate) + ">" + simpleDateFormat.format(cdate));
//		            	list.add(j, obj);
//		            	break;
		            }
		            else if(compare < 0)
		            {	
		            	SLog.d(" sdate < cdate " +  simpleDateFormat.format(sdate) + "<" + simpleDateFormat.format(cdate));
		            	list.add(j, obj);
		            	
		            	//printDate(dateKey);
		            	break;
		            }
		            else
		            {
		            	SLog.d(" sdate = cdate " + simpleDateFormat.format(sdate) + ">" + simpleDateFormat.format(cdate));
		            }
		            
		            if(j == list.size()-1)
		            {
		            	list.add( obj);
		            	break;
		            }
		            
		            //System.out.println("date : "+simpleDateFormat.format(sdate));
		            //System.out.println("date : "+simpleDateFormat.format(cdate));
		            
		            
				}
				
			}
			
		}
		catch(ParseException e)
		{
			e.printStackTrace();
		}
		
		//SLog.d("\n\n ========================================== result");
		//printDate(dateKey);
		
	}
	
	/**
	 * 시간 단위별 계산
	 * @param dateStr
	 * @param dateFormat
	 */
	public void calcAboutTime(String dateStr, String dateFormat){
		
		try {
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
			Date sdate = simpleDateFormat.parse(dateStr);
			//Date curDate = Calendar.getInstance().getTime();
			
			long diff = Calendar.getInstance().getTime().getTime() - sdate.getTime();
			
			SLog.d("초 >> " + diff / 1000  );
			SLog.d("분 >> " + diff / (60*1000));
			SLog.d("시 >> " + diff / (60*60*1000));
			SLog.d("일 >> " + diff / (24*60*60*1000));
			
			int months 	= (int)(diff / (31*24*60*60*1000));
			int days 	= (int)(diff / (24*60*60*1000));
			int hours 	= (int)(diff / (60*60*1000));
			int minutes = (int)(diff / (60*1000));
			
			SLog.d("months" + months);
			SLog.d("days" + days);
			SLog.d("hours" +hours);
			SLog.d("minutes" + minutes);
			
			
			if(diff < 1000)
				SLog.d("just now...");
			
			if( (diff / (24*60*60*1000)) > 31)
			{
				
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public void calcAboutTime(Date date){
		
		
	}
	
	/**
	 * 날짜 console 출력
	 * @param key
	 */
	public void printDate(String key)
	{
		SLog.d(">>>>>>>>>>> ArrayList length(result) : " + list.size());
		
		for( int j=0; j<list.size() ; j++ )
		{
			JSONObject obj = list.get(j);
			String date = (String) obj.get(key);
			System.out.println(date);
			
		}
		
		
	}

}
