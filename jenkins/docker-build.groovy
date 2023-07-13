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
        sh(script: "./testing-scripts/test.sh")
        def RunCheck = sh(script: "start-golang.sh", returnStatus: true)
        if (RunCheck !=0 ) {
            error("Application crashed when testing")
        }
    }
}


stage('Performance check') {
    node("k1") {

    }
}