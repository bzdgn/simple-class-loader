package com.levent.classloader;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DerbyServerClassLoader extends ClassLoader {
	
	private ClassLoader parent;
	private String connectionString;
	
	public DerbyServerClassLoader(String connectionString) {
		this(ClassLoader.getSystemClassLoader(), connectionString);
	}
	
	public DerbyServerClassLoader(ClassLoader parent, String connectionString) {
		super(parent);
		this.parent = parent;
		this.connectionString = connectionString;
	}
	
	@Override
	public Class<?> findClass(String name) throws ClassNotFoundException {
		Class cls = null;
		
		try {
			cls = parent.loadClass(name);			// Delegate to the parent Class Loader
		} catch (ClassNotFoundException clnfE) {	// If parent fails, try to locate and load the class
			byte[] bytes = new byte[0];
			try {
				bytes = loadClassFromDatabase(name);
			} catch (SQLException sqlE) {
				throw new ClassNotFoundException("Unable to load class", sqlE);
			}
			return defineClass(name, bytes, 0, bytes.length);
		}
		
		return cls;
	}
	
	private byte[] loadClassFromDatabase(String name) throws SQLException {
		PreparedStatement pstmt = null;
		Connection connection = null;
		
		try {
			connection = DriverManager.getConnection(connectionString);
			
			String sql = "SELECT CLASS FROM CLASSES WHERE CLASS_NAME = ?";
			pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, name);
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				Blob blob = rs.getBlob(1);
				byte[] data = blob.getBytes(1, (int) blob.length());
				return data;
			}
		} catch (SQLException e) {
			System.out.println("Unexpected exception: " + e.toString());
		} catch (Exception e) {
			System.out.println("Unexpected exception: " + e.toString());
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			
			if(connection != null) {
				connection.close();
			}
		}
		
		return null;
	}
	
}
