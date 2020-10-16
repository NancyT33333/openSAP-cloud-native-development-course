echo "Hint: run script with 'source localEnvironmentSetup.sh'"
echo "This script prepares the current shell's environment variables (not permanently)"

# Used for backing services like the PostgreSQL database
export VCAP_APPLICATION={}
export VCAP_SERVICES='{"postgresql-x64-12":[{"name":"test","label":"postgresql-x64-12","credentials":{"dbname":"postgres","hostname":"localhost","password":"postgres","port":"5432","uri":"postgres://postgres:postgres@localhost:5432/postgres","username":"postgres"},"tags":["relational","postgresql"],"plan":"free"}]}'

# Used for dependent service call
export USER_ROUTE=https://bulletinboard-ads0192837.cfapps.eu10.hana.ondemand.com

# Overwrite logging library defaults
export APPENDER=STDOUT
export LOG_APP_LEVEL=TRACE

echo \$VCAP_SERVICES=$VCAP_SERVICES
