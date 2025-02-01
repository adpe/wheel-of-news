#!/bin/bash
IMAGE_NAME=adpe/wheel-of-news:latest
CONTAINER_NAME=wheel-of-news

start=$(date +"%s")

ssh -p ${SSH_PORT} ${SSH_USER}@${SSH_HOST} -i key.txt -t -t -o StrictHostKeyChecking=no << 'ENDSSH'
docker pull $IMAGE_NAME

if [ "$(docker ps -qa -f name=$CONTAINER_NAME)" ]; then
    if [ "$(docker ps -q -f name=$CONTAINER_NAME)" ]; then
        echo "Container is running -> stopping it..."
        docker stop $CONTAINER_NAME;
    fi
fi

docker run -d --rm -p 8000:8000 --name $CONTAINER_NAME $IMAGE_NAME

exit
ENDSSH

if [ $? -eq 0 ]; then
  exit 0
else
  exit 1
fi

end=$(date +"%s")

diff=$(($end - $start))

echo "Deployed in : ${diff}s"