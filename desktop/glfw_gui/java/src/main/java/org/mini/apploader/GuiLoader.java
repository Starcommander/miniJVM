/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mini.apploader;

import org.mini.gui.*;
import org.mini.layout.UITemplate;
import org.mini.layout.XContainer;
import org.mini.layout.XEventHandler;
import org.mini.layout.XViewSlot;
import org.mini.net.MiniHttpClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import org.mini.glfw.Glfw;
import org.mini.gui.event.GActionListener;
import org.mini.util.NativeCallback;
import org.mini.util.WasmUtil;
import org.mini.util.wasm.Browser;
import org.mini.util.wasm.Downloader;

/**
 * @author Gust
 */
public class GuiLoader extends GApplication {

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
//
//                final GCallBack ccb = GCallBack.getInstance();
//                devW = ccb.getDeviceWidth();
//                devH = ccb.getDeviceHeight();
//                System.out.println("devW :" + devW + ", devH  :" + devH);
//
//                GForm.hideKeyboard();
//                GLanguage.setCurLang(AppLoader.getDefaultLang());
//
//                if (AppLoader.getGuiStyle() == 0) {
//                    GToolkit.setStyle(new GStyleBright());
//                } else {
//                    GToolkit.setStyle(new GStyleDark());
//                }
//
//                setPickListener((uid, url, data) -> {
//                    if (data == null && url != null) {
//                        File f = new File(url);
//                        if (f.exists()) {
//                            try {
//                                FileInputStream fis = new FileInputStream(f);
//                                data = new byte[(int) f.length()];
//                                fis.read(data);
//                                fis.close();
//                            } catch (IOException ex) {
//                                ex.printStackTrace();
//                            }
//                        }
//                    }
//                    switch (uid) {
//
//                        case PICK_PHOTO:
//                        case PICK_CAMERA: {
//
//                            if (data != null) {
//
//                            }
//                            break;
//                        }
//                        case PICK_QR: {
//
//                            break;
//                        }
//                        case PICK_HEAD: {
//                            if (data != null) {
//
//                            }
//                            break;
//                        }
//                    }
//                });
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

    GObject getMainSlot() {
      GPanel mainPanel = new GPanel();
      mainPanel.setSize(300,300);
      mainPanel.setLocation(30f, 30f);
//      GViewPort g = new GViewPort();
//      g.setSize(0.3f, 0.3f);
//      g.setLocation(0.3f, 0.3f);
      GButton button = new GButton();
      button.setSize(90f, 30f);
      button.setLocation(0f, 0f);
      button.setText(STR_START);
      button.setName(STR_START);
      button.setActionListener((obj) -> onAction(obj));
      mainPanel.add(button);
//      g.add(button);
      return mainPanel;
    }
    
    void onAction(GObject gobj)
    {
      String name = gobj.getName();
      System.out.println("org.mini.apploader.GuiLoader.onAction(" + name + ")");
//      if (new File(targetFile + ".finish").exists())
//      if (new File(targetFile).exists())
//      {
//        System.out.println("org.mini.apploader.GuiLoader.onAction() File is here: " + targetFile);
//        System.out.println("org.mini.apploader.GuiLoader.onAction() - Size: " + new File(targetFile).length());
//        System.out.println("org.mini.apploader.GuiLoader.onAction() - Please refresh webpage to load an other file.");
//        return;
//      }

org.mini.util.wasm.FileSystem.openFileDialog("/", (s) -> onActionFinish(s));
      
    }
    
    boolean onActionFinish(String jarPath)
    {
      System.out.println("onActionFinish(String jarPath): " + jarPath);
      if (jarPath == null) { return true; }
      
//      getForm().clear();
      SimpleAppLoader.runApp(jarPath);

//      GButton button = new GButton(); //TODO: Das ist nur zum Test
//      button.setSize(90f, 30f);
//      button.setLocation(0f, 0f);
//      button.setText("Halligalli");
//      button.setName("Halligalli");
//getForm().clear();
//getForm().add(button);
      return true;
    }
//    GViewSlot getMainSlot() {
//
//        String xmlStr = "";
//        try {
//            xmlStr = new String(GToolkit.readFileFromJar("/res/ui/AppManager.xml"), "utf-8");
//        } catch (Exception e) {
//        }
//
//        UITemplate uit = new UITemplate(xmlStr);
//        for (String s : uit.getVariable()) {
//            uit.setVar(s, GLanguage.getString(s));
//        }
//
//        eventHandler = new AppmEventHandler();
//        XContainer container = (XViewSlot) XContainer.parseXml(uit.parse());
//        container.build((int) devW, (int) (devH), eventHandler);
//        mainSlot = container.getGui();
//        appList = mainSlot.findByName("LIST_APP");
//        contentView = mainSlot.findByName("VP_CONTENT");
//        logBox = mainSlot.findByName("INPUT_LOG");
//        GList langList = mainSlot.findByName("LIST_LANG");
//        langList.setSelectedIndex(AppLoader.getDefaultLang());
//        GList styleList = mainSlot.findByName("LIST_STYLE");
//        if (GToolkit.getStyle() instanceof GStyleBright) {
//            styleList.setSelectedIndex(0);
//        } else {
//            styleList.setSelectedIndex(1);
//        }
//        String url = AppLoader.getDownloadUrl();
//        System.out.println("downloadurl=" + url);
//        if (url != null) GToolkit.setCompText(mainSlot, "INPUT_URL", url);
//
//        mgrForm.setSizeChangeListener((width, height) -> container.reSize(width, height));
//        reloadAppList();
//        return this.mainSlot;
//    }
//
//    class AppmEventHandler extends XEventHandler {
//        @Override
//        public void action(GObject gobj) {
//            String name = gobj.getName();
//            if ("APP_DELETE_BTN".equals(name)) {
//                String appName = curSelectedItem.getLabel();
//                AppLoader.removeApp(appName);
//                mainPanelShowLeft();
//                reloadAppList();
//            } else if ("BT_DOWN".equals(name)) {
//                String url = GToolkit.getCompText("INPUT_URL");
//                AppLoader.setDownloadUrl(url);
//
//                MiniHttpClient hc = new MiniHttpClient(url, cltLogger, getDownloadCallback());
//                hc.start();
//            } else if ("BT_BACK".equals(name)) {
//                mainPanelShowLeft();
//            } else if ("BT_BACK1".equals(name)) {
//                mainSlot.moveTo(0, 0);
//            } else if ("BT_STARTWEB".equals(name)) {
//                GButton uploadbtn = (GButton) gobj;
//                GLabel uploadLab = (GLabel) mgrForm.findByName("LAB_WEBSRV");
//                if (webServer != null) {
//                    webServer.stopServer();
//                }
//                if (uploadbtn.getText().equals(GLanguage.getString(STR_STOP))) {
//                    uploadbtn.setText(GLanguage.getString(STR_START));
//                    uploadLab.setText(GLanguage.getString(STR_START_WEB_SRV_FOR_UPLOAD));
//                    String s = GLanguage.getString(STR_SERVER_STOPED);
//                    GForm.addMessage(s);
//                    log(s);
//                } else {
//                    webServer = new MiniHttpServer(MiniHttpServer.DEFAULT_PORT, srvLogger);
//                    webServer.setUploadCompletedHandle(files -> {
//                        for (MiniHttpServer.UploadFile f : files) {
//                            AppLoader.addApp(f.filename, f.data);
//                            String s = GLanguage.getString(STR_UPLOAD_FILE) + " " + f.filename + " " + f.data.length;
//                            GForm.addMessage(s);
//                            log(s);
//                        }
//                        reloadAppList();
//                    });
//                    webServer.start();
//                    uploadbtn.setText(GLanguage.getString(STR_STOP));
//                    uploadLab.setText(GLanguage.getString(STR_WEB_LISTEN_ON) + webServer.getPort());
//                    String s = GLanguage.getString(STR_SERVER_STARTED);
//                    GForm.addMessage(s);
//                    log(s);
//                }
//            } else if ("APP_RUN_BTN".equals(name)) {
//                if (curSelectedItem != null) {
//                    String appName = curSelectedItem.getLabel();
//                    if (appName != null) {
//                        AppLoader.runApp(appName);
//                    }
//                }
//            } else if ("APP_SET_BOOT_BTN".equals(name)) {
//                if (curSelectedItem != null) {
//                    AppLoader.setBootApp(curSelectedItem.getLabel());
//                }
//            } else if ("APP_UPGRADE_BTN".equals(name)) {
//                if (curSelectedItem != null) {
//                    String appName = curSelectedItem.getLabel();
//                    if (appName != null) {
//                        String url = AppLoader.getApplicationUpgradeurl(appName);
//                        if (url != null) {
//                            MiniHttpClient hc = new MiniHttpClient(url, cltLogger, getDownloadCallback());
//                            hc.start();
//                        }
//                    }
//                }
//            } else if ("APP_DELETE_BTN".equals(name)) {
//                if (curSelectedItem != null) {
//                    String appName = curSelectedItem.getLabel();
//                    if (appName != null) {
//                        AppLoader.removeApp(appName);
//                        reloadAppList();
//                        mainPanelShowLeft();
//                    }
//                }
//            } else if ("BT_SETTING".equals(name)) {
//                mainSlot.moveTo(2, 0);
//            } else if ("LI_ENG".equals(name)) {
//                GLanguage.setCurLang(GLanguage.ID_ENG);
//                AppLoader.setDefaultLang(GLanguage.ID_ENG);
//                AppManager.getInstance().active();
//            } else if ("LI_CHS".equals(name)) {
//                GLanguage.setCurLang(GLanguage.ID_CHN);
//                AppLoader.setDefaultLang(GLanguage.ID_CHN);
//                AppManager.getInstance().active();
//            } else if ("LI_CHT".equals(name)) {
//                GLanguage.setCurLang(GLanguage.ID_CHT);
//                AppLoader.setDefaultLang(GLanguage.ID_CHT);
//                AppManager.getInstance().active();
//            } else if ("LI_BRIGHT".equals(name)) {
//                GToolkit.setStyle(new GStyleBright());
//                AppLoader.setGuiStyle(0);
//                instance = new AppManager();
//                active();
//            } else if ("LI_DARK".equals(name)) {
//                GToolkit.setStyle(new GStyleDark());
//                AppLoader.setGuiStyle(1);
//                instance = new AppManager();
//                active();
//            }
//        }
//
//
//        public void onStateChange(GObject gobj, String cmd) {
//            String name = gobj.getName();
//            if ("INPUT_SEARCH".equals(name)) {
//                GTextObject search = (GTextObject) gobj;
//                String str = search.getText();
//                if (appList != null) {
//                    appList.filterLabelWithKey(str);
//                    //System.out.println("key=" + str);
//                }
//            }
//        }
//    }
//
//    MiniHttpClient.DownloadCompletedHandle getDownloadCallback() {
//        return (url, data) -> {
//            log("Download success " + url + " ,size: " + data.length);
//            GForm.addMessage((data == null ? GLanguage.getString(STR_FAIL) : GLanguage.getString(STR_SUCCESS)) + " " + GLanguage.getString(STR_DOWNLOAD) + " " + url);
//            String jarName = null;
//            if (url.lastIndexOf('/') > 0) {
//                jarName = url.substring(url.lastIndexOf('/') + 1);
//                if (jarName.indexOf('?') > 0) {
//                    jarName = jarName.substring(0, jarName.indexOf('?'));
//                }
//            }
//            if (jarName != null && data != null) {
//                AppLoader.addApp(jarName, data);
//            }
//            reloadAppList();
//            updateContentViewInfo(jarName);
//        };
//    }
//
//    void reloadAppList() {
//        if (appList == null) {
//            return;
//        }
//        appList.clear();
//        List<String> list = AppLoader.getAppList();
//        if (list != null && list.size() > 0) {
//            for (String appName : list) {
//                //System.out.println("appName:" + appName);
//                if (AppLoader.isJarExists(appName)) {
//                    byte[] iconBytes = AppLoader.getApplicationIcon(appName);
//                    GImage img = null;
//                    if (iconBytes != null) {
//                        img = GImage.createImage(iconBytes);
//                    }
//                    GListItem item = new GListItem(img, appName) {
//                        public boolean paint(long vg) {
//                            super.paint(vg);
//                            if (getLabel() != null && getLabel().equals(AppLoader.getBootApp())) {
//                                GToolkit.drawRedPoint(vg, "v", getX() + getW() - 20, getY() + getH() * .5f, 10);
//                            }
//                            return true;
//                        }
//                    };
//                    appList.add(item);
//                    item.setActionListener(gobj -> {
//                        curSelectedItem = (GListItem) gobj;
//                        updateContentViewInfo(appName);
//                        mainPanelShowRight();
//                    });
//                }
//            }
//        }
//        GForm.flush();
//    }
//
//    void updateContentViewInfo(String appName) {
//        GLabel nameLab = (GLabel) contentView.findByName(APP_NAME_LABEL);
//        nameLab.setText(AppLoader.getApplicationName(appName));
//        //
//        GTextBox descLab = (GTextBox) contentView.findByName(APP_DESC_LABEL);
//        String dStr = "-";
//        long d = AppLoader.getApplicationFileDate(appName);
//        if (d > 0) {
//            dStr = getDateString(d);
//        }
//        String txt = GLanguage.getString(STR_VERSION) + "\n  " + AppLoader.getApplicationVersion(appName) + "\n"
//                + GLanguage.getString(STR_FILE_SIZE) + "\n  " + AppLoader.getApplicationFileSize(appName) + "\n"
//                + GLanguage.getString(STR_FILE_DATE) + "\n  " + dStr + "\n"
//                + GLanguage.getString(STR_UPGRADE_URL) + "\n  " + AppLoader.getApplicationUpgradeurl(appName) + "\n"
//                + GLanguage.getString(STR_DESC) + "\n  " + AppLoader.getApplicationDesc(appName) + "\n";
//        descLab.setText(txt);
//
//        //re set image
//        GImageItem icon = (GImageItem) contentView.findByName(APP_ICON_ITEM);
//        if (curSelectedItem != null) icon.setImg(curSelectedItem.getImg());
//        contentView.reSize();
//    }
//
//    void mainPanelShowLeft() {
//        if (curSelectedItem != null) {
//            appList.setSelectedIndex(-1);
//            curSelectedItem = null;
//        }
//        mainSlot.moveTo(0, 200);
//    }
//
//    void mainPanelShowRight() {
//        mainSlot.moveTo(1, 200);
//    }
//
//
//    MiniHttpServer.SrvLogger srvLogger = new MiniHttpServer.SrvLogger() {
//        @Override
//        void log(String s) {
//            AppManager.log(s);
//        }
//    };
//
//    MiniHttpClient.CltLogger cltLogger = new MiniHttpClient.CltLogger() {
//        @Override
//        public void log(String s) {
//            AppManager.log(s);
//        }
//    };
//
//    /**
//     * @param s
//     */
//    public static void log(String s) {
//        GTextBox box = getInstance().logBox;
//        if (box != null) {
//            box.setCaretIndex(box.getText().length());
//            Calendar c = Calendar.getInstance();
//            box.insertTextAtCaret("\n" + getDateString(c.getTimeInMillis()) + " " + s);
//            box.setScroll(1.f);
//            box.flush();
//        }
//    }
//
//    public static String getDateString(long millis) {
//        Calendar c = Calendar.getInstance();
//        c.setTimeInMillis(millis);
//        int year = c.get(Calendar.YEAR);
//        int month = c.get(Calendar.MONTH) + 1;
//        int dayInMonth = c.get(Calendar.DAY_OF_MONTH);
//        int hour = c.get(Calendar.HOUR_OF_DAY);
//        int minute = c.get(Calendar.MINUTE);
//        int seconds = c.get(Calendar.SECOND);
//        String ret = String.valueOf(year);
//        ret += "-";
//        ret += month < 10 ? "0" + month : String.valueOf(month);
//        ret += "-";
//        ret += dayInMonth < 10 ? "0" + dayInMonth : String.valueOf(dayInMonth);
//        ret += " ";
//        ret += hour < 10 ? "0" + hour : String.valueOf(hour);
//        ret += ":";
//        ret += minute < 10 ? "0" + hour : String.valueOf(minute);
//        ret += ":";
//        ret += seconds < 10 ? "0" + hour : String.valueOf(seconds);
//        return ret;
//
//    }
    
    public static void main(String[] args) {
        GCallBack ccb = GCallBack.getInstance();
        ccb.init(800, 200, null);

        GuiLoader.getInstance().active();

        Glfw.executeMainLoop();
        ccb.destroy();
    }
}
