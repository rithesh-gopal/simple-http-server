package org.ritheshgopal.httpserver.util;

import java.util.HashMap;
import java.util.Map;

public class HttpUtil {

	public static Map<String, String> convertQueryToMap(String query){
		Map<String, String> map = new HashMap<>();
		try {
		String[] paramSplit = query.split("&");
		
		map.put(paramSplit[0].split("=")[1], paramSplit[1].split("=")[1]);

		}catch(Exception exp) {
			exp.printStackTrace();
		}
		return map;
		
	}
}
