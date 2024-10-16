from django.views import View
import pymysql
from http.client import HTTPConnection
from bs4 import BeautifulSoup
from django.conf import settings
from django.http import JsonResponse

class BbtiView(View):
    def get(self, request):
        connection = pymysql.connect(host=settings.END_POINT, port=3306, user=settings.USER, password=settings.PASSWORD, db=settings.DB, charset='utf8')
        cursor = connection.cursor()

        randnum = 7
        inputBBTI = [int(request.GET['bbti'])//100, int(request.GET['bbti'])//10%10, int(request.GET['bbti'])%10] # parameter
        recommend_result = \
            [ { 
            "recommendations": 
                [ { 
                "type": "title 1", 
                "books": [ ]
                }, 
                { 
                "type": "title 2",
                "books": [] 
                },
                { 
                "type": "title 3",
                "books": []
                } ]
                } ]

        
        # 감성적
        # 8xx (o), 8x3 (x), 805 (x)
        if inputBBTI[0] == 1:
            cursor.execute("select ISBN from bookisbn where SMBL between 800 and 899 and SMBL != 805 and MOD(SMBL, 10) != 3 order by rand() limit %s", randnum)

        # 현실적
        # 3xx (o), 06x (o)
        elif inputBBTI[0] == 2:
            cursor.execute("select ISBN from bookisbn where SMBL between 300 and 399 or SMBL between 60 and 69 order by rand() limit %s", randnum)

        tmp = (cursor.fetchall())
        for i in range(len(tmp)): recommend_result[0]['recommendations'][0]['books'].append({"ISBN": tmp[i][0]})

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
        for i in range(len(tmp)): recommend_result[0]['recommendations'][1]['books'].append({"ISBN": tmp[i][0]})

        # 자극
        # 8x3 (o), 805 (o), 05x (o)
        if inputBBTI[2] == 7:
            cursor.execute("select ISBN from bookisbn where (SMBL between 800 and 893 and MOD(SMBL, 10) = 3) or (SMBL = 805) or (SMBL between 50 and 59) order by rand() limit %s", randnum)

        # 영감
        # 1[0-6]x (o), 04x (o)
        elif inputBBTI[2] == 8:
            cursor.execute("select ISBN from bookisbn where SMBL between 100 and 169 or SMBL between 40 and 49 order by rand() limit %s", randnum)

        tmp = (cursor.fetchall())
        for i in range(len(tmp)): recommend_result[0]['recommendations'][2]['books'].append({"ISBN": tmp[i][0]})

        connection.close()


        ########################################################################################################################################
        ### BBTI별 책 추천 api
        for j in range(3):
            for i in range(len(recommend_result[0]['recommendations'][j]['books'])):
                recommend_result[0]['recommendations'][0]['books'].append
                isbn = recommend_result[0]['recommendations'][0]['books'][i]['ISBN']
                auth_key = settings.LIB_AUTH_TOKEN
                conn = HTTPConnection('data4library.kr')
                
                # GET 요청 보내기
                conn.request("GET", f"/api/srchDtlList?authKey={auth_key}&isbn13={isbn}&loaninfoYN=Y&format=xml", headers={'Host': 'data4library.kr'})

                # 응답 받기
                response = conn.getresponse()
                response = response.read().decode()
                xml = BeautifulSoup(response, "xml")
                
                bookimageURL = xml.find("response").find("detail").find("book").find("bookImageURL").text # 책표지

                recommend_result[0]['recommendations'][j]['books'][i]["bookimageURL"] = bookimageURL

        return JsonResponse(recommend_result[0], status=200)