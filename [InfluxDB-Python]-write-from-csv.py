#https://stackoverflow.com/questions/43186129/importing-csv-file-data-into-influxdb
import datetime
import random
import time
import os
import csv
from csv import reader
import argparse
from influxdb import client as influxdb


db = influxdb.InfluxDBClient('gzqa-stg.deckers.com', 8086, '','' , 'jmeter')
def read_data():
    with open('data/integration-spike.csv') as f:
        return [x.split(',') for x in f.readlines()[1:]]

a = read_data()

for metric in a:
    influx_metric = [{
        'measurement': 'your_measurement',
        'time': a[0],
        'fields': {
            'value': a[1]
        }
    }]
    db.write_points(influx_metric)
