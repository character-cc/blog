bin/kc.bat start --proxy-headers xforwarded --proxy-trusted-addresses=127.0.0.1 --hostname=http://127.0.0.1/keycloak --http-enabled=true --http-port=9090
bin/kc.bat start-dev --proxy-headers xforwarded --proxy-trusted-addresses=127.0.0.1 --hostname=http://localhost/keycloak --http-enabled=true --http-port=9090
.\nginx.exe