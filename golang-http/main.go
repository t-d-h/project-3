package main

import (
    "fmt"
    "net/http"
)

func root(w http.ResponseWriter, req *http.Request) {

    fmt.Fprintf(w, "Web site version 1\n")
}


func hello(w http.ResponseWriter, req *http.Request) {

    fmt.Fprintf(w, "xin chaooo\n")
}

func headers(w http.ResponseWriter, req *http.Request) {

    for name, headers := range req.Header {
        for _, h := range headers {
            fmt.Fprintf(w, "%v: %v\n", name, h)
        }
    }
}

func main() {

    http.HandleFunc("/hello", hello)
    http.HandleFunc("/headers", headers)
	http.HandleFunc("/", root)

    http.ListenAndServe(":8090", nil)
}