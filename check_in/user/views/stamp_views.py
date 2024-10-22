from django.views import View
from user.models import LibCode, Visitlib
from django.http import JsonResponse
import datetime, json, urllib

class BoardView(View):
    def get(self, request):
        userid = request.GET['userid']
        try:
            int(userid)
        except:
            userid = userid[1:]
        
        query_set = Visitlib.objects.filter(idnum=userid).order_by('visitdate').values('visitlib')
        
        visitlib = []
        for i in query_set:
            visitlib.append(i['visitlib'])

        stamp_result = \
        {
            "userid": userid,
            "transportation":
                [ {
                    "type": "뚜벅이",
                    "visited_libraries": []
                },
                {
                    "type": "킥보드",
                    "visited_libraries": [] 
                },
                {
                    "type": "자전거",
                    "visited_libraries": [] 
                },
                {
                    "type": "버스",
                    "visited_libraries": [] 
                },
                {
                    "type": "기차",
                    "visited_libraries": [] 
                },
                {
                    "type": "비행기",
                    "visited_libraries": [] 
                },
                {
                    "type": "우주선",
                    "visited_libraries": [] 
                },                                                    
                ]
        }

        repeat = len(visitlib) if len(visitlib)<=70 else 70
        for i in range(repeat): # 최대 70번
            stamp_result['transportation'][i // 10]['visited_libraries'].append(visitlib[i])

        for i in range(7 - ((len(visitlib) - 1) // 10 + 1)):
            if len(visitlib) < 61:
                stamp_result['transportation'][(len(visitlib) - 1) // 10 + 1 + i]['visited_libraries'] = "None"

        return JsonResponse(stamp_result, status=200)

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
            print('no code')
            return JsonResponse({"success" : "False", "libraryname" : "None"}, status=200)

        query_set = Visitlib.objects.filter(idnum=userid).filter(visitdate=visitdate).values()
        if len(query_set) != 0:
            print('already visit')
            return JsonResponse({"success" : "False", "libraryname" : "None"}, status=200)
        
        visit = Visitlib.objects.create(idnum=userid, visitdate=visitdate, visitlib=query.libraryname)
        visit.save()
        

        return JsonResponse({"success" : "True", "libraryname" : query.libraryname}, status=200)

