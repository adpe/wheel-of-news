#!/bin/bash
IMAGE_NAME="adpe/wheel-of-news:latest"
CONTAINER_NAME_APP="wheel-of-news_app"
CONTAINER_NAME_DB="wheel-of-news_db"

start=$(date +"%s")

if [[ -z "$SSH_PORT" || -z "$SSH_USER" || -z "$SSH_HOST" ]]; then
  echo "Error: One or more SSH environment variables are missing"
  exit 1
fi

ssh -i key.txt -p "$SSH_PORT" "$SSH_USER@$SSH_HOST" -o StrictHostKeyChecking=no << EOF
IMAGE_NAME="$IMAGE_NAME"
CONTAINER_NAME_APP="$CONTAINER_NAME_APP"
CONTAINER_NAME_DB="$CONTAINER_NAME_DB"
SPRING_DATASOURCE_URL=$1
SPRING_JPA_SHOW_SQL=$2
LOGGING_LEVEL_ORG_HIBERNATE_SQL=$3
LOGGING_LEVEL_ORG_HIBERNATE_ORM_JDBC_BIND=$4
NEWS_API_KEY=$5
NEWS_FETCH_CRON=$6

docker pull \$IMAGE_NAME

if [ "\$(docker ps -qa -f name=\$CONTAINER_NAME_APP)" ]; then
    if [ "\$(docker ps -q -f name=\$CONTAINER_NAME_APP)" ]; then
        echo "Container is running -> stopping it..."
        docker stop \$CONTAINER_NAME_APP;
    fi

    echo "Removing existing container..."
    docker rm $CONTAINER_NAME_APP
fi

docker run -d -p 9000:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL=$SPRING_DATASOURCE_URL \
  -e SPRING_JPA_SHOW_SQL=$SPRING_JPA_SHOW_SQL \
  -e LOGGING_LEVEL_ORG_HIBERNATE_SQL=$LOGGING_LEVEL_ORG_HIBERNATE_SQL \
  -e LOGGING_LEVEL_ORG_HIBERNATE_ORM_JDBC_BIND=$LOGGING_LEVEL_ORG_HIBERNATE_ORM_JDBC_BIND \
  -e NEWS_API_KEY=$NEWS_API_KEY \
  -e NEWS_FETCH_CRON=$NEWS_FETCH_CRON \
  -e IMAGE_NAME=$IMAGE_NAME \
  --name $CONTAINER_NAME_APP \
  --volumes-from $CONTAINER_NAME_DB \
  $IMAGE_NAME

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
