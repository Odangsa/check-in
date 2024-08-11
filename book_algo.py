import pymysql
import csv
import pandas as pd
import re
from http.client import HTTPConnection
import xml.etree.ElementTree as ET
import requests
from bs4 import BeautifulSoup

connection = pymysql.connect(host='[ENDPOINT]', port=3306, user='[USER]', password='[PASSWORD]', db='[DB]', charset='utf8')
cursor = connection.cursor()


### DB

# file = pd.read_csv("[CSV].csv", encoding='utf-8')
# test = file[['CL_SMBL_NO','ISBN_THIRTEEN_NO']]

# n = 0

# for i in range(223000, len(test['CL_SMBL_NO'])):
#     if pd.isnull(file['CL_SMBL_NO'][i]) or pd.isnull(file['ISBN_THIRTEEN_NO'][i]):
#         n = n + 1
#         continue
#     #print(test['CL_SMBL_NO'][i], type(test['CL_SMBL_NO'][i]))
#     test['CL_SMBL_NO'][i] = str(test['CL_SMBL_NO'][i])
#     if ' ' in test['CL_SMBL_NO'][i]:
#         test['CL_SMBL_NO'][i] = test['CL_SMBL_NO'][i].rstrip()
#     if '.' in test['CL_SMBL_NO'][i] or ',' in test['CL_SMBL_NO'][i]:
#         tmp, *_ = re.split('[.,]', test['CL_SMBL_NO'][i])
#         tmp = re.sub('[A-Z | ㄱ-ㅎ | ㅏ-ㅣ | 가-힣]', '', tmp)
#     else:
#         tmp = re.sub('[A-Z | a-z | ㄱ-ㅎ | ㅏ-ㅣ | 가-힣]', '', test['CL_SMBL_NO'][i])
    
#     try:
#         test['CL_SMBL_NO'][i] = int(tmp)
#         test['ISBN_THIRTEEN_NO'][i] = int(test['ISBN_THIRTEEN_NO'][i])
#     except ValueError:
#         n = n + 1
#         continue
#     # db
#     try:
#         cursor.execute("insert into bookisbn (ISBN, SMBL) values (%s, %s)", (test['ISBN_THIRTEEN_NO'][i], test['CL_SMBL_NO'][i]))
#         connection.commit()
#     except pymysql.err.IntegrityError:
#         print("duplicate error ", i)
#         continue
    
    
### BBTI algo
randnum = 1 # BBTI 분야별 랜덤 추출 개수
inputBBTI = [1, 3, 7] # 프론트에서 넘어온 BBTI 정보 배열
recommend_result = []

# 감성적
# 8xx (o), 8x3 (x), 805 (x)
if inputBBTI[0] == 1:
    cursor.execute("select ISBN from bookisbn where SMBL between 800 and 899 and SMBL != 805 and MOD(SMBL, 10) != 3 order by rand() limit %s", randnum)

# 현실적
# 3xx (o), 06x (o)
elif inputBBTI[0] == 2:
    cursor.execute("select ISBN from bookisbn where SMBL between 300 and 399 or SMBL between 60 and 69 order by rand() limit %s", randnum)

tmp = (cursor.fetchall())
for i in range(len(tmp)): recommend_result.append({"ISBN": tmp[i][0]})

# 통계 x
# 1[7-9]x (o), 2xx (o), 9xx(o)
if inputBBTI[1] == 3:
    cursor.execute("select ISBN from bookisbn where SMBL between 170 and 199 or SMBL between 200 and 299 or SMBL between 900 and 999 order by rand() limit %s", randnum)

# 통계 o
# 3[0-3]x (o)
elif inputBBTI[1] == 4:
    cursor.execute("select ISBN from bookisbn where SMBL between 300 and 339 order by rand() limit %s", randnum)

# 자연과학
# 4xx (o)
elif inputBBTI[1] == 5:
    cursor.execute("select ISBN from bookisbn where SMBL between 400 and 499 order by rand() limit %s", randnum)

# 기술과학
# 5xx (o), 00[4-5] (o)
elif inputBBTI[1] == 6:
    cursor.execute("select ISBN from bookisbn where SMBL between 500 and 599 or SMBL in (4, 5) order by rand() limit %s", randnum)
    
tmp = (cursor.fetchall())
for i in range(len(tmp)): recommend_result.append({"ISBN": tmp[i][0]})

# 자극
# 8x3 (o), 805 (o), 05x (o)
if inputBBTI[2] == 7:
    cursor.execute("select ISBN from bookisbn where (SMBL between 800 and 893 and MOD(SMBL, 10) = 3) or (SMBL = 805) or (SMBL between 50 and 59) order by rand() limit %s", randnum)

# 영감
# 1[0-6]x (o), 04x (o)
elif inputBBTI[2] == 8:
    cursor.execute("select ISBN from bookisbn where SMBL between 100 and 169 or SMBL between 40 and 49 order by rand() limit %s", randnum)

tmp = (cursor.fetchall())
for i in range(len(tmp)): recommend_result.append({"ISBN": tmp[i][0]})

connection.close()



### BBTI별 책 추천 api 호출
for i in range(len(recommend_result)):
    isbn = recommend_result[i]["ISBN"]
    auth_key = '[AUTH_KEY]'
    conn = HTTPConnection('data4library.kr')
    
    # GET 요청 보내기
    conn.request("GET", f"/api/srchDtlList?authKey={auth_key}&isbn13={isbn}&loaninfoYN=Y&format=xml", headers={'Host': 'data4library.kr'})

    # 응답 받기
    response = conn.getresponse()
    response = response.read().decode()
    xml = BeautifulSoup(response, "xml") 
    
    bookname = xml.find("response").find("detail").find("book").find("bookname").text # 책이름
    authors = xml.find("response").find("detail").find("book").find("authors").text # 저자명
    publisher = xml.find("response").find("detail").find("book").find("publisher").text # 출판사명
    pub_year = xml.find("response").find("detail").find("book").find("publication_year").text # 출판년도
    bookimageURL = xml.find("response").find("detail").find("book").find("bookImageURL").text # 책표지
    description = xml.find("response").find("detail").find("book").find("description").text # 한줄설명

    recommend_result[i]["bookname"] = bookname
    recommend_result[i]["authors"] = authors
    recommend_result[i]["publisher"] = publisher
    recommend_result[i]["publishyear"] = pub_year
    recommend_result[i]["bookimageURL"] = bookimageURL
    recommend_result[i]["description"] = description

print("[ BBTI 추천 도서 ]")
print(recommend_result)



### 오늘의 인기 도서 api 호출
    
# GET 요청 보내기
conn.request("GET", f"/api/loanItemSrch?authKey={auth_key}&pageSize=3", headers={'Host': 'data4library.kr'})

# 응답 받기
response = conn.getresponse()
response = response.read().decode()
xml = BeautifulSoup(response, "xml")
books = xml.find("docs")

popular_result = []

for book in books:
    bookname = book.find("bookname").text # 책이름
    authors = book.find("authors").text # 저자명
    ISBN = book.find("isbn13").text # ISBN
    topic = book.find("class_nm").text # 주제분류
    bookimageURL = book.find("bookImageURL").text # 책표지

    popular_result.append({
        "ISBN": ISBN,
        "bookname": bookname,
        "authors": authors,
        "topic": topic,
        "bookimageURL": bookimageURL
    })

print("\n\n[ 오늘의 인기 도서 ]")
print(popular_result)
