package com.levent.example;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import com.levent.classloader.DerbyServerClassLoader;
import com.levent.derbyutility.DbSingleton;
import com.levent.greeter.IGreet;

public class ClientClassLoaderDBVersionDemo {
	
	// apache derby in-memory db
	private static final String connectionString = "jdbc:derby://localhost:1527/memory:myDB;create=true";
	private static final String classFileName1 = "Greet1.class";
	private static final String classFileName2 = "Greet2.class";
	private static final String className1 = "com.levent.greeter.Greet1";
	private static final String className2 = "com.levent.greeter.Greet2";
	
	public static void main(String[] args) {
		prepareClass();
		
		try {
			IGreet iGreet = null;
			
			DerbyServerClassLoader cl = new DerbyServerClassLoader(connectionString);
			
			Class clazz1 = cl.findClass(className1);
			iGreet = (IGreet) clazz1.newInstance();
			System.out.println("Version 1 IGreet.getQuote() : " + iGreet.getQuote());
			
			Class clazz2 = cl.findClass(className2);
			iGreet = (IGreet) clazz2.newInstance();
			System.out.println("Version 2 IGreet.getQuote() : " + iGreet.getQuote());			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	private static void prepareClass() {
		DbSingleton instance = DbSingleton.getInstance();
		
		Connection conn = null;
		
		try {
			conn = instance.getConnection();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		Statement sta;
		
		if (conn != null) {
			try {
				sta = conn.createStatement();
				int count = sta
						.executeUpdate("CREATE TABLE CLASSES (CLASS_NAME VARCHAR(50), CLASS BLOB)");
				System.out.println("CLASSES Table created");
				sta.close();
				
				PreparedStatement psta = conn.prepareStatement("INSERT INTO CLASSES (CLASS_NAME, CLASS) values (?, ?)");
				byte[] bytes = null;
				InputStream blobObject = null;
				
				psta.setString(1, className1);
				bytes = readJarFileAsByteArray(classFileName1);
				blobObject = new ByteArrayInputStream(bytes); 
				psta.setBlob(2, blobObject, bytes.length);
				count = psta.executeUpdate();
				
				psta.setString(1, className2);
				bytes = readJarFileAsByteArray(classFileName2);
				blobObject = new ByteArrayInputStream(bytes); 
				psta.setBlob(2, blobObject, bytes.length);
				count += psta.executeUpdate();
				
				System.out.println(count + " record(s) created.");
				sta.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}
		
	private static byte[] readJarFileAsByteArray(String classFileName) {
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		
	    File file = new File(s + "/lib/" + classFileName);
	    byte[] fileData = new byte[(int) file.length()];
	    DataInputStream dis;
		try {
			dis = new DataInputStream(new FileInputStream(file));
		    dis.readFully(fileData);
		    dis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	    return fileData;
	}

}
