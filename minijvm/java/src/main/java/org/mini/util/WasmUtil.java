package org.mini.util;

public class WasmUtil
{
  /** Returns whether the JVM runs in web-browser and therefore we have access to javascript */
  public static native boolean isWebAssembly();

  public static void executeJS(String script) { executeJS(script, false); }
  /** Executes a script as string
   * @param forceMain Ensures that script runs in main thread, for executing document, window and more. */
  public static native void executeJS(String script, boolean forceMain);

 /** Returns: 1=MainThread 2=BrowserThread 3=Both 0=None */
  public static native int getThreadType();
}
