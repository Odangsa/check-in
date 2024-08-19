from django.urls import path
from book.views import DetailView, PopularView

urlpatterns = [
    path('detail/', DetailView.as_view()),
    path('recommend/popular/', PopularView.as_view())
]
