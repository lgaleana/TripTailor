<!doctype html>
<html>
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
		<title>TripTailor</title>

		<link href="http://fonts.googleapis.com/css?family=Lato" rel="stylesheet" type="text/css">
		<link href="/TripTailor/css/general.css" rel="stylesheet" type="text/css">
		<link href="/TripTailor/css/stats.css" rel="stylesheet" type="text/css">
		<link rel="shortcut icon" type="images/x-icon" href="/img/favicon.ico">

	</head>

	<body>
		<div id="topbar">
			<div class="center-vertical">
				<h4 id="logo"><a href="http://localhost:8080/TripTailor">TripTailor Hostels</a></h4>
			</div>
		</div>	


		<div id="container">
			<%@ page import="java.util.Map" %>
			<% Map<String, Double> allStats = (Map<String, Double>) request.getAttribute("allStats"); %>
			<% Map<String, Double> stats = (Map<String, Double>) request.getAttribute("stats"); %>

			<h1>Today</h1>
			<span><h2><%= stats.get("searches").intValue() %></h2>searches</span>
			<span><h2><%= stats.get("adwords").intValue() %></h2>adword searches</span>
			<span><h2><%= stats.get("searchSession") %></h2>searches/session</span>
			<span><h2><%= stats.get("linkSearch") %></h2>hostels clicked/search</span>
			<span><h2><%= stats.get("hostelSearches").intValue() %></h2>hostel searches</span>
			<span><h2><%= stats.get("attributeSearches").intValue() %></h2>attribute searches</span>

			<div class="line"></div>

			<h1>All the time</h1>
			<span><h2><%= allStats.get("searches").intValue() %></h2>searches</span>
			<span><h2><%= allStats.get("adwords").intValue() %></h2>adword searches</span>
			<span><h2><%= allStats.get("searchSession") %></h2>searches/session</span>
			<span><h2><%= allStats.get("linkSearch") %></h2>hostels clicked/search</span>
			<span><h2><%= allStats.get("hostelSearches").intValue() %></h2>hostel searches</span>
			<span><h2><%= allStats.get("attributeSearches").intValue() %></h2>attribute searches</span>
		</div>

		<!-- <script src="/TripTailor/js/stats.js"></script> -->
	</body>
</html>
