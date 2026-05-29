pipeline {

    agent any

    tools {
        maven 'Maven'
        jdk 'jdk21'
    }

    stages {

        stage('Clean Project') {
            steps {
                bat 'mvn clean'
            }
        }

        stage('Run Selenium Tests') {
            steps {

                catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {

                    bat 'mvn test'

                }
            }
        }

        stage('Generate Allure Report') {
            steps {

                allure([
                    includeProperties: false,
                    jdk: '',
                    results: [[path: 'allure-results']]
                ])

            }
        }

        stage('Run JMeter Performance Tests') {
            steps {

                bat 'if exist performance_testing\\results\\results.jtl del performance_testing\\results\\results.jtl'

                bat 'if exist performance_testing\\results\\html-report rmdir /s /q performance_testing\\results\\html-report'

                bat 'jmeter -n -t performance_testing\\api_test.jmx -l performance_testing\\results\\results.jtl -e -o performance_testing\\results\\html-report'
            }
        }
    }

    post {

        always {

            archiveArtifacts artifacts: 'performance_testing/results/html-report/**', fingerprint: true

            archiveArtifacts artifacts: 'performance_testing/results/results.jtl', fingerprint: true

            junit 'target/surefire-reports/*.xml'
        }

        success {
            echo 'All tests passed and reports generated successfully!'
        }

        unstable {
            echo 'Some tests failed — check Allure report for details.'
        }

        failure {
            echo 'Build failed — check console output.'
        }
    }
}

