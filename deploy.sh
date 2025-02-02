#!/bin/bash
IMAGE_NAME="adpe/wheel-of-news:latest"
CONTAINER_NAME="wheel-of-news"

start=$(date +"%s")

if [[ -z "$SSH_PORT" || -z "$SSH_USER" || -z "$SSH_HOST" ]]; then
  echo "Error: One or more SSH environment variables are missing"
  exit 1
fi

ssh -i key.txt -p "$SSH_PORT" "$SSH_USER@$SSH_HOST" -o StrictHostKeyChecking=no << EOF
IMAGE_NAME="$IMAGE_NAME"
CONTAINER_NAME="$CONTAINER_NAME"

docker pull \$IMAGE_NAME

if [ "\$(docker ps -qa -f name=\$CONTAINER_NAME)" ]; then
    if [ "\$(docker ps -q -f name=\$CONTAINER_NAME)" ]; then
        echo "Container is running -> stopping it..."
        docker stop \$CONTAINER_NAME;
    fi
fi

docker run -d --rm -p 9000:8080 --name \$CONTAINER_NAME \$IMAGE_NAME

exit
EOF

status=$?

end=$(date +"%s")
diff=$((end - start))

if [ $status -eq 0 ]; then
  echo "Deployment successful in ${diff}s"
  exit 0
else
  echo "Deployment failed"
  exit 1
fi
