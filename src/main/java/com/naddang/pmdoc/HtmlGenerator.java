package com.naddang.pmdoc;

import java.io.FileWriter;
import java.io.IOException;

public class HtmlGenerator {
	public static void generateHtml(String outputPath, String htmlContent) {
		try (FileWriter writer = new FileWriter(outputPath)) {
			writer.write(htmlContent);
			System.out.println("HTML FILE IS GENERATED. :" + outputPath);
		} catch (IOException e) {
			System.err.println("ERROR: ERROR OCCURRED. :" + e.getMessage());
		}
	}
}