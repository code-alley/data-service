package kr.co.inslab.codealley.dataservice.provider;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import kr.co.inslab.codealley.dataservice.common.Const;
import kr.co.inslab.codealley.dataservice.log.SLog;

/**
 * ReviewBoard의 WEB API를 이용하여 data획득 클래스 
 *
 * @author  jdkim
 */
public class ReviewBoardProvider extends BaseProvider{

	private final String authId = "USERNAME";
	private final String authPasswd = "PASSWORD";
	
	//"http://review-board.cloudapp.net/reviewboard/api/repositories/"
	public String authFormat = "%s/api/";
	public String reposFormat 	= "%s/api/repositories/";
	public String reviewsFormat = "%s/api/review-requests/?status=all";
	public String reviewCountFormat = "%s/api/review-requests/?status=%s&counts-only=true"; //One of all, discarded, pending, submitted
	
	/**
	 * 생성자
	 * @param _host
	 */
	public ReviewBoardProvider(String _host){
		host = _host;
		
	}
	
	/**
	 * 코드리뷰 대상 Repository
	 * @return
	 */
	public JSONObject getRepositories() {
		JSONObject result = new JSONObject();
		
		if(!isHost())	return result.put(Const.JSON_KEY_ERROR, "host is net set!");
		
		String url = String.format(reposFormat, host);
		
		HttpResponse<JsonNode> jsonResponse;
		try {
			SLog.d("getRepositories", url);
			
			jsonResponse = Unirest.get(url).asJson();
			JSONObject obj = jsonResponse.getBody().getObject();
			SLog.d("response", obj.toString());
			
			StringBuilder sb = new StringBuilder();
			
			JSONArray repos = (JSONArray) obj.get("repositories");
			JSONArray reposNew = new JSONArray();
			for(int i=0;i<repos.length();i++){
			
				JSONObject repoObj = (JSONObject) repos.get(i);
				String repoName = (String) repoObj.get("name");
				SLog.d("repoName", repoName);
				
				JSONObject objNew = new JSONObject();
				objNew.put("name", repoName);
				reposNew.put(objNew);
			}
			
			result.put("repositories", reposNew);
			
			/*
			Iterator keys = obj.keys();
			String key;
			ArrayList<String> repositories = new ArrayList<String>();
			while (keys.hasNext()) {
				
				key = (String) keys.next();
				JSONObject repoObj = (JSONObject) obj.get(key);
				SLog.d("repository name", repoObj.getString("name"));
				sb.append(repoObj.getString("name"));
				if(keys.hasNext())
					sb.append("|");
			}
			
			
			result.put(Parameter.VALUE_REPOSITORIES, sb.toString());
			return result;
			*/
			
		} catch (UnirestException e) {
			e.printStackTrace();
			result.put(Const.JSON_KEY_ERROR, e.getLocalizedMessage());
		}
		
		return result;
		
	}
	
	/**
	 * WEB API 사용자 인증 처리(Basic Auth)
	 */
	public void basicAuth(){
		String url = String.format(authFormat, host);
		HttpResponse<JsonNode> response = null;
		try {
			response = Unirest.get(url).basicAuth(authId, authPasswd).asJson();
			JSONObject root = response.getBody().getObject();
			
			SLog.d("BasicAuth", root.toString());
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * 진행중인 리뷰 리스트
	 * @param repoName
	 * @return
	 */
	public JSONObject getReviews(String repoName) {
		basicAuth();
		
		JSONObject result = new JSONObject();
		
		if(!isHost())	return result.put(Const.JSON_KEY_ERROR, "host is net set!");
		
		String url = String.format(reviewsFormat, host);
		
		HttpResponse<JsonNode> jsonResponse;
		try {
			SLog.d("getReviews", url);
			
			jsonResponse = Unirest.get(url).asJson();
			JSONObject root = jsonResponse.getBody().getObject();
			SLog.d("response", root.toString());
			
			JSONArray reviewsNew = new JSONArray();
			JSONArray reviews = (JSONArray) root.get("review_requests");
			for(int i=0;i<reviews.length();i++)
			{
				JSONObject obj = (JSONObject) reviews.get(i);
				
				//JSONObject review_requests = (JSONObject) obj.get("review_requests");
				JSONObject links = (JSONObject) obj.get("links");
				JSONObject repository = links.has("repository") ? (JSONObject) links.get("repository") : new JSONObject();
				
				String repoTitle 		= repository.has("title") ? repository.getString("title") : "";
				String status 			= obj.getString("status");
				String absolute_url 	= obj.getString("absolute_url");
				String time_added		= obj.getString("time_added");
				String summary			= obj.getString("summary");
				String last_updated		= obj.getString("last_updated");
				
				if(repoName == null || repoTitle.equalsIgnoreCase(repoName) || repoName.equalsIgnoreCase("all")){
					JSONObject reviewObj = new JSONObject();
					reviewObj.put("summary", summary);
					reviewObj.put("repository", repoTitle);
					reviewObj.put("status", status);
					reviewObj.put("url", absolute_url);
					reviewObj.put("last_updated", last_updated);
					
					reviewsNew.put(reviewObj);
				}
				
				
			}
			result.put("reviews", reviewsNew);
			
		} catch (UnirestException e) {
			e.printStackTrace();
			result.put(Const.JSON_KEY_ERROR, e.getLocalizedMessage());
		}
		
		return result;
	}
	
	/**
	 * 상태별 리뷰 카운트 조회
	 * @param status
	 * @return
	 */
	public JSONObject getReviewCount(String status)
	{
		basicAuth();
		
		JSONObject result = new JSONObject();
		try
		{
			if(!isHost())	return result.put(Const.JSON_KEY_ERROR, "host is net set!");
			
			String url = String.format(reviewCountFormat, host, status.toLowerCase());
			
			HttpResponse<JsonNode> jsonResponse;
			SLog.d("getReviewCount req", url);
			
			jsonResponse = Unirest.get(url).asJson();
			JSONObject root = jsonResponse.getBody().getObject();
			SLog.d("getReviewCount resp", root.toString());
			
			result.put("status", status);
			result.put("count", root.getInt("count"));
			
		} catch (UnirestException | JSONException e) {
			e.printStackTrace();
			result.put(Const.JSON_KEY_ERROR, e.getLocalizedMessage());
		}
		return result;
	}
	
	
}
