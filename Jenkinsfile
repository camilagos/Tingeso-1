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
                        bat "docker context use default"
                        withCredentials([string(credentialsId: 'git-credentials', variable: 'dhpsw')]) {
                            bat 'echo %dhpsw% | docker login -u camilagos --password-stdin'
                            bat "docker build -t camilagos/back-image:latest ."
                            bat "docker push camilagos/back-image:latest"
                        }
                    }                    
                }
            }
        }
    }
}
