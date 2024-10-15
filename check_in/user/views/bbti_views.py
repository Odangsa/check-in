from django.views import View
from django.http import JsonResponse
from user.models import UserInfo

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