<!doctype html>
<html>
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
		<title>TripTailor</title>

		<link href="http://fonts.googleapis.com/css?family=Lato" rel="stylesheet" type="text/css">
		<link href="/TripTailor/css/general.css" rel="stylesheet" type="text/css">
		<link href="/TripTailor/css/hostels_search.css" rel="stylesheet" type="text/css">
		<link rel="shortcut icon" type="images/x-icon" href="/img/favicon.ico">

	</head>

	<body>
		<%@ page import="java.util.List, com.triptailor.model.Hostel, com.triptailor.model.Location, java.util.PriorityQueue, com.triptailor.classifier.HostelClassifier.TagHolder" %>

		<div id="topbar">
			<div class="center-vertical">
				<h4 id="logo"><a href="http://localhost:8080/TripTailor">TripTailor Hostels</a></h4>
				<% Location location = (Location) request.getAttribute("location"); %>
				<div id="location"><%= location.getCity() %>, <%= location.getCountry() %></div>
			</div>
			<!-- <form class="center-vertical" action="" method="GET">
				<input id="query" type="text" placeholder="Hostel or tags">
				<input id="city" type="text" placeholder="City">
				<input type="submit" value="Find hostels">
			</form> -->
		</div>	


		<div id="container">
			<% List<Hostel> results = (List<Hostel>) request.getAttribute("results"); %>
			<% int searchId = (int) request.getAttribute("searchId"); %>

			<%! int i; %>
			<% for(i = 0; i < results.size(); i++) { %>
			<% Hostel hostel = results.get(i); %>
			<div <% if(i % 2 == 0) { %> class="hostel-entry left" <% } else { %> class="hostel-entry" <% } %>>
			<a href=<%= hostel.getLink() %> target="_blank" onclick="saveHostelClick(<%= searchId %>, <%= hostel.getId() %>); return true;">
				<% String name = hostel.getName(); %>
				<h1><%= name %></h1>

				<div>
					<% PriorityQueue<TagHolder> queue = hostel.getTags(); %>
					<% while(!queue.isEmpty()) { %>
						<% TagHolder tag = queue.poll(); %>
						<span <% if(tag.type == 0) { %> class="shared" <% } else { %> class="unique" <% } %>><%= tag.name %></span>
					<% } %>
				</div>

				<div class="price">
					<% if(hostel.getPrice() > 0) { %>
						<h3><%= hostel.getPrice() %> USD</h3>
					<% } %>
				</div>
			</a>
			</div>
			<% } %>
		</div>

		<script src="/TripTailor/js/hostels_search.js"></script>
	</body>
</html>
