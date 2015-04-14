package com.triptailor.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DatabaseHelper {
	
	protected Connection connect;
	protected PreparedStatement statement;
	protected ResultSet resultSet;
	protected ResultSet resultSet2;
	
	public DatabaseHelper() {
		try {
			DataSource ds = (DataSource) new InitialContext().lookup("java:/comp/env/jdbc/TripTailorDB");
			connect = ds.getConnection();
		} catch (NamingException | SQLException e) {}
	}
	
	public void close() {
		try {
			if(resultSet2 != null)
				resultSet2.close();
			if(resultSet != null)
				resultSet.close();
			if (statement != null)
				statement.close();
			if (connect != null)
				connect.close();
		} catch (SQLException e) {}
	}
}