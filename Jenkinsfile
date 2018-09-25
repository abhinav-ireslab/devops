pipeline {
  agent none
  stages {
    stage('Maven Install') {
      agent {
        docker {
          image 'maven:3.5.0'
        }
      }
      steps {
		echo 'Making build.'
		set /p cmd=Command:
		%cmd%
		goto execute
        cmd 'mvn clean install'
      }
    } 
  }
}