package it.caldesi.webbot.utils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

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

}
