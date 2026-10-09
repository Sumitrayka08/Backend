import urllib.request
import json
import sys

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

print("--- TESTING USER FLOW FOR frontendtest2026@gmail.com ---")

# 1. Register or Login frontendtest2026@gmail.com
status, resp = post_json(f"{BASE_URL}/api/auth/login", {"email": "frontendtest2026@gmail.com", "password": "Password123!"})
if status != 200:
    print("User not found via login, attempting registration...")
    status, resp = post_json(f"{BASE_URL}/api/auth/register", {
        "name": "Frontend Test",
        "email": "frontendtest2026@gmail.com",
        "password": "Password123!",
        "role": "FARMER"
    })

assert status == 200, f"Registration/Login failed: {resp}"
token = resp["token"]
headers = {"Authorization": f"Bearer {token}"}
print(f"[SUCCESS] User authenticated cleanly! Role: {resp['user']['role']}")

# 2. Test GET /api/expert/queries/my for frontendtest2026@gmail.com
status, data = get_json(f"{BASE_URL}/api/expert/queries/my", headers)
print(f"[TEST 1] GET /api/expert/queries/my -> Status: {status}, Count: {len(data) if isinstance(data, list) else 0}")
assert status == 200, f"Expected 200 OK, got {status}: {data}"

# 3. Test POST /api/expert/queries for frontendtest2026@gmail.com
status, data = post_json(f"{BASE_URL}/api/expert/queries", {"question": "Can I spray neem oil during crop flowering stage?"}, headers)
print(f"[TEST 2] POST /api/expert/queries -> Status: {status}, Query ID: {data.get('id')}")
assert status == 200, f"Expected 200 OK, got {status}: {data}"

# 4. Verify GET /api/expert/queries/my now contains the newly created query
status, data = get_json(f"{BASE_URL}/api/expert/queries/my", headers)
print(f"[TEST 3] GET /api/expert/queries/my -> Status: {status}, Count: {len(data)}")
assert status == 200 and len(data) >= 1, f"Expected at least 1 query, got {data}"

# 5. Verify security blocking for FARMER frontendtest2026@gmail.com
status, data = get_json(f"{BASE_URL}/api/expert/queries/available", headers)
print(f"[TEST 4] GET /api/expert/queries/available (Expert-only) -> Status: {status}")
assert status == 403, f"Expected 403 Forbidden for farmer on expert endpoint, got {status}"

print("\n--- ALL TESTS PASSED SUCCESSFULLY FOR frontendtest2026@gmail.com! ---")

