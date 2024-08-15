from django.urls import path
from detail.views import DetailView

urlpatterns = [
    path('', DetailView.as_view())
]
