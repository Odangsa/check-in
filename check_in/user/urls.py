from django.urls import path
from user import views

urlpatterns = [
    path('recentlib/', views.RecentLibView.as_view()),
    path('stamp/board', views.BoardView.as_view()),
    path('stamp/register', views.RegisterView.as_view()),
]
