import json
from django.views import View
from django.http import JsonResponse

class DetailsView(View):
    def get(self, request):
        isbn = request.GET['isbn']
        addr = request.GET['addr']

        return JsonResponse({'message':'created', 'isbn':isbn, 'addr':addr}, status=200)