package kr.co.inslab.codealley.dataservice.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import kr.co.inslab.codealley.dataservice.common.Const;
import kr.co.inslab.codealley.dataservice.log.SLog;

/*
--- 이슈 리스트 
 {
    "issues": [
        {
            "id": 2,
            "project": {
                "id": 1,
                "name": "first project"
            },
            "tracker": {
                "id": 1,
                "name": "Bug"
            },
            "status": {
                "id": 2,
                "name": "In Progress"
            },
            "priority": {
                "id": 2,
                "name": "Normal"
            },
            "author": {
                "id": 1,
                "name": "Redmine Admin"
            },
            "assigned_to": {
                "id": 1,
                "name": "Redmine Admin"
            },
            "subject": "bug : unknown host",
            "description": "unknown host....desc",
            "start_date": "2014-12-17",
            "done_ratio": 0,
            "created_on": "2014-12-17T02:41:20Z",
            "updated_on": "2014-12-17T02:42:41Z"
        }
    ],
    "total_count": 1,
    "offset": 0,
    "limit": 25
}

-- 이슈 상태 리스트
{
    "issue_statuses": [
        {
            "id": 1,
            "name": "New",
            "is_default": true
        },
        {
            "id": 2,
            "name": "In Progress"
        },
        {
            "id": 3,
            "name": "Resolved"
        },
        {
            "id": 4,
            "name": "Feedback"
        },
        {
            "id": 5,
            "name": "Closed",
            "is_closed": true
        },
        {
            "id": 6,
            "name": "Rejected",
            "is_closed": true
        }
    ]
}
 */

/**
 * Redmine의 WEB API를 이용하여 data획득 클래스 
 * @author jdkim
 *
 */
public class RedmineProvider extends BaseProvider{

	String issueFormat = "%s/issues.json?limit=20&status_id=%s&key=%s";		// 상태별 이슈 
	//"http://jdkim-test.cloudapp.net:3000/issues.json?status_id=2"; status_id는 상태별 필터
	String linkFormat = "%s/issues";
	
	String issueStatusFormat = "%s/issue_statuses.json"; //상태종류
	String projectsFormat = "%s/projects.json"; //프로젝트 리스트
	String projectIssueFormat = "%s/issues.json?limit=20&project_id=%s&key=%s"; //프로젝트별 이슈
	
	String apikey;
	/**
	 * 생성자
	 * @param url
	 */
	public RedmineProvider(String url, String apikey){
		host = url;
		this.apikey = apikey;
	}
	
	/**
	 * 이슈리스트
	 * @param status : Redmine 에 등록된 status id
	 * @param projectId : Redmine 에 등록된 project id
	 * @return
	 */
	public JSONObject getIssues(String status, String projectId){
		JSONObject result = new JSONObject();
		
		if(!isHost())	return result.put(Const.JSON_KEY_ERROR, "host is not set!");
		
		if(status == null && projectId == null) return result.put(Const.JSON_KEY_ERROR, "status and project id not exist!");
		
		String url;
		
		//프로젝트ID 가 넘어오면 프로젝트별 이슈를 조회한다
		if(projectId != null && !projectId.equals("0")) {
			url = String.format(projectIssueFormat, host, projectId, apikey);
			
		} else {
			if(status == null || status.equalsIgnoreCase("0"))
				url = String.format(issueFormat, host, "*", apikey);
			else
				url = String.format(issueFormat, host, status, apikey);
		}
		
		String link = String.format(linkFormat, host);
		
		HttpResponse<JsonNode> jsonResponse;
		try {
			SLog.d("getIssues", url);
			
			jsonResponse = Unirest.get(url).asJson();
			JSONObject root = jsonResponse.getBody().getObject();
			SLog.d("response", root.toString());
			
			JSONArray issues = root.has("issues") ? root.getJSONArray("issues") : new JSONArray();
			
			JSONArray issuesNew = new JSONArray();
			
			JSONObject item;
			JSONObject newItem;
			for(int i=0;i<issues.length();i++)
			{
				newItem = new JSONObject();
				
				item = (JSONObject) issues.get(i);
				
				newItem.put("project"	, getString(item, "project"));
				newItem.put("tracker"	, getString(item, "tracker"));
				newItem.put("status"	, getString(item, "status"));
				newItem.put("priority"	, getString(item, "priority"));
				newItem.put("author"	, getString(item, "author"));
				//newItem.put("assigned"	, getString(item, "assigned_to"));
				newItem.put("subject"	, item.getString("subject"));
				newItem.put("description"	, item.getString("description"));
				newItem.put("created"	, item.getString("created_on"));
				newItem.put("updated"	, item.getString("updated_on"));
				newItem.put("link", link + "/" + item.getInt("id"));
				issuesNew.put(newItem);
			}
			
			result.put("total_count", root.has("total_count") ? root.get("total_count") : 0);
			result.put("issues", issuesNew);
			result.put("projects", this.getProjects());
			
			return result;
			
			
		} catch (UnirestException | JSONException e) {
			e.printStackTrace();
			result.put(Const.JSON_KEY_ERROR, e.getLocalizedMessage());
		}
		
		return result;
	}
	
	/**
	 * 상태별 이슈 갯수를 얻는다.
	 * @param status
	 * @return
	 */
	public JSONObject getIssueCount(String status) {
		JSONObject result = new JSONObject();
		try
		{
			List<String> statusIds = this.convertStatusNameToId(status);

			int issueCount = 0;
			
			for(String statusId : statusIds) {
				JSONObject issueResult = getIssues(statusId, null);
				JSONArray issues = issueResult.getJSONArray("issues");
				
				issueCount+=issues.length();
			}
			
			result.put("count", issueCount);
			
			if(status == null)
				status = "0"; // 0 is all
			
			result.put("status", status);
		}
		catch(JSONException e)
		{
			e.printStackTrace();
			result.put(Const.JSON_KEY_ERROR, e.getLocalizedMessage());
		}
		
		return result;
	}
	
	
	
	/**
	 * 하위 JSONObject의 name 값 얻기
	 * "assigned_to": {
                "id": 1,
                "name": "Redmine Admin"
            },
	 * @param obj
	 * @param key
	 * @return
	 */
	public String getString(JSONObject obj, String key)
	{
		if(obj == null) return null;
		
		JSONObject sub = obj.getJSONObject(key);
		
		if(sub == null) return null;
		
		return sub.getString("name");
	}
	
	/**
	 * Status : All(0), New(1), In Progress(2), Resolved(3), Feedback(4), Closed(5), Rejected(6)
	 * @param status
	 * @return
	 */
	public JSONObject getIssuesWithStatus(String status){
	
		return null;
	}
	
	/**
	 * Signpost 에서 보내온 StatusName 을 Redmine 에 등록된 StatusID 로 맵핑
	 * @param status (new|inprogress|resolved)
	 * @return
	 */
	public List<String> convertStatusNameToId(String status) {
		List<String> statusIds = new ArrayList<String>();
		
		SLog.d("status", status);
		try {
			//redmine 상태 목록 조회
			String url = String.format(issueStatusFormat, host);
			
			HttpResponse<JsonNode> jsonResponse;
			jsonResponse = Unirest.get(url).asJson();
			
			JSONObject root = jsonResponse.getBody().getObject();
			JSONArray issueStatuses = root.getJSONArray("issue_statuses");
			JSONObject issueStatus;

			//맵핑될 status 정보 조회
			Properties properties = new Properties();
			properties.load(this.getClass().getResourceAsStream("/resources/dataservice.properties"));
			String[] statusNames = properties.getProperty("redmine.status." + status).split("\\|");

			for(String statusName : statusNames) {
				
				for(int i=0; i<issueStatuses.length(); i++){
					issueStatus = (JSONObject)issueStatuses.get(i);
					int issueStatusId = issueStatus.getInt("id");
					String issueStatusName = issueStatus.getString("name").toLowerCase().replaceAll("\\s+","");
					
					if(issueStatusName.equals(statusName)) {
						statusIds.add(String.valueOf(issueStatusId));
					}
				}
			}
			SLog.d("statusIds", statusIds.toString());
			
		} catch (IOException | UnirestException e) {
			e.printStackTrace();
		} 
		
		return statusIds;
	}

	/**
	 * 프로젝트 목록 조회
	 * @return
	 */
	public JSONArray getProjects() {
		JSONArray projects = new JSONArray();
		String url = String.format(projectsFormat, host);
		
		HttpResponse<JsonNode> jsonResponse;
		try {
			jsonResponse = Unirest.get(url).asJson();
			JSONObject root = jsonResponse.getBody().getObject();
			
			projects = root.has("projects") ? root.getJSONArray("projects") : new JSONArray();
			
		} catch (UnirestException e) {
			e.printStackTrace();
		}
		
		return projects;
	}
}
