package com.triptailor.database;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

public class SearchesDatabase extends DatabaseHelper {
	
	public SearchesDatabase() {
		super();
	}
	
	public int saveHostelSearch(String sessionId, String hostelName, String city, int adwords) {
		try {
			statement = connect.prepareStatement("SELECT id FROM location WHERE city = ?");
			statement.setString(1, city);
			resultSet = statement.executeQuery();
			
			if(resultSet.next()) {
				int cityId = resultSet.getInt(1);
				
				statement = connect.prepareStatement("SELECT id FROM hostel WHERE name = ?");
				statement.setString(1, hostelName);
				resultSet = statement.executeQuery();
				
				if(resultSet.next()) {
					statement = connect.prepareStatement("INSERT INTO search (sess, city_id, hostel_id, timestamp, adwords) "
							+ "VALUES (?, ?, ?, ?, ?)",
							Statement.RETURN_GENERATED_KEYS);
					statement.setString(1, sessionId);
					statement.setInt(2, cityId);
					statement.setInt(3, resultSet.getInt(1));
					statement.setLong(4, System.currentTimeMillis());
					statement.setInt(5, adwords);
					statement.executeUpdate();
					
					resultSet2 = statement.getGeneratedKeys();
					resultSet2.next();
					
					return resultSet2.getInt(1);
				}
				else
					return -1;
			}
			else
				return -1;
		} catch (SQLException e) {
			System.out.println(e);
			return -1;
		}
	}
	
	public int saveTagsSearch(String sessionId, List<String> tags, String city, int adwords) {
		try {
			statement = connect.prepareStatement("SELECT id FROM location WHERE city = ?");
			statement.setString(1, city);
			resultSet = statement.executeQuery();
			
			if(resultSet.next()) {
				int cityId = resultSet.getInt(1);
				
				String query = "SELECT id FROM attribute WHERE name = ?";
				for(int i = 1; i < tags.size(); i++)
					query += " OR name = ?";
				statement = connect.prepareStatement(query);
				for(int i = 0; i < tags.size(); i++)
					statement.setString(i + 1, tags.get(i));
				resultSet = statement.executeQuery();
				
				statement = connect.prepareStatement("INSERT INTO search (sess, city_id, hostel_id, timestamp, adwords) "
						+ "VALUES (?, ?, ?, ?, ?)",
						Statement.RETURN_GENERATED_KEYS);
				statement.setString(1, sessionId);
				statement.setInt(2, cityId);
				statement.setNull(3, Types.INTEGER);
				statement.setLong(4, System.currentTimeMillis());
				statement.setInt(5, adwords);
				statement.executeUpdate();
				
				resultSet2 = statement.getGeneratedKeys();
				resultSet2.next();
				int searchId = resultSet2.getInt(1);
				
				while(resultSet.next()) {
					statement = connect.prepareStatement("INSERT INTO attribute_search (attribute_id, search_id) VALUES (?, ?)");
					statement.setInt(1, resultSet.getInt(1));
					statement.setInt(2, searchId);
					statement.executeUpdate();
				}
				
				return searchId;
			}
			else
				return -1;
		} catch (SQLException e) {
			System.out.println(e);
			return -1;
		}
	}
	
	public int saveCitySearch(String sessionId, String city, int adwords) {
		try {
			statement = connect.prepareStatement("SELECT id FROM location WHERE city = ?");
			statement.setString(1, city);
			resultSet = statement.executeQuery();
			
			if(resultSet.next()) {
				int cityId = resultSet.getInt(1);
				
				statement = connect.prepareStatement("INSERT INTO search (sess, city_id, hostel_id, timestamp, adwords) "
						+ "VALUES (?, ?, ?, ?, ?)",
						Statement.RETURN_GENERATED_KEYS);
				statement.setString(1, sessionId);
				statement.setInt(2, cityId);
				statement.setNull(3, Types.INTEGER);
				statement.setLong(4, System.currentTimeMillis());
				statement.setInt(5, adwords);
				statement.executeUpdate();
				
				resultSet = statement.getGeneratedKeys();
				resultSet.next();
				
				return resultSet.getInt(1);
			}
			else
				return -1;
		} catch (SQLException e) {
			System.out.println(e);
			return -1;
		}
	}
}
