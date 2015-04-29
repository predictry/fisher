## Predictry Analytics

Predictry analytics consist 2 main things (actions stats and sales stats)


###Command
#### php artisan logs:matrix-calculation
Processing log json formatted file from s3 to CSV and import into mysql db.

arg:
bucket

option:
--prefix
--start_date
--end_date

sample:
php artisan logs:matrix-calculation trackings --prefix=action-logs-json-formatted/ --start_date=2015-04-19 --end_date=2015-04-30
