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
                         withDockerRegistry(credentialsId: 'docker-credentials',
                         url: 'https://index.docker.io/v1/'){
                            bat "docker build -t camilagos/back-image ."
                            bat "docker push camilagos/back-image"
                        }
                    }                    
                }
            }
        }
    }
}
