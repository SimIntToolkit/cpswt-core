# docker network create cpswt-core
docker run \
  --name cpswt-core \
  --restart=always \
  --detach \
  --network cpswt-core \
  --env DOCKER_HOST=tcp://docker:2376 \
  --env DOCKER_CERT_PATH=/certs/client \
  --env DOCKER_TLS_VERIFY=1 \
  --publish 8081:8080 \
  -v /var/run/docker.sock:/var/run/docker.sock \
  cpswt-core:latest