package com.triptailor.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.triptailor.database.StatsDatabase;

@SuppressWarnings("serial")
public class ApplicationController extends HttpServlet {
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		StatsDatabase databaseHelper = new StatsDatabase();
		BufferedReader reader = request.getReader();
		String line;
		if((line = reader.readLine()) != null) {
			String[] parameters = line.split("&");
			int searchId = Integer.parseInt(parameters[0].split("=")[1]);
			int hostelId = Integer.parseInt(parameters[1].split("=")[1]);
			databaseHelper.saveHostelClick(searchId, hostelId);
		}
		databaseHelper.close();
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		StatsDatabase databaseHelper = new StatsDatabase();
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		request.setAttribute("stats", databaseHelper.getStats(calendar.getTimeInMillis()));
		request.setAttribute("allStats", databaseHelper.getStats(0));
		databaseHelper.close();
		request.getRequestDispatcher("/WEB-INF/stats.jsp").forward(request, response);
	}
}
