package kr.co.inslab.codealley.dataservice.provider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import kr.co.inslab.codealley.dataservice.common.Parameter;
import kr.co.inslab.codealley.dataservice.common.Tools;
import kr.co.inslab.codealley.dataservice.servlet.ResponseHandler;

/**
 * 요청에 대한 카테고리 분류 및 Tool Provider적용
 * @author jdkim
 *
 */
public class ToolsHandler {

	/**
	 * Tool의 카테고리별 분류 처리
	 * @param request
	 * @param response
	 */
	public void process(HttpServletRequest request, HttpServletResponse response) {
		
		String toolType 	= request.getParameter(Parameter.KEY_TOOL_TYPE);
		String toolName 	= request.getParameter(Parameter.KEY_TOOL_NAME);
		String toolUrl 		= request.getParameter(Parameter.KEY_TOOL_URL);
		String api 			= request.getParameter(Parameter.KEY_API);
		String repoName		= request.getParameter(Parameter.KEY_REPOSITORY);
		String projectName	= request.getParameter(Parameter.KEY_PROJECT);
		String status		= request.getParameter(Parameter.KEY_STATUS);
		String token		= request.getParameter(Parameter.KEY_TOKEN);
		String apikey		= request.getParameter(Parameter.KEY_APIKEY);
		
		JSONObject result = null;
		
		if(toolType.equalsIgnoreCase(Parameter.CATEGORY_MANAGEMENT_SYSTEM)){
			result = getDataManagement(toolName, toolUrl, api, repoName, token);
		}
		else if(toolType.equalsIgnoreCase(Parameter.CATEGORY_CODE_REVIEW)){
		
			result = getDataCodeReview(toolName, toolUrl, api, repoName, status);
			
		}
		else if(toolType.equalsIgnoreCase( Parameter.CATEGORY_STATIC_ANALYSIS)){
		
			result = getDataStaticAnalysis(request, toolName, toolUrl, api, projectName);
		}
		else if(toolType.equalsIgnoreCase( Parameter.CATEGORY_BUILD_SYSTEM)){
			
			result = getDataBuild(toolName, toolUrl, api);
		}
		else if(toolType.equalsIgnoreCase( Parameter.CATEGORY_PROJECT_MANAGEMENT)){
			
			result = getDataProjectManagement(request, toolName, toolUrl, api, apikey);
		}
		else if(toolType.equalsIgnoreCase( Parameter.CATEGORY_DOCUMENTATION)){
			
			result = getDataDocumentation(toolName, toolUrl, api);
		}
		else if(toolType.equalsIgnoreCase( Parameter.CATEGORY_TEST)){
			
			result = getDataTest(toolName, toolUrl, api, projectName, apikey);
		}
		
		if(result == null){
			result = new JSONObject();
			result.put("error", "...");
		}
		
		ResponseHandler resHandler = new ResponseHandler();
		resHandler.setResult(request, response, result);
		
		// provider占쏙옙 찾占쏙옙 占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙.
		//AbstractProvider provider = findProvider(toolName);
		//JSONObject obj = provider.runTask();
		
		
	}

	/**
	 * Test Management Tool의 해당 Provider를 생성하여 요청된 API를 처리한다.
	 * @param toolName
	 * @param toolUrl
	 * @param api
	 * @param projectName
	 * @return
	 */
	private JSONObject getDataTest(String toolName, String toolUrl, String api, String projectName, String apikey) {
		if(toolName.equalsIgnoreCase(Tools.TESTLINK)){
			TestLinkProvider provider = new TestLinkProvider(toolUrl, apikey);
			if(api.equalsIgnoreCase(Parameter.API_PROJECTS))
			{
				return provider.getProjects();
			}
			else if(api.equalsIgnoreCase(Parameter.API_TESTS))
			{
				return provider.getTests(projectName);
			}
			else if(api.equalsIgnoreCase(Parameter.API_TEST_COUNT))
			{
				return provider.getTestCount();
			}
		}
		return null;
		
	}

	/**
	 * Documentation Tool의 해당 Provider를 생성하여 요청된 API를 처리한다.
	 * @param toolName
	 * @param toolUrl
	 * @param api
	 * @return
	 */
	private JSONObject getDataDocumentation(String toolName, String toolUrl,
			String api) {
		if(toolName.equalsIgnoreCase(Tools.XWIKI)){
			XWikiProvider provider = new XWikiProvider(toolUrl);
			if(api.equalsIgnoreCase(Parameter.API_UPDATES))
			{
				return provider.getUpdates();
			}
		}
		return null;
	}

	/**
	 * Project Management Tool의 해당 Provider를 생성하여 요청된 API를 처리한다.
	 * @param toolName
	 * @param toolUrl
	 * @param api
	 * @return
	 */
	private JSONObject getDataProjectManagement(HttpServletRequest request, String toolName,
			String toolUrl, String api, String apikey) {
		
		if(toolName.equalsIgnoreCase(Tools.REDMINE)){
			RedmineProvider provider = new RedmineProvider(toolUrl, apikey);
			if(api.equalsIgnoreCase(Parameter.API_ISSUES))
			{
				return provider.getIssues(request.getParameter(Parameter.KEY_STATUS), request.getParameter(Parameter.KEY_PROJECT_ID));
			}
			else if(api.equalsIgnoreCase(Parameter.API_ISSUE_COUNT))
			{
				return provider.getIssueCount(request.getParameter(Parameter.KEY_STATUS));
			}
		}
		return null;
	}

	/**
	 * Build Tool의 해당 Provider를 생성하여 요청된 API를 처리한다.
	 * @param toolName
	 * @param toolUrl
	 * @param api
	 * @return
	 */
	private JSONObject getDataBuild(String toolName, String toolUrl, String api) {
		if(toolName.equalsIgnoreCase(Tools.JENKINS)){
			JenkinsProvider provider = new JenkinsProvider(toolUrl);
			if(api.equalsIgnoreCase(Parameter.API_BUILDS))
			{
				return provider.getBuilds();
			}
			else if(api.equalsIgnoreCase(Parameter.API_BUILD_COUNT))
			{
				return provider.getBuildCount();
			}
		}
		return null;
	}

	/**
	 * StaticAnalysis Tool의 해당 Provider를 생성하여 요청된 API를 처리한다.
	 * @param toolName
	 * @param toolUrl
	 * @param api
	 * @return
	 */
	private JSONObject getDataStaticAnalysis(HttpServletRequest request, String toolName, String toolUrl,
			String api, String projectName) {
		if(toolName.equalsIgnoreCase(Tools.SONARQUBE)){
			SonarQubeProvider provider = new SonarQubeProvider(toolUrl);
			
			
			if(api.equalsIgnoreCase(Parameter.API_PROJECTS))
			{
				return provider.getProjects();
			}
			else if(api.equalsIgnoreCase(Parameter.API_ISSUES))
			{
				String status	= request.getParameter(Parameter.KEY_STATUS);
				if(status != null)
					return provider.getIssuesWithStatus(projectName, status);
				else
					return provider.getIssues(projectName);
			}
		}
		return null;
	}

	/**
	 * CodeReview Tool의 해당 Provider를 생성하여 요청된 API를 처리한다.
	 * @param toolName
	 * @param toolUrl
	 * @param api
	 * @return
	 */
	private JSONObject getDataCodeReview(String toolName, String toolUrl,
			String api, String repoName, String status) {
		
		if(toolName.equalsIgnoreCase(Tools.REVIEWBOARD)){
			ReviewBoardProvider provider = new ReviewBoardProvider(toolUrl);
			
			
			if(api.equalsIgnoreCase(Parameter.API_REPOSITORIES))
			{
				return provider.getRepositories();
			}
			else if(api.equalsIgnoreCase(Parameter.API_REVIEWS))
			{
				return provider.getReviews(repoName);
			}
			else if(api.equalsIgnoreCase(Parameter.API_REVIEW_COUNT))
				return provider.getReviewCount(status);
		}
		return null;
	}

	/**
	 * 저장소 관리 Tool의 해당 Provider를 생성하여 요청된 API를 처리한다.
	 * @param toolName
	 * @param toolUrl
	 * @param api
	 * @return
	 */
	private JSONObject getDataManagement(String toolName, String toolUrl, String api, String repoName, String token) {
		
		ManagementProvider provider = null;
		
		if(toolName.equalsIgnoreCase(Tools.GITBLIT)){
			provider = new GitblitProvider(toolUrl);
		} else {
			provider = new GitlabProvider(toolUrl, token);
		}
		
		if(api.equalsIgnoreCase(Parameter.API_REPOSITORIES)){
			return provider.getRepositories();
		}else if(api.equalsIgnoreCase(Parameter.API_COMMITS)){
			return provider.getCommits(repoName);
		}else if(api.equalsIgnoreCase(Parameter.API_COMMIT_COUNT)){
			return provider.getCommitCount(repoName);
		}
		
		return null;
	}

	

}
