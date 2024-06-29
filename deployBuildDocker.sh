./gradlew clean shadowJar
docker build -t protocol-sn-user-plugin:latest .
docker compose up --detach