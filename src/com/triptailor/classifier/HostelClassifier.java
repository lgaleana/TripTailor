package com.triptailor.classifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.triptailor.model.Hostel;

public class HostelClassifier {
	
	private final double WEIGHT_BASE = 1.5;
	private final int QUEUE_SIZE = 6;
	
	// Classify by hostel
	public List<Hostel> classify(List<Hostel> model, Hostel hostel, Map<String, Object> stop) {
		int reviews = getHighestNoReviews(model);
		int highestNoReviews = hostel.getNoReviews() < reviews ? hostel.getNoReviews() : reviews;
		
		model.add(new Hostel(hostel.getId(), hostel.getName(), hostel.getNoReviews(), hostel.getPrice(), hostel.getUrl(),
				new HashMap<String, Double>(hostel.getAttributes())));
		
		for(Hostel modelEntry : model) {
			String[] names = modelEntry.getName().split(" ");
			List<String> nameWords = new ArrayList<String>();
			for(int i = 0; i < names.length; i ++)
				nameWords.add(names[i].toLowerCase());
			
			double rating = 0;
			Map<String, Double> modelHostel = modelEntry.getAttributes();
			PriorityQueue<TagHolder> sharedQueue = new PriorityQueue<TagHolder>(QUEUE_SIZE, new TagComparator());
			
			for(Map.Entry<String, Double> hostelEntry : hostel.getAttributes().entrySet()) {
				String attribute = hostelEntry.getKey();
				double distance, modelHostelRating;
				double hostelRating = hostelEntry.getValue();
				
				if(modelHostel.containsKey(attribute)) {
					modelHostelRating = modelHostel.get(attribute);
					distance = hostelRating - modelHostelRating;
					if(!stop.containsKey(attribute) && !nameWords.contains(attribute)) {
						int size = QUEUE_SIZE / 2;
						if(modelEntry.getName().equals(hostel.getName()))
							size = QUEUE_SIZE;
						addToQueue(sharedQueue, new TagHolder(attribute, modelHostelRating, 0), size);
						modelHostel.remove(attribute);
					}
				}
				else {
					distance = hostelRating;
				}
				rating += Math.pow(distance, 2);
			}
			
			// Tags
			int size = QUEUE_SIZE - sharedQueue.size();
			PriorityQueue<TagHolder> queue = new PriorityQueue<TagHolder>(QUEUE_SIZE, new TagComparator());
			for(Map.Entry<String, Double> modelHostelEntry : modelHostel.entrySet())
				if(!stop.containsKey(modelHostelEntry.getKey()) && !nameWords.contains(modelHostelEntry.getKey()))
					addToQueue(queue, new TagHolder(modelHostelEntry.getKey(), modelHostelEntry.getValue(), 1), size);
			
			for(TagHolder holder : sharedQueue)
				addToQueue(queue, holder, QUEUE_SIZE);
			
			// Review penalizer
			highestNoReviews++;
			int modelReviews = modelEntry.getNoReviews() + 1;
			int reviewDifference = highestNoReviews / modelReviews;
			rating = (rating * Math.pow(WEIGHT_BASE, reviewDifference)) / WEIGHT_BASE;
			
			modelEntry.setTags(queue);
			modelEntry.setRating(rating);
		}
		
		Collections.sort(model, new HostelComparator());
		
		return model;
	}
	
	// Classify by tags
	public List<Hostel> classifyByTags(List<Hostel> model, List<String> attributes, Map<String, Object> stop) {
		int averageNoReviews = getAverageNoReviews(model);
		
		for(Hostel modelEntry : model) {
			String[] names = modelEntry.getName().split(" ");
			List<String> nameWords = new ArrayList<String>();
			for(int i = 0; i < names.length; i ++)
				nameWords.add(names[i].toLowerCase());
			
			double rating = 0;
			Map<String, Double> modelHostel = modelEntry.getAttributes();
			PriorityQueue<TagHolder> sharedQueue = new PriorityQueue<TagHolder>(QUEUE_SIZE, new TagComparator());
			
			for(String attribute : attributes) {
				double value;
				
				if(modelHostel.containsKey(attribute)) {
					value = modelHostel.get(attribute);
					addToQueue(sharedQueue, new TagHolder(attribute, value, 0), QUEUE_SIZE);
					modelHostel.remove(attribute);
				}
				else
					value = 0;
				rating += value;
			}
			
			rating /= attributes.size();
			
			// Tags
			int size = QUEUE_SIZE - sharedQueue.size();
			PriorityQueue<TagHolder> queue = new PriorityQueue<TagHolder>(QUEUE_SIZE, new TagComparator());
			for(Map.Entry<String, Double> modelHostelEntry : modelHostel.entrySet())
				if(size > 0 && !stop.containsKey(modelHostelEntry.getKey()) && !nameWords.contains(modelHostelEntry.getKey()))
					addToQueue(queue, new TagHolder(modelHostelEntry.getKey(), modelHostelEntry.getValue(), 1), size);
			
			for(TagHolder holder : sharedQueue)
				addToQueue(queue, holder, QUEUE_SIZE);
			
			// Review penalizer
			averageNoReviews++;
			int modelReviews = modelEntry.getNoReviews() + 1;
			int reviewDifference = averageNoReviews / modelReviews;
			reviewDifference = reviewDifference < 1 ? 1 : reviewDifference;
			rating = (rating / Math.pow(WEIGHT_BASE, reviewDifference)) * WEIGHT_BASE;
			
			modelEntry.setTags(queue);
			modelEntry.setRating(rating);
		}
		
		Collections.sort(model, new HostelComparator2());
		
		return model;
	}
	
	private void addToQueue(PriorityQueue<TagHolder> queue, TagHolder element, int size) {
		if(queue.size() == size) {
			ArrayList<TagHolder> aux = new ArrayList<TagHolder>(size - 1);
			while(queue.size() > 1)
				aux.add(queue.poll());
			TagHolder exit = queue.poll();
			for(TagHolder h : aux)
				queue.add(h);
			if(element.rating > exit.rating)
				queue.add(element);
			else {
				queue.add(exit);
			}
		}
		else
			queue.add(element);
	}
	
	private int getHighestNoReviews(List<Hostel> model) {
		int highestNoReviews = 0;
		for(Hostel hostel : model) {
			int hostelReviews = hostel.getNoReviews();
			if(hostelReviews > highestNoReviews)
				highestNoReviews = hostelReviews;

		}
		return highestNoReviews;
	}
	
	private int getAverageNoReviews(List<Hostel> model) {
		int averageNoReviews = 0;
		for(Hostel entry: model)
			averageNoReviews += entry.getNoReviews();
		return averageNoReviews / model.size();
	}

	public static class TagHolder {
		public String name;
		public double rating;
		public int type;
		
		public TagHolder(String name, double rating, int type) {
			this.name = name;
			this.rating = rating;
			this.type = type;
		}
	}
	
	private class HostelComparator implements Comparator<Hostel> {
	    @Override
	    public int compare(Hostel h1, Hostel h2) {
	        if(h1.getRating() < h2.getRating())
	        	return -1;
	        if(h1.getRating() == h2.getRating())
	        	return 0;
	        else
	        	return 1;
	    }
	}
	
	private class HostelComparator2 implements Comparator<Hostel> {
	    @Override
	    public int compare(Hostel h1, Hostel h2) {
	        if(h1.getRating() > h2.getRating())
	        	return -1;
	        if(h1.getRating() == h2.getRating())
	        	return 0;
	        else
	        	return 1;
	    }
	}
	
	private class TagComparator implements Comparator<TagHolder> {
	    @Override
	    public int compare(TagHolder h1, TagHolder h2) {
	        if(h1.rating > h2.rating)
	        	return -1;
	        if(h1.rating == h2.rating)
	        	return 0;
	        else
	        	return 1;
	    }
	}
}