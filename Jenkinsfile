pipeline{
    agent any
    tools{
        maven "maven"

    }
    stages{
        stage("Build JAR File"){
            steps{
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/camilagos/Tingeso-1']])
                dir("demo"){
                    bat "mvn clean install"
                }
            }
        }
        stage("Test"){
            steps{
                dir("demo"){
                    bat "mvn test"
                }
            }
        }        
        stage("Build and Push Docker Image"){
            steps{
                dir("demo"){
                    script{
                        withCredentials([usernamePassword(credentialsId: 'docker-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                            bat 'echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin'
                            bat "docker build -t camilagos/spring-image:latest ."
                            bat "docker push camilagos/spring-image:latest"
}
                    }                    
                }
            }
        }
    }
}
