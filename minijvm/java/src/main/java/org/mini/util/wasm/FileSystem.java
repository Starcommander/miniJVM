/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mini.util.wasm;

import org.mini.util.NativeCallback;
import org.mini.util.NativeCallback.CallbackItem;
import org.mini.util.WasmUtil;

/**
 *
 * @author Paul Kashofer (Starcommander@github)
 */
public class FileSystem
{
  /** Shows a file dialog, imports the file to tarDir, and returns the fullFilePath via Callback.
   * @param tarDir The target dir, where to store the file in internal-filesystem.
   * @param cb The callback that is executed when finish. **/
  public static void openFileDialog(String tarDir, CallbackItem cb)
  {
      long id = NativeCallback.registerCallback(cb);
      
      String command = "var fakeFD = document.getElementById('FakeFileDialog');";
      command = command + "fakeFD.onchange = () => {";
      command = command + "  if (fakeFD.files.length === 0) {";
      command = command + "    Module.ccall('native_callback', null, ['string', 'number'], [null, " + id + "]);";
      command = command + "    return;";
      command = command + "  };";
      command = command + "  console.log('The selected file: ' + fakeFD.files[0].name);";
command = command + "  console.log('Step0');";
      command = command + "  var fileReader = new FileReader();";
command = command + "  console.log('Step1');";
      command = command + "fileReader.onload = function(e) {";
      command = command + "  var arrayBuffer = fileReader.result;";
      command = command + "  var tarPath = '" + tarDir + "/' + fakeFD.files[0].name;";
      command = command + "  FS.writeFile(tarPath, new Uint8Array(arrayBuffer));";
command = command + "  console.log('Step2');";
//      command = command + "  FS.writeFile('" + targetFile + ".finish', 'finish');";
command = command + "  console.log('Step3');";
      command = command + "Module.ccall('native_callback', null, ['string', 'number'], [tarPath, " + id + "]);";
command = command + "  console.log('Step4');";
      command = command + "};";
      command = command + "fileReader.readAsArrayBuffer(new Blob([fakeFD.files[0]]));";
      command = command + "  fakeFD.files.length = 0";
      command = command + "};";
      command = command + "fakeFD.click();";
      
      WasmUtil.executeJS(command);
  }
}
