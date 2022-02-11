/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mini.net;

import javax.cldc.io.Connector;
import javax.cldc.io.HttpConnection;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.mini.util.WasmUtil;

/**
 * @author Gust
 */
public class MiniHttpClient extends Thread {

    String url;
    String targetFile;
    boolean targetFileForce = false; // TargetFile was set by user, write in any case.
    DownloadCompletedHandle handle;
    boolean exit;
    HttpConnection c = null;
    public static final MiniHttpClient.CltLogger DEFAULT_LOGGER = new CltLogger() {
        @Override
        public void log(String s) {
            System.out.println(s);
        }
    };
    CltLogger logger = DEFAULT_LOGGER;

    public MiniHttpClient(final String url, CltLogger logger, final DownloadCompletedHandle handle) {
        this.url = url;
        this.handle = handle;
        exit = false;
        if (logger != null) this.logger = logger;
    }
    
    public void setTargetFile(String targetFile) { this.targetFile = targetFile; }

    abstract static public class CltLogger {
        public abstract void log(String s);
    }

    public void stopNow() {
        if (c != null) {
            try {
                c.close();
            } catch (IOException ex) {
            }
        }
        exit = true;
    }

    private void runWasm() {
      if (targetFile == null)
      {
        int randomNum = (int)(java.lang.Math.random()*100000);
        int len = url.indexOf("?");
        if (len < 0) { len = url.length(); } 
        targetFile = randomNum + "_" + url.substring(url.lastIndexOf("/")+1, len);
      }
      else
      {
        targetFileForce = true;
      }
      String s = "{console.log('Pre fetch'); fetch('" + url + "')" +
        ".then(response => response.arrayBuffer())" +
        ".then(data => {console.log('Got the data'); FS.writeFile('" + targetFile + "', new DataView(data)); FS.writeFile('" + targetFile + ".finish', 'finish'); });}";
      System.out.println("MiniHttpClient Download as wasm run()");
      WasmUtil.executeJS(s,true,false);
      File finishFile = new File(targetFile + ".finish");
      while (!finishFile.exists())
      {
        try
        {
          Thread.sleep(1000);
          logger.log("Waiting for data ...");
        }
        catch (InterruptedException ex)
        {
          ex.printStackTrace();
          callbackHandleCompleted(null);
          return;
        }
      }
      byte[] data;
      try(FileInputStream fis = new FileInputStream(targetFile); ByteArrayOutputStream bos = new ByteArrayOutputStream())
      {
        int b;
        while ((b = fis.read()) != -1) { bos.write(b); }
        data = bos.toByteArray();
        logger.log("Successfully received data size: " + data.length);
      }
      catch (IOException ex)
      {
        ex.printStackTrace();
        callbackHandleCompleted(null);
        return;
      }
      
      callbackHandleCompleted(data);
    }
    
    @Override
    public void run() {
      if (WasmUtil.isWebAssembly()) { runWasm(); return; }
        DataInputStream dis = null;
        byte[] data;
        try {
            logger.log("http url:" + url);
            c = (HttpConnection) Connector.open(url);
            int rescode = c.getResponseCode();
            if (rescode == 200) {
                int len = (int) c.getLength();
                dis = c.openDataInputStream();
                if (len > 0) {
                    data = new byte[len];
                    dis.readFully(data);
                } else {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int ch;
                    while ((ch = dis.read()) != -1 || exit) {
                        baos.write(ch);
                    }
                    data = baos.toByteArray();
                }
                if (targetFileForce)
                {
                  try(FileOutputStream fos = new FileOutputStream(targetFile))
                  {
                    fos.write(data);
                  }
                }
                callbackHandleCompleted(data);
            } else if (rescode == 301 || rescode == 302) {
                String redirect = c.getHeaderField("Location");
                logger.log("Redirect: " + redirect);
                MiniHttpClient hc = new MiniHttpClient(redirect, logger, handle);
                hc.start(); //TODO: Test this on wasm instead run()
            } else {
                callbackHandleCompleted(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            callbackHandleCompleted(null);
        } finally {
            try {
                if (dis != null) {
                    dis.close();
                }
                if (c != null) {
                    c.close();
                }
            } catch (Exception e) {
              e.printStackTrace();
            }
        }
    }
    
    void callbackHandleCompleted(byte[] data)
    {
      if (handle != null)
      {
        handle.onCompleted(url, data);
      }
    }

    public interface DownloadCompletedHandle {
      /** Called when download completed.
       * @param url Downloaded url.
       * @param data Downloaded data. May be null on error. */
      void onCompleted(String url, byte[] data);
    }

}
