## Build Image
```sh
docker build -t cpswt-core:lts -f Dockerfile .
```

## Run
```sh
docker run -it --rm -p 8081:8080 -v /var/run/docker.sock:/var/run/docker.sock cpswt-core:lts
```