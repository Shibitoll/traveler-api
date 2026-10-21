# ===================================================================
# VALIDATION TESTS
# ===================================================================

# Test 1: Missing required field (title)
POST {{host}}/api/travel-plans
Content-Type: application/json
{
  "description": "Plan without title",
  "budget": 1000.00
}

HTTP 400

# Test 2: Empty title
POST {{host}}/api/travel-plans
Content-Type: application/json
{
  "title": "   ",
  "description": "Plan with empty title"
}

HTTP 400

# Test 3: Title too long
POST {{host}}/api/travel-plans
Content-Type: application/json
{
  "title": "This is a very long title that exceeds the maximum allowed length of 255 characters. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
  "budget": 1000.00
}

HTTP 400

# Test 4: End date before start date
POST {{host}}/api/travel-plans
Content-Type: application/json
{
  "title": "Invalid Date Range Plan",
  "start_date": "2025-06-15",
  "end_date": "2025-06-01"
}

HTTP 400

# Test 5: Negative budget
POST {{host}}/api/travel-plans
Content-Type: application/json
{
  "title": "Negative Budget Plan",
  "budget": -500.00
}

HTTP 400

# Test 6: Valid plan creation (baseline)
POST {{host}}/api/travel-plans
Content-Type: application/json
{
  "title": "Valid Test Plan",
  "description": "This should work",
  "start_date": "2025-06-01",
  "end_date": "2025-06-15",
  "budget": 2500.99,
  "currency": "EUR",
  "is_public": true
}

HTTP 201
[Captures]
valid_plan_id: jsonpath "$.id"
valid_plan_version: jsonpath "$.version"

# LOCATION VALIDATION TESTS

# Test 7: Create location missing name
POST {{host}}/api/travel-plans/{{valid_plan_id}}/locations
Content-Type: application/json
{
  "address": "Somewhere without name",
  "budget": 100.00,
  "parent_version": 1
}

HTTP 400

# Test 8: Invalid latitude
POST {{host}}/api/travel-plans/{{valid_plan_id}}/locations
Content-Type: application/json
{
  "name": "Invalid Latitude Location",
  "latitude": 91.0,
  "longitude": 2.2945,
  "parent_version": 1
}

HTTP 400

# Test 9: Invalid longitude
POST {{host}}/api/travel-plans/{{valid_plan_id}}/locations
Content-Type: application/json
{
  "name": "Invalid Longitude Location",
  "latitude": 48.8584,
  "longitude": 181.0,
  "parent_version": 1
}

HTTP 400

# Test 10: Negative budget for location
POST {{host}}/api/travel-plans/{{valid_plan_id}}/locations
Content-Type: application/json
{
  "name": "Negative Budget Location",
  "budget": -50.00,
  "parent_version": 1
}

HTTP 400

# Test 11: Valid location creation
POST {{host}}/api/travel-plans/{{valid_plan_id}}/locations
Content-Type: application/json
{
  "name": "Valid Test Location",
  "address": "123 Test Street, Test City",
  "latitude": 48.8584,
  "longitude": 2.2945,
  "arrival_date": "2025-06-02T09:00:00Z",
  "departure_date": "2025-06-02T17:00:00Z",
  "budget": 150.50,
  "notes": "This is a valid location",
  "parent_version": 1
}

HTTP 201
[Captures]
valid_location_id: jsonpath "$.id"

# Test 12: Update location with invalid latitude
PUT {{host}}/api/locations/{{valid_location_id}}
Content-Type: application/json
{
  "name": "Updated Location",
  "latitude": -91.0,
  "parent_version": 2
}

HTTP 400

# CLEANUP
DELETE {{host}}/api/travel-plans/{{valid_plan_id}}?version=2

HTTP 204