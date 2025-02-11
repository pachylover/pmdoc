package com.naddang.pmdoc;

import com.google.gson.JsonObject;

public class Main {
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Usage: pmdoc <Postman JSON FILE PATH>");
			return;
		}
		
		String jsonFilePath = args[0];
		JsonObject postmanJson = JsonParser.parseJson(jsonFilePath);
		if (postmanJson == null) {
			System.err.println("ERROR: Cannot parse JSON file");
			return;
		}
		String fileName = jsonFilePath.substring(jsonFilePath.lastIndexOf("/") + 1);
		String htmlContent = HtmlTemplate.createHtml(postmanJson);
		HtmlGenerator.generateHtml(fileName + ".html", htmlContent);
	}
}