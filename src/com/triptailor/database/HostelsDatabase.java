package com.triptailor.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.triptailor.model.Hostel;

public class HostelsDatabase extends DatabaseHelper {
	
	public HostelsDatabase() {
		super();
	}
	
	public List<Hostel> loadModel(String city, String country) {
		List<Hostel> model = new ArrayList<Hostel>();
		
		try {
			statement =
					connect.prepareStatement("SELECT id, name, no_reviews, price, url FROM hostel WHERE location_id = (SELECT id FROM "
							+ "location WHERE city = ? AND country = ?)");
			statement.setString(1, city);
			statement.setString(2, country);
			resultSet = statement.executeQuery();

			while(resultSet.next()) {
				Long hostelId = resultSet.getLong(1);

				statement =
				connect.prepareStatement("SELECT name, freq, cfreq, rating FROM hostel_attribute, attribute "
						+ "WHERE hostel_id = ? AND attribute_id = id");
				statement.setLong(1, hostelId);
				resultSet2 = statement.executeQuery();

				ArrayList<String> names = new ArrayList<String>();
				ArrayList<double[]> modifiers = new ArrayList<double[]>();
				int n = 0;
				while(resultSet2.next()) {
					names.add(resultSet2.getString(1));
					
					double[] container = new double[3];
					container[0] = resultSet2.getDouble(2);
					container[1] = resultSet2.getDouble(3);
					container[2] = resultSet2.getDouble(4);
					modifiers.add(container);
					
					n += container[0];
				}
				
				HashMap<String, Double> vector = new HashMap<String, Double>();
				for(int i = 0; i < names.size(); i++) {
					double freq = modifiers.get(i)[0];
					double cfreq = modifiers.get(i)[1];
					double rating = modifiers.get(i)[2];
					
					double crating = (cfreq / n) * (rating / freq);
					
					vector.put(names.get(i), crating);
				}
				
				if(!vector.isEmpty())
					model.add(new Hostel(hostelId, resultSet.getString(2), resultSet.getInt(3),
							Math.round(resultSet.getFloat(4) * 100) / ((float) 100), resultSet.getString(5), vector));
			}
		} catch (SQLException e) {
			model.clear();
			System.out.println(e);
		}
		
		return model;
	}
	
	public Hostel loadHostel(String hostelName) {
		HashMap<String, Double> hostel = new HashMap<String, Double>();
		
		try {
			statement = connect.prepareStatement("SELECT id, name, no_reviews, price, url FROM hostel WHERE name = ?");
			statement.setString(1, hostelName);
			resultSet = statement.executeQuery();

			if(resultSet.next()) {
				Long hostelId = resultSet.getLong(1);

				statement =
				connect.prepareStatement("SELECT name, rating FROM hostel_attribute, attribute WHERE hostel_id = ? AND attribute_id = id");
				statement.setLong(1, hostelId);
				resultSet2 = statement.executeQuery();

				while(resultSet2.next())
					hostel.put(resultSet2.getString(1), resultSet2.getDouble(2));
				
				return new Hostel(hostelId, resultSet.getString(2), resultSet.getInt(3),
						Math.round(resultSet.getFloat(4) * 100) / ((float) 100), resultSet.getString(5),
						hostel);
			}
		} catch (SQLException e) {
			hostel.clear();
			System.out.println(e);
		}
		
		return new Hostel(0, "", 0, 0, "", hostel);
	}
	
	public int isAHostel(String query) {
		try {
			statement = connect.prepareStatement("SELECT id FROM hostel WHERE name = ?");
			statement.setString(1, query);
			resultSet = statement.executeQuery();

			if(resultSet.next())
				return 1;
			
			statement = connect.prepareStatement("SELECT id FROM attribute WHERE name = ?");
			statement.setString(1, query);
			resultSet = statement.executeQuery();

			if(resultSet.next())
				return 0;
		} catch (SQLException e) {
			System.out.println(e);
		}
		
		return -1;
	}
	
	public HashMap<String, List<String>> getHostelHints(String query) {
		HashMap<String, List<String>> hints = new HashMap<String, List<String>>();
		ArrayList<String> hostelHints = new ArrayList<String>();
		ArrayList<String> tagHints = new ArrayList<String>();
		String hostelsWord = "hostels";
		String tagsWord = "tags";
		int LIMIT = 10;
		
		hints.put(hostelsWord, hostelHints);
		hints.put(tagsWord, tagHints);
		
		query = query.toLowerCase().replace("-", " ");
		
		try {
			statement = connect.prepareStatement("SELECT h.name, l.city FROM hostel h, location l WHERE name LIKE ? "
					+ "AND h.location_id = l.id LIMIT ?");
			statement.setString(1, "%" + query + "%");
			statement.setInt(2, LIMIT);
			resultSet = statement.executeQuery();
			
			while(resultSet.next())
				hostelHints.add(resultSet.getString(1) + ", " + resultSet.getString(2));
			
			int hostelsSize = LIMIT - hostelHints.size();
			if(hostelsSize > 0) {
				statement = connect.prepareStatement("SELECT h.name, l.city FROM hostel h, location l WHERE l.city LIKE ? "
						+ "AND h.location_id = l.id LIMIT ?");
				statement.setString(1, "%" + query + "%");
				statement.setInt(2, hostelsSize);
				resultSet = statement.executeQuery();

				while(resultSet.next()) {
					String hostelHint = resultSet.getString(1) + ", " + resultSet.getString(2);
					if(!hostelHints.contains(hostelHint))
						hostelHints.add(hostelHint);
				}
			}
			
			String[] possibleTags = query.split(" ");
			
			statement = connect.prepareStatement("SELECT name FROM attribute WHERE name LIKE ? LIMIT 5");
			statement.setString(1, possibleTags[possibleTags.length - 1] + "%");
			resultSet = statement.executeQuery();

			while(resultSet.next())
				tagHints.add(resultSet.getString(1));
		} catch (SQLException e) {
			System.out.println(e);
		}
		
		return hints;
	}
}
