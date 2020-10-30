REM This script prepares the current shell's environment variables (not permanently)

REM Used for backing services like the PostgreSQL database
SET VCAP_APPLICATION={}
SET VCAP_SERVICES={"rabbitmq-lite": [{"credentials": {"hostname": "127.0.0.1","password": "guest","uri": "amqp://guest:guest@127.0.0.1:5672","username": "guest"},"name": "rabbitmq-lite","label": "rabbitmq-lite","tags": ["rabbitmq33","rabbitmq","amqp"]}],"postgresql-x64-12":[{"name":"test","label":"postgresql-x64-12","credentials":{"dbname":"postgres","hostname":"localhost","password":"postgres","port":"5432","uri":"postgres://postgres:postgres@localhost:5432/postgres","username":"postgres"},"tags":["relational","postgresql"],"plan":"free"}]}

REM Used for dependent service call
SET USER_ROUTE=https://opensapcp5userservice.cfapps.eu10.hana.ondemand.com

REM Overwrite logging library defaults
SET APPENDER=STDOUT
SET LOG_APP_LEVEL=TRACE
