/*
 * Author: Paul Kashofer [Starcommander@github]
 */
package org.mini.util.wasm;

import java.net.URL;
import org.mini.net.MiniHttpClient;
import org.mini.reflect.Launcher;
import org.mini.util.WasmUtil;

/**
 *
 * @author Paul Kashofer [Starcommander@github]
 */
public class MainClassLoader
{
  final static String TARGET_FILE = "/app.jar";
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args)
  {
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
        if (keyVal[0].equals("jar")) { jarUrl = keyVal[1]; }
        if (keyVal[0].equals("main")) { className = keyVal[1]; }
      }
    }
    if (className == null || jarUrl == null)
    {
      System.out.println("Error: Missing uri-variables 'main' and/or 'jar'");
    }
    System.out.println("Downloading jar from: " + jarUrl);
    final String classNameFinal = className;
    MiniHttpClient cli = new MiniHttpClient(jarUrl, null, (url, data) -> loadJar(classNameFinal, data));
    cli.setTargetFile(TARGET_FILE);
    if (WasmUtil.getThreadType()==0) { cli.run(); }
    else { 
      
      System.out.println("Starting in BG"); //TODO: Irgendwie startet er nicht im BG, sondern synchron.
      cli.start(); }
  }
  
  static void loadJar(String className, byte[] data)
  {
    System.out.println("Starting main class: " + className);
    StandaloneGuiAppClassLoader sgacl = new StandaloneGuiAppClassLoader(TARGET_FILE);
    Thread.currentThread().setContextClassLoader(sgacl);
    try{
      Class c = sgacl.loadClass(className);
      java.lang.reflect.Method m = c.getDeclaredMethod("main", String[].class);
      m.invoke(c, new Object[]{""});
    } catch (Exception e) { e.printStackTrace(); }
  }
  
  //TODO: This is a copy of org/mini/apploader/StandalongGuiAppClassLoader.java
  static class StandaloneGuiAppClassLoader extends ClassLoader {
    String[] jarPath = new String[1];

    public StandaloneGuiAppClassLoader(String jarPath) {
        super(ClassLoader.getSystemClassLoader());
        this.jarPath[0] = jarPath;
    }


    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {

        // Classname: Designation of categories under the directory
        String classname = name.replace('.', '/') + ".class";
        byte[] classData = Launcher.getFileData(classname, jarPath);
        if (classData == null) {
            throw new ClassNotFoundException();
        } else {
            return defineClass(name, classData, 0, classData.length);
        }
    }

    @Override
    protected URL findResource(String path) {
        URL url = Launcher.getFileUrl(path, jarPath);
        return url;
    }
  }
}
