package com.triptailor.model;

import java.util.Map;
import java.util.PriorityQueue;

import com.triptailor.classifier.HostelClassifier.TagHolder;

public class Hostel {
	
	private long id;
	private String name;
	private String description;
	private float price;
	private String image;
	private String url;
	private String city;
	private String country;
	private int noReviews;
	private Map<String, Double> attributes;
	
	private double rating;
	private PriorityQueue<TagHolder> tags;
	
	public Hostel(long id, String name, int noReviews, float price, String url, Map<String, Double> attributes) {
		this.id = id;
		this.name = name;
		this.noReviews = noReviews;
		this.price = price;
		this.url = url;
		this.attributes = attributes;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
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
	public int getNoReviews() {
		return noReviews;
	}
	public void setNoReviews(int no_reviews) {
		this.noReviews = no_reviews;
	}
	public Map<String, Double> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, Double> attributes) {
		this.attributes = attributes;
	}
	public double getRating() {
		return rating;
	}
	public void setRating(double rating) {
		this.rating = rating;
	}
	public PriorityQueue<TagHolder> getTags() {
		return tags;
	}
	public void setTags(PriorityQueue<TagHolder> tags) {
		this.tags = tags;
	}
}
