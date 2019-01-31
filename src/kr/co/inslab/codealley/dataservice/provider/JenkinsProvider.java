package kr.co.inslab.codealley.dataservice.provider;

import java.io.IOException;

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

import kr.co.inslab.codealley.dataservice.common.Const;
import kr.co.inslab.codealley.dataservice.log.SLog;


/* 
 <feed 
    xmlns="http://www.w3.org/2005/Atom">
    <title>All all builds</title>
    <link type="text/html" href="http://codealley-dev.cloudapp.net:8080/jenkins/view/All/" rel="alternate"/>
    <updated>2014-12-12T09:29:18Z</updated>
    <author>
        <name>Jenkins Server</name>
    </author>
    <id>urn:uuid:903deee0-7bfa-11db-9fe1-0800200c9a66</id>
    <entry>
        <title>signpost_dev_build #16 (stable)</title>
        <link type="text/html" href="http://codealley-dev.cloudapp.net:8080/jenkins/view/All/job/signpost_dev_build/16/" rel="alternate"/>
        <id>tag:hudson.dev.java.net,2014:signpost_dev_build:2014-12-12_18-29-18</id>
        <published>2014-12-12T09:29:18Z</published>
        <updated>2014-12-12T09:29:18Z</updated>
    </entry>
  </feed>
 */

/**
 * Jenkins의 WEB API를 이용하여 data획득 클래스 
 *
 * @author  jdkim
 */
public class JenkinsProvider extends BaseProvider{

	String rssFormat ="%s/view/All/rssAll";
	// http://codealley-dev.cloudapp.net:8080/jenkins/view/All/rssAll
	String statusKeyword = " (broken ";
	
	/**
	 * 생성자
	 * @param toolUrl
	 */
	public JenkinsProvider(String toolUrl) {
		host = toolUrl;
	}

	/*
	 2014.12.16 RSS로 빌드정보 획득 확인.
	 빌드결과의 상태정보를 별도로 있지않이 XML텍스트 정보에서 추출분석
	 상태정보 분석 키워드는 " (broken "
	 빌드실패시 위의 키워드가 title에 추가됨.
	 */
	
	/**
	 * 빌드 이력정보
	 * @return
	 */
	public JSONObject getBuilds() {
		
		JSONObject result 		= new JSONObject();
		JSONArray builds 	= new JSONArray();
		
		String url = String.format(rssFormat, host);
		
		try {
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document dom = db.parse(url);
			Element root = dom.getDocumentElement();	//<feed>
			
			NodeList entryList = root.getElementsByTagName("entry");
			SLog.d("entryList",  ""+entryList.getLength());
			
			String nodeName;
			String nodeValue;
			JSONObject buildItem;
			
			for(int i=0;i<entryList.getLength();i++)
			{
				Node entry = entryList.item(i);
				NodeList entryChilds = entry.getChildNodes();

				buildItem = new JSONObject();
				
				for(int j=0;j<entryChilds.getLength();j++)
				{
					Node child = entryChilds.item(j);
					
					nodeName 	= child.getNodeName();
					nodeValue 	= child.getTextContent();
					SLog.d("child NodeName",  ""+child.getNodeName());
					SLog.d("child Content ",  ""+child.getTextContent());
					
					if(nodeName.equalsIgnoreCase("link")){
						
						Node link = child.getAttributes().getNamedItem("href");
						nodeValue = link.getNodeValue();
						SLog.d("link", link.getNodeValue());
						
					}
					else if(nodeName.equalsIgnoreCase("id"))
					{
						// "id" is pass.. 
						continue;
					}
					else
					{
						
					}
					
					//빌드 결과 분석
					if(nodeName.equalsIgnoreCase("title")){
						if(nodeValue.contains(statusKeyword))
							buildItem.put("status", "fail");
						else
							buildItem.put("status", "success");
					}
					
					buildItem.put(nodeName, nodeValue);
					
				} // end : for loop j
				
				builds.put(buildItem);
				
			} // end : for loop i
			
		} 
		catch (ParserConfigurationException e) {
			e.printStackTrace();
			return result.put(Const.JSON_KEY_ERROR, e.getLocalizedMessage());
		} catch (SAXException e) {
			e.printStackTrace();
			return result.put(Const.JSON_KEY_ERROR, e.getLocalizedMessage());
		} catch (IOException e) {
			e.printStackTrace();
			return result.put(Const.JSON_KEY_ERROR, e.getLocalizedMessage());
		}
		
		return result.put("builds", builds);
	}

	/**
	 * 빌드 성공, 실패 별 카운트 조회
	 * @return
	 */
	public JSONObject getBuildCount()
	{
		JSONObject result 		= new JSONObject();
		
		try
		{
			JSONObject buildsResult 		= getBuilds();
			
			JSONArray builds = buildsResult.getJSONArray("builds");
			
			int success_count = 0;
			int fail_count = 0;
			
			for(int i=0;i<builds.length();i++)
			{
				JSONObject item = builds.getJSONObject(i);
				
				if(item.getString("status").equalsIgnoreCase("success"))
					success_count++;
				else
					fail_count++;
			}
			
			result.put("success_count", success_count);
			result.put("fail_count", fail_count);
			 
		}
		catch (JSONException e)
		{
			e.printStackTrace();
			return result.put(Const.JSON_KEY_ERROR, e.getLocalizedMessage());
		}
		
		return result;
	}
	
}
