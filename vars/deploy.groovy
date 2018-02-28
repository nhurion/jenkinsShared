#!/usr/bin/groovy

//def call(String projectName, String jarName, String deploymentEnv = 'dev', String deploymentServer = '46.226.109.170', String deploymentUser = 'deploy') {
def call(Map config){
    def filePath = "/opt/projects/${config.deploymentEnv}/${config.projectName}/"
    //input "Do you approve the deployment of ${projectName} on ${deploymentEnv}?"
    echo "Deploying ${config.projectName} on ${config.deploymentEnv}"
    unstash "target"
    sshagent(credentials: ['deploy_ssh']) {
        sh "ssh -o StrictHostKeyChecking=no ${config.deploymentUser}@${config.deploymentServer} 'echo hello'"
        sh "ssh -f ${config.deploymentUser}@${config.deploymentServer} 'pkill -e -f ${config.deploymentEnv}/${config.projectName} || true' "
        sh "scp target/*.jar ${config.deploymentUser}@${config.deploymentServer}:${filePath}"
        sh "ssh -f ${config.deploymentUser}@${config.deploymentServer} 'cd ${filePath} && nohup java -jar ${filePath}${config.jarName} & '"
    }
}