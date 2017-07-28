package it.caldesi.webbot.utils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class FileUtils {

	private static final String START_DELIM = "\\$#";
	private static final String END_DELIM = "#\\$";

	public static String readFile(URL resource) throws IOException {
		String filePath = resource.getFile();
		if (filePath.startsWith("/") || filePath.startsWith("\\")) {
			filePath = filePath.substring(1);
		}
		// System.out.println(filePath);
		Path pathObj = Paths.get(filePath);
		byte[] encoded = Files.readAllBytes(pathObj);
		return new String(encoded, StandardCharsets.UTF_8);
	}

	public static String loadParametrizedJS(String js, Map<String, String> paramValues) {
		for (Map.Entry<String, String> entry : paramValues.entrySet()) {
			js = js.replaceAll(START_DELIM + entry.getKey() + END_DELIM, entry.getValue());
		}
		return js;
	}

}
