pipeline {
  agent any
  stages {
    stage('Build jar') {
      agent {
        docker { 
          image 'maven:3.8.1-adoptopenjdk-11'
          args '-v $HOME/.m2:/root/.m2 -l traefik.enable=false'
        }
      }
      steps {
        sh 'mvn clean package -Dmaven.test.skip'
        sh 'tar czvf app.tar.gz Dockerfile target/*.jar'
        stash includes: 'app.tar.gz', name: 'app'
        cleanWs()
      }
    }
    stage('Build image') {
      steps {
        script {
          withCredentials([usernamePassword(credentialsId: 'portainer-global', usernameVariable: 'PORTAINER_USERNAME', passwordVariable: 'PORTAINER_PASSWORD')]) {
            def json = """
              {"Username": "$PORTAINER_USERNAME", "Password": "$PORTAINER_PASSWORD"}
            """
            def jwtResponse = httpRequest \
              acceptType: 'APPLICATION_JSON', \
              contentType: 'APPLICATION_JSON', \
              validResponseCodes: '200', \
              httpMode: 'POST', \
              ignoreSslErrors: true, \
              consoleLogResponseBody: true, \
              requestBody: json, \
              url: "https://portainer.hsichin.com/api/auth"
            def jwtObject = new groovy.json.JsonSlurper().parseText(jwtResponse.getContent())
            env.JWTTOKEN = "Bearer ${jwtObject.jwt}"
          }
        }
        unstash 'app'
        sh '''
          curl "https://portainer.hsichin.com/api/endpoints/26/docker/build?dockerfile=Dockerfile&t=camuscheung%2Fapp" \\
            -X "POST" \\
            -H "authorization: $JWTTOKEN" \\
            -H "content-type: application/x-tar" \\
            --data-binary @app.tar.gz
        '''
      }
    }
    stage('Nofity watchtower') {
      steps {
        script {
          withCredentials([string(credentialsId: 'watchtower', variable: 'WATCHTOWER_TOKEN')]) {
          def token = "Bearer $WATCHTOWER_TOKEN"
          httpRequest \
            customHeaders: [[name: 'Authorization', value: token]], \
            validResponseCodes: '200', \
            url: 'http://watchtower:8080/v1/update'
          }
        }
      }
    }
  }
  post {
    success {
      // Remove dangling images
      sh 'docker image prune -f'
    }
    always {
      // Clean up workspace
      cleanWs()
    }
  }
}