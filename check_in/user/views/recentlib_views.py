from django.views import View
from django.http import JsonResponse
from user.models import Visitlib
from django.db.models.aggregates import Max, Count

class RecentLibView(View):
    def get(self, request):
        userid = request.GET['userid']

        query_set = Visitlib.objects.filter(idnum=userid).values('visitlib').annotate(
            recent = Max('visitdate'), count = Count('visitlib')
        ).order_by('-recent')

        recent_libraries = []
        for data in query_set[:3]:
            recent_libraries.append({"library" : data['visitlib'], "visit_count": data['count']})
        
        response = {'userid': userid, 'recent_libraries': recent_libraries}
        return JsonResponse(response, status=200)