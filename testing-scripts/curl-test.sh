#!/bin/bash
for i in `seq 1 20`; 
do curl localhost:8090 > /dev/null; 
done