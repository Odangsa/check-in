from django.db import models

class Visit(models.Model):
    userid = models.CharField(max_length=50)
    visitdate = models.DateField()
    libraryname = models.CharField(max_length=50)
