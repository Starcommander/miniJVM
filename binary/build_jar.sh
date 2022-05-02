#!/bin/bash

# ####################
# Builds the jar files.
# Possible arguments: [-a] [java_base]
# (-a for all, java_base for path to different javac)
# ####################

echo "Requirement: jdk1.8 jar javac "
cd $(dirname $0)

JAVAC=javac
JAR=jar

if [ "$1" = "-a" ]; then
  BUILDALL="y"
  shift
fi
if [ ! -z "$1" -a -x "$1/javac" ]; then
  JAVAC="$1/javac"
fi

check_java_vers()
{
  local j_v=$($JAVAC -version 2>&1 | grep -o " 1.8.*")
  if [ -z "$j_v" ]; then
    echo "Error: Wrong java version:"
    $JAVAC -version
    echo "Must be 1.8"
    echo "Hint: Use java_home as first argument"
    exit 1
  fi
}

include_example_res() # Args: path/name.class classes-dir
{
  if [ "$1" = "org/mini/apploader/AppManager.class" ]; then
    mkdir "$2/res"
    find ../ -name NotoSansCJKsc-Medium.otf -exec cp '{}' "$2/res" \;
  fi
}

build_jar_example() # Args: subDir path/name.class tardir
{
  if [ -f "$1/$2" ]; then
    local tmp_dir=$(mktemp -d)
    local key_name=$(echo $2 | rev | cut -d '.' -s -f 2-99 | rev)
    local key_name_dot=$(echo $key_name | tr '/' '.')
    local key_name_base=$(basename $key_name | tr '/' '.')

    mkdir -p "$tmp_dir/classes/$(dirname $2)"
    cp "$1/$key_name"* "$tmp_dir/classes/$(dirname $2)"
    echo "Manifest-Version: 1.0" > "$tmp_dir/MANIFEST.MF"
    echo "Main-Class: $key_name_dot" >> "$tmp_dir/MANIFEST.MF"
    echo "Created-By: Starcommander@github" >> "$tmp_dir/MANIFEST.MF"
    include_example_res "$2" "$tmp_dir/classes"
    ${JAR} cmf "$tmp_dir/MANIFEST.MF" $3/Ex_$key_name_base.jar -C "$tmp_dir/classes" ./
    rm -r "$tmp_dir"
  fi
}

build_jar() # Args: jarName srcPath tarPath bootCP cp [grepSubFilter]
{
    local filter="java"
    if [ ! -z "$6" ]; then
      local filter="$2/$6"
    fi
    rm -rf $3/$1
    mkdir classes
    find $2/java -name "*.java" | grep "$filter" > source.txt
    ${JAVAC} -bootclasspath $4 -cp $5 -encoding "utf-8" -d classes @source.txt || exit 1
    if [ -d $2/resource/ ]; then
      cp -R $2/resource/* classes/
    fi
    build_jar_example classes HelloGlfw.class "$3"
    build_jar_example classes test/HelloConsole.class "$3"
    build_jar_example classes org/mini/apploader/AppManager.class "$3"
    ${JAR} cf $1 -C classes ./
    rm -rf source.txt
    rm -rf classes
    mv $1 $3/
}

ask_build() # Args: msg
{
  if [ "$BUILDALL" = "y" ]; then
    return 0
  fi
  echo "$1 [y,n]"
  read -e -p ">>> " INPUT
  if [ "$INPUT" = "y" ]; then
    return 0
  else
    return 1
  fi
}

check_java_vers

mkdir -p lib
mkdir -p libex

if ask_build "build lib/minijvm_rt.jar" ; then
  build_jar minijvm_rt.jar ../minijvm/java/src/main lib "." "."
fi

if ask_build "build libex/glfw_gui.jar" ; then
  build_jar glfw_gui.jar ../desktop/glfw_gui/java/src/main libex "lib/minijvm_rt.jar" "." "java/org"
fi

if ask_build "build libex/minijvm_test.jar" ; then
  build_jar minijvm_test.jar ../test/minijvm_test/src/main libex "lib/minijvm_rt.jar" "."
fi

if ask_build "build libex/glfw_test.jar" ; then
  build_jar glfw_test.jar ../desktop/glfw_gui/java/src/main libex "lib/minijvm_rt.jar" "libex/glfw_gui.jar" "java/test"
fi
