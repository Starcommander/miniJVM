/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mini.util.wasm;

import org.mini.util.WasmUtil;

/**
 * @author Paul Kashofer (Starcommander@github)
 */
public class UserAgent
{
  private static String userAgentStr;
  
  public static String getUserAgentStr()
  {
    if (userAgentStr == null)
    {
      String script = "navigator.userAgent;";
      userAgentStr = WasmUtil.strExecuteJS(script, true);
    }
    return userAgentStr;
  }
  
  public static boolean isMobileDevice()
  {
    return getUserAgentStr().toLowerCase().contains("mobile");
  }
}
