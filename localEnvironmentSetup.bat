REM This script prepares the current shell's environment variables (not permanently)

REM Used for backing services like the PostgreSQL database
SET VCAP_APPLICATION={}
SET VCAP_SERVICES={"postgresql-x64-12":[{"name":"test","label":"postgresql-x64-12","credentials":{"dbname":"postgres","hostname":"localhost","password":"postgres","port":"5432","uri":"postgres://postgres:postgres@localhost:5432/postgres","username":"postgres"},"tags":["relational","postgresql"],"plan":"free"}]}

REM Used for dependent service call
SET USER_ROUTE=https://bulletinboard-ads0192837.cfapps.eu10.hana.ondemand.com

REM Overwrite logging library defaults
SET APPENDER=STDOUT
SET LOG_APP_LEVEL=TRACE
