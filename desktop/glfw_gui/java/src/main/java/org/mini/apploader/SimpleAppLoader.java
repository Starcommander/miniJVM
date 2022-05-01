/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mini.apploader;

import java.io.File;
import java.lang.reflect.Method;
import org.mini.zip.Zip;

/**
 *
 * @author Paul Kashofer (Starcommander@github)
 */
public class SimpleAppLoader
{
  public static final String MANIFEST_FILE = "META-INF/MANIFEST.MF";
  public static final String MAINCLASS_KEY = "Main-Class";
  
  public static void runApp(String jarName) {
System.out.println("org.mini.apploader.SimpleAppLoader.runApp(pre)");
    Object app = null;
    try {
        Class c = getApplicationClass(jarName);
        if (c != null) {
            app = c.newInstance();
            Method method = c.getMethod("main", String[].class);
            method.invoke(app, (Object)new String[0]);
        }
        else
        {
System.out.println("org.mini.apploader.SimpleAppLoader.runApp(Class=NULL)");
        }

System.out.println("org.mini.apploader.SimpleAppLoader.runApp(post)");
    } catch (Exception ex) {
        ex.printStackTrace();
    }
  }
  
  private static Class getApplicationClass(String jarPath) {
    try {
        String className = getManifestConfig(jarPath, MAINCLASS_KEY);
System.out.println("org.mini.apploader.SimpleAppLoader.getApplicationClass(): " + className);
        if (className != null && className.length() > 0) {
            StandalongGuiAppClassLoader sgacl = new StandalongGuiAppClassLoader(jarPath, ClassLoader.getSystemClassLoader());
            Thread.currentThread().setContextClassLoader(sgacl);
            Class c = sgacl.loadClass(className);

            return c;
        }
    } catch (Exception ex) {
        ex.printStackTrace();
    }
    return null;
  }
  
  private static String getManifestConfig(String jarPath, String key)
  {
    try {
        File f = new File(jarPath);
        if (f.exists()) {
            byte[] b = Zip.getEntry(jarPath, MANIFEST_FILE);
            if (b != null) {

                String s = new String(b, "utf-8");
                s = s.replace("\r", "\n");
                String[] ss = s.split("\n");
                for (String line : ss) {
                    int pos = line.indexOf(":");
                    if (pos > 0) {
                        String k = line.substring(0, pos).trim();
                        String v = line.substring(pos + 1).trim();
                        if (k.equalsIgnoreCase(key)) {
                            return v;
                        }
                    }
                }
            }
        }
    } catch (Exception ex) {
        ex.printStackTrace();
    }
    return null;
  }
  
}
