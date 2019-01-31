package kr.co.inslab.codealley.dataservice.provider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kr.co.inslab.codealley.dataservice.common.Const;
import kr.co.inslab.codealley.dataservice.common.Parameter;
import kr.co.inslab.codealley.dataservice.util.DateSortUtil;
import testlink.api.java.client.TestLinkAPIClient;
import testlink.api.java.client.TestLinkAPIResults;

/**
 * TestLink Tool의 WEB API를 이용하여 테스트결과 정보를 얻는 클래스 
 * @author jdkim
 *
 */
public class TestLinkProvider extends BaseProvider{

	String rpcFormat = "%s/lib/api/xmlrpc/v1/xmlrpc.php"; //"http://testlink.cloudapp.net/testlink/lib/api/xmlrpc/v1/xmlrpc.php";
	
	// TestLink site -> admin/admin계정으로 api key발급
	// apikey를 signpost 에서 파라미터로받아서 처리
    //String devKey = "09f84218026e1183c13d85206a8a27eb";
    String url;
    
    TestLinkAPIClient api;
    
    /**
     * 생성자
     * @param _host
     */
	public TestLinkProvider(String _host, String apikey)
	{
		host = _host;
		url = String.format(rpcFormat, host);
		api = new TestLinkAPIClient(apikey, url);
	}
	
	
	/**
	 * TestLinkAPIClient 프로젝트 리스트를 얻는다
	 * @return 
	 */
	public ArrayList<String> apiGetProjects()
	{
		ArrayList<String> projectList = new ArrayList<String>();
		
        try     {
        	
        	TestLinkAPIResults results = api.getProjects();
        	
        	//TestLinkAPIResults results = api.getProjectTestPlans("DataService");
        	
        	//TestLinkAPIResults results = api.getCasesForTestPlan("DataService", "DataService �׽�Ʈ ��ȹ");
        	
        	//TestLinkAPIResults results =api.getLastExecutionResult("DataService", "DataService �׽�Ʈ ��ȹ", "TestCase1");
        	
        	//TestLinkAPIResults results = api.getBuildsForTestPlan("DataService", "DataService �׽�Ʈ ��ȹ");
        	
        	//
        	
        	
        	Map<String,Object> map;// = results.getData(0);
        	for(int i=0;i<results.size();i++)
        	{
        		map = results.getData(i);
        		
            	String projectName = (String) map.get("name");
            	//System.out.println("project : " + projectName);
            	
            	projectList.add(projectName);
        	}
        	
        	
        } 
        catch (Exception e){
        	e.printStackTrace();
        }
        
        return projectList;
	}
	
	
	/**
	 * TestLinkAPIClient 계획(Plan)을 얻는다 
	 * @param project 명
	 * @return
	 */
	public ArrayList<String> apiGetProjectTestPlans(String project)
	{
		ArrayList<String> planList = new ArrayList<String>();
		
        try     {

        	TestLinkAPIResults results = api.getProjectTestPlans(project);
        	
        	Map<String,Object> map;
        	for(int i=0;i<results.size();i++)
        	{
        		map = results.getData(i);
        		
        		String planName = (String) map.get("name");
            	//System.out.println("planName : " + planName);
            	
            	planList.add(planName);
        	}
        } 
        catch (Exception e){
        	e.printStackTrace();
        }
        
        return planList;
        
	}


	/**
	 * TestLinkAPIClient 테스트케이스정보를 얻는다.
	 * @param project
	 * @param plan
	 * @return
	 */
	public ArrayList<String> apiGetCasesForTestPlan(String project, String plan)
	{
		ArrayList<String> testcaseList = new ArrayList<String>();
		
        try     {

        	//TestLinkAPIResults results = api.getProjects();
        	
        	//TestLinkAPIResults results = api.getProjectTestPlans("DataService");
        	
        	TestLinkAPIResults results = api.getCasesForTestPlan(project, plan);
        	
        	//TestLinkAPIResults results =api.getLastExecutionResult("DataService", "DataService �׽�Ʈ ��ȹ", "TestCase1");
        	
        	//TestLinkAPIResults results = api.getBuildsForTestPlan("DataService", "DataService �׽�Ʈ ��ȹ");
        	
        	//
        	
        	
        	
        	
        	Map<String,Object> map;// = results.getData(0);
        	for(int i=0;i<results.size();i++)
        	{
        		System.out.println("\n############################################");
        		map = results.getData(i);
        		Set<String> keySet = map.keySet();
            	Iterator iter = keySet.iterator();
            	while(iter.hasNext()){
            		
            		try{
            			String key = (String) iter.next();
            			
            			Object obj =  (Object)map.get(key);
            			//System.out.println("object : " + obj.getClass().getSimpleName());
            			
            			
            			/*
            			if( obj instanceof String )
            			{
                    		String value = (String)map.get(key);
                    		
                    		System.out.println("key : " + key);
                    		System.out.println("value : " + value);
            			}
            			else
            				*/
            			if( obj instanceof Object[] )
            			{
            				Object[] objs = (Object[]) obj;
                			//System.out.println("size : " + objs.length);
                			
                			Object tmp = objs[0];
                			if( tmp instanceof HashMap )
                			{
                				String tcase_name = (String) ((HashMap) tmp).get("tcase_name");
                				System.out.println(project + " / " + plan + " / " + tcase_name);
                				
                				testcaseList.add(tcase_name);
                				
                			}
            			}

            		}
            		catch(Exception e)
            		{
            			e.printStackTrace();
            		}
            		
            		
            	}
        	}
        	
        	
        } 
        catch (Exception e){
        	e.printStackTrace();
        }
        
       return testcaseList;
	}
	
	/**
	 * TestLinkAPIClient 최종 실행 결과
	 * @param project
	 * @param plan
	 * @param testcase
	 * @return
	 */
	public Map apiGetLastExecutionResult(String project, String plan, String testcase)
	{
        try     {
        	
        	TestLinkAPIResults results =api.getLastExecutionResult(project, plan, testcase);
        	//TestLinkAPIResults results = api.getLatestBuildForTestPlan(project, plan);
        	
        	//TestLinkAPIResults results = api.getBuildsForTestPlan("DataService", "DataService �׽�Ʈ ��ȹ");
        	System.out.println(results);
        	
        	
        	if(results != null && results.size() > 0)
        		return results.getData(0);
        	
        } 
        catch (Exception e){
        	e.printStackTrace();
        }

        return null;
	}
	
	
	/**
	 * TestLinkAPIClient
	 * testcase당 connection이 발생하여 testcase 갯수만큼 비례하여 처리시간이 소요된다.
	 * DB등 cache기능이 필요함.
	 * @return
	 */
	public JSONArray getTests() {
		//JSONObject result = new JSONObject();
		JSONArray testResults = new JSONArray();
		
		ArrayList<String> projectList = apiGetProjects();
		String projectName;
		for( int i=0 ; i<projectList.size() ; i++ )
		{
			projectName = projectList.get(i);
			
			ArrayList<String> planList = apiGetProjectTestPlans(projectName);
			
			for( int j=0 ; j<planList.size() ; j++ )
			{
				String planName = planList.get(j);
				
				
				ArrayList<String> testcaseList = apiGetCasesForTestPlan(projectName, planName);
				
				
				for( int k=0 ; k<testcaseList.size() ; k++ )
				{
					String testcaseName = testcaseList.get(k);
					Map map = apiGetLastExecutionResult(projectName, planName, testcaseName);
					
					int id = Integer.parseInt(map.get("id").toString());
					if(id != -1) {
						testResults.put(makeTestResult(projectName, planName, testcaseName, map));
					}
					
				}
			}
		}
		
		return testResults;
	}
	

	/**
	 * 테스트결과를 얻는다.
	 * @param projectName
	 * @return
	 */
	public JSONArray getTestsByProject(String projectName) {
		JSONArray testResults = new JSONArray();


		ArrayList<String> planList = apiGetProjectTestPlans(projectName);

		for( int j=0 ; j<planList.size() ; j++ )
		{
			String planName = planList.get(j);

			ArrayList<String> testcaseList = apiGetCasesForTestPlan(projectName, planName);

			for( int k=0 ; k<testcaseList.size() ; k++ )
			{
				String testcaseName = testcaseList.get(k);
				Map map = apiGetLastExecutionResult(projectName, planName, testcaseName);

				int id = Integer.parseInt(map.get("id").toString());
				if(id != -1) {
					testResults.put(makeTestResult(projectName, planName, testcaseName, map));
				}
			}
		}

		return testResults;
	}

	/**
	 * 테스트케이스수행 결과정보를 JSONObject에 담는다.
	 * @param projectName
	 * @param planName
	 * @param testcaseName
	 * @param map
	 * @return
	 */
	private JSONObject makeTestResult(String projectName, String planName, String testcaseName, Map map)
	{
		//System.out.println("\n############################################");

		String tcversion_number 	= (String) map.get("tcversion_number");
		String notes 				= (String) map.get("notes");
		String execution_duration 	= (String) map.get("execution_duration");
		String build_id 			= (String) map.get("build_id");
		String execution_ts 		= (String) map.get("execution_ts");
		String tester_id 			= (String) map.get("tester_id");
		String tcversion_id 		= (String) map.get("tcversion_id");
		String platform_id 			= (String) map.get("platform_id");
		String execution_type 		= (String) map.get("execution_type");
		String status 				= (String) map.get("status");
		String testplan_id 			= (String) map.get("testplan_id");


		//System.out.println(execution_ts + " / " + status);

		JSONObject obj = new JSONObject();
		obj.put("project", projectName);
		obj.put("plan", planName);
		obj.put("testcase", testcaseName);
		obj.put("execution_date", execution_ts);
		obj.put("execution_duration", execution_duration);

		if(status.equalsIgnoreCase("f"))
			obj.put("status", "fail");
		else if(status.equalsIgnoreCase("b"))
			obj.put("status", "block");
		else if(status.equalsIgnoreCase("p"))
			obj.put("status", "pass");

		obj.put("notes", notes);
		
		return obj;
	}
	
	/**
	 * API요청 샘플
	 */
	public void apiSample()
	{
        try     {

        	TestLinkAPIResults results = api.getProjects();
        	
        	//TestLinkAPIResults results = api.getProjectTestPlans("DataService");
        	
        	//TestLinkAPIResults results = api.getCasesForTestPlan("DataService", "DataService �׽�Ʈ ��ȹ");
        	
        	//TestLinkAPIResults results =api.getLastExecutionResult("DataService", "DataService �׽�Ʈ ��ȹ", "TestCase1");
        	
        	//TestLinkAPIResults results = api.getBuildsForTestPlan("DataService", "DataService �׽�Ʈ ��ȹ");
        	
        	//
        	
        	
        	
        	int size = results.size();
        	System.out.println(results.toString());
        	
        	Map<String,Object> map;// = results.getData(0);
        	for(int i=0;i<results.size();i++)
        	{
        		System.out.println("\n############################################");
        		map = results.getData(i);
        		Set<String> keySet = map.keySet();
            	Iterator iter = keySet.iterator();
            	while(iter.hasNext()){
            		
            		try{
            			String key = (String) iter.next();
            			
            			Object obj =  (Object)map.get(key);
            			System.out.println("object : " + obj.getClass().getSimpleName());
            			
            			
            			
            			if( obj instanceof String )
            			{
                    		String value = (String)map.get(key);
                    		
                    		System.out.println("key : " + key);
                    		System.out.println("value : " + value);
            			}
            			else if( obj instanceof Object[] )
            			{
            				Object[] objs = (Object[]) obj;
                			System.out.println("size : " + objs.length);
                			
                			Object tmp = objs[0];
                			if( tmp instanceof HashMap )
                			{
                				Set keys = ((HashMap) tmp).keySet();
                				Iterator iterator = keys.iterator();
                				while (iterator.hasNext())
                				{
                					String k = (String) iterator.next();
                					System.out.println("k : "+ k);
                					System.out.println("v : "+ ((HashMap) tmp).get(k));
                				}
                			}
                			System.out.println("getSimpleName : "+tmp.getClass().getSimpleName());
            			}

                		System.out.println("------------------------");
            		}
            		catch(Exception e)
            		{
            			e.printStackTrace();
            		}
            		
            		
            	}
        	}
        	
        	
        	System.out.println(size);
        } 
        catch (Exception e){
        	e.printStackTrace();
        }
        
        //System.out.println(api.ping());
        System.out.println(api.isConnected);
	}


	/**
	 * 테스트 프로젝트명를 얻는다
	 * @return
	 */
	public JSONObject getProjects() {
		
		JSONObject result = new JSONObject();
		JSONArray projects = new JSONArray();
		
		ArrayList<String> projectList = apiGetProjects();
		for( int i=0 ; i<projectList.size() ; i++ )
		{
			String projectName = projectList.get(i);
			
			JSONObject obj = new JSONObject();
			obj.put("name", projectName);
			projects.put(obj);
		}
		
		return result.put(Parameter.API_PROJECTS, projects);
	}

	/**
	 * 테스트 수행 결과정보를 얻는다.
	 * @param projectName
	 * @return
	 */
	public JSONObject getTests(String projectName) {
		JSONObject result = new JSONObject();
		
		DateSortUtil dsu = new DateSortUtil();
		
		JSONArray testsResult;
		if(projectName == null || projectName.equalsIgnoreCase("all"))
		{
			testsResult = getTests();
			dsu.sort(testsResult, "execution_date", null); 
			result.put("tests", testsResult);
		}
		else
		{
			result.put("tests", getTestsByProject(projectName));
		}
		
		return result;
	}

	/**
	 * 테스트 수행이 몇번되었는지..
	 * @return
	 */
	public JSONObject getTestCount()
	{
		JSONObject result = new JSONObject();
		
		try
		{
			JSONObject testsResult = getTests(null);
			JSONArray tests = testsResult.getJSONArray("tests");
			
			int pass_count = 0;
			int fail_count = 0;
			for(int i=0;i<tests.length();i++)
			{
				JSONObject item = tests.getJSONObject(i);
				
				if(item.getString("status").equalsIgnoreCase("pass"))
					pass_count++;
				else if(item.getString("status").equalsIgnoreCase("fail"))
					fail_count++;
			}

			result.put("pass_count", pass_count);
			result.put("fail_count", fail_count);
			
		}
		catch(JSONException e)
		{
			e.printStackTrace();
			result.put(Const.JSON_KEY_ERROR, e.getLocalizedMessage());
		}
		
		return result;
	}



}
