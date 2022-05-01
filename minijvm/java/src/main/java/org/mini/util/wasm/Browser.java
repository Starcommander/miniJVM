/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mini.util.wasm;

import java.util.Properties;
import org.mini.util.WasmUtil;

/**
 *
 * @author ppp
 */
public class Browser
{
  public static Properties getUrlVariables()
  {
    Properties prop = new Properties();
    String uriBar = WasmUtil.strExecuteJS("window.location.href.toString();", true);
    int varsIdx = uriBar.indexOf('?');
    String className = null;
    String jarUrl = null;
    if (varsIdx > 0)
    {
      String uriVars[] = uriBar.substring(varsIdx+1).split("&");
      for (String var : uriVars)
      {
        if (!var.contains("=")) { continue; }
        String keyVal[] = var.split("=");
        prop.put(keyVal[0], keyVal[1]);
      }
    }
    return prop;
  }
  
}
