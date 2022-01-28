#!/bin/bash

echo "Requirement: jdk1.8 jar javac "
cd $(dirname $0)

JAVAC=javac
JAR=jar


build_jar() # Args: jarName srcPath tarPath bootCP cp
{
    rm -rf $3/$1
    mkdir classes
    find $2/java -name "*.java" >source.txt
    ${JAVAC} -bootclasspath $4 -cp $5 -encoding "utf-8" -d classes @source.txt
    if [ -d $2/resource/ ]; then
      cp -R $2/resource/* classes/
    fi
    ${JAR} cf $1 -C classes ./
    rm -rf source.txt
    rm -rf classes
    mv $1 $3/
}

ask_build() # Args: msg
{
  echo "$1 [y,n]"
  read -e -p ">>> " INPUT
  if [ "$INPUT" = "y" ]; then
    return 0
  else
    return 1
  fi
}

mkdir -p lib
mkdir -p libex

if ask_build "build lib/minijvm_rt.jar" ; then
$(build_jar minijvm_rt.jar ../minijvm/java/src/main lib "." ".")
fi

if ask_build "build libex/glfw_gui.jar" ; then
$(build_jar glfw_gui.jar ../desktop/glfw_gui/java/src/main libex "lib/minijvm_rt.jar" ".")
fi

if ask_build "build libex/minijvm_test.jar" ; then
$(build_jar minijvm_test.jar ../test/minijvm_test/src/main libex "lib/minijvm_rt.jar" ".")
fi


