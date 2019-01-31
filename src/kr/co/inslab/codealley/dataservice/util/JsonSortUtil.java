package kr.co.inslab.codealley.dataservice.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonSortUtil {
	
	/**
	 * json객체 정렬(내림차순)
	 * @param updates
	 * @param sortKey
	 * @return 
	 */
	public static JSONArray sort(JSONArray updates, final String sortKey) {
		
		JSONArray sortedUpdates = new JSONArray();
		List<JSONObject> jsonValues = new ArrayList<JSONObject>();
	    for (int i = 0; i < updates.length(); i++) {
	        jsonValues.add(updates.getJSONObject(i));
	    }
	    
	    Collections.sort(jsonValues, new Comparator<JSONObject>() {
	        //You can change "Name" with "ID" if you want to sort by ID
	        //private static final String KEY_NAME = "modified";
	        @Override
	        public int compare(JSONObject a, JSONObject b) {
	            String valA = new String();
	            String valB = new String();

	            try {
	                valA = a.get(sortKey).toString();
	                valB = b.get(sortKey).toString();
	            } 
	            catch (JSONException e) {
	                //do something
	            }

	            return -valA.compareTo(valB);
	            //if you want to change the sort order, simply use the following:
	            //return -valA.compareTo(valB);
	        }
	    });

	    for (int i = 0; i < updates.length(); i++) {
	        sortedUpdates.put(jsonValues.get(i));
	    }
	    
	    return sortedUpdates;
	}
	
}
