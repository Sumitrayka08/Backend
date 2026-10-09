import urllib.request
import json

BASE_URL = "http://localhost:8080"

def post_json(url, data, headers=None):
    if headers is None:
        headers = {}
    headers["Content-Type"] = "application/json"
    req = urllib.request.Request(url, data=json.dumps(data).encode('utf-8'), headers=headers, method='POST')
    try:
        with urllib.request.urlopen(req) as resp:
            return resp.status, json.loads(resp.read().decode('utf-8'))
    except urllib.error.HTTPError as e:
        return e.code, json.loads(e.read().decode('utf-8'))

def get_json(url, headers=None):
    if headers is None:
        headers = {}
    req = urllib.request.Request(url, headers=headers, method='GET')
    try:
        with urllib.request.urlopen(req) as resp:
            return resp.status, json.loads(resp.read().decode('utf-8'))
    except urllib.error.HTTPError as e:
        return e.code, json.loads(e.read().decode('utf-8'))

def put_json(url, data, headers=None):
    if headers is None:
        headers = {}
    headers["Content-Type"] = "application/json"
    req = urllib.request.Request(url, data=json.dumps(data).encode('utf-8'), headers=headers, method='PUT')
    try:
        with urllib.request.urlopen(req) as resp:
            return resp.status, json.loads(resp.read().decode('utf-8'))
    except urllib.error.HTTPError as e:
        return e.code, json.loads(e.read().decode('utf-8'))

print("--- STARTING SECURITY VERIFICATION TESTS ---")

# 1. Login as Farmer
status, farmer_resp = post_json(f"{BASE_URL}/api/auth/login", {"email": "farmer@agrinexus.com", "password": "farmer123"})
if status != 200:
    status, farmer_resp = post_json(f"{BASE_URL}/api/auth/login", {"email": "farmer@agrinexus.com", "password": "FarmerPass123!"})

assert status == 200, f"Farmer login failed: {farmer_resp}"
farmer_token = farmer_resp["token"]
farmer_headers = {"Authorization": f"Bearer {farmer_token}"}
print(f"[SUCCESS] Farmer Login 200 OK. User role: {farmer_resp['user']['role']}")

# 2. Login as Expert
status, expert_resp = post_json(f"{BASE_URL}/api/auth/login", {"email": "expert@agrinexus.com", "password": "admin123"})
if status != 200:
    status, expert_resp = post_json(f"{BASE_URL}/api/auth/login", {"email": "expert@agrinexus.com", "password": "expert123"})
if status != 200:
    status, expert_resp = post_json(f"{BASE_URL}/api/auth/login", {"email": "expert@agrinexus.com", "password": "ExpertPass123!"})

assert status == 200, f"Expert login failed: {expert_resp}"
expert_token = expert_resp["token"]
expert_headers = {"Authorization": f"Bearer {expert_token}"}
print(f"[SUCCESS] Expert login 200 OK. User role: {expert_resp['user']['role']}")

# 3. Login as Admin
status, admin_resp = post_json(f"{BASE_URL}/api/auth/login", {"email": "admin@agrinexus.com", "password": "admin123"})
if status != 200:
    status, admin_resp = post_json(f"{BASE_URL}/api/auth/login", {"email": "admin@agrinexus.com", "password": "AdminPass123!"})

assert status == 200, f"Admin login failed: {admin_resp}"
admin_token = admin_resp["token"]
admin_headers = {"Authorization": f"Bearer {admin_token}"}
print(f"[SUCCESS] Admin login 200 OK. User role: {admin_resp['user']['role']}")

# TEST 1: FARMER GET /api/expert/queries/my -> Should return 200
status, data = get_json(f"{BASE_URL}/api/expert/queries/my", farmer_headers)
print(f"[TEST 1] FARMER GET /api/expert/queries/my -> Status: {status}")
assert status == 200, f"Expected 200 for FARMER GET /api/expert/queries/my, got {status}"

# TEST 2: FARMER POST /api/expert/queries -> Should return 200
status, data = post_json(f"{BASE_URL}/api/expert/queries", {"question": "How to manage leaf spot on wheat?"}, farmer_headers)
print(f"[TEST 2] FARMER POST /api/expert/queries -> Status: {status}")
assert status == 200, f"Expected 200 for FARMER POST /api/expert/queries, got {status}"
query_id = data["id"]

# TEST 3: FARMER GET /api/expert/queries/available -> Should return 403 Forbidden
status, data = get_json(f"{BASE_URL}/api/expert/queries/available", farmer_headers)
print(f"[TEST 3] FARMER GET /api/expert/queries/available -> Status: {status}")
assert status == 403, f"Expected 403 for FARMER GET /api/expert/queries/available, got {status}"

# TEST 4: FARMER PUT /api/expert/queries/{id}/respond -> Should return 403 Forbidden
status, data = put_json(f"{BASE_URL}/api/expert/queries/{query_id}/respond", {"response": "FARMER TRYING TO RESPOND"}, farmer_headers)
print(f"[TEST 4] FARMER PUT /api/expert/queries/{query_id}/respond -> Status: {status}")
assert status == 403, f"Expected 403 for FARMER PUT /api/expert/queries/respond, got {status}"

# TEST 5: EXPERT GET /api/expert/queries/available -> Should return 200
status, data = get_json(f"{BASE_URL}/api/expert/queries/available", expert_headers)
print(f"[TEST 5] EXPERT GET /api/expert/queries/available -> Status: {status}")
assert status == 200, f"Expected 200 for EXPERT GET /api/expert/queries/available, got {status}"

# TEST 6: EXPERT PUT /api/expert/queries/{id}/respond -> Should return 200
status, data = put_json(f"{BASE_URL}/api/expert/queries/{query_id}/respond", {"response": "Apply zinc fertilizer and control moisture."}, expert_headers)
print(f"[TEST 6] EXPERT PUT /api/expert/queries/{query_id}/respond -> Status: {status}")
assert status == 200, f"Expected 200 for EXPERT PUT /api/expert/queries/respond, got {status}"

# TEST 7: ADMIN GET /api/admin/users -> Should return 200
status, data = get_json(f"{BASE_URL}/api/admin/users", admin_headers)
print(f"[TEST 7] ADMIN GET /api/admin/users -> Status: {status}")
assert status == 200, f"Expected 200 for ADMIN GET /api/admin/users, got {status}"

# TEST 8: FARMER GET /api/admin/users -> Should return 403 Forbidden
status, data = get_json(f"{BASE_URL}/api/admin/users", farmer_headers)
print(f"[TEST 8] FARMER GET /api/admin/users -> Status: {status}")
assert status == 403, f"Expected 403 for FARMER GET /api/admin/users, got {status}"

print("\n--- ALL SECURITY & AUTHORIZATION TESTS PASSED SUCCESSFULLY! ---")

