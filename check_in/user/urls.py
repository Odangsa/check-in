from django.urls import path
from user import views

urlpatterns = [
    path('recentlib/', views.RecentLibView.as_view())
    # path('stamps/', PopularView.as_view())
]
