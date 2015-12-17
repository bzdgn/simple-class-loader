package com.levent.example;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

	public static void main(String[] args) {
		//Relative path
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		
		URL url;
		
		try {
			url = new URL("file:///" + s + "/lib/greeter.jar");
			URLClassLoader ucl = new URLClassLoader( new URL[] {url} );
			Class clazz = ucl.loadClass("com.levent.Greet");
			Object o = clazz.newInstance();
			System.out.println(o.toString());	// print out the hashCode of the class
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

	}

}
