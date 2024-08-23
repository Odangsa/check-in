# Generated by Django 5.1 on 2024-08-21 11:32

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('user', '0001_initial'),
    ]

    operations = [
        migrations.CreateModel(
            name='LibCode',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('libraryname', models.CharField(max_length=50)),
                ('code', models.IntegerField()),
            ],
        ),
        migrations.AlterField(
            model_name='visit',
            name='visitdate',
            field=models.DateField(),
        ),
    ]