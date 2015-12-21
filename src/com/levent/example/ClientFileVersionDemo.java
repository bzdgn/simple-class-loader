package com.levent.example;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.levent.greeter.IGreet;

public class ClientFileVersionDemo {

	public static void main(String[] args) {
		//Relative path
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		
		URL url1, url2;
		
		try {
			// Version1
			url1 = new URL("file:///" + s + "/lib/greeter1.jar");
			URLClassLoader ucl1 = new URLClassLoader( new URL[] {url1} );
			Class clazz1 = ucl1.loadClass("com.levent.greeter.Greet1");
			IGreet iGreet1 = (IGreet) clazz1.newInstance();
			
			// Version2
			url2 = new URL("file:///" + s + "/lib/greeter2.jar");
			URLClassLoader ucl2 = new URLClassLoader( new URL[] {url2} );
			Class clazz2 = ucl2.loadClass("com.levent.greeter.Greet2");
			IGreet iGreet2 = (IGreet) clazz2.newInstance();
			
			System.out.println("Version 1 IGreet.getQuote() : " + iGreet1.getQuote());
			System.out.println("Version 2 IGreet.getQuote() : " + iGreet2.getQuote());
			
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
