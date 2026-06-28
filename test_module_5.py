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

print("Module 5 - RBAC & Teaching Monitor API Test")
sys.stdout.flush()

resp = api("POST", "/login", {"username":u,"password":p,"role":r})
if resp.get("code") != 200:
    print("Login FAILED:", resp.get("description",resp.get("msg","")))
    sys.exit(1)
token = resp["data"]["token"]
print("Login OK\n")

tests = [
    # Role CRUD
    ("GET", "/role/list", None, "Role List"),
    ("POST", "/role/add", {"roleName":"TestRole","roleCode":"test","description":"test desc"}, "Role Add"),
    # Permission CRUD
    ("GET", "/permission/list", None, "Permission List"),
    ("GET", "/permission/current", None, "Permission Current"),
    ("POST", "/permission/add", {"permissionName":"TestPerm","permissionCode":"test:add","description":"test"}, "Permission Add"),
    # UserRole
    ("GET", "/userRole/listByUserId?userId=5", None, "UserRole ListByUser"),
    # RolePermission
    ("GET", "/rolePermission/listByRoleId?roleId=1", None, "RolePerm ListByRole"),
    # MonitorRecord CRUD
    ("GET", "/monitorRecord/list", None, "MonitorRecord List"),
    ("POST", "/monitorRecord/add", {"userId":4,"trainingId":1,"progress":50.00,"duration":120,"status":1}, "MonitorRecord Add"),
    # TeachingGuidance CRUD
    ("GET", "/teachingGuidance/list", None, "TeachingGuidance List"),
    ("POST", "/teachingGuidance/add", {"teacherId":5,"studentId":4,"trainingId":1,"guidanceContent":"test guidance","guidanceTime":"2026-06-29 10:00:00"}, "TeachingGuidance Add"),
]

pass_count = 0
for i, (method, path, data, name) in enumerate(tests):
    print(f"  [{i+1}/{len(tests)}] {name}...", end=" ")
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

print(f"\n{pass_count}/{len(tests)} passed")
if pass_count < len(tests):
    print("Note: Run migration_v5.sql first to create tables and seed data")
