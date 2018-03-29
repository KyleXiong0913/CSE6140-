#!/bin/bash

set -e

javac *.java
rm -rf results/ || true
mkdir results

datafiles=`ls -S -r data`
algs=(BnB Approx LS1 LS2)

for file in ${datafiles[@]}; do
  for alg in ${algs[@]}; do
    cmd="java -Xss5m Program -inst data/$file -alg $alg -time 600 -seed 10"
    echo "-> $cmd"
    eval $cmd
  done
done

rm *.class
