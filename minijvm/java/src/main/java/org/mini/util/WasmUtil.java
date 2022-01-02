package org.mini.util;

public class WasmUtil
{
  /** Returns whether the JVM runs in web-browser and therefore we have access to javascript **/
  public static native boolean isWebAssembly();

  public static native void executeJS(String script);
}
