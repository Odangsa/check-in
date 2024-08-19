from django.urls import path
from user import views

urlpatterns = [
    path('recentlib/', views.RecentLibView.as_view()),
    path('stamp/', views.StampView.as_view())
]
