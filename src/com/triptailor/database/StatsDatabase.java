package com.triptailor.database;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.triptailor.util.Util;

public class StatsDatabase extends DatabaseHelper {
	
	public StatsDatabase() {
		super();
	}
	
	public void saveHostelClick(int searchId, int hostelId) {
		try {
			statement = connect.prepareStatement("INSERT INTO hostel_search VALUES(?, ?, ?)");
			statement.setInt(1, hostelId);
			statement.setInt(2, searchId);
			statement.setLong(3, System.currentTimeMillis());
			statement.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e);
		}
	}
	
	public Map<String, Double> getStats(long timestamp) {
		HashMap<String, Double> stats = new HashMap<String, Double>();
		
		try {
			statement = connect.prepareStatement("SELECT id, sess, adwords, hostel_id FROM search WHERE timestamp > ? ORDER BY sess");
			statement.setLong(1, timestamp);
			resultSet = statement.executeQuery();
			
			int searches = 0;
			int adwords = 0;
			String previousSess = "";
			double searchPerSess = 0;
			int noSess = 0;
			double linkPerSearch = 0;
			int hostelSearches = 0;
			int attributeSearches = 0;
			
			while(resultSet.next()) {
				if(resultSet.isLast())
					searches = resultSet.getRow();
				
				adwords += resultSet.getInt(3);
				
				String sess = resultSet.getString(2);
				if(!sess.equals(previousSess))
					noSess++;
				searchPerSess++;
				previousSess = sess;
				
				int id = resultSet.getInt(1);
				statement = connect.prepareStatement("SELECT COUNT(*) FROM hostel_search WHERE search_id = ?");
				statement.setInt(1, id);
				resultSet2 = statement.executeQuery();
				if(resultSet2.next())
					linkPerSearch += resultSet2.getInt(1);
				
				resultSet.getInt(4);
				if(!resultSet.wasNull())
					hostelSearches++;
				
				statement = connect.prepareStatement("SELECT search_id FROM attribute_search WHERE search_id = ?");
				statement.setInt(1, id);
				resultSet2 = statement.executeQuery();
				if(resultSet2.next())
					attributeSearches++;
			}
			
			stats.put("searches", (double) searches);
			stats.put("adwords", (double) adwords);
			stats.put("searchSession", Util.trunk(searchPerSess / noSess, 2));
			stats.put("linkSearch", Util.trunk(linkPerSearch / searches, 2));
			stats.put("hostelSearches", (double) hostelSearches);
			stats.put("attributeSearches", (double) attributeSearches);
		} catch (SQLException e) {
			System.out.println(e);
		}
		
		return stats;
	}
}
