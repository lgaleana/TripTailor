package com.triptailor.model;

import java.util.List;
import java.util.Map;

public class Location {
	public static final int TAG_LIMIT = 10;
	
	private int id;
	private String city;
	private String country;
	private List<String> commonTags;
	private Map<String, Object> commonServices;
	
	public Location(int id, String city, String country, List<String> commonTags, Map<String, Object> commonServices) {
		this.id = id;
		this.city = city;
		this.country = country;
		this.commonTags = commonTags;
		this.commonServices = commonServices;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public List<String> getCommonTags() {
		return commonTags;
	}
	public void setCommonTags(List<String> commonTags) {
		this.commonTags = commonTags;
	}
	public Map<String, Object> getCommonServices() {
		return commonServices;
	}
	public void setCommonServices(Map<String, Object> commonServices) {
		this.commonServices = commonServices;
	}
}
