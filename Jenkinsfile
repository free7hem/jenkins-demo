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
    stage('Build') {
      steps {
        script {
          sh 'mvn clean package -Dmaven.test.skip'
        }
      }
    }
    stage('Get JWT Token') {
      steps {
        script {
          withCredentials([usernamePassword(credentialsId: 'portainer-global', passwordVariable: 'p_passwd', usernameVariable: 'p_user')]) {
             // some block
          }
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
    // stage('Build Docker Image on Portainer') {
    //   steps {
    //     script {
    //       // Build the image
    //       withCredentials([usernamePassword(credentialsId: 'Github', usernameVariable: 'GITHUB_USERNAME', passwordVariable: 'GITHUB_PASSWORD')]) {
    //           def repoURL = """
    //             https://portainer.<yourdomain>.com/api/endpoints/1/docker/build?t=reactApp:latest&remote=https://$GITHUB_USERNAME:$GITHUB_PASSWORD@github.com/$GITHUB_USERNAME/react-bolierplate.git&dockerfile=Dockerfile&nocache=true
    //           """
    //           def imageResponse = httpRequest httpMode: 'POST', ignoreSslErrors: true, url: repoURL, validResponseCodes: '200', customHeaders:[[name:"Authorization", value: env.JWTTOKEN ], [name: "cache-control", value: "no-cache"]]
    //       }
    //     }
    //   }
    // }
  }
}