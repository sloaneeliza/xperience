pipeline {
    agent any
    
    environment {
        DOCKER_IMAGE = 'xperience-server'
        DOCKER_TAG = "${env.BUILD_NUMBER}"
        CONTAINER_NAME = 'xperience-server-container'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Unit Tests') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Build JAR') {
            steps {
                sh 'mvn clean package'
                archiveArtifacts 'target/*.jar'
            }
        }
        
        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}")
                }
            }
        }
        
        stage('Deploy') {
            steps {
                script {
                    sh "docker stop ${env.CONTAINER_NAME} || true"
                    sh "docker rm ${env.CONTAINER_NAME} || true"
                    sh """
                    docker run -d \
                      --name ${env.CONTAINER_NAME} \
                      -p 8080:8080 \
                      --restart unless-stopped \
                      ${env.DOCKER_IMAGE}:${env.DOCKER_TAG}
                    """
                }
            }
        }
    }
    
    post {
        failure {
            echo 'Pipeline failed!'
        }
    }
}
