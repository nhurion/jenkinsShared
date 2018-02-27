
def call(String projectName, String deploymentEnv = 'dev') {
    def workspacePath = pwd()
    sh 'sleep 60'
    sh "curl --retry-delay 10 --retry 5 http://${projectName}.${deploymentEnv}.hurion.be/manage/info -o ${workspacePath}/info-${deploymentEnv}.json"
    archiveArtifacts artifacts: "info-${deploymentEnv}.json", fingerprint:true

}
