package it.caldesi.webbot.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Utils {

	public static String adjustUrl(String url) {
		if (!url.startsWith("http://") && !url.startsWith("https://"))
			url = "http://" + url;
		return url;
	}

	public static ArrayList<Class<?>> getClassesForPackage(String pkgname, boolean subClasses) {
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		// Get a File object for the package
		File directory = null;
		String fullPath;
		String relPath = pkgname.replace('.', '/');
		System.out.println("ClassDiscovery: Package: " + pkgname + " becomes Path:" + relPath);
		URL resource = ClassLoader.getSystemClassLoader().getResource(relPath);
		System.out.println("ClassDiscovery: Resource = " + resource);
		if (resource == null) {
			throw new RuntimeException("No resource for " + relPath);
		}
		fullPath = resource.getFile();
		System.out.println("ClassDiscovery: FullPath = " + resource);

		try {
			directory = new File(resource.toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(
					pkgname + " (" + resource
							+ ") does not appear to be a valid URL / URI.  Strange, since we got it from the system...",
					e);
		} catch (IllegalArgumentException e) {
			directory = null;
		}
		System.out.println("ClassDiscovery: Directory = " + directory);

		if (directory != null && directory.exists()) {
			// Get the list of the files contained in the package
			String[] files = directory.list();
			for (int i = 0; i < files.length; i++) {
				// we are only interested in .class files
				if (files[i].endsWith(".class")) {
					// removes the .class extension
					String className = pkgname + '.' + files[i].substring(0, files[i].length() - 6);
					if(!subClasses){
						if(className.contains("$"))
							continue;
					}
					System.out.println("ClassDiscovery: className = " + className);
					try {
						classes.add(Class.forName(className));
					} catch (ClassNotFoundException e) {
						throw new RuntimeException("ClassNotFoundException loading " + className);
					}
				}
			}
		} else {
			JarFile jarFile = null;
			try {
				String jarPath = fullPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
				jarFile = new JarFile(jarPath);
				Enumeration<JarEntry> entries = jarFile.entries();
				while (entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();
					String entryName = entry.getName();
					if (entryName.startsWith(relPath) && entryName.length() > (relPath.length() + "/".length())) {
						System.out.println("ClassDiscovery: JarEntry: " + entryName);
						String className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
						if(!subClasses){
							if(className.contains("$"))
								continue;
						}
						System.out.println("ClassDiscovery: className = " + className);
						try {
							classes.add(Class.forName(className));
						} catch (ClassNotFoundException e) {
							throw new RuntimeException("ClassNotFoundException loading " + className);
						}
					}
				}
			} catch (IOException e) {
				throw new RuntimeException(pkgname + " (" + directory + ") does not appear to be a valid package", e);
			} finally {
				if (jarFile != null)
					try {
						jarFile.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
		return classes;
	}

	public static Object getFieldValue(Class<?> c, String fieldName) throws Exception {
		Field field = null;
		try {
			field = c.getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(c.newInstance());
		} catch (Exception e) {
			// try to find the field into superclass hierarchy
			Class<?> superClass = c.getSuperclass();
			while (superClass != null) {
				try {
					field = c.getDeclaredField(fieldName);
					field.setAccessible(true);
					return field.get(c.newInstance());
				} catch (Exception e1) {
					superClass = superClass.getSuperclass();
				}
			}
		}

		throw new IllegalArgumentException();
	}

}
