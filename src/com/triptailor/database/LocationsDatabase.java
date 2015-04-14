package com.triptailor.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.triptailor.model.Location;

public class LocationsDatabase extends DatabaseHelper {

	public LocationsDatabase() {
		super();
	}
	
	public Location loadLocation(String city, Map<String, Object> stop) {
		Location location = null;
		
		try {
			statement = connect.prepareStatement("SELECT id, city, country FROM location WHERE city = ?");
			statement.setString(1, city);
			resultSet = statement.executeQuery();

			if(resultSet.next()) {
				int locationId = resultSet.getInt(1);
				city = resultSet.getString(2);
				String country = resultSet.getString(3);

				statement =
				connect.prepareStatement("SELECT a.name, SUM(ha.rating) FROM hostel h, attribute a, hostel_attribute ha, location l "
						+ "WHERE h.id = ha.hostel_id AND a.id = ha.attribute_id AND h.location_id = l.id AND l.id = ? "
						+ "GROUP BY a.name ORDER BY SUM(ha.rating) DESC LIMIT ?");
				statement.setInt(1, locationId);
				statement.setInt(2, Location.TAG_LIMIT * 2);
				resultSet = statement.executeQuery();
				
				List<String> commonTags = new ArrayList<String>(Location.TAG_LIMIT);
				int i = 0;
				while(resultSet.next() && i < Location.TAG_LIMIT) {
					if(!stop.containsKey(resultSet.getString(1))) {
						commonTags.add(resultSet.getString(1));
						i++;
					}
				}
				
				statement =
				connect.prepareStatement("SELECT DISTINCT s.name FROM hostel h, service s, hostel_service hs, location l "
						+ "WHERE h.id = hs.hostel_id AND s.id = hs.service_id AND h.location_id = l.id AND l.id = ? LIMIT ?");
				statement.setInt(1, locationId);
				statement.setInt(2, Location.TAG_LIMIT);
				resultSet = statement.executeQuery();
						
				Map<String, Object> commonServices = new HashMap<String, Object>(Location.TAG_LIMIT);
				while(resultSet.next())
					commonServices.put(resultSet.getString(1), null);
				
				location = new Location(locationId, city, country, commonTags, commonServices);
			}
			else
				location = new Location(-1, city, "", new ArrayList<String>(), new HashMap<String, Object>());
				
		} catch (SQLException e) {
			location = new Location(-1, city, "", new ArrayList<String>(), new HashMap<String, Object>());
			System.out.println(e);
		}
		
		return location;
	}
	
	public List<String> getLocationHints(String query) {
		ArrayList<String> hints = new ArrayList<String>();
		int LIMIT = 10;
		
		query = query.toLowerCase().replace("-", " ");
		
		try {
			statement = connect.prepareStatement("SELECT city, country FROM location WHERE city LIKE ? LIMIT 10");
			statement.setString(1, query + "%");
			resultSet = statement.executeQuery();

			while(resultSet.next())
				hints.add(resultSet.getString(1) + ", " + resultSet.getString(2));
			
			int newSize = LIMIT - hints.size();
			if(newSize > 0) {
				statement = connect.prepareStatement("SELECT city, country FROM location WHERE country LIKE ? LIMIT ?");
				statement.setString(1, query + "%");
				statement.setInt(2, newSize);
				resultSet = statement.executeQuery();

				while(resultSet.next())
					hints.add(resultSet.getString(1) + ", " + resultSet.getString(2));
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
		
		return hints;
	}
}
