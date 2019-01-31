package kr.co.inslab.codealley.dataservice.provider;

import java.util.List;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Project;
import org.json.JSONArray;
import org.json.JSONObject;

import kr.co.inslab.codealley.dataservice.common.Const;
import kr.co.inslab.codealley.dataservice.common.Parameter;

/**
 * Gitlab의 API를 이용하여 data획득 클래스 
 * @author minchulahn
 *
 */
public class GitlabProvider extends ManagementProvider {
	
	GitLabApi gitLabApi;
	
	/**
	 * 생성자
	 * @param url
	 * @param token
	 */
	public GitlabProvider(String url, String token) {
		// Create a GitLabApi instance to communicate with your GitLab server
		gitLabApi = new GitLabApi(url, token);
	}

	/**
	 * Repository 정보
	 */
	@Override
	public JSONObject getRepositories() {
		JSONObject result = new JSONObject();
		
		try {
			// Get the list of all projects your account has access to
			List<Project> projects = gitLabApi.getProjectApi().getAllProjects();
			JSONArray arr_projects = new JSONArray();
			
			for(Project project : projects) {
				JSONObject objNew = new JSONObject();
				objNew.put("id", project.getId());
				objNew.put("name", project.getName());
				arr_projects.put(objNew);
			}
			result.put(Parameter.API_REPOSITORIES, arr_projects);
			return result;
			
		} catch (GitLabApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.put(Const.JSON_KEY_ERROR, e.getLocalizedMessage());
		}
		
		return result;
	}

	/**
	 * Commit 정보
	 */
	@Override
	public JSONObject getCommits(String repoName) {
		JSONObject result = new JSONObject();
		
		if(repoName.equalsIgnoreCase("all") ){
			try {
				// Get the list of all projects your account has access to
				List<Project> projects = gitLabApi.getProjectApi().getAllProjects();
				JSONArray arr_commits = new JSONArray();
				
				for(Project project : projects) {
					// Get a list of commits associated with the specified project
					List<Commit> commits = gitLabApi.getCommitsApi().getCommits(project.getId());
					
					for(Commit commit : commits) {
						JSONObject objNew = new JSONObject();
						objNew.put("commit", commit.getId());
						objNew.put("title", commit.getTitle());
						objNew.put("repository", project.getName());
						objNew.put("pubDate", commit.getCreatedAt().getTime());
						objNew.put("link", project.getWebUrl() + "/commit/" + commit.getId());
						arr_commits.put(objNew);
					}
				}
				
				return result.put("commits", arr_commits);
			} catch (GitLabApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				result.put(Const.JSON_KEY_ERROR, e.getLocalizedMessage());
			}
		} else {
			try {
				// Get the list of all projects your account has access to
				List<Project> projects = gitLabApi.getProjectApi().getAllProjects();
				int projectId = 0;
				String projectWebUrl = "";
				
				for(Project project : projects) {
					if(project.getName().equalsIgnoreCase(repoName)) {
						projectId = project.getId();
						projectWebUrl = project.getWebUrl();
						break;
					}
				}
				
				// Get a list of commits associated with the specified project
				List<Commit> commits = gitLabApi.getCommitsApi().getCommits(projectId);
				JSONArray arr_commits = new JSONArray();
				
				for(Commit commit : commits) {
					JSONObject objNew = new JSONObject();
					objNew.put("commit", commit.getId());
					objNew.put("title", commit.getTitle());
					objNew.put("repository", repoName);
					objNew.put("pubDate", commit.getCreatedAt().getTime());
					objNew.put("link",  projectWebUrl + "/commit/" + commit.getId());
					arr_commits.put(objNew);
				}
				
				return result.put("commits", arr_commits);
			} catch (GitLabApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				result.put(Const.JSON_KEY_ERROR, e.getLocalizedMessage());
			}
		}
		
		return result;
	}

	/**
	 * Commit Count 정보
	 */
	@Override
	public JSONObject getCommitCount(String repoName) {
		JSONObject result = new JSONObject();
		
		try {
			// Get the list of projects your account has access to
			List<Project> projects = gitLabApi.getProjectApi().getAllProjects();
			int count = 0;
			
			for(Project project : projects) {
				// Get a list of commits associated with the specified project
				List<Commit> commits = gitLabApi.getCommitsApi().getCommits(project.getId());					
				count += commits.size();
			}
			
			return result.put("count", count);
		} catch (GitLabApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.put(Const.JSON_KEY_ERROR, e.getLocalizedMessage());
		}
		
		return result;
	}
}
