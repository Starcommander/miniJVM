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
TAR1="$TAR_DIR/mini_jvm.js"
KEY1=""
REP1=""

# For Console-App: Add input-textfield and button
TAR2="$TAR_DIR/mini_jvm.html"
KEY2=$(cat "$TAR2" | grep -n '<textarea id="output"' | awk '{ print $1 }' | tr -d ':')
REP2=$(cat <<EOF
  <textarea id="output" rows="8" readonly="readonly"></textarea>
  <input type="text" id="inputTxt" disabled="disabled"> <!-- Hugo -->
  <button onclick="submitInputStream()">Submit</button>
  <script>
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

if [ "$MOD_TYPE" = "CONSOLE" ]; then
  do_pre "$TAR2"
  modify_target "$KEY2" 1 "$REP2" "$TAR2"
  do_post "$TAR2"
else
  echo "Arguments error in modify.sh: Use arguments: tarFile modType"
fi
