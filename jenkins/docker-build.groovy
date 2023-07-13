def pkgName

stage('Code checking') {
    node ("k1") {
        git branch: 'main', url: "https://github.com/t-d-h/project-3.git"
        def ExecutingCheck = sh(script: "timeout 5 go run golang-http/main.go", returnStatus: true)
        if (ExecutingCheck != 124) {
            error("Error when executing the code, please see the above logs")
        }
    }
}
stage('Performance check') {
    node("k1") {
        sh(script: "timeout 60 go run golang-http/main.go &&")
        def RunCheck = sh(script: "for i in `seq 1 20`; do curl localhost:8090; done", returnStatus: true)
        if (RunCheck !=0 ) {
            error("Performance test failed")
        }
    }
}