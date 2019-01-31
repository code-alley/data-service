package kr.co.inslab.codealley.dataservice.provider;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import kr.co.inslab.codealley.dataservice.common.Const;
import kr.co.inslab.codealley.dataservice.common.Parameter;
import kr.co.inslab.codealley.dataservice.log.SLog;

/**
 * SonarQube의 WEB API를 이용하여 data획득 클래스 
 *
 * @author  jdkim
 */
public class SonarQubeProvider extends BaseProvider {

	String pojectsFormat = "%s/api/projects/index"; //"http://sonarqube.cloudapp.net:9000/api/projects/index";
	String issuesFormat = "%s/api/issues/search?componentRoots=%s&sort=UPDATE_DATE&asc=false";
	String statusFormat = "%s/api/issues/search?statuses=%s";
	String linkNoramlFormat	= "%s/issues/search#componentRoots=%s|fileUuids=%s|sort=UPDATE_DATE|asc=false";
	String linkStatusFormat	= "%s/issues/search#sort=UPDATE_DATE|asc=false|statuses=%s";
	
	/**
	 * 생성자
	 * @param _host
	 */
	public SonarQubeProvider(String _host){
		this.host = _host;
	}
	
	/**
	 * 분석된 프로젝트 리스트
	 * @return
	 */
	public JSONObject getProjects(){
		
		JSONObject result = new JSONObject();
		
		if(!isHost())	return result.put(Const.JSON_KEY_ERROR, "host is net set!");
		
		String url = String.format(pojectsFormat, host);
		
		HttpResponse<JsonNode> jsonResponse;
		try {
			SLog.d("getProjects", url);
			
			jsonResponse = Unirest.get(url).asJson();
			JSONArray root = jsonResponse.getBody().getArray();
			SLog.d("response", root.toString());
			
			JSONArray projects = new JSONArray();
		
			for(int i=0;i<root.length();i++)
			{
				JSONObject temp = (JSONObject) root.get(i);
				
				JSONObject newObj = new JSONObject();
				newObj.put("name", temp.getString("k"));
				
				projects.put(newObj);
			}
			
			
			result.put(Parameter.API_PROJECTS, projects);
			return result;
			
			
		} catch (UnirestException e) {
			e.printStackTrace();
			result.put(Const.JSON_KEY_ERROR, e.getLocalizedMessage());
		}
		
		return result;
	}

	/**
	 * 해당 프로젝트의 이슈정보를 얻는다
	 * @param projectName
	 * @return
	 */
	public JSONObject getIssues(String projectName) {
		
		JSONObject result = new JSONObject();
		/*
		ArrayList<String> issuesList = new ArrayList<String>();;
		
		if(projectName == null || projectName == "" || projectName.equalsIgnoreCase("all") ){
			JSONObject root = getProjects();
			if(root == null)
				return result.put(Const.JSON_KEY_ERROR, "getProjects() failed!");
			else if(root.has(Const.JSON_KEY_ERROR))
				return result;
			
			
			JSONArray projects = root.getJSONArray(Parameter.VALUE_PROJECTS);
			for(int i=0;i<projects.length();i++)
			{	
				JSONObject name = projects.getJSONObject(i);
				issuesList.add(name.getString("name"));
			}
		}
		else
			issuesList.add(projectName);
		*/
		
		if(!isHost())	return result.put(Const.JSON_KEY_ERROR, "host is net set!");
		
		//프로젝트 필터링 
		String componentRoots = "";
		if(!projectName.equals("all")) {
			componentRoots = projectName;
		}
		
		String url = String.format(issuesFormat, host, componentRoots);
				
		HttpResponse<JsonNode> jsonResponse;
		try {
			SLog.d("getIssues", url);
			
			jsonResponse = Unirest.get(url).asJson();
			JSONObject root = jsonResponse.getBody().getObject();
			JSONArray issues = root.getJSONArray("issues");
			JSONArray components = root.getJSONArray("components");
			SLog.d("response", root.toString());
			
			//project = all
			//if(projectName == null || projectName == "" || projectName.equalsIgnoreCase("all") )
			//	return result.put(Parameter.VALUE_ISSUES, issues);
			
			//링크 설정 (이슈가 존재하는 해당 파일정보 추가)
			for(int i=0;i<issues.length();i++) {
				JSONObject issue = issues.getJSONObject(i);
				
				String projectNameLink = issue.getString("project");
				int componentId = issue.getInt("componentId");
				String fileUuids = "";
				
				for(int j=0;j<components.length();j++) {
					JSONObject component = components.getJSONObject(j);
					if(componentId  == component.getInt("id")) {
						fileUuids = component.getString("uuid");
						break;
					}
				}
				
				String link = String.format(linkNoramlFormat, host, projectNameLink, fileUuids);
				issue.put("link", link);
				issues.put(i, issue);
			}
			
			return result.put(Parameter.API_ISSUES, issues);
			
			
		} catch (UnirestException e) {
			e.printStackTrace();
			result.put(Const.JSON_KEY_ERROR, e.getLocalizedMessage());
		}
		
		return result;
		
	}

	/**
	 * 각 이슈상태의 총 갯수
	 * @param projectName
	 * @param status
	 * @return
	 */
	public JSONObject getIssuesWithStatus(String projectName, String status) {
		
		JSONObject result = new JSONObject();
		
		
		if(!isHost())	return result.put(Const.JSON_KEY_ERROR, "host is net set!");
		
		
		String url = String.format(statusFormat, host, status.toUpperCase());
		
		
		
		HttpResponse<JsonNode> jsonResponse;
		try {
			SLog.d("getIssues-Status", url);
			
			jsonResponse = Unirest.get(url).asJson();
			JSONObject root = jsonResponse.getBody().getObject();
			JSONObject paging = root.getJSONObject("paging");
		
			result.put(status, paging.getInt("total"));
			
			String link = String.format(linkStatusFormat, host, status.toUpperCase());
			//SLog.d("status link", link);
			result.put("link", link);
			return result;
			
			
		} catch (UnirestException e) {
			e.printStackTrace();
			result.put(Const.JSON_KEY_ERROR, e.getLocalizedMessage());
		}
		
		return result;
	}
	
}
