from django.views import View
from user.models import Visit
from django.http import JsonResponse

level_name = ['뚜벅이', '킥보드', '자전거', '버스', '기차', '비행기', '우주선']

class StampView(View):
    def get(self, request):
        userid = request.GET['userid']
        
        query_set = Visit.objects.filter(userid=userid).order_by('visitdate').values()
        
        stamps = []
        
        level = 0
        query_count = 0

        while level<len(level_name):
            visited = []
            for i in range(8):
                if query_count < len(query_set):
                    visited.append(query_set[query_count]['libraryname'])
                    query_count += 1
                else:
                    break
            
            if visited == []:
                visited = "None"
            stamp = {"type": level_name[level], "visited_libraries": visited }
            stamps.append(stamp)
            level += 1
            

        response = {"userid": userid, "transportation": stamps}
        return JsonResponse(response, status=200)