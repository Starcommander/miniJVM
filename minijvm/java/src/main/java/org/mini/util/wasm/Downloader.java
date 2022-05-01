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
 * @author ppp
 */
public class Downloader
{
  /** Downloads a file to tarDir, and calls cb on finish.
   * @param url The url to download.
   * @param tarDir The target dir where to store the file.
   * @param cb The CallbackItem with targetfile, or null on error. **/
  public static void fetch(String url, String tarDir, CallbackItem cb)
  {
    long id = NativeCallback.registerCallback(cb);
    
    int idx = url.lastIndexOf("/");
    String targetFile = tarDir + "/" + url.substring(idx);
    
    StringBuilder sb = new StringBuilder();
    sb.append("function CheckResponseError(response) {\n" +
    "  if (response.status >= 200 && response.status <= 299) {\n" +
    "    return response;\n" +
    "  } else {\n" +
    "    throw Error(response.statusText);\n" +
    "  }\n" +
    "}\n");
    
    sb.append("{fetch('").append(url).append("')");
    sb.append("  .then(CheckResponseError)");
    sb.append("  .then(response => response.arrayBuffer())");
    sb.append("  .then(data => {");
    sb.append("     FS.writeFile('").append(targetFile).append("', new Uint8Array(data));");
    sb.append("     Module.ccall('native_callback', null, ['string', 'number'], ['").append(targetFile).append("', ").append(id).append("]);");
    sb.append("}).catch((error) => { Module.ccall('native_callback', null, ['string', 'number'], [null, ").append(id).append("]); });");
    sb.append("}");
    WasmUtil.executeJS(sb.toString(),true,false);
  }
}
