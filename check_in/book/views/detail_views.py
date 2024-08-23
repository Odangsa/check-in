import json
from django.views import View
from django.http import JsonResponse
from http.client import HTTPConnection
import xml.etree.ElementTree as ET
import requests
import json
from django.conf import settings

class DetailView(View):
    def get(self, request):
        lng = request.GET['lng']
        lat = request.GET['lat']

        isbn = request.GET['isbn']

        response = getlibraries(float(lng), float(lat), isbn)

        return JsonResponse(response, status=200)

region_dict = {'서울':11, '부산':21, '대구':22, '인천':23, '광주': 24, '대전':25, '울산':26, '세종':29, '경기':31, '강원':32, '충북':33, '충남':33, '전북':35, '전남':36, '경북':37, '경남':38, '제주':39}

def getlibraries(lng:float, lat:float, isbn: str):
    conn = HTTPConnection('data4library.kr')
    auth_key = settings.LIB_AUTH_TOKEN
    
    # 도서 상세정보 요청
    body = f'authKey={auth_key}&isbn13={isbn}'
    conn.request('GET', f'/api/srchDtlList?{body}', headers={'Host': 'data4library.kr'})
    response = conn.getresponse()
    r = response.read().decode()
    
    root = ET.fromstring(r)
    result = {'ISBN': isbn,
            'bookname': root[1][0][1].text,
            'authors': root[1][0][2].text,
            'publisher': root[1][0][3].text, 
            'publishyear': root[1][0][6].text,
            'bookimageURL': root[1][0][8].text,
            'description': root[1][0][11].text,
            }

    # 도서 소장 Library 목록 요청
    region = region_dict[loc2addr(lng,lat)]
    body = f'authKey={auth_key}&isbn={isbn}&region={region}&pageSize=1500'
    conn.request('GET', f'/api/libSrchByBook?{body}', headers={'Host': 'data4library.kr'})
    response = conn.getresponse()
    r = response.read().decode()
    
    root = ET.fromstring(r)
    libs = root[4]

    # 도서관 좌표 획득 (도서나루 api 불완전)
    lib_list = list()
    for lib in libs :
        try:
            float(lib[6].text) # api 소유 데이터가 가끔 한칸씩 밀려서
            lib_list.append([lib[1].text, (float(lib[5].text), float(lib[6].text)), lib[2].text])
        except:
            lib_list.append([lib[1].text, (float(lib[4].text), float(lib[5].text)), lib[2].text])
    usr_loc = (lat, lng)

    # 거리별 도서관 정렬
    lib_list.sort(key=lambda x: getdistance(usr_loc, x[1]))
    
    lib_json = []
    for i in lib_list:
        lib_json.append({'libname':i[0], 
                        'libaddr':i[2],
                        'latitude': i[1][0],
                        'longitude':i[1][1],
                        'distance':None
                        })
    
    # 상위 3개 거리정보 추가
    kakao_mobility_key = settings.KAKAO_MOBILITY_TOKEN
    
    for i in range(3):
        response = requests.get('https://apis-navi.kakaomobility.com/v1/directions', 
                                params={'origin': str(usr_loc[0])+','+str(usr_loc[1]), 'destination': str(lib_json[i]['longitude'])+','+str(lib_json[i]['latitude'])},
                                headers={'Authorization': 'KakaoAK '+kakao_mobility_key})
        r = response.content.decode('utf-8')
        root = json.loads(r)
        lib_json[i]['distance'] = round(root['routes'][0]['summary']['distance']/1000.0,1)
    
    result['libs'] = lib_json
    
    return result

def loc2addr(lng, lat):
    kakao_key = settings.KAKAO_REST_TOKEN
    try:
        response = requests.get('https://dapi.kakao.com/v2/local/geo/coord2regioncode.xml', 
                                params={'x': lat, 'y':lng},
                                headers={'Authorization': 'KakaoAK '+kakao_key})
    except Exception as e:
        print(e)
        return None
    
    r = response.content.decode('utf-8')
    root = ET.fromstring(r)

    return root.find('documents').find('region_1depth_name').text[:2]
    
def getdistance(loc1: tuple, loc2: tuple):
    return pow(loc2[0]-loc1[0],2)+pow(loc2[1]-loc1[1],2)