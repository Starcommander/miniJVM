#!/bin/bash

#${GCCHOME} setup as: /usr/bin/gcc

echo "First enter sudo access:"
sudo echo "Continue..."
cd .. || exit 3

GCC="sudo docker run --rm -v $(pwd):/src emscripten/emsdk emcc"
OSNAME="Darwin"
UNAME=`uname -a`

if [[ $UNAME == *$OSNAME* ]]
then
    echo "Mac"
    BINDIR="macos"
    LIBDIR="mac"
    LIBFILE="libgui.dylib"
else
    echo "Linux"
    echo "UBUNTU lib install : sudo apt-get install openjdk-8-jdk gcc libxi-dev libxcursor-dev libxrandr-dev libgl1-mesa-dev libxinerama-dev"
    echo "CentOS lib install : yum -y install java-1.8.0-openjdk gcc mesa-libGL-devel libXi-devel libXcursor-devel libXrandr-devel libXinerama-devel"

    BINDIR="centos_x64"
    LIBDIR="centos_x64"
    LIBFILE="libgui.so"
fi

echo "Copy asset files..."
mkdir -p web/asset_dir/lib/
cp binary/lib/minijvm_rt.jar web/asset_dir/lib/ || exit 2
#cp binary/libex/glfw_gui.jar web/asset_dir/lib/ || exit 2
cp binary/libex/minijvm_test.jar web/asset_dir/lib/ || exit 2

echo "Compile mini_jvm"
CSRC="minijvm/c"

SRCLIST=$(find ${CSRC}  -type f  -name "*.c" -not -path "${CSRC}/utils/sljit/*"  -not -path "${CSRC}/cmake-*" -not -path "${CSRC}/.*")

##Execute local files is not possible
#You have to preload files that are accessable
DO_PRE="--preload-file web/asset_dir"

##Using threads is not easy.
#You have to enable the apache_mod mod_headers
#- a2enmod headers
#You have to set two header-values
#- Edit /etc/apache2/apache2.conf
#- Add line: Header set Cross-Origin-Embedder-Policy require-corp
#- Add line: Header set Cross-Origin-Opener-Policy same-origin
#- Restart or reload apache
DO_THREADS="-s PTHREAD_POOL_SIZE=8"

# Run main() in bg to get free js-console
DO_THREAD_SWITCH="-s PROXY_TO_PTHREAD"

echo "Using compiler: ${GCC}"
mkdir -p web/build
${GCC} -o web/build/mini_jvm.html $DO_PRE $DO_THREAD_SWITCH -I${CSRC}/jvm -I${CSRC}/utils/ -I${CSRC}/utils/sljit/ -I${CSRC}/utils/https/ -I${CSRC}/utils/https/mbedtls/include/ $DO_THREADS $SRCLIST ${CSRC}/utils/sljit/sljitLir.c -pthread  -lpthread -lm -ldl
echo "Build exit value: $?"
exit 0

####### END #########


echo "compile glfw_gui"

CSRC="../desktop/glfw_gui/c"
SRCLIST=`find ${CSRC} -type f -name "*.c"  -not -path "${CSRC}/cmake-*" -not -path "${CSRC}/.*"`
#
if [[ $UNAME == *$OSNAME* ]] 
then
    ${GCC} -shared -fPIC -o ${LIBFILE} -I../minijvm/c/jvm -I${CSRC}/ -I${CSRC}/deps/include -L${CSRC}/deps/lib/${LIBDIR} -lpthread -lglfw3 -framework Cocoa -framework IOKit -framework OpenGL -framework CoreFoundation -framework CoreVideo $SRCLIST
else
    ${GCC} -shared -fPIC -o ${LIBFILE} -I../minijvm/c/jvm -I${CSRC}/ -I${CSRC}/deps/include -L${CSRC}/deps/lib/${LIBDIR}   $SRCLIST -pthread -lglfw3 -lX11 -lXi -lpthread -lXcursor -lXrandr -lGL -lXinerama
fi
mv mini_jvm ${LIBFILE} ${BINDIR}/

