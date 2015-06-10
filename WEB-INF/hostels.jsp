<!DOCTYPE html>
<html lang="en">
	<head>
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
		<title>TripTailor Hostels</title>

		<link href='http://fonts.googleapis.com/css?family=Ubuntu:400,500' rel='stylesheet' type='text/css'>
		<link href="/v2/css/bootstrap.min.css" rel="stylesheet">
    <link href="/v2/css/general.css" rel="stylesheet">
		<link href="/v2/css/index.css" rel="stylesheet">
		<link href="/v2/css/hostels.css" rel="stylesheet">
		<link rel="shortcut icon" type="images/x-icon" href="/img/favicon.ico">

	</head>
	<body>
		<%@ page import="java.util.List, com.triptailor.model.Hostel, com.triptailor.model.Location, java.util.PriorityQueue, com.triptailor.classifier.HostelClassifier.TagHolder" %>
		<% Location location = (Location) request.getAttribute("location"); %>
		<% List<Hostel> results = (List<Hostel>) request.getAttribute("results"); %>

		<nav class="navbar navbar-triptailor">
		<div class="container-fluid">
			<!-- Brand and toggle get grouped for better mobile display -->
			<div class="navbar-header">
				<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
					<span class="sr-only">Toggle navigation</span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="http://localhost:8080/v2_react"><span class="medium">TripTailor</span> Hostels</a>
			</div>

			<!-- Collect the nav links, forms, and other content for toggling -->
			<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
				<ul class="nav navbar-nav navbar-right">
					<li><a href="#">How it works</a></li>
					<li><a href="#">About us</a></li>
					<li><a class="a-addthis" href="#"><div class="addthis_sharing_toolbox addthis_top"></div></a></li>
				</ul>
			</div><!-- /.navbar-collapse -->
		</div><!-- /.container-fluid -->
		</nav>

		<div class="container-fluid">
			<div class="row header-row first-header">
				<div class="col-md-10">
					<div class="row">
						<div class="col-md-2 col-label">
							<label for="city">Pick a city</label>
						</div>
						<div class="col-md-4">
              <input id="city" type="text" class="form-control" autocomplete="off" value="<%= location.getCity() %>, <%= location.getCountry() %>" />
							<div class="relative">
								<div id="autocompleteCity" class="autocomplete"></div>
							</div>
						</div>
						<div class="col-md-2 col-label">
							<label for="query">Write some tags</label>
						</div>
						<div class="col-md-4">
							<input id="query" type="text" class="form-control" autocomplete="off" />	
							<div class="relative">
								<div id="autocompleteQuery" class="autocomplete"></div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-md-2">
					<a id="submit" class="submit" href="" onclick="return prepareForm()">Search</a>
				</div>
			</div> <!-- row -->

			<div class="row header-row">
				<% List<String> commonTags = location.getCommonTags(); %>

				<div class="col-xs-10">
					<div class="row">
						<div class="col-xs-2">
							<div>Tags</div>
						</div>
						<div class="col-xs-10">
							
						</div>
					</div>
					<div class="row">
						<div class="col-xs-2 col-also">
							<div class="also-label">Also try</div>
						</div>
						<div class="col-xs-10">
							<% for(int i = 0; i < commonTags.size(); i++) { %>
								<div class="tag"><%= commonTags.get(i) %></div>
							<% } %>
						</div>
					</div>
				</div>
				<div class="col-xs-2 results-number-col">
					<div class="results-number-container">
						<p>Matching Results</p>
						<p><strong><%= results.size() %></strong></p>
					</div>
				</div>
			</div> <!-- row -->
		</div> <!-- containder-fluid -->

		<div class="container-fluid">

			<% int i; %>
			<% for(i = 0; i < results.size(); i++) { %>
			<% Hostel hostel = results.get(i); %>

			<% if(i % 3 == 0) { %>
					<div class="row bottom-tips">
			<% } %>

			<div class="col-md-4">
				<div class="tip amsterdam">
					<a href=<%= hostel.getUrl() %> class="tip-a">
						<div class="tip-content-container">
							<div class="tip-content text-center">
								<h3><%= hostel.getName() %></h3>
								<div class="tip-results medium"><%= hostel.getTags().poll().name %></div>
							</div>
						</div>
					</a>
				</div>
			</div>

			<% if((i + 1) % 3 == 0) { %>
					</div>
			<% } %>

			<% } %>

			<% if(i % 3 != 0) { %>
					</div>
			<% } %>
		</div> <!-- container-fluid -->

		<div class="container-fluid footer">
			<div class="row">
				<div class="col-sm-4">
					<p class="footer-header">Company</p>
					<ul class="list-unstyled footer-list">
						<li>How it works</li>
						<li>About Us</li>
						<li>Contact</li>
					</ul>
				</div>
				<div class="col-sm-4 love">
					<p class="footer-header">Spread the love</p>
					<div class="addthis_sharing_toolbox"></div>
				</div>
			</div>
			<div class="row disclaimer">
				<div class="col-sm-12">
					<p>&copy; TripTailor</p>
				</div>
			</div>
		</div><!-- /footer -->

		<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
		<script src="/v2/js/bootstrap.min.js"></script>
		<script src="/v2/js/index.js"></script>
		<!-- addthis -->
		<script type="text/javascript" src="//s7.addthis.com/js/300/addthis_widget.js#pubid=ra-520ea186234cadc3" async="async"></script>
	</body>
</html>
