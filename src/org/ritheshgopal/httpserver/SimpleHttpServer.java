package org.ritheshgopal.httpserver;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.ritheshgopal.httpserver.util.HttpUtil;
import org.ritheshgopal.httpserver.vo.UserVO;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
			e.printStackTrace();
		}
	}
	static class TestHandler implements HttpHandler{

		@Override
		public void handle(HttpExchange exchange) throws IOException {

			if(HttpUtil.validateRequest(exchange,"/test","GET")) {
				byte[] response = "This is a test page".getBytes();
				exchange.sendResponseHeaders(200, response.length);
				OutputStream os = exchange.getResponseBody();
				os.write(response);
				os.close();
			}
		}
	}
	static class InfoHandler implements HttpHandler{

		@Override
		public void handle(HttpExchange exchange) throws IOException {
			if(HttpUtil.validateRequest(exchange,"/info","GET")) {
				byte[] response = "Use /add to add a id and name. Example: localhost:8000/addUser?id=1&name=RitheshGopal".getBytes();
				exchange.sendResponseHeaders(200, response.length);
				OutputStream os = exchange.getResponseBody();
				os.write(response);
				os.close();
			}
		}
	}
	static class AddUserHandler implements HttpHandler{

		@Override
		public void handle(HttpExchange exchange) throws IOException {
			UserVO user = null;
			OutputStream os = exchange.getResponseBody();
			
			if(HttpUtil.validateRequest(exchange,"/addUser","POST")) {
				int initialSize = users.size();
				if(exchange.getRequestURI().getPath()=="addUser") {
					exchange.sendResponseHeaders(404, "Content not found".length());
					os.write("Content not found".getBytes());
					os.close();
				}else {
					try {
						user = HttpUtil.convertRequestJson(HttpUtil.readInputStream(exchange.getRequestBody()));
					}catch(JsonParseException | JsonMappingException  exp) {
						exchange.sendResponseHeaders(417, "Expectation Failed, Invalid JSON ".length());
						os.write("Expectation Failed, Invalid JSON ".getBytes());
						os.close();
					}
					if(user!=null)
						users.add(user);
					if(users.size()>initialSize) {

						exchange.sendResponseHeaders(200, "Successfully added".length());
					
						os.write("Successfully added".getBytes());
						os.close();

					}else {
						exchange.sendResponseHeaders(500, "Internal Server Error".length());
						os.write("Internal Server Error".getBytes());
						os.close();
					}
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
