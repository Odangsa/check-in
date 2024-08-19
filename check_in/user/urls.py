from django.urls import path

urlpatterns = [
    path('recentlib/', ),
    path('stamps/', PopularView.as_view())
]
