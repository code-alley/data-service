package kr.co.inslab.codealley.dataservice.config;


import javax.servlet.ServletContext;

import kr.co.inslab.codealley.dataservice.log.SLog;

/**
 * 서비스의 환경 변수 설정 클래스
 *
 * @author  jdkim
 */
public class Config {

	public static boolean isDebug = true;
	
	public static String CONTEXT_PATH 		= null;

	public static final String WIN_DIR = "\\";
	public static final String LINUX_DIR = "/";

	public static String DIR;
	
	public static boolean isWindowsOs;
	
	public static Config instance = null;
	
	/**
	 * Instance 생성
	 * @return
	 */
	public static Config getInstance(){
		if(instance == null)
			instance = new Config();
		
		return instance;
	}
	
	/**
	 * 초기 셋팅
	 * @param servletContext
	 */
	public void init(ServletContext servletContext) {
		
		CONTEXT_PATH	= servletContext.getContextPath();
		
		SLog.i("getContextPath : " + servletContext.getContextPath());
		
		String osName = System.getProperty("os.name");
		SLog.i("OS : " + osName);

		if(osName.toLowerCase().contains("window") )
		{
			isWindowsOs = true;
			DIR = WIN_DIR;
		}
		else
		{
			isWindowsOs = false;
			DIR = LINUX_DIR;
		}

	}
	
	
}
