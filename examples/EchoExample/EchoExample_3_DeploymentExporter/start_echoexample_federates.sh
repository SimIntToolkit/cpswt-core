#!/usr/bin/env bash

FEDMANAGER_ENDPOINT="localhost:8083/fedmgr"

wait_for_federation_manager_REST() {
	QUERY="curl ${FEDMANAGER_ENDPOINT} \
        	--write-out %{http_code} \
        	--silent \
        	--output /dev/null"

	echo -n "Waiting for federation manager RESTful API to get online..."

    HTTP_CODE=`${QUERY}`
    while [ "${HTTP_CODE}" -ne 200 ] ; do
        echo -n "."
        sleep 1
        HTTP_CODE=`${QUERY}`
    done
    echo "--------------------------"
    echo "Federation manager started"

}

# issue start
initialize_federation_manager() {
  curl -i -X POST ${FEDMANAGER_ENDPOINT} --data '{"action": "START"}' -H "Content-Type: application/json"
}

# start the federates
start_federates() {
	for FED in "EchoServer-LateJoiner" "EchoClient-LateJoiner"; do
    	mvn exec:java -P ${FED},ExecJava &
    done
}

wait_for_federation_manager_REST
initialize_federation_manager
start_federates