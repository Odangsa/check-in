from http.client import HTTPConnection
import xml.etree.ElementTree as ET
import requests

region_dict = {'서울':11, '부산':21, '대구':22, '인천':23, '광주': 24, '대전':25, '울산':26, '세종':29, '경기':31, '강원':32, '충북':33, '충남':33, '전북':35, '전남':36, '경북':37, '경남':38, '제주':39}

def check():
    libs = [1,2,3,4]
    for i in libs:
        if i%2==0:
            libs.remove(i)
    print(libs)

def getlibraries(usr_addr: str, isbn: str):
    conn = HTTPConnection('data4library.kr')
    try:
        authKey = open('authKey.txt', 'r').read()
    except Exception as e:
        print(e)
        return None
    region = region_dict[usr_addr.split()[0]]
    body = f'authKey={authKey}&isbn={isbn}&region={region}&pageSize=1500'

    # 도서 소장 Library 목록 요청
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
            lib_list.append([lib[1].text, (float(lib[5].text), float(lib[6].text)), lib[0].text])
        except:
            lib_list.append([lib[1].text, (float(lib[4].text), float(lib[5].text)), lib[0].text])
    usr_loc = addr2loc(usr_addr)

    # 거리별 도서관 정렬
    lib_list.sort(key=lambda x: getdistance(usr_loc, x[1]))
    
    # 65개 다 보여주면 너무 오래 걸림..
    lib_list = lib_list[:10]

    # 대출 가능여부 추가
    result = []
    for lib in lib_list:
        body = f'authKey={authKey}&libCode={lib[2]}&isbn13={isbn}'
        conn.request('GET', f'/api/bookExist?{body}', headers={'Host': 'data4library.kr'})
        response = conn.getresponse()
        r = response.read().decode()
        root = ET.fromstring(r)
        if root[1][0].text == 'Y':
            if root[1][1].text=='Y':
                result.append([lib[0], 'Y'])
            else:
                result.append([lib[0], 'N'])

    json = []
    for i in result:
        json.append({'libname':i[0], 'loanavailable':i[1]})
    
    print(json)

def addr2loc(addr: str):
    try:
        kakao_key = open('kakaoRestKey.txt', 'r').read()
    
        response = requests.get('https://dapi.kakao.com/v2/local/search/address.xml', 
                                params={'query': addr, 'analyze_type': 'similar'},
                                headers={'Authorization': 'KakaoAK '+kakao_key})
    except Exception as e:
        print(e)
        return None
    
    r = response.content.decode('utf-8')
    root = ET.fromstring(r)
    return (float(root[0][5].text), float(root[0][4].text))
    
def getdistance(loc1: tuple, loc2: tuple):
    return pow(loc2[0]-loc1[0],2)+pow(loc2[1]-loc1[1],2)