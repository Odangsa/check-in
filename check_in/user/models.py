from django.db import models

# class Visit(models.Model):
#     userid = models.CharField(max_length=50)
#     visitdate = models.DateField()
#     libraryname = models.CharField(max_length=50)

class LibCode(models.Model):
    libraryname = models.CharField(max_length=50)
    code = models.IntegerField()


# class Bookisbn(models.Model):
#     isbn = models.BigIntegerField(db_column='ISBN', primary_key=True)  # Field name made lowercase.
#     smbl = models.IntegerField(db_column='SMBL')  # Field name made lowercase.

#     class Meta:
#         app_label = 'book'
#         managed = False
#         db_table = 'bookisbn'

class Visitlib(models.Model):
    idnum = models.BigIntegerField(db_column='IDnum', primary_key=True)  # Field name made lowercase.
    visitlib = models.CharField(db_column='visitLib', max_length=50, primary_key=True)  # Field name made lowercase.
    visitdate = models.DateField(db_column='visitDate', primary_key=True)  # Field name made lowercase.

    class Meta:
        app_label = 'visit'
        managed = False
        db_table = 'visitlib'
