#!/bin/bash

TAR_DIR=$1
MOD_TYPE=$2 # CONSOLE

do_pre() # Args: tarFile
{
  local fn=$(basename "$1")
  cp "$1" "/tmp/backup_$fn" || exit 1
}

modify_target() # Args: KeyNum numLines Replacement tarFile
{
  local line_cnt=$(echo $1 | wc -l)
  if [ -z "$1" ]; then
    echo "Error, key not found for modify!"
    exit 2
  elif [ $line_cnt -ne 1 ]; then
    echo "Error, key found multible times: $line_cnt"
  fi
  echo "### Modify $4 ###"
  local key=$1
  local num=$2
  local p1=$(cat "$4" | head -n $(( key - 1 )) )
  local p2="$3"
  local p3=$(tail -n +$(( key + num )) "$4")
  echo "$p1" > "$4"
  echo -e "$p2" >> "$4"
  echo "$p3" >> "$4"
}

do_post() # Args: tarFile
{
  local fn=$(basename "$1")
  echo "Diff of $fn"
  diff "/tmp/backup_$fn" "$1"
}

# Currently not used.
TAR1="$TAR_DIR/index.js"
KEY1=""
REP1=""

# For Console-App: Add input-textfield and button
TAR2="$TAR_DIR/index.html"
KEY2=$(cat "$TAR2" | grep -n '<textarea id="output"' | awk '{ print $1 }' | tr -d ':')
REP2=$(cat <<EOF
  <textarea id="output" rows="8" readonly="readonly"></textarea>
  <input type="text" id="inputTxt" onkeydown="submitKeyCheck(event)" disabled="disabled" /> <!-- Starcommander mod -->
  <button onclick="submitInputStream()">Submit</button>
  <script>
    function submitKeyCheck(e)
    {
      var code = (e.keyCode ? e.keyCode : e.which);
      if (code == 13) {
        submitInputStream();
      }
    }
    function submitInputStream()
    {
      var el = document.getElementById("inputTxt");
      if (!el.disabled)
      {
        el.value = el.value + " ";
        el.disabled = true;
      }
    }
  </script>
EOF
)

# function submitKeyDown(event)
# {
#   console.log('Key down: KEY=' + event.key + " CODE=" + event.code + " LOCALE=" + event.locale + " LOC=" + event.location);
# }

REP_FILEDIALOG='<input type="file" style="display:none" id="FakeFileDialog"/>'
REP_GUIINPUT=$(cat <<EOF
  <textarea id="output" rows="8" readonly="readonly"></textarea>
  <input type="text" id="inputTxt" value="X" oninput="submitInput(event)" autocomplete="off" style="width: 0px; height: 0px; position: absolute; top: -9999px;" />
  <script> <!-- Starcommander mod -->
    function submitInput(event)
    {
      if (event.inputType = 'insertText')
      {
//        console.log('Key press: ' + event.data);
        Module.ccall('callback_key_js', // name of C function
          '', // return type
          ['number','number'], // argument types
          [event.data.charCodeAt(0), 0]); // arguments
      }
      else if (event.inputType = 'insertLineBreak')
      {
        console.log('Key press: ENTER');
      }
      else if (event.inputType = 'insertFromPaste')
      {
        console.log('Key paste: ' + event.data);
      }
      else if (event.inputType = 'deleteByComposition')
      {
        console.log('Key press: deleteByComposition');
      }
      else if (event.inputType = 'deleteContentBackward')
      {
        console.log('Key press: deleteContentBackward');
      }
    }
  </script>
EOF
)

if [ "$MOD_TYPE" = "CONSOLE" ]; then
  do_pre "$TAR2"
  # Add input textfield and button
  modify_target "$KEY2" 1 "$REP2" "$TAR2"
  # Hide gl-canvas on console-app
  sed -i -e 's#<div class="emscripten_border">#<div hidden class="emscripten_border">#g' "$TAR2"
  # Hide emscripten-switches
  sed -i -e "s#^<span id='controls'>#<!--#g" "$TAR2"
  sed -i -e "s#^</span>#-->#g" "$TAR2"
  do_post "$TAR2"
elif [ "$MOD_TYPE" = "WINAPP" ]; then
  do_pre "$TAR2"
  modify_target "$KEY2" 1 "$REP_GUIINPUT\n$REP_FILEDIALOG" "$TAR2"
  do_post "$TAR2"
else
  echo "Arguments error in modify.sh: Use arguments: tarFile modType"
fi
