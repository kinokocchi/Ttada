from django.conf.urls import patterns, include, url

# Uncomment the next two lines to enable the admin:
# from django.contrib import admin
# admin.autodiscover()

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'resttest.views.home', name='home'),
    # url(r'^resttest/', include('resttest.foo.urls')),

    # Uncomment the admin/doc line below to enable admin documentation:
#    url(r'^admin/doc/', include('django.contrib.admindocs.urls')),

    url(r'^echo/(?P<path>.*)$',    'echo.views.echo'),
    url(r'^appping/$',    'echo.views.appping'),
    url(r'^django/app-login/$',    'echo.views.applogin'),
    url(r'^app-login/$',    'echo.views.applogin'),
    url(r'^(.*)$',    'echo.views.echo'  ),

    # Uncomment the next line to enable the admin:
    # url(r'^admin/', include(admin.site.urls)),
)
