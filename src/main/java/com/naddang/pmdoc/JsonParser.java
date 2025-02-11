package com.naddang.pmdoc;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.FileReader;
import java.io.IOException;

public class JsonParser {
	public static JsonObject parseJson(String filePath) {
		try (FileReader reader = new FileReader(filePath)) {
			return new Gson().fromJson(reader, JsonObject.class);
		} catch (IOException e) {
			System.err.println("FILE READ ERROR " + e.getMessage());
			return null;
		}
	}
}