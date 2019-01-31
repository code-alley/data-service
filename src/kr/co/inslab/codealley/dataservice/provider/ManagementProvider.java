package kr.co.inslab.codealley.dataservice.provider;

import org.json.JSONObject;

/**
 * 저장소 관리 Provider 의 추상 메소드
 *
 * @author  jdkim
 */
public abstract class ManagementProvider {
	public String host;
	public abstract JSONObject getRepositories();
	public abstract JSONObject getCommits(String repoName);
	public abstract JSONObject getCommitCount(String repoName);
	
	public boolean isHost() {return host != null ? true : false;}
}
