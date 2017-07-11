package it.caldesi.webbot.utils;

public class Utils {

	public static String adjustUrl(String url) {
		if (!url.startsWith("http://") && !url.startsWith("https://"))
			url = "http://" + url;
		return url;
	}

}
