from django.views import View
from django.http import JsonResponse
from user.models import UserInfo
import json

class BbtiView(View):
    def get(self, request):
        userid = request.GET['userid']

        result = \
            {
                "success": "False",
                "bbti": "None"
            }

        try:
            userinfo = UserInfo.objects.get(idnum=userid)
            result['success'] = "True"
            result['bbti'] = userinfo.bbti
            return JsonResponse(result, status=200)
        except:
            return JsonResponse(result, status=200)
        
    def post(self, request):
        body = json.loads(request.body)
        userid = body['userid']
        bbti = body['bbti']

        result = \
            { 
                "success" : "False", 
                "userid" : "None",
                "bbti" : "None"
            }

        try:
            userinfo = UserInfo.objects.get(idnum=userid)
        except:
            return JsonResponse(result, status=200)

        userinfo.bbti = bbti
        try:
            userinfo.save()
        except:
            return JsonResponse(result, status=200)
        
        result['success'] = "True"
        result['userid'] = userid
        result['bbti'] = bbti
        return JsonResponse(result, status=200)