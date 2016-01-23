# Create your views here.
from django.http import HttpResponse
from django.core.context_processors import csrf
from django.views.decorators.csrf import csrf_exempt
from django.views.decorators.csrf import ensure_csrf_cookie
from django.middleware.csrf import get_token

#@csrf_exempt
def echo (request, path):
#    print "COOKIES:"
#    for key, val in request.COOKIES.iteritems():
#        print "   '"+key+"' : '" + val + "'"
#
#    print "HEADERS:"
#    for key in sorted(request.META.keys()):
#        print "   '"+key+"' : '" , request.META[key] , "'"

    msg = "<html>"
    msg += "<p>" + path + "</p>"
    if request.method == 'POST' :
        userid = request.POST.get('login-id', "")
        userpwd = request.POST.get('login-pwd', "")
        msg += "<p> login-id: " + userid + "</p>"
        msg += "<p> login-pwd: " + userpwd + "</p>"
#        print request.body 
    msg += "</html>"
    return HttpResponse(msg)


@csrf_exempt
def appping (request):
    return getCsrfedResponse(request)


@csrf_exempt
def applogin (request):
    if request.method == 'POST' :
        print request.POST
        userid = request.POST.get('login-id', "")
        userpwd = request.POST.get('login-pwd', "")

        print userid
        print userpwd
    
#    csrf_token = get_token(request)
#    resp = HttpResponse("OK")
#    resp.set_cookie("csrftoken", csrf_token)
#    return resp

    return getCsrfedResponse(request)


@ensure_csrf_cookie
@csrf_exempt
def getCsrfedResponse(request):
    return HttpResponse("OK")



def echo (request, path):
    print request.method
    print path
    return HttpResponse("OK")
#    if request.method == 'PUT':
#        print request.read()
    
    
    
    
    





