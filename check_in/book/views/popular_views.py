from django.views import View
from django.http import JsonResponse
from http.client import HTTPConnection
from bs4 import BeautifulSoup
from django.conf import settings

class PopularView(View):
    def get(self, request):
        
        # GET 요청 보내기
        conn = HTTPConnection('data4library.kr')
        auth_key = settings.LIB_AUTH_TOKEN
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

        result = {'books': popular_result}

        return JsonResponse(result, status=200)