#!/bin/bash

#${GCCHOME} setup as: /usr/bin/gcc

echo "First enter sudo access, needed for docker:"
sudo echo "Continue..."
cd .. || exit 3

GCC="sudo docker run --rm -v $(pwd):/src emscripten/emsdk emcc"

pre_cleanup()
{
  rm -f web/build/* || exit 1
  rm -f web/asset_dir/lib/* || exit 1
}

copy_asset()
{
  echo "Copy asset files..."
  mkdir -p web/asset_dir/lib/
  cp binary/lib/minijvm_rt.jar web/asset_dir/lib/ || exit 2
  cp binary/libex/minijvm_test.jar web/asset_dir/lib/ || exit 2
}

gcc_minijvm()
{
  echo "Compile mini_jvm"

  SRCLIST=$(find ${CSRC}  -type f  -name "*.c" -not -path "${CSRC}/utils/sljit/*"  -not -path "${CSRC}/cmake-*" -not -path "${CSRC}/.*")

  echo "Using compiler: ${GCC}"
  mkdir -p web/build
  INCL_APP="-I${CSRC}/jvm -I${CSRC}/utils/ -I${CSRC}/utils/sljit/ -I${CSRC}/utils/https/ -I${CSRC}/utils/https/mbedtls/include/"

  ${GCC} -o web/build/index.html $APP_SWITCH $DO_PRE $DO_THREAD_SWITCH $MEM_SWITCH $INCL_APP $DO_THREADS $SRCLIST ${CSRC}/utils/sljit/sljitLir.c -pthread  -lpthread -lm -ldl
  EXT_VAL=$?
  echo "Build exit value: $EXT_VAL"
  if [ "$EXT_VAL" != "0" ]; then
    exit $EXT_VAL
  fi
  sudo chown 1000:1000 web/build/*
}

set_vars()
{
  # Sources
  CSRC="minijvm/c"
  CSRC_GUI="desktop/glfw_gui/c"

  APP_SWITCH="-D EMSCRIPTEN_CONSOLE"

  ##Execute local files is not possible
  #You have to preload files that are accessable
  DO_PRE="--preload-file web/asset_dir@/"

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

  # Not enough memory error fix
  # A slow solution: MEM_SWITCH="-s ALLOW_MEMORY_GROWTH=1"
  MEM_SWITCH="-s INITIAL_MEMORY=33554432"

  # Dynamic linking leads to error
  #DYN_LINK_SWITCH="--use-preload-plugins -s MAIN_MODULE=2"

  # Call this the main_module to allow linking to side-module ($1)
  #MODULE_SWITCH="-s MAIN_MODULE"
}

set_vars
pre_cleanup
copy_asset
gcc_minijvm
./web/modify.sh "web/build/" "CONSOLE"
echo "Files stored in web/build/"

