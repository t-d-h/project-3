def pkgName

stage('Code checking') {
    node ("k1") {
        git branch: 'main', url: "https://github.com/t-d-h/project-3.git"
        // executing-test
        def ExecutingCheck = sh(script: "timeout 5 go run golang-http/main.go", returnStatus: true)
        if (ExecutingCheck != 124) {
            error("Error when executing the code, please see the above logs")
        }
        // connection test
        sh(script: "/home/jenkins/testing-scripts/start-golang.sh")
        def RunCheck = sh(script: "/home/jenkins/testing-scripts/curl-test.sh > /dev/null", returnStatus: true)
        if (RunCheck !=0 ) {
            error("Application crashed when testing")
        }
        println("========End of code checking stage========")
    }
}

// stage('Confirm to build container image') {
//     input message: "All test passed, do you want to build the image?", ok: 'OK'
// }

stage('Building-pushing container image') {
    node("k1") {
        git branch: 'main', url: "https://github.com/t-d-h/project-3.git"
        def getVersion = sh(script: "cat golang-http/info", returnStdout: true)
        buildContainerImage = sh(script: "docker build -t ${getVersion} golang-http/")
        pushIMGtoRegistry = sh(script: "docker push ${getVersion}")
        println("========End of container building stage========")
    }
}

// stage('Confirm to deploy') {
//     input message: "Do you want to deploy it on dev?", ok: 'OK'
// }

stage('Deploy on dev') {
    node("k1") {
        git branch: 'main', url: "https://github.com/t-d-h/project-3.git"
        def getVersion = sh(script: "cat golang-http/info", returnStdout: true)
        sh(script: "cd k8s-manifest/golang-http/overlays/dev && kustomize edit set image ${getVersion}")
        sh(script: "kubectl apply -k k8s-manifest/golang-http/overlays/dev")
        println("========End of container building stage========")
    }
}