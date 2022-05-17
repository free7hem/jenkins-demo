mvn clean package -Dmaven.test.skip
tar czvf app.tar.gz Dockerfile target/*.jar