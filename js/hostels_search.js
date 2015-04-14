var saveHostelClick = function(searchId, hostelId) {
	if (window.XMLHttpRequest) {
		xmlhttp = new XMLHttpRequest();
	}
	else {
		xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
	}

	xmlhttp.open("POST", "http://localhost:8080/TripTailor/stats/hostel-click", true);
	xmlhttp.send("searchId=" + searchId + "&hostelId=" + hostelId);
};
