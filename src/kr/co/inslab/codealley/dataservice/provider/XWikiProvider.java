package kr.co.inslab.codealley.dataservice.provider;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import kr.co.inslab.codealley.dataservice.common.Const;
import kr.co.inslab.codealley.dataservice.log.SLog;
import kr.co.inslab.codealley.dataservice.util.JsonSortUtil;

//
/*
 1. author가 admin이 아닌 항목(where구문)의 최근항목(number) 10개, 응답형식(media) xml, 쿼리 타입(type) hql
 2. admin이 아닌것으로 질의하는 이유는 
     admin의 포함된 항목중에는 사용자가 추가하지 않은 xwiki시스템과 관련된것들이 있어 제거하기 위함이다.
     
 http://jdkim-test.cloudapp.net:8080/xwiki/rest/wikis/xwiki/query?q=where%20doc.author!=%27XWiki.superadmin%27%20and%20doc.author!=%27%27&type=hql&number=10&media=xml
 http://jdkim-test.cloudapp.net:8080/xwiki/rest/wikis/xwiki/query?q=where doc.author!='XWiki.superadmin'&type=hql&media=json&number=10
	
 <searchResults xmlns="http://www.xwiki.org" template="http://jdkim-test.cloudapp.net:8080/xwiki/rest/wikis/xwiki/query?q={query}(&type={xwql,hql,lucene})(&number={number})(&start={start})(&orderField={fieldname}(&order={asc|desc}))(&distinct=1)(&prettyNames={false|true})(&wikis={wikis})(&className={classname})">
	<searchResult>
	<link href="http://jdkim-test.cloudapp.net:8080/xwiki/rest/wikis/xwiki/spaces/XWiki/pages/NewPagr" rel="http://www.xwiki.org/rel/page"/>
	<type>page</type>
	<id>xwiki:XWiki.NewPagr</id>
	<pageFullName>XWiki.NewPagr</pageFullName>
	<title>NewPage</title>
	<wiki>xwiki</wiki>
	<space>XWiki</space>
	<pageName>NewPagr</pageName>
	<modified>2014-12-18T05:29:28Z</modified>
	<author>XWiki.KimJd</author>
	<version>1.1</version>
	</searchResult>
</searchResults>


http://jdkim-test.cloudapp.net:8080/xwiki/rest/wikis/xwiki/spaces/XWiki/pages/NewPagr

<page xmlns="http://www.xwiki.org">
	<link href="http://jdkim-test.cloudapp.net:8080/xwiki/rest/wikis/xwiki/spaces/XWiki" rel="http://www.xwiki.org/rel/space"/>
	<link href="http://jdkim-test.cloudapp.net:8080/xwiki/rest/wikis/xwiki/spaces/XWiki/pages/KimJd" rel="http://www.xwiki.org/rel/parent"/>
	<link href="http://jdkim-test.cloudapp.net:8080/xwiki/rest/wikis/xwiki/spaces/XWiki/pages/NewPagr/history" rel="http://www.xwiki.org/rel/history"/>
	<link href="http://jdkim-test.cloudapp.net:8080/xwiki/rest/syntaxes" rel="http://www.xwiki.org/rel/syntaxes"/>
	<link href="http://jdkim-test.cloudapp.net:8080/xwiki/rest/wikis/xwiki/spaces/XWiki/pages/NewPagr" rel="self"/>
	<link href="http://jdkim-test.cloudapp.net:8080/xwiki/rest/wikis/xwiki/classes/XWiki.NewPagr" rel="http://www.xwiki.org/rel/class"/>
	<id>xwiki:XWiki.NewPagr</id>
	<fullName>XWiki.NewPagr</fullName>
	<wiki>xwiki</wiki>
	<space>XWiki</space>
	<name>NewPagr</name>
	<title>NewPage</title>
	<parent>XWiki.KimJd</parent>
	<parentId>xwiki:XWiki.KimJd</parentId>
	<version>1.1</version>
	<author>XWiki.KimJd</author>
	<xwikiRelativeUrl>
	http://jdkim-test.cloudapp.net:8080/xwiki/bin/view/XWiki/NewPagr
	</xwikiRelativeUrl>
	<xwikiAbsoluteUrl>
	http://jdkim-test.cloudapp.net:8080/xwiki/bin/view/XWiki/NewPagr
	</xwikiAbsoluteUrl>
	<translations/>
	<syntax>xwiki/2.1</syntax>
	<language/>
	<majorVersion>1</majorVersion>
	<minorVersion>1</minorVersion>
	<created>2014-12-18T05:27:55Z</created>
	<creator>XWiki.KimJd</creator>
	<modified>2014-12-18T05:29:28Z</modified>
	<modifier>XWiki.KimJd</modifier>
	<comment/>
	<content>
	NewPage Content
	 == Heading 2==
	 = Heading 1=
	 * List item
	 * List item
	 {{velocity}}
	 #* Your velocity code here *#
	 {{/velocity}}
	 * List item
	</content>
</page>

 */


/**
 * XWiki Provider
 * 
 * XWiki 접근방법 참조
 *  : https://network.xwiki.com/xwiki/bin/view/DocXE35En/XWikiQueryGuide
 * @author jdkim
 *
 */
public class XWikiProvider extends BaseProvider{
	
	// test xwiki host : jdkim-test.cloudapp.net:8080
	String updateFormat = "%s/rest/wikis/xwiki/query";
	//		"http://%s/xwiki/rest/wikis/xwiki/query?
	//		q=where doc.author!='XWiki.superadmin'&type=hql&number=10&media=xml";
	
	/**
	 * 생성자
	 * @param url
	 */
	public XWikiProvider(String url){
		host = url;
	}
	
	/**
	 * 목록 검색
	 * @param query
	 * @param media
	 * @param number
	 * @return
	 */
	public JSONObject search(String query, String media, String number ){
		
		if(query == null) return null;
		
		StringBuilder sb = new StringBuilder();
		sb.append(String.format(updateFormat, host));
		sb.append("?q=");
		sb.append(query);
		
		sb.append("&type=hql");
		
		if(media != null)	// xml or json
		{
			sb.append("&media=");
			sb.append(media);
		}
			
		sb.append("&number=");
		if(number == null)
			sb.append("10");	// 기본 10개로..
		else
			sb.append(number);
		
		
		
		// 응답 정보
		JSONObject result = new JSONObject();
		
		// 페이지별 요청 리스트
		ArrayList<String> pageUrlList = new ArrayList<String>();
		
		
		HttpResponse<JsonNode> jsonResponse;
		try {
			SLog.d("xwiki search", sb.toString());
			
			jsonResponse = Unirest.get(sb.toString()).asJson();
			JSONObject root = jsonResponse.getBody().getObject();
			SLog.d("response", root.toString());
			
			JSONArray searchResults = root.getJSONArray("searchResults");
			for(int i=0;i<searchResults.length();i++)
			{
				JSONObject resultItem = (JSONObject) searchResults.get(i);
				JSONArray links = resultItem.getJSONArray("links");
				
				String link = null;
				if(links != null && links.length() > 0){
					JSONObject linkItem = (JSONObject) links.get(0);
					link = linkItem.getString("href");
					//SLog.d("link", link);
				}
				
				String author = resultItem.getString("author");
				
				if(link != null && author != null && author.length() > 0)
				{
					SLog.d("page", author + " / " + link);
					pageUrlList.add(link);
				}
				
			}
			
			SLog.d("requet total count", pageUrlList.size());
			
			JSONArray updates = new JSONArray();
			for(String reqUrl : pageUrlList){
				
				jsonResponse = Unirest.get(reqUrl + "?media=json").asJson();
				root = jsonResponse.getBody().getObject();
				SLog.d("response", root.toString());
				
				JSONObject item = new JSONObject();
				setItemValue(root, item, "title");
				setItemValue(root, item, "content");
				setItemValue(root, item, "author");
				setItemValue(root, item, "xwikiAbsoluteUrl");
				setItemValue(root, item, "modifier");
				setItemValue(root, item, "modified");
				
				updates.put(item);
			}
            
			JSONArray sortedUpdates = JsonSortUtil.sort(updates, "modified");
			result.put("updates", sortedUpdates);
			
		} catch (UnirestException | JSONException e) {
			e.printStackTrace();
			result.put(Const.JSON_KEY_ERROR, e.getLocalizedMessage());
		}
		
		return result;
	}
	
	/**
	 * json에 객체 주입
	 * @param src
	 * @param dest
	 * @param key
	 */
	private void setItemValue(JSONObject src, JSONObject dest, String key) {
		
		if(src.has(key))
			dest.put(key, src.get(key));
		
	}

	private final String query = "where doc.author!=\'XWiki.superadmin\'";
	
	/**
	 * 최근 업데이트 목록 조회
	 * @return
	 */
	public JSONObject getUpdates(){
		
		JSONObject result = new JSONObject();
		
		if(!isHost())	return result.put(Const.JSON_KEY_ERROR, "host is net set!");
		
		try {
			result = search(URLEncoder.encode(query,"UTF-8"), "json", "10");
			
			if(result == null)
			{	
				result = new JSONObject();
				return result.put(Const.JSON_KEY_ERROR, "search fail : " + query);
			}
			
			
		} catch (UnsupportedEncodingException | JSONException e) {
			e.printStackTrace();
			return result.put(Const.JSON_KEY_ERROR, e.getLocalizedMessage());
		}
		
		
		return result;
	}
}
