package org.ritheshgopal.httpserver.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.ritheshgopal.httpserver.vo.UserVO;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

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
	
	public static UserVO convertRequestJson(String requestJson) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(requestJson, UserVO.class);
	}
	public static String readInputStream(InputStream input) throws IOException {
		BufferedReader bf = new BufferedReader(new InputStreamReader(input));
		StringBuilder inputString = new StringBuilder();
		String line;
        while ((line = bf.readLine()) != null) {
            inputString.append(line);
        }
		
		return inputString.toString();
	}
	public static boolean validateRequest(HttpExchange exchange, String uri, String method) throws IOException {
		
		OutputStream os = exchange.getResponseBody();
		byte[] response = null;
		if(!uri.equalsIgnoreCase(exchange.getRequestURI().getPath())) {
			response = HttpConstants.NOT_FOUND.getBytes();
			exchange.sendResponseHeaders(HttpConstants.NOT_FOUND_CODE, response.length);
		}
		else if(!method.equalsIgnoreCase(exchange.getRequestMethod())) {
			response = HttpConstants.METHOD_NOT_ALLOWED.getBytes();
			exchange.sendResponseHeaders(HttpConstants.METHOD_NOT_ALLOWED_CODE, response.length);
		}
		if(response!=null) {
			os.write(response);
			os.close();
			return false;
		}
		return true;
	}
}
