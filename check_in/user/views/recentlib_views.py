from django.views import View
from django.http import JsonResponse
from user.models import Visit
from django.db.models.aggregates import Max, Count

class RecentLibView(View):
    def get(self, request):
        userid = request.GET['userid']

        query_set = Visit.objects.filter(userid=userid).values('libraryname').annotate(
            recent = Max('visitdate'), count = Count('libraryname')
        ).order_by('recent')[:3]

        recent_libraries = []
        for data in query_set:
            recent_libraries.append({"library" : data['libraryname'], "visit_count": data['count']})
        
        response = {'userid': userid, 'recent_libraries': recent_libraries}
        return JsonResponse(response, status=200)