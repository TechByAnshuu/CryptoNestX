pipeline {
  agent { label 'docker-agent' }

  environment {
    DOCKERHUB_CREDS  = credentials('dockerhub-cryptonest')
    SONAR_TOKEN      = credentials('sonar-token')
    SLACK_WEBHOOK    = credentials('slack-webhook')
    IMAGE_TAG        = "${env.BUILD_NUMBER}-${env.GIT_COMMIT?.take(8) ?: 'unknown'}"
    SERVICES         = 'auth-service portfolio-service exchange-service notification-service'
    REGISTRY         = 'cryptonest'
  }

  options {
    buildDiscarder(logRotator(numToKeepStr: '20'))
    timeout(time: 30, unit: 'MINUTES')
    disableConcurrentBuilds()
    timestamps()
  }

  stages {

    stage('Checkout') {
      steps {
        git branch: 'main',
            url: 'https://github.com/your-org/cryptonestx.git',
            credentialsId: 'github-token'
        sh 'git log --oneline -5'
      }
    }

    stage('Maven Build — All Services') {
      steps {
        sh '''
          mvn clean package -DskipTests \
            -pl auth-service,portfolio-service,exchange-service,notification-service \
            -B --no-transfer-progress
        '''
      }
    }

    stage('Unit Tests') {
      steps {
        sh '''
          mvn test \
            -pl auth-service,portfolio-service,exchange-service,notification-service \
            -B --no-transfer-progress
        '''
      }
      post {
        always {
          junit '**/target/surefire-reports/*.xml'
          jacoco(
            execPattern: '**/target/jacoco.exec',
            classPattern: '**/target/classes',
            sourcePattern: '**/src/main/java',
            minimumLineCoverage: '80'
          )
        }
      }
    }

    stage('SonarQube Analysis') {
      steps {
        withSonarQubeEnv('SonarQube') {
          sh '''
            mvn sonar:sonar \
              -Dsonar.projectKey=cryptonestx \
              -Dsonar.login=${SONAR_TOKEN} \
              -B --no-transfer-progress
          '''
        }
      }
    }

    stage('Quality Gate') {
      steps {
        timeout(time: 5, unit: 'MINUTES') {
          waitForQualityGate abortPipeline: true
        }
      }
    }

    stage('Build Docker Images') {
      steps {
        script {
          env.SERVICES.split(' ').each { svc ->
            sh "docker build -t ${env.REGISTRY}/${svc}:${env.IMAGE_TAG} ./${svc}"
            sh "docker tag ${env.REGISTRY}/${svc}:${env.IMAGE_TAG} ${env.REGISTRY}/${svc}:latest"
          }
        }
      }
    }

    stage('Push to Docker Hub') {
      steps {
        sh 'echo $DOCKERHUB_CREDS_PSW | docker login -u $DOCKERHUB_CREDS_USR --password-stdin'
        script {
          env.SERVICES.split(' ').each { svc ->
            sh "docker push ${env.REGISTRY}/${svc}:${env.IMAGE_TAG}"
            sh "docker push ${env.REGISTRY}/${svc}:latest"
          }
        }
      }
    }

    stage('Deploy — Staging (Docker Swarm)') {
      when { branch 'main' }
      steps {
        sh '''
          docker stack deploy \
            --compose-file docker-compose.prod.yml \
            --with-registry-auth \
            cryptonest
        '''
        sh 'sleep 30 && docker stack ps cryptonest --no-trunc'
      }
    }

    stage('Smoke Tests — Staging') {
      when { branch 'main' }
      steps {
        sh '''
          for port in 8081 8082 8083 8084; do
            status=$(curl -s -o /dev/null -w "%{http_code}" \
              http://localhost:${port}/actuator/health)
            if [ "$status" != "200" ]; then
              echo "Health check FAILED on port $port (HTTP $status)"
              exit 1
            fi
            echo "Port $port: UP"
          done
        '''
      }
    }

    stage('Approval Gate — Production') {
      when { branch 'main' }
      steps {
        timeout(time: 30, unit: 'MINUTES') {
          input message: '🚀 Deploy to Production?',
                ok: 'Approve & Deploy',
                submitter: 'lead-engineers'
        }
      }
    }

    stage('Deploy — Production') {
      when { branch 'main' }
      steps {
        script {
          env.SERVICES.split(' ').each { svc ->
            sh """
              docker service update \
                --image ${env.REGISTRY}/${svc}:${env.IMAGE_TAG} \
                --update-parallelism 1 \
                --update-delay 15s \
                --update-failure-action rollback \
                cryptonest_${svc.replace('-', '_')}
            """
          }
        }
      }
    }

  }

  post {
    success {
      slackSend(
        color: 'good',
        message: "✅ *CryptoNestX Build #${env.BUILD_NUMBER} SUCCEEDED*\n" +
                 "Branch: `${env.GIT_BRANCH}` | Tag: `${env.IMAGE_TAG}`\n" +
                 "<${env.BUILD_URL}|View in Jenkins>"
      )
    }
    failure {
      slackSend(
        color: 'danger',
        message: "❌ *CryptoNestX Build #${env.BUILD_NUMBER} FAILED*\n" +
                 "Branch: `${env.GIT_BRANCH}`\n" +
                 "<${env.BUILD_URL}|View in Jenkins>"
      )
    }
    always {
      cleanWs()
      sh 'docker logout'
    }
  }
}
