package org.ritheshgopal.httpserver;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ritheshgopal.httpserver.util.HttpUtil;
import org.ritheshgopal.httpserver.vo.UserVO;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class SimpleHttpServer {

	private static List<UserVO> users = new ArrayList<>();
	public static void main(String[] args)  {
		try {
			HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
			server.createContext("/test", new TestHandler());
			server.createContext("/info", new InfoHandler());
			server.createContext("/addUser", new AddUserHandler());
			server.createContext("/getUsers", new GetUserHandler());
			server.setExecutor(null);
			server.start();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	static class TestHandler implements HttpHandler{

		@Override
		public void handle(HttpExchange exchange) throws IOException {
			byte[] response = "This is a test page".getBytes();
			exchange.sendResponseHeaders(200, response.length);
			OutputStream os = exchange.getResponseBody();
			os.write(response);
			os.close();
		}
		
	}
	static class InfoHandler implements HttpHandler{

		@Override
		public void handle(HttpExchange exchange) throws IOException {
			byte[] response = "Use /add to add a id and name. Example: localhost:8000/addUser?id=1&name=RitheshGopal".getBytes();
			exchange.sendResponseHeaders(200, response.length);
			OutputStream os = exchange.getResponseBody();
			os.write(response);
			os.close();
		}
		
	}
	static class AddUserHandler implements HttpHandler{

		@Override
		public void handle(HttpExchange exchange) throws IOException {
			int initialSize = users.size();
			if(exchange.getRequestURI().getPath()=="addUser") {
				exchange.sendResponseHeaders(404, "Content not found".length());
				OutputStream os = exchange.getResponseBody();
				os.write("Content not found".getBytes());
				os.close();
			}else {
			Map<String, String> requestQuery = HttpUtil.convertQueryToMap(exchange.getRequestURI().getQuery());
			
			for (Entry<String, String> entry : requestQuery.entrySet()) {
				users.add(new UserVO(Long.valueOf(entry.getKey()), entry.getValue()));
			}
			if(users.size()>initialSize) {

				exchange.sendResponseHeaders(200, "Successfully added".length());
				OutputStream os = exchange.getResponseBody();
				os.write("Successfully added".getBytes());
				os.close();
			
			}
			}
		}
		
	}
	
	 static class GetUserHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange exchange) throws IOException {
			String res = "";
			for (UserVO userVO : users) {
				res += userVO.getId();
				res+=" ";
				res += userVO.getName();
				
			}
			ObjectMapper objectMapper = new ObjectMapper();
			exchange.sendResponseHeaders(200, objectMapper.writeValueAsString(users).length());
			OutputStream os = exchange.getResponseBody();
			os.write(objectMapper.writeValueAsString(users).getBytes());
			os.close();
		}
		
	}
	
}
