package kr.co.inslab.codealley.dataservice.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import kr.co.inslab.codealley.dataservice.common.Const;
import kr.co.inslab.codealley.dataservice.common.Parameter;
import kr.co.inslab.codealley.dataservice.log.SLog;

/*
<?xml version="1.0" encoding="UTF-8"?>
<rss 
    xmlns:content="http://purl.org/rss/1.0/modules/content/" 
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" 
    xmlns:dc="http://purl.org/dc/elements/1.1/" 
    xmlns:taxo="http://purl.org/rss/1.0/modules/taxonomy/" version="2.0">
    <channel>
        <title>first_repo.git (HEAD)</title>
        <link>http://catools.cloudapp.net:8080/gitblit/summary/first_repo.git</link>
        <description />
        <image>
            <title>Gitblit</title>
            <url>http://catools.cloudapp.net:8080/gitblit/gitblt_25.png</url>
            <link>http://catools.cloudapp.net:8080/gitblit</link>
        </image>
        <item>
            <title>modify................</title>
            <link>http://catools.cloudapp.net:8080/gitblit/commit/first_repo.git/3f2ea753066fd14c203bfb94fa3bf23996a1b1c1</link>
            <description>modify................&lt;br/&gt;</description>
            <category>commit:3f2ea753066fd14c203bfb94fa3bf23996a1b1c1</category>
            <category>parent:c3330fde54a2214ae38be2b5737d86d39b0a0401</category>
            <category>ref:refs/heads/master</category>
            <category>ref:HEAD</category>
            <pubDate>Thu, 11 Dec 2014 03:27:24 GMT</pubDate>
            <guid>http://catools.cloudapp.net:8080/gitblit/commit/first_repo.git/3f2ea753066fd14c203bfb94fa3bf23996a1b1c1</guid>
            <dc:creator>jongdeukkim</dc:creator>
            <dc:date>2014-12-11T03:27:24Z</dc:date>
        </item>
        <item>
            <title>오류 수정</title>
            <link>http://catools.cloudapp.net:8080/gitblit/commit/first_repo.git/c3330fde54a2214ae38be2b5737d86d39b0a0401</link>
            <description>오류 수정&lt;br/&gt;</description>
            <category>commit:c3330fde54a2214ae38be2b5737d86d39b0a0401</category>
            <category>parent:389c60df9f48e5f66485985da187347ee9bd1f81</category>
            <pubDate>Tue, 09 Dec 2014 07:21:03 GMT</pubDate>
            <guid>http://catools.cloudapp.net:8080/gitblit/commit/first_repo.git/c3330fde54a2214ae38be2b5737d86d39b0a0401</guid>
            <dc:creator>jongdeukkim</dc:creator>
            <dc:date>2014-12-09T07:21:03Z</dc:date>
        </item>
        <item>
            <title>프로젝트 설명 추가</title>
            <link>http://catools.cloudapp.net:8080/gitblit/commit/first_repo.git/389c60df9f48e5f66485985da187347ee9bd1f81</link>
            <description>프로젝트 설명 추가&lt;br/&gt;</description>
            <category>commit:389c60df9f48e5f66485985da187347ee9bd1f81</category>
            <category>parent:4f47ca815ca1c75927f2e352b6af36e884197e00</category>
            <pubDate>Tue, 09 Dec 2014 07:20:00 GMT</pubDate>
            <guid>http://catools.cloudapp.net:8080/gitblit/commit/first_repo.git/389c60df9f48e5f66485985da187347ee9bd1f81</guid>
            <dc:creator>jongdeukkim</dc:creator>
            <dc:date>2014-12-09T07:20:00Z</dc:date>
        </item>
        <item>
            <title>add README.TXT</title>
            <link>http://catools.cloudapp.net:8080/gitblit/commit/first_repo.git/4f47ca815ca1c75927f2e352b6af36e884197e00</link>
            <description>add README.TXT&lt;br/&gt;</description>
            <category>commit:4f47ca815ca1c75927f2e352b6af36e884197e00</category>
            <pubDate>Tue, 09 Dec 2014 07:18:39 GMT</pubDate>
            <guid>http://catools.cloudapp.net:8080/gitblit/commit/first_repo.git/4f47ca815ca1c75927f2e352b6af36e884197e00</guid>
            <dc:creator>jongdeukkim</dc:creator>
            <dc:date>2014-12-09T07:18:39Z</dc:date>
        </item>
    </channel>
</rss>
 */

/**
 * Gitblit의 WEB API를 이용하여 data획득 클래스 
 * @author jdkim
 *
 */
public class GitblitProvider extends ManagementProvider {

	 

	public String reposFormat 	= "%s/rpc?req=LIST_REPOSITORIES";	// 1:host
	public String commitsFormat = "%s/feed/%s";		// 1:host , 2:git repo name
	
	/**
	 * 생성자
	 * @param _host
	 */
	public GitblitProvider(String _host){
		host = _host;
		
	}
	
	/**
	 * Repository 정보
	 */
	@Override
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
			
			Iterator keys = obj.keys();
			String key;
			JSONArray repos = new JSONArray();
			
			while (keys.hasNext()) {
				
				key = (String) keys.next();
				JSONObject repoObj = (JSONObject) obj.get(key);
				SLog.d("repository name", repoObj.getString("name"));
				
				JSONObject repoObjNew = new JSONObject();
				repoObjNew.put("name", repoObj.getString("name"));
				repos.put(repoObjNew);
			}
			
			
			result.put(Parameter.API_REPOSITORIES, repos);
			return result;
			
			
		} catch (UnirestException e) {
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
		
		ArrayList<String> repoList = new ArrayList<String>();;
		
		if(repoName == null || repoName == "" || repoName.equalsIgnoreCase("all") ){
			JSONObject repos = getRepositories();
			if(repos == null)
				return result.put(Const.JSON_KEY_ERROR, "getRepositories() failed!");
			else if(repos.has(Const.JSON_KEY_ERROR))
				return result;
			
			
			JSONArray repositories = repos.getJSONArray("repositories");
			for(int i=0;i<repositories.length();i++)
			{	
				JSONObject name = repositories.getJSONObject(i);
				repoList.add(name.getString("name"));
				//SLog.d("repo", name.getString("name"));
			}
		}
		else
			repoList.add(repoName);
		
		
		
		if(!isHost())	return result.put(Const.JSON_KEY_ERROR, "host is net set!");
		
		
		JSONArray commits = new JSONArray();
		
		for(int idx=0;idx<repoList.size();idx++){
			
			repoName = repoList.get(idx);
			
			
			String url = String.format(commitsFormat, host, repoName);
			SLog.d("getCommits", url);
			
			
			try {
				
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document dom = db.parse(url);
				Element root = dom.getDocumentElement();
				
				NodeList channel = root.getElementsByTagName("channel");
				//SLog.d("channel getNodeName",  channel.item(0).getNodeName());
				
				NodeList items = channel.item(0).getChildNodes();
				for(int i=0;i<items.getLength();i++){
					Node item = items.item(i);
					
					
					if(item.getNodeName().equalsIgnoreCase("item")){
						NodeList item_sub = item.getChildNodes();
						
						JSONObject obj = new JSONObject();
						for(int j=0;j<item_sub.getLength();j++){
							
							Node sub = item_sub.item(j);
							
							//SLog.d("getNodeName", sub.getNodeName());
							//SLog.d("getTextContent", sub.getTextContent());
							String tagName = sub.getNodeName();
							
							if( tagName == "title" || tagName == "link" || tagName == "description" ||
									tagName == "pubDate" || tagName == "dc:creator" || tagName == "category") 
							{
								String textContent = sub.getTextContent();
								if(tagName.equalsIgnoreCase("category")  )
								{
									if(textContent.contains("commit:"))
									{
										String[] tokens = textContent.split(":");
										if(tokens.length > 1)
											obj.put("commit", tokens[1]);
									}

								}
								else
									obj.put(sub.getNodeName(), sub.getTextContent());
							}
						}
						
						//SLog.d("===============================");
						obj.put("repository", repoName);
						commits.put(obj);
						
					}
					
				}
				
			} 
			catch (ParserConfigurationException e) {
				e.printStackTrace();
				result.put(Const.JSON_KEY_ERROR, e.getLocalizedMessage());
			} catch (SAXException e) {
				e.printStackTrace();
				result.put(Const.JSON_KEY_ERROR, e.getLocalizedMessage());
			} catch (IOException e) {
				e.printStackTrace();
				result.put(Const.JSON_KEY_ERROR, e.getLocalizedMessage());
			}
			
			
		}

		
		
		return result.put("commits", commits);
	}
	
	/**
	 * Commit Count 정보
	 */
	public JSONObject getCommitCount(String repoName)
	{
		JSONObject result = new JSONObject();
		
		
		
		try{
			JSONObject commitsResult = getCommits(repoName);
			JSONArray commits = commitsResult.getJSONArray("commits");
			
			result.put("count", commits.length());
			
			if(repoName == null)
				repoName = "all";
			
			result.put("repository", repoName);
		}
		catch(JSONException e)
		{
			result.put(Const.JSON_KEY_ERROR, e.getLocalizedMessage());
		}
		
		return result;
	}

}
