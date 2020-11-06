echo "Hint: run script with 'source localEnvironmentSetup.sh'"
echo "This script prepares the current shell's environment variables (not permanently)"

# Used for backing services like the PostgreSQL database
export VCAP_APPLICATION={}
export VCAP_SERVICES='{ "xsuaa": [ { "credentials": { "clientid": "sb-clientId!t0815", "clientsecret": "dummy-clientsecret", "identityzone": "trial1234", "identityzoneid": "a09a3440-1da8-4082-a89c-3cce186a9b6c", "tenantid": "a09a3440-1da8-4082-a89c-3cce186a9b6c", "uaadomain": "localhost", "tenantmode": "shared", "url": "dummy-url", "verificationkey": "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAm1QaZzMjtEfHdimrHP3/2Yr+1z685eiOUlwybRVG9i8wsgOUh+PUGuQL8hgulLZWXU5MbwBLTECAEMQbcRTNVTolkq4i67EP6JesHJIFADbK1Ni0KuMcPuiyOLvDKiDEMnYG1XP3X3WCNfsCVT9YoU+lWIrZr/ZsIvQri8jczr4RkynbTBsPaAOygPUlipqDrpadMO1momNCbea/o6GPn38LxEw609ItfgDGhL6f/yVid5pFzZQWb+9l6mCuJww0hnhO6gt6Rv98OWDty9G0frWAPyEfuIW9B+mR/2vGhyU9IbbWpvFXiy9RVbbsM538TCjd5JF2dJvxy24addC4oQIDAQAB-----END PUBLIC KEY-----", "xsappname": "bulletinboard-012345" }, "label": "xsuaa", "name": "uaa-bulletinboard", "plan": "application", "tags": [ "xsuaa" ] } ], "postgresql-x64-12": [ { "name": "test", "label": "postgresql-x64-12", "credentials": { "dbname": "postgres", "hostname": "localhost", "password": "postgres", "port": "5432", "uri": "postgres://postgres:postgres@localhost:5432/postgres", "username": "postgres" }, "tags": [ "relational", "postgresql" ], "plan": "free" } ] }'

# Used for dependent service call
export USER_ROUTE=https://opensapcp5userservice.cfapps.eu10.hana.ondemand.com

# Overwrite logging library defaults
export APPENDER=STDOUT
export LOG_APP_LEVEL=TRACE

echo \$VCAP_SERVICES=$VCAP_SERVICES
