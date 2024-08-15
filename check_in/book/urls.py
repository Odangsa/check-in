from django.urls import path
from .views.detail_views import DetailView
from .views.popular_views import PopularView

urlpatterns = [
    path('detail/', DetailView.as_view()),
    path('recommend/popular/', PopularView.as_view())
]
