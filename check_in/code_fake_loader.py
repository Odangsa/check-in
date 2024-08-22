from http.client import HTTPConnection
import json
import xml.etree.ElementTree as ET

conn = HTTPConnection('data4library.kr')
with open('secrets.json', 'r') as f:
    auth_key = json.loads(f.read())['LIB_AUTH_TOKEN']

# 도서 상세정보 요청
body = f'authKey={auth_key}&pageSize=1500'
conn.request('GET', f'/api/libSrch?{body}', headers={'Host': 'data4library.kr'})
response = conn.getresponse()
r = response.read().decode()

root = ET.fromstring(r)

new_list = []
for i in range(int(root[4].text)):
    # print(i, root[5][i][1].text)
    new_data = {"model": "user.libcode"}
    new_data["fields"] = {}
    new_data["fields"]["libraryname"] = root[5][i][1].text
    new_data["fields"]["code"] = '{0:04d}'.format(i)
    new_list.append(new_data)

with open('code_data.json', 'w') as f:
    json.dump(new_list, f, ensure_ascii=False, indent=2)