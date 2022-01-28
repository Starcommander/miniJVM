package org.mini.util;

public class WasmUtil
{
  private static int is_webassembly = -1;

  /** Returns whether the JVM runs in web-browser and therefore we have access to javascript */
  public static boolean isWebAssembly()
  {
    if (is_webassembly == 0) { return false; }
    if (is_webassembly == 1) { return true; }
    if (isWebAssemblyNative()) { is_webassembly = 1; }
    else { is_webassembly = 0; }
    return (is_webassembly == 1);
  }
  private static native boolean isWebAssemblyNative();

  public static void executeJS(String script) { executeJS(script, false, false); }
  /** Executes a script as string
   * @param forceMain Ensures that script runs in main thread, for executing document, window and more. */
  public static native int executeJS(String script, boolean forceMain, boolean returnInt);

 /** Returns: 1=MainThread 2=BrowserThread 3=Both 0=None */
  public static native int getThreadType();

  public static native void setMainLoop(boolean active);

}
