from django.views import View
from django.conf import settings
from django.http import JsonResponse
import requests, json

class KakaoCallBackView(View):
   def get(self, request):
      data = {
         "grant_type"   : "authorization_code",
         "client_id"    : settings.KAKAO_REST_TOKEN,
         "redirection_uri" : "http://127.0.0.1/user/kakao/callback",
         "code"         : request.GET["code"]
      }

      kakao_token_api = 'https://kauth.kakao.com/oauth/token'
      access_token = requests.post(kakao_token_api, data=data).json()["access_token"]

      kakao_user_api = "https://kapi.kakao.com/v2/user/me"
      header = {"Authorization": f"Bearer ${access_token}"}
      user_information = requests.get(kakao_user_api, headers=header).json()

      kakao_id = user_information["id"]
      kakao_nickname = user_information["properties"]["nickname"]

      test = {
         "id" : kakao_id,
         "nickname" : kakao_nickname
      }

      return JsonResponse(test, status=200)