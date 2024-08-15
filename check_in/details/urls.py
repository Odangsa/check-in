from django.urls import path
from details.views import DetailsView

urlpatterns = [
    path('', DetailsView.as_view())
]
