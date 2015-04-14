package com.triptailor.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.triptailor.classifier.HostelClassifier;
import com.triptailor.database.HostelsDatabase;
import com.triptailor.database.LocationsDatabase;
import com.triptailor.database.SearchesDatabase;
import com.triptailor.model.Hostel;
import com.triptailor.model.Location;

@SuppressWarnings("serial")
public class HostelsController extends HttpServlet {
	
	public static int LOCAL = 1;
	private String redirectUrl = "http://triptailor.co";
	
	private int cityUri = 3 + LOCAL;
	private int parametersUri = 4 + LOCAL;
	private int hintsWordUri = 2 + LOCAL;
	private int hintsClassUri = 3 + LOCAL;
	private int totalUris = 5 + LOCAL;
	
	private String hintsWord = "hints";
	private String hintsClassHostelWord = "hostels";
	private String hintsClassLocationWord = "locations";
	
	private String adWordsParameter = "gclid";
	private String adWordsParameter2 = "ad";
	
	private final String HOSTELS_WORD = "hostels";
	private final String TAGS_WORD = "tags";
	
	private final String STOP_FILE = "stop_tags.txt";
	public static Map<String, Object> stop;;
	
	public void init() {
		stop = new HashMap<String, Object>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(getServletContext().getRealPath("/" + STOP_FILE))));
			String line;
			while((line = reader.readLine()) != null)
				stop.put(line, null);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String[] uris = request.getRequestURI().split("/");
		
		if(uris.length == totalUris) {
			if(uris[hintsWordUri].equals(hintsWord))
				returnHints(response, uris);
			else
				classify(request, response, uris);
		}
		else if(uris.length == totalUris - 1) {
			displayAll(request, response, uris);
		}
		else {
			response.sendRedirect(redirectUrl);
		}
	}
	
	private void classify(HttpServletRequest request, HttpServletResponse response, String[] uris) throws ServletException, IOException {
		List<Hostel> results = new ArrayList<Hostel>();
		int searchId = -1;
		
		String[] locations = uris[cityUri].replace("-", " ").replace("%21", "-").split(",");
		String city = locations[0].replaceAll("[^a-zA-Z -]", "");
		
		LocationsDatabase locationsDatabase = new LocationsDatabase();
		Location location = locationsDatabase.loadLocation(city, stop);
		locationsDatabase.close();
		
		if(location != null) {
			HostelsDatabase hostelsDatabase = new HostelsDatabase();
			
			List<Hostel> model =  hostelsDatabase.loadModel(location.getCity(), location.getCountry());
			
			if(model.size() > 0) {
				String parameters = uris[parametersUri].replace("-", " ").replace("%21", "-");
				String[] terms = parameters.split(",");
				String possibleHostel = terms[0];
				for(int i = 1; i < terms.length - 1; i++)
					possibleHostel += terms[i];
				
				int adwords = (request.getParameter(adWordsParameter) == null &&  request.getParameter(adWordsParameter2) == null) ? 0 : 1;
				
				SearchesDatabase searchesDatabase = new SearchesDatabase();
				
				HostelClassifier classifier = new HostelClassifier();
				
				if(hostelsDatabase.isAHostel(possibleHostel) == 1) {
					Hostel hostel = hostelsDatabase.loadHostel(possibleHostel);
					results = classifier.classify(model, hostel, stop);
					
					searchId = searchesDatabase.saveHostelSearch(request.getSession().getId(), hostel.getName(), city, adwords);
				}
				else {
					List<String> possibleTags = Arrays.asList(parameters.split("[ ,]"));
					results = classifier.classifyByTags(model, possibleTags, stop);
					
					searchId = searchesDatabase.saveTagsSearch(request.getSession().getId(), possibleTags, city, adwords);
				}
				searchesDatabase.close();
			}
			hostelsDatabase.close();
		}
		
		request.setAttribute("results", results);
		request.setAttribute("location", location);
		request.setAttribute("searchId", searchId);
		request.getRequestDispatcher("/WEB-INF/hostels.jsp").forward(request, response);
		
		/*response.setContentType("text/html");
		PrintWriter out = response.getWriter();
	    out.println("<h1>" + request.getParameter(adWordsParameter) + "</h1>");*/
	}
	
	private void returnHints(HttpServletResponse response, String[] uris) throws ServletException, IOException {
		
		
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		
		if(uris[hintsClassUri].equals(hintsClassHostelWord)) {
			HostelsDatabase hostelsDatabase = new HostelsDatabase();
			
			HashMap<String, List<String>> hints = hostelsDatabase.getHostelHints(uris[parametersUri]);
			List<String> hostelHints = hints.get(HOSTELS_WORD);
			List<String> tagHints = hints.get(TAGS_WORD);
			
			String json = "{\"" + HOSTELS_WORD + "\":[";
			for(String hint : hostelHints)
				json += "\"" + hint + "\",";
			if(hostelHints.size() > 0)
				json = json.substring(0, json.length() - 1);
			
			json += "], \"" + TAGS_WORD + "\":[";
			for(String hint : tagHints)
				json += "\"" + hint + "\",";
			if(tagHints.size() > 0)
				json = json.substring(0, json.length() - 1);
			json += "]}";
			
			out.print(json);
			out.flush();
			
			hostelsDatabase.close();
		}
		else if(uris[hintsClassUri].equals(hintsClassLocationWord)) {
			LocationsDatabase locationsDatabase = new LocationsDatabase();
			
			List<String> hints = locationsDatabase.getLocationHints(uris[parametersUri]);
			
			String json = "[";
			for(String hint : hints)
				json += "\"" + hint + "\",";
			if(hints.size() > 0)
				json = json.substring(0, json.length() - 1);
			json += "]";
			
			out.print(json);
			out.flush();
			
			locationsDatabase.close();
		}
	}
	
	private void displayAll(HttpServletRequest request, HttpServletResponse response, String[] uris) throws ServletException, IOException {
		List<Hostel> results = new ArrayList<Hostel>();
		int searchId = -1;
		
		String[] locations = uris[cityUri].replace("-", " ").replace("%21", "-").split(",");
		String city = locations[0].replaceAll("[^a-zA-Z -]", "");
		
		LocationsDatabase locationsDatabase = new LocationsDatabase();
		Location location = locationsDatabase.loadLocation(city, stop);
		locationsDatabase.close();
		
		if(location != null) {
			HostelsDatabase hostelsDatabase = new HostelsDatabase();
			
			List<Hostel> model =  hostelsDatabase.loadModel(location.getCity(), location.getCountry());
			
			if(model.size() > 0) {
				int adwords = (request.getParameter(adWordsParameter) == null &&  request.getParameter(adWordsParameter2) == null) ? 0 : 1;
				
				HostelClassifier classifier = new HostelClassifier();
				
				List<String> possibleTags = new ArrayList<String>();
				possibleTags.add("");
				results = classifier.classifyByTags(model, possibleTags, stop);
				
				SearchesDatabase searchesDatabase = new SearchesDatabase();
				searchId = searchesDatabase.saveCitySearch(request.getSession().getId(), city, adwords);
				searchesDatabase.close();
			}
			hostelsDatabase.close();
		}
		
		request.setAttribute("results", results);
		request.setAttribute("location", location);
		request.setAttribute("searchId", searchId);
		request.getRequestDispatcher("/WEB-INF/hostels.jsp").forward(request, response);
		
		/*response.setContentType("text/html");
		PrintWriter out = response.getWriter();
	    out.println("<h1>" + request.getParameter(adWordsParameter) + "</h1>");*/
	}
}
