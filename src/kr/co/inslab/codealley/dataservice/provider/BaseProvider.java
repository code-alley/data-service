package kr.co.inslab.codealley.dataservice.provider;

/**
 * Provider 들의 기본 클래스
 *
 * @author  jdkim
 */
public class BaseProvider {

	String host;
	
	/**
	 * Host 존재여부 확인
	 * @return
	 */
	public boolean isHost(){
		
		return host != null ? true : false; 
	}
}
