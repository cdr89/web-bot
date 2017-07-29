package it.caldesi.webbot.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class FileUtils {

	public static String readResource(String jsPath) {
		try (InputStream resourceAsStream = FileUtils.class.getResourceAsStream(jsPath)) {
			try (Scanner s = new Scanner(resourceAsStream)) {
				s.useDelimiter("\\A");
				String script = s.hasNext() ? s.next() : "";
				return script;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

}
