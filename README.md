# TripTailor Web App

## Controllers
**HostelsController**: Handles requests to classify by Hostels, Tags and City.
**ApplicationController**: Handles requests for statistics.

## Classifier
**HostelClassifier**: Classifies hostel vectors. A vector is composed of tags.

## Database
**DatabaseHelper**: Parent class that opens and closes database helpers.
**HostelsDatabase**: Loading a hostel, getting hostel hints, etc.
**LocationsDatabase**: Loading a location, getting location hints, etc.
**SearchesDatabase**: Saves hostel, tags and city searches.
**StatsDatabase**: Saves and returns TripTailor statistics.

## Model
**Hostel**: Hostel model.
**Location**: Location model.

## Util
**Util**: Utility class.
