#!/bin/bash
for i in `seq 1 20`; 
do curl $1 > /dev/null; 
done