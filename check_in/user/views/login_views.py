from django.shortcuts import redirect
from django.views import View
from django.http import JsonResponse
from django.conf import settings
from user.models import UserInfo
import json

class KakaoLoginView(View):
   def get(self, request):
      kakao_api = 'https://kauth.kakao.com/oauth/authorize?response_type=code'
      redirect_uri = 'http://127.0.0.1:8000/user/kakao/callback'
      client_id = settings.KAKAO_REST_TOKEN

      return redirect(f'{kakao_api}&client_id={client_id}&redirect_uri={redirect_uri}')
   
class LoginView(View):
    def post(self, request):
        body = json.loads(request.body)
        kakao_id = body['userid']
        kakao_nickname = body['nickname']
       
        try:
            userinfo = UserInfo.objects.get(idnum=kakao_id)
        except:
            userinfo = UserInfo.objects.create(idnum=kakao_id, nickname=kakao_nickname)

        result = \
                {
                    "userid" : userinfo.idnum,
                    "nickname" : userinfo.nickname,
                    "bbti" : userinfo.bbti,
                }

        return JsonResponse(result, status=200)