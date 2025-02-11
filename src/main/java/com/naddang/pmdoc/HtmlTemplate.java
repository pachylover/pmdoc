package com.naddang.pmdoc;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class HtmlTemplate {
	
	public static String createHtml(JsonObject jsonObject) {
		// "info"와 "item"을 가져오는 부분
		
		// 제목, 설명, 변수를 가져와서 HTML 에 추가
		
		return "<html lang=\"en\">" +
				"<head>" +
				"<meta charset=\"UTF-8\">" +
				"<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
				"<title>Postman Collection</title>" +
				"</head>" +
				"<body>" +
				loadCssFromClasspath("/style.css") +
				loadScriptFromClasspath("/default.js") +
				createTitleHtml(jsonObject) +
				createDescriptionHtml(jsonObject) +
				createVariablesHtml(jsonObject) +
				
				// "item"을 가져와서 HTML 에 추가
				createRequestsHtml(jsonObject) +
				"</body></html>";
	}
	
	// JsonObject 를 안전하게 가져오는 메서드
	private static JsonObject getJsonObject(JsonObject jsonObject, String key) {
		if (jsonObject != null && jsonObject.has(key)) {
			return jsonObject.getAsJsonObject(key);
		}
		return null;
	}
	
	// JsonArray 를 안전하게 가져오는 메서드
	private static JsonArray getJsonArray(JsonObject jsonObject, String key) {
		if (jsonObject != null && jsonObject.has(key)) {
			return jsonObject.getAsJsonArray(key);
		}
		return null;
	}
	
	/**
	 * @param jsonObject Postman JSON 파일을 파싱한 JsonObject
	 * @return HTML 문서의 제목을 생성
	 */
	public static String createTitleHtml(JsonObject jsonObject) {
		JsonObject info = getJsonObject(jsonObject, "info");
		return "<h1>" + (info != null && info.get("name") != null ? info.get("name").getAsString() : "No Name") + "</h1>";
	}
	
	/**
	 * @param jsonObject Postman JSON 파일을 파싱한 JsonObject
	 * @return HTML 문서의 설명을 생성
	 */
	public static String createDescriptionHtml(JsonObject jsonObject) {
		JsonObject info = getJsonObject(jsonObject, "info");
		return "<p>" + (info != null && info.get("description") != null ? info.get("description").getAsString() : "No Description") + "</p>";
	}
	
	/**
	 * @param jsonObject Postman JSON 파일을 파싱한 JsonObject
	 * @return HTML 문서의 변수 섹션을 생성
	 */
	public static String createVariablesHtml(JsonObject jsonObject) {
		JsonArray variables = getJsonArray(jsonObject, "variable");
		StringBuilder htmlContent = new StringBuilder("<h2>Variables:</h2>");
		htmlContent.append("<ul>");
		if (variables != null && !variables.isEmpty()) {
			for (int i = 0; i < variables.size(); i++) {
				JsonObject variable = variables.get(i).getAsJsonObject();
				String key = variable.has("key") ? variable.get("key").getAsString() : "No Key";
				String value = variable.has("value") ? variable.get("value").getAsString() : "No Value";
				htmlContent.append("<li>")
						.append("<strong>").append(key).append("</strong>: ").append(value)
						.append("</li>");
			}
		} else {
			htmlContent.append("<p>No variables found in the collection.</p>");
		}
		htmlContent.append("</ul>");
		return htmlContent.toString();
	}
	
	/**
	 * @param jsonObject Postman JSON 파일을 파싱한 JsonObject
	 * @return HTML 문서의 요청 섹션을 생성
	 */
	public static String createRequestsHtml(JsonObject jsonObject) {
		JsonArray items = getJsonArray(jsonObject, "item");
		StringBuilder htmlContent = new StringBuilder("<h2>Requests:</h2>");
		
		htmlContent.append("<ul>");
		
		if (items != null && !items.isEmpty()) {
			for (int i = 0; i < items.size(); i++) {
				JsonObject item = items.get(i).getAsJsonObject();
				String name = item.has("name") ? item.get("name").getAsString() : "No Name";
				// Collapsible li 추가
				htmlContent.append("<li class=\"collapsible\"><h3>").append(name).append("</h3></li>");
				htmlContent.append("<div class=\"collapsible-content\">");
				JsonArray requests = getJsonArray(item, "item");
				if (requests != null && !requests.isEmpty()) {
					htmlContent.append("<ul>");
					for (int j = 0; j < requests.size(); j++) {
						JsonObject request = requests.get(j).getAsJsonObject();
						String requestName = request.has("name") ? request.get("name").getAsString() : "No Name";
						// Collapsible li 추가 (하위 요청)
						htmlContent.append("<li class=\"collapsible\"><h4>").append(requestName).append("</h4></li>");
						htmlContent.append("<div class=\"collapsible-content\">");
						JsonObject requestObject = getJsonObject(request, "request");
						if (requestObject != null) {
							String method = requestObject.has("method") ? requestObject.get("method").getAsString() : "No Method";
							htmlContent.append("<p><strong>Method:</strong> ").append(method).append("</p>");
							JsonArray headers = getJsonArray(requestObject, "header");
							if (headers != null && !headers.isEmpty()) {
								htmlContent.append("<p><strong>Headers:</strong></p><ul>");
								for (int k = 0; k < headers.size(); k++) {
									JsonObject header = headers.get(k).getAsJsonObject();
									String key = header.has("key") ? header.get("key").getAsString() : "No Key";
									String value = header.has("value") ? header.get("value").getAsString() : "No Value";
									htmlContent.append("<li>")
											.append("<strong>").append(key).append("</strong>: ").append(value)
											.append("</li>");
								}
								htmlContent.append("</ul>");
							}
							JsonObject body = getJsonObject(requestObject, "body");
							if (body != null) {
								String mode = body.has("mode") ? body.get("mode").getAsString() : "No Mode";
								htmlContent.append("<p><strong>Body Mode:</strong> ").append(mode).append("</p>");
								if (body.has("raw")) {
									htmlContent.append("<p><strong>Body:</strong></p><pre>")
											.append(body.get("raw").getAsString());
									htmlContent.append("</pre>");
								}
							}
							JsonObject url = getJsonObject(requestObject, "url");
							if (url != null) {
								String raw = url.has("raw") ? url.get("raw").getAsString() : "No URL";
								htmlContent.append("<p><strong>URL:</strong> ").append(raw).append("</p>");
							}
						}
						htmlContent.append("</div>"); // 하위 요청 내용 닫기
						htmlContent.append("</li>");
					}
					htmlContent.append("</ul>");
				}
				htmlContent.append("</div>"); // 요청 그룹 내용 닫기
				htmlContent.append("</li>");
			}
		} else {
			htmlContent.append("<p>No requests found in the collection.</p>");
		}
		htmlContent.append("</ul>");
		return htmlContent.toString();
	}
	
	/**
	 * HTML 문서의 스타일을 반환
	 * @return HTML 문서의 스타일
	 */
	private static String loadCssFromClasspath(String path) {
		try (InputStream inputStream = HtmlTemplate.class.getResourceAsStream(path)) {
			if (inputStream == null) {
				throw new Exception("CSS file not found in classpath: " + path);
			}
			Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8);
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("<style>");
			while (scanner.hasNextLine()) {
				stringBuilder.append(scanner.nextLine()).append("\n");
			}
			stringBuilder.append("</style>");
			return stringBuilder.toString();
		} catch (Exception e) {
			System.err.println("ERROR: CSS FILE IS NOT EXIST " + e.getMessage());
			return "";
		}
	}
	
	/**
	 * HTML 문서의 스크립트를 반환
	 * @return HTML 문서의 스크립트
	 */
	private static String loadScriptFromClasspath(String path) {
		try (InputStream inputStream = HtmlTemplate.class.getResourceAsStream(path)) {
			if (inputStream == null) {
				throw new Exception("CSS file not found in classpath: " + path);
			}
			Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8);
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("<script type=\"text/javascript\">");
			while (scanner.hasNextLine()) {
				stringBuilder.append(scanner.nextLine()).append("\n");
			}
			stringBuilder.append("</script>");
			return stringBuilder.toString();
		} catch (Exception e) {
			System.err.println("JS FILE IS NOT EXIST " + e.getMessage());
			return "";
		}
	}
}