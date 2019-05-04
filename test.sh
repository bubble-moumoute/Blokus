#!/usr/bin/env bash

javac **/*.java
ret=$?

nbTest=0
nbTestPassed=0

if (($# == 1)); then
  java test/$1
  lastRet=$?
  ret=$(($ret || $lastRet))
  nbTest=$((nbTest + 1))
  if (($lastRet == 0)); then
    printf "$1: \e[32mSuccess\e[0m\n"
    nbTestPassed=$((nbTestPassed + 1))
  else
    printf "$1: \e[31mFailure\e[0m\n"
  fi
else
  for test in test/*.class; do
    echo "-------------------------------------------"
    echo "$test"
    echo "-------------------------------------------"
    java ${test%.class}
    lastRet=$?
    ret=$(($ret || $lastRet))
    nbTest=$((nbTest + 1))
    if (($lastRet == 0)); then
      printf "$test: \e[32mSuccess\e[0m\n"
      nbTestPassed=$((nbTestPassed + 1))
    else
      printf "$test: \e[31mFailure($lastRet)\e[0m\n"
    fi
    echo
  done
fi

rm -r **/*.class

echo "~~~~~~~~~~~~~~~~Result~~~~~~~~~~~~~~~~"
if (($nbTest == $nbTestPassed)); then
  printf "\e[32mSuccess\e[0m\n"
else
  printf "\e[31mFailure\e[0m\n"
fi
printf "\e[35;1m$nbTestPassed tests passed out of $nbTest\e[0m\n"

exit $ret
