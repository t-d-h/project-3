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
        def RunCheck = sh(script: "/home/jenkins/testing-scripts/curl-test.sh localhost:8090 > /dev/null", returnStatus: true)
        if (RunCheck !=0 ) {
            error("Application crashed when testing")
        }
        println("========End of code checking stage========")
    }
}

stage('Building and pushing container image') {
    node("k1") {
        git branch: 'main', url: "https://github.com/t-d-h/project-3.git"
        def getVersion = sh(script: "cat golang-http/info", returnStdout: true)
        buildContainerImage = sh(script: "docker build -t ${getVersion} golang-http/")
        pushIMGtoRegistry = sh(script: "docker push ${getVersion}")

    }
}

stage('Confirm to deploy on dev environment') {
    input message: "Pushing container success, do you want to deploy it on dev?", ok: 'OK'
}

stage('Deploy on dev') {
    node("k1") {
        git branch: 'main', url: "https://github.com/t-d-h/project-3.git"
        def getVersion = sh(script: "cat golang-http/info", returnStdout: true)
        sh(script: "cd k8s-manifest/golang-http/overlays/dev && kustomize edit set image ${getVersion}")
        sh(script: "kubectl apply -k k8s-manifest/golang-http/overlays/dev")
        println("========Deployed on dev environment========")
    }
}

stage('Checking dev environment') {
    node("k1") {
        sh(script: "/home/jenkins/testing-scripts/start-golang.sh")
        def RunCheck = sh(script: "/home/jenkins/testing-scripts/curl-test.sh https://dev-app.t-d-h.net > /dev/null", returnStatus: true)
        if (RunCheck !=0 ) {
            error("Application crashed after deploying on dev environment")
        }
    }
}

stage('Confirm to deploy on prod') {
    input message: "The application is working normally on dev environment, do you want to deploy it on prod?", ok: 'OK'
}

stage('Deploy in prod environment') {
    node("k1") {
        git branch: 'main', url: "https://github.com/t-d-h/project-3.git"
        def getVersion = sh(script: "cat golang-http/info", returnStdout: true)
        sh(script: "cd k8s-manifest/golang-http/overlays/prod && kustomize edit set image ${getVersion}")
        sh(script: "kubectl apply -k k8s-manifest/golang-http/overlays/prod")
    }
}

stage('Checking prod environment') {
    node("k1") {
        sh(script: "/home/jenkins/testing-scripts/start-golang.sh")
        def RunCheck = sh(script: "/home/jenkins/testing-scripts/curl-test.sh https://app.t-d-h.net > /dev/null", returnStatus: true)
        if (RunCheck !=0 ) {
            error("Application crashed after deploying on prod environment")
        }
    }
}