docker run \
  --name cpswt-core \
  --restart=on-failure \
  --detach \
  --network cpswt-core \
  --env DOCKER_HOST=tcp://docker:2376 \
  --env DOCKER_CERT_PATH=/certs/client \
  --env DOCKER_TLS_VERIFY=1 \
  --publish 9090:8080 \
  --volume cpswt-core-data:/var/cpswt-core_home \
  --volume cpswt-core-docker-certs:/certs/client:ro \
  cpswt-core:latest