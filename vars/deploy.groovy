#!/usr/bin/groovy
def call(String projectName, String jarName, String deploymentEnv = 'dev', String deploymentServer = '46.226.109.170', String deploymentUser = 'deploy') {

    stage ("Deployment of ${projectName} on ${deploymentEnv}"){
        steps {
            script {
                filePath = "/opt/projects/${deploymentEnv}/${projectName}/"
            }
            //input 'Do you approve the deployment?'
            echo 'deploying...'
            unstash "target"
            sshagent (credentials: ['deploy_ssh']) {
                sh "ssh -o StrictHostKeyChecking=no ${deploymentUser}@${deploymentServer} 'echo hello'"
                sh "ssh -f ${deploymentUser}@${deploymentServer} 'pkill -e -f ${deploymentEnv}/${projectName} || true' "
                sh "scp target/*.jar ${deploymentUser}@${deploymentServer}:${filePath}"
                sh "ssh -f ${deploymentUser}@${deploymentServer} 'cd ${filePath} && nohup java -jar ${filePath}${jarName} & '"
            }
        }
    }
}