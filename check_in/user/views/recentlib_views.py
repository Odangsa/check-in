from django.views import View
from django.http import JsonResponse

class DetailView(View):
    def get(self, request):
        userid = request.GET['userid']

        recent_libraries = 1
        response = {'userid': userid, 'recent_libraries': recent_libraries}
        return JsonResponse(response, status=200)