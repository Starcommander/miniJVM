/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mini.apploader;

import org.mini.gui.*;
import org.mini.glfw.Glfw;
import org.mini.util.wasm.Browser;
import org.mini.util.wasm.Downloader;

/**
 * @author Gust
 */
public class GuiLoader extends GApplication {

    static final String STR_INFO = "The Jar must contain a start class from java8,";
    static final String STR_INFO2 = "and a manifest.mf with Main-Class notation.";
    static final String STR_START = "Open JAR";

    static GuiLoader instance = new GuiLoader();

    GForm mgrForm;
    GStyle style;

    static public GuiLoader getInstance() {
        return instance;
    }

    public void active() {
        if (style == null) {
            style = GToolkit.getStyle();
        }
        GToolkit.setStyle(style);
        GCallBack.getInstance().setApplication(this);
    }

    @Override
    public GForm getForm() {
      if (mgrForm != null) { return mgrForm; }
        mgrForm = new GForm() {

            @Override
            public void init() {
                super.init();
                String jarUrl = Browser.getUrlVariables().getProperty("jar");
                if (jarUrl==null)
                {
                  add(getMainSlot());
                }
                else
                {
                  Downloader.fetch(jarUrl, "/",(s) -> onActionFinish(s));
                }
            }
        };
        return mgrForm;
    }
    
    public void repaintForm()
    {
      long vg = getForm().getCallBack().getNvContext();
      getForm().display(vg);
    }
    
    public void resizeForm(int width, int height)
    {
      Glfw.glfwSetWindowSize(getForm().getCallBack().getDisplay(), width, height);
    }

    GObject getMainSlot() {
      GPanel mainPanel = new GPanel();
      mainPanel.setSize(500,300);
      mainPanel.setLocation(30f, 30f);
      GLabel label = new GLabel();
      label.setSize(250f, 30f);
      label.setLocation(0f, 0f);
      label.setText(STR_INFO);
      GLabel label2 = new GLabel();
      label2.setSize(250f, 30f);
      label2.setLocation(0f, 30f);
      label2.setText(STR_INFO2);
      GButton button = new GButton();
      button.setSize(90f, 30f);
      button.setLocation(0f, 80f);
      button.setText(STR_START);
      button.setName(STR_START);
      button.setActionListener((obj) -> onAction(obj));
      mainPanel.add(label);
      mainPanel.add(label2);
      mainPanel.add(button);
      return mainPanel;
    }
    
    void onAction(GObject gobj)
    {
      String name = gobj.getName();
      org.mini.util.wasm.FileSystem.openFileDialog("/", (s) -> onActionFinish(s));
    }
    
    boolean onActionFinish(String jarPath)
    {
      if (jarPath == null) { return true; }
      
      SimpleAppLoader.runApp(jarPath);
      return true;
    }
    
    public static void main(String[] args) {
        GCallBack ccb = GCallBack.getInstance();
        ccb.init(800, 200, null);

        GuiLoader.getInstance().active();

        Glfw.executeMainLoop();
        ccb.destroy();
    }
}
