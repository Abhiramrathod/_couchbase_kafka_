pipeline {
    agent any

    stages {
        stage('Checkout code') {
            steps {
                script {
                    checkout scm
                }
            }
        }
        stage('Set up JDK 17') {
            steps {
                script {
                    env.JAVA_HOME = tool name: 'JDK 17', type: 'jdk'
                    env.PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
                }
            }
        }
        stage('Cache Maven packages') {
            steps {
                script {
                    if(fileExists('.m2/repository')) {
                        cache path: '.m2/repository', key: "maven-${env.BRANCH_NAME}-${env.BUILD_ID}"
                    }
                }
            }
        }
        stage('Build with maven') {
            steps {
                script {
                    sh 'mvn clean install'
                }
            }
        }
    }
}