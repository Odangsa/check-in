from django.views import View
from user.models import Visit, LibCode
from django.http import JsonResponse
import datetime, json

level_name = ['뚜벅이', '킥보드', '자전거', '버스', '기차', '비행기', '우주선']

class BoardView(View):
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
    
class RegisterView(View):
    def post(self, request):
        body = json.loads(request.body)
        userid = body['userid']
        code = body['code']
        date = body['date']
        visitdate = datetime.date(int("20"+date[0:2]), int(date[2:4]), int(date[4:6]))

        try:
            query = LibCode.objects.get(code=int(code))
        except Exception:
            return JsonResponse({"success" : "False", "libraryname" : "None"}, status=200)

        query_set = Visit.objects.filter(userid=userid).filter(visitdate=visitdate).values()
        if len(query_set) != 0:
            return JsonResponse({"success" : "False", "libraryname" : "None"}, status=200)
        
        visit = Visit(userid=userid, visitdate=visitdate, libraryname=query.libraryname)
        visit.save()

        return JsonResponse({"success" : "True", "libraryname" : query.libraryname}, status=200)


