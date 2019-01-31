package kr.co.inslab.codealley.dataservice.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import kr.co.inslab.codealley.dataservice.common.Const;
import kr.co.inslab.codealley.dataservice.log.SLog;

/**
 * 요청에 대한 응답정보를 처리하는 클래스
 * @author jdkim
 *
 */
public class ResponseHandler {

	/**
	 * 응답정보 처리, JSONP supported
	 * @param request
	 * @param response
	 * @param result
	 */
	public void setResult(HttpServletRequest request, HttpServletResponse response, JSONObject result) {
		
		String callback = request.getParameter("callback");
		String resultStr;
		if(callback != null)
		{
			resultStr = callback + "(" + result.toString() + ")";
		}
		else
			resultStr = result.toString();
		
		SLog.d(request.getRequestURI() + " >>> " + resultStr);
		
		response.setContentType(Const.CONTENT_TYPE_JSON);
		response.setCharacterEncoding("UTF-8");
		
		PrintWriter out;
		try {
			out = response.getWriter();
			
			out.print(resultStr);
			out.flush();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 응답정보 처리, JSONP supported
	 * @param request
	 * @param response
	 * @param contentType
	 * @param bSuccess
	 * @param params
	 * @param errorMessage
	 */
	public void setResult(HttpServletRequest request, HttpServletResponse response,
			String contentType, boolean bSuccess, Map<String, Object> params, String errorMessage) {
		
		if(params == null)
			params = new HashMap<String, Object>();
		
		//params.put(Const.JSON_KEY_REQUEST_URI, request.getRequestURI());
		//params.put(Const.JSON_KEY_REQUEST_METHOD, request.getMethod());
		
		if(contentType == null)
			response.setContentType(Const.CONTENT_TYPE_JSON);
		else
			response.setContentType(contentType);
		
		PrintWriter out;
		try {
			out = response.getWriter();
			
			out.print(getResponseMsg(request, bSuccess, params, errorMessage));
			out.flush();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * 응답 메세지 처리
	 * @param request
	 * @param bSuccess
	 * @param params
	 * @param errorMessage
	 * @return
	 */
	private String getResponseMsg(HttpServletRequest request, boolean bSuccess, Map<String, Object> params,
			String errorMessage) {

		JSONObject resultJson = new JSONObject();
		resultJson.put("result", bSuccess);
		
		String key;
		if(params != null){
			Iterator<String> iter = params.keySet().iterator();
			while(iter.hasNext()){
				key = iter.next();
				resultJson.put(key, params.get(key));
			}
		}
		
		if(errorMessage !=  null){
			resultJson.put(Const.JSON_KEY_ERROR, errorMessage);
		}
		
		String resultMsg = null;
		
		/*
		if(bSuccess)
		{
			resultMsg = successMsg != null ? successMsg : SUCCESS_MSG;
		}
		else
		{	
			if(errorMessage == null)
				errorMessage = "";

			resultMsg = String.format(FAIL_MSG, errorMessage);
		}
		*/

		String callback = request.getParameter("callback");
		if(callback != null)
		{
			resultMsg = callback + "(" + resultJson.toString() + ")";
		}
		else
			resultMsg = resultJson.toString();
		
		SLog.d(request.getRequestURI() + " >>> " + resultMsg);
		
		
		return resultMsg;
	}
}
