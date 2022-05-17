pipeline {
  agent any
  stages {
    stage('Prepare') {
      steps {
        script {
          sh 'mvn --version'
        }
      }
    }
    stage('Build Jar') {
      steps {
        script {
          sh 'mvn clean package -Dmaven.test.skip'
          sh 'tar czvf app.tar.gz Dockerfile target/*.jar'
        }
      }
    }
    stage('Get JWT Token') {
      steps {
        script {
          withCredentials([usernamePassword(credentialsId: 'portainer-global', usernameVariable: 'PORTAINER_USERNAME', passwordVariable: 'PORTAINER_PASSWORD')]) {
              def json = """
                  {"Username": "$PORTAINER_USERNAME", "Password": "$PORTAINER_PASSWORD"}
              """
              def jwtResponse = httpRequest acceptType: 'APPLICATION_JSON', contentType: 'APPLICATION_JSON', validResponseCodes: '200', httpMode: 'POST', ignoreSslErrors: true, consoleLogResponseBody: true, requestBody: json, url: "https://pt.gocheung.com/api/auth"
              def jwtObject = new groovy.json.JsonSlurper().parseText(jwtResponse.getContent())
              env.JWTTOKEN = "Bearer ${jwtObject.jwt}"
          }
        }
        echo "${env.JWTTOKEN}"
      }
    }
    stage('Build Image') {
      steps {
        script {
          sh '''
			curl "https://pt.gocheung.com/api/endpoints/26/docker/build?dockerfile=Dockerfile&t=camuscheung%2Fapp" \\
			  -X "POST" \\
			  -H "authorization: $JWTTOKEN" \\
			  -H "content-type: application/x-tar" \\
			  --data-binary @app.tar.gz
		  '''
        }
      }
    }
    stage('Deploy') {
      steps {
        script {
          sh 'curl -H "Authorization: Bearer Calong@2015" watchtower:8080/v1/update'
        }
      }
    }
  }
}