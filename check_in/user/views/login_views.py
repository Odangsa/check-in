from django.shortcuts import redirect
from django.views import View
from django.conf import settings

class KakaoLoginView(View):
   def get(self, request):
      kakao_api = 'https://kauth.kakao.com/oauth/authorize?response_type=code'
      redirect_uri = 'http://127.0.0.1:8000/user/kakao/callback'
      client_id = settings.KAKAO_REST_TOKEN

      return redirect(f'{kakao_api}&client_id={client_id}&redirect_uri={redirect_uri}')