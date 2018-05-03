#!/usr/bin/groovy

def call(String projectName, String jarName, String deploymentEnv = 'dev', boolean approvalRequired = true, String deploymentServer = '46.226.109.170', String deploymentUser = 'deploy') {
    def filePath = "/opt/projects/${deploymentEnv}/${projectName}/"
    def approved = !approvalRequired
    if (approvalRequired) {
        approved = deployOnEnv(projectName, deploymentEnv)
        echo "appoved : $approved"
    }
    if (approved) {
        echo "Deploying ${projectName} on ${deploymentEnv}"
        unstash "target"
        sshagent(credentials: ['deploy_ssh']) {
            sh "ssh -o StrictHostKeyChecking=no ${deploymentUser}@${deploymentServer} 'echo hello'"
            sh "ssh -f ${deploymentUser}@${deploymentServer} 'pkill -e -f ${deploymentEnv}/${projectName} || true' "
            sh "scp target/*.jar ${deploymentUser}@${deploymentServer}:${filePath}"
            sh "ssh -f ${deploymentUser}@${deploymentServer} 'cd ${filePath} && nohup java -jar ${filePath}${jarName} & '"
        }
    }
}

def deployOnEnv(String projectName, String deploymentEnv = 'dev') {
  try {
    timeout(time: 50, unit: 'MINUTES') {
        def approval = input message: "Do you approve the deployment of ${projectName} on ${deploymentEnv}?", 
                    parameters: [booleanParam(defaultValue: false, description: '', name: 'approvalResponse')]
        return approval
    }
  } catch(e) {
    return false
  }
}
