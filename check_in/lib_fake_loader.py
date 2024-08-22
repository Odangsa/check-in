import json, random, datetime

libs = ["금천구립가산도서관", "구로꿈나무어린이도서관", "서울특별시교육청구로도서관", "금천구립시흥도서관", "서울특별시교육청고척도서관"]

new_list = []

i = 1 
for j in range(0,30):
    new_data = {"model": "user.visit"}
    new_data["fields"] = {}
    new_data["fields"]["userid"] = "junho"
    new_data["fields"]["visitdate"] = str(datetime.date(2024,1,i))
    i += 1
    new_data["fields"]["libraryname"] = libs[random.randrange(0,5)]
    new_list.append(new_data)

i = 1 
for j in range(0,30):
    new_data = {"model": "user.visit"}
    new_data["fields"] = {}
    new_data["fields"]["userid"] = "oyeong"
    new_data["fields"]["visitdate"] = str(datetime.date(2024,1,i))
    i += 1
    new_data["fields"]["libraryname"] = libs[random.randrange(0,5)]
    new_list.append(new_data)

with open('lib_data.json', 'w') as f:
    json.dump(new_list, f, ensure_ascii=False, indent=2)