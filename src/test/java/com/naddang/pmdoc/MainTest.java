package com.naddang.pmdoc;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
	
	@org.junit.jupiter.api.Test
	void main() {
		String[] args = new String[] { "src/test/resources/PostmanEcho.postman_collection.json" };
		Main.main(args);
	}
}