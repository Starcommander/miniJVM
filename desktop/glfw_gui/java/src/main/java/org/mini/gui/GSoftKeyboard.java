package org.mini.gui;

import java.util.ArrayList;
import java.util.HashMap;
import org.mini.apploader.GuiLoader;

public class GSoftKeyboard
{
  public static final String SPECIAL_DEL = "d";
  public static final String SPECIAL_ENTER = "e";
  public static final String SPECIAL_SWITCH_TO = "s=";
  private static final String PIX_KEYB_DEF = "/res/keyb-def.png";
  private static final String PIX_KEYB_DEF_UP = "/res/keyb-def-up.png";
  
  static GSoftKeyboard instance;
  int lastX, lastY;
  boolean skipIdenticalTouch = true;
  boolean active = false;
  String curKeyboard = PIX_KEYB_DEF;
  GPanel panel;
  GImageItem image;
  HashMap<String,ArrayList<KeyModel>> keyboards = new HashMap<>();
  
  private GSoftKeyboard()
  {
  }
  
  private void ensureInit()
  {
    if (panel != null) { return; }
    panel = new GPanel()
    {
      @Override
      public boolean isMenu()
      {
        return true;
      }
    
      @Override
      public boolean isContextMenu()
      {
        return true;
      }
    };
    keyboards.put(PIX_KEYB_DEF, createKeyList((char)0));
    keyboards.put(PIX_KEYB_DEF_UP, createKeyList((char)32));
    GImage img = GImage.createImageFromJar(curKeyboard);
    image = new GImageItem(img)
    {
      @Override
      public void mouseButtonEvent(int button, boolean pressed, int x, int y)
      {
        super.mouseButtonEvent(button, pressed, x, y);
System.out.println(".mouseButtonEvent() b="+button);
        if (pressed) { onPressed(x,y); }
      }
    };
    panel.add(image);
  }
  
  public static GSoftKeyboard getInstance()
  {
    if (instance == null) { instance = new GSoftKeyboard(); }
    return instance;
  }
  
  
  /** Sets the keyboard active, and showing for textfields.
   * @param active True to set active.
   * @param skipIdenticalTouch For recognize and skip touch-release.
   * <br>Useful on mobile devices, but not on desktop with mouse. */
  public void setActive(boolean active, boolean skipIdenticalTouch)
  {
    this.active = active;
    this.skipIdenticalTouch = skipIdenticalTouch;
  }
  
  /** *  List of keyboards.
   * <br>The hashmap contains the image-file-name with appendant keyModel-list.
   * @return The whole list. */
  public HashMap<String,ArrayList<KeyModel>> getKeyboards()
  {
    return keyboards;
  }
  
  /** Switching, using the SPECIAL_SWITCH_TO key. */
  private void switchKeyboard(String imgFile)
  {
    curKeyboard = imgFile;
    image.setImg(GImage.createImageFromJar(curKeyboard));
    hide();
    show();
  }

  public void show()
  {
    if (active)
    {
      ensureInit();
    }
    else
    {
      return;
    }
    float w = GuiLoader.getInstance().getForm().getW();
    float h = GuiLoader.getInstance().getForm().getH();
    float ratio = (float)image.getImg().getWidth() / image.getImg().getHeight();
    if (w>h) // widescreen
    {
      float kw = w/2; // ~ 1/4 screen
      float kh = (w/2)/ratio;
      panel.setSize(kw, kh);
      image.setSize(kw, kh);
      panel.setLocation((w/2) - (kw/2), h - kh);
    }
    else
    {
      float kw = w;
      float kh = w/ratio;
      panel.setSize(kw, kh); // Full width
      image.setSize(kw, kh);
      panel.setLocation(0, h - kh);
    }
    GuiLoader.getInstance().getForm().add(panel);
  }
  
  public void hide()
  {
    if (!active) { return; }
    GuiLoader.getInstance().getForm().remove(panel);
  }

  private void onPressed(int x, int y)
  {
    if (skipIdenticalTouch && lastX == x && lastY == y)
    {
      System.out.println("Skip-Touch-Release");
      return;
    }
    lastX = x;
    lastY = y;
    float scalerX = image.getImg().getWidth() / panel.getW();
    float scalerY = image.getImg().getHeight() / panel.getH();
    x = (int)(scalerX*(x-panel.getX()));
    y = (int)(scalerY*(y-panel.getY()));
    System.out.println("org.mini.gui.GSoftKeyboard.onPressed(" + x + "/" + y + ")");
    for (KeyModel km : keyboards.get(curKeyboard))
    {
      if (km.x > x) { continue; }
      if (km.x+km.w < x) { continue; }
      if (km.y > y) { continue; }
      if (km.y+km.h < y) { continue; }
      if (km.special != null)
      {
        if (km.special.equals(SPECIAL_DEL))
        {
          GuiLoader.getInstance().getForm().keyEventGlfw(259, 8, 1, 0);
        }
        else if (km.special.equals(SPECIAL_ENTER))
        {
          GuiLoader.getInstance().getForm().keyEventGlfw(257, 13, 1, 0);
        }
        else if (km.special.startsWith(SPECIAL_SWITCH_TO))
        {
          String newKb = km.special.substring(SPECIAL_SWITCH_TO.length());
          switchKeyboard(newKb);
        }
      }
      else
      {
        GuiLoader.getInstance().getForm().characterEvent(km.c);
      }
    }
  }

  /** Fills the KeyList.
   * @param up Using 32 for upper case, otherwise 0. */
  private ArrayList<KeyModel> createKeyList(char up)
  {
    ArrayList<KeyModel> keyList = new ArrayList<>();
    int x = 8;
    int y = 40;
    int w = 58;
    int h = 82;
    keyList.add(new KeyModel((char)('q'-up), x, y, w, h, null)); x+=63;
    keyList.add(new KeyModel((char)('w'-up), x, y, w, h, null)); x+=63;
    keyList.add(new KeyModel((char)('e'-up), x, y, w, h, null)); x+=63;
    keyList.add(new KeyModel((char)('r'-up), x, y, w, h, null)); x+=63;
    keyList.add(new KeyModel((char)('t'-up), x, y, w, h, null)); x+=63;
    keyList.add(new KeyModel((char)('y'-up), x, y, w, h, null)); x+=63;
    keyList.add(new KeyModel((char)('u'-up), x, y, w, h, null)); x+=63;
    keyList.add(new KeyModel((char)('i'-up), x, y, w, h, null)); x+=63;
    keyList.add(new KeyModel((char)('o'-up), x, y, w, h, null)); x+=63;
    keyList.add(new KeyModel((char)('p'-up), x, y, w, h, null));
    
    x = 38;
    y = 146;
    keyList.add(new KeyModel((char)('a'-up), x, y, w, h, null)); x+=63;
    keyList.add(new KeyModel((char)('s'-up), x, y, w, h, null)); x+=63;
    keyList.add(new KeyModel((char)('d'-up), x, y, w, h, null)); x+=63;
    keyList.add(new KeyModel((char)('f'-up), x, y, w, h, null)); x+=63;
    keyList.add(new KeyModel((char)('g'-up), x, y, w, h, null)); x+=63;
    keyList.add(new KeyModel((char)('h'-up), x, y, w, h, null)); x+=63;
    keyList.add(new KeyModel((char)('j'-up), x, y, w, h, null)); x+=63;
    keyList.add(new KeyModel((char)('k'-up), x, y, w, h, null)); x+=63;
    keyList.add(new KeyModel((char)('l'-up), x, y, w, h, null));
    
    x=102;
    y=250;
    keyList.add(new KeyModel((char)('z'-up), x, y, w, h, null)); x+=63;
    keyList.add(new KeyModel((char)('x'-up), x, y, w, h, null)); x+=63;
    keyList.add(new KeyModel((char)('c'-up), x, y, w, h, null)); x+=63;
    keyList.add(new KeyModel((char)('v'-up), x, y, w, h, null)); x+=63;
    keyList.add(new KeyModel((char)('b'-up), x, y, w, h, null)); x+=63;
    keyList.add(new KeyModel((char)('n'-up), x, y, w, h, null)); x+=63;
    keyList.add(new KeyModel((char)('m'-up), x, y, w, h, null)); x+=63;
    
    keyList.add(new KeyModel(' ', x, y, 2*w, h, SPECIAL_DEL)); // Delete
    String spec = SPECIAL_SWITCH_TO + PIX_KEYB_DEF_UP;
    if (up > 0) { spec = SPECIAL_SWITCH_TO + PIX_KEYB_DEF; }
    keyList.add(new KeyModel(' ', 10, y, (int)(1.5f*w), h, spec)); // Shift
    
    y = 360;
    x = 230;
    keyList.add(new KeyModel(' ', x, y, 4*w, h, null)); x+=(4*w);// Space
    keyList.add(new KeyModel(' ', x, y, 3*w, h, SPECIAL_ENTER)); // Enter
    return keyList;
  }
  
  class KeyModel
  {
    public int x;
    public int y;
    public int w;
    public int h;
    public char c;
    public String special; //Enter, Del, KeySwitch=name
    
    public KeyModel(char c, int x, int y, int w, int h, String special)
    {
      this.x = x;
      this.y = y;
      this.w = w;
      this.h = h;
      this.c = c;
      this.special = special;
    }
  }
}
