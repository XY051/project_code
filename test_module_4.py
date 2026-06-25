import urllib.request, json, ssl, sys
base = "http://localhost:8088/api"
ctx = ssl._create_unverified_context()

def api(method, path, data=None, token=None, timeout=5):
    body = json.dumps(data).encode() if data is not None else None
    req = urllib.request.Request(base + path, data=body, method=method)
    req.add_header("Content-Type", "application/json")
    if token:
        req.add_header("Authorization", token)
    try:
        return json.loads(urllib.request.urlopen(req, context=ctx, timeout=timeout).read())
    except Exception as e:
        return {"error": str(e)[:80]}

u = sys.argv[1] if len(sys.argv) >= 2 else "admin"
p = sys.argv[2] if len(sys.argv) >= 3 else "123456"
r = sys.argv[3] if len(sys.argv) >= 4 else "admin"

print("Module 4 - Lab & Equipment API Test")
sys.stdout.flush()

resp = api("POST", "/login", {"username":u,"password":p,"role":r})
if resp.get("code") != 200:
    print("Login FAILED:", resp.get("description",resp.get("msg","")))
    sys.exit(1)
token = resp["data"]["token"]
print("Login OK\n")

tests = [
    ("GET", "/lab/list", None, "Lab List"),
    ("POST", "/lab/add", {"name":"T1","location":"B101","capacity":30,"status":1}, "Lab Add"),
    ("GET", "/labBooking/list", None, "LabBooking List"),
    ("POST", "/labBooking/add", {"labId":1,"purpose":"test","bookDate":"2026-06-25"}, "LabBooking Add"),
    ("GET", "/equipment/list", None, "Equipment List"),
    ("POST", "/equipment/add", {"name":"P1","model":"X1","code":"E1","status":1}, "Equipment Add"),
    ("GET", "/equipmentMaintenance/list", None, "EqMaintenance"),
]

pass_count = 0
for i, (method, path, data, name) in enumerate(tests):
    print(f"  [{i+1}/7] {name}...", end=" ")
    sys.stdout.flush()
    resp = api(method, path, data, token)
    code = resp.get("code")
    if code == 200:
        pass_count += 1
        print("OK")
    else:
        err = resp.get("description") or resp.get("msg") or resp.get("error","?")
        print(f"FAIL ({err})")
    sys.stdout.flush()

print(f"\n{pass_count}/7 passed")
if pass_count < 7:
    print("Note: EqMaintenance needs backend restart after adding 4 Java files")