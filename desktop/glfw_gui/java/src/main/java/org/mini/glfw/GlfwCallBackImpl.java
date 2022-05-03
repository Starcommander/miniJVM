package org.mini.glfw;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.mini.apploader.AppLoader;
import org.mini.apploader.Sync;
import org.mini.apploader.GApplication;
import org.mini.gui.GCallBack;
import org.mini.gui.GObject;
import org.mini.gui.GToolkit;
import org.mini.glwrap.GLUtil;

import java.io.File;

import static org.mini.gl.GL.GL_TRUE;
import static org.mini.glfw.Glfw.*;
import static org.mini.gui.GObject.HEIGHT;
import static org.mini.gui.GObject.WIDTH;
import static org.mini.nanovg.Nanovg.*;

/**
 * @author Gust
 */
public class GlfwCallBackImpl extends GCallBack {


    long display;

    int winWidth, winHeight;
    int fbWidth, fbHeight;
    float pxRatio;

    public int mouseX, mouseY, button;
    long mouseLastPressed;
    int clickCount = 0;
    int CLICK_PERIOD = 200;

    boolean drag;
    int hoverX, hoverY; // mouse
    int buttonOnDrag;

    long vg;

    // not in mobile
    float fps;
    float fpsExpect = 60;

    public GlfwCallBackImpl() {
    }

    public void setDisplay(long display) {
        this.display = display;
    }

    public long getDisplay() {
        return display;
    }

    public long getNvContext() {
        return vg;
    }

    public int getDeviceWidth() {
        return (int) winWidth;
    }

    public int getDeviceHeight() {
        return (int) winHeight;
    }

    public int getFrameBufferWidth() {
        return (int) fbWidth;
    }

    public int getFrameBufferHeight() {
        return (int) fbHeight;
    }

    public float getDeviceRatio() {
        return pxRatio;
    }

    public String getResRoot() {
        return "./";
    }

    public void setDisplayTitle(String title) {
        Glfw.glfwSetWindowTitle(display, title);
    }


    public void init(int width, int height, Runnable onSurfaceCreated) {
        this.winWidth = width;
        this.winHeight = height;

        if (!Glfw.glfwInit()) {
            System.out.println("glfw init error.");
            System.exit(1);
        }
        String osname = System.getProperty("os.name");
        if (osname != null) {
            if (osname.contains("Mac")) {
                glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
                glfwWindowHint(Glfw.GLFW_COCOA_RETINA_FRAMEBUFFER, GL_TRUE);
            } else if (osname.contains("Linux")) {
                glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
            }
        }
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);

        glfwWindowHint(GLFW_DEPTH_BITS, 16);
        glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, GLFW_TRUE);

        display = Glfw.glfwCreateWindow(winWidth, winHeight, GLUtil.toUtf8(""), 0, 0);
        if (display == 0) {
            glfwTerminate();
            System.exit(1);
        }
        Glfw.glfwSetCallback(display, this);
        Glfw.glfwMakeContextCurrent(display);
        glfwSwapInterval(1);
        reloadWindow();
        System.out.println("fbWidth=" + fbWidth + "  ,fbHeight=" + fbHeight);

        vg = nvgCreateGL3(NVG_ANTIALIAS | NVG_STENCIL_STROKES);
        if (vg == 0) {
            System.out.println("Could not init nanovg.\n");
            System.out.println("callback.getNvContext() is null.");
        }
        GToolkit.FontHolder.loadFont(vg);
        if (onSurfaceCreated != null) { onSurfaceCreated.run(); }
    }

    @Override
    public void mainLoop() {
        try {
            if (!gform.isInited()) {
                gform.init();
            }
            //user define contents
            if (GObject.flushReq()) {
                gform.display(vg);
                glfwSwapBuffers(display);
            }
            glfwPollEvents();
            Sync.sync((int) fpsExpect);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    public void destroy() {
        nvgDeleteGL3(vg);
        glfwTerminate();
        GToolkit.removeForm(vg);
        vg = 0;
        System.exit(0);//some thread not exit ,that will continue running
    }

    /**
     * @return the fps
     */
    public float getFps() {
        return fps;
    }

    public void setFps(float fps) {
        fpsExpect = fps;
    }

    @Override
    public void key(long window, int key, int scancode, int action, int mods) {
        try {
            if (gform == null) {
                return;
            }
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                glfwSetWindowShouldClose(window, GLFW_TRUE);
            }
            gform.keyEventGlfw(key, scancode, action, mods);
            gform.flush();
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    @Override
    public void character(long window, char character) {
        try {
            if (gform == null) {
                return;
            }
            gform.characterEvent(character);
            gform.flush();
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    public int getTouchOrMouseX() {
        return mouseX;
    }

    public int getTouchOrMouseY() {
        return mouseY;
    }

    @Override
    public void mouseButton(long window, int button, boolean pressed) {
        try {
            if (gform == null) {
                return;
            }
            if (window == display) {
                switch (button) {
                    case Glfw.GLFW_MOUSE_BUTTON_1: {//left
                        if (pressed) {
                            drag = true;
                            hoverX = mouseX;
                            hoverY = mouseY;
                            buttonOnDrag = button;
                        } else {
                            drag = false;
                        }
                        break;
                    }
                    case Glfw.GLFW_MOUSE_BUTTON_2: {//right
                        if (pressed) {
                            drag = true;
                            hoverX = mouseX;
                            hoverY = mouseY;
                            buttonOnDrag = button;
                            //gform.longTouchedEvent(mouseX, mouseY);
                        } else {
                            drag = false;
                        }
                        break;
                    }
                    case Glfw.GLFW_MOUSE_BUTTON_3: {//middle
                        break;
                    }
                }
                gform.mouseButtonEvent(button, pressed, mouseX, mouseY);
                //click event
                long cur = System.currentTimeMillis();
                if (pressed) {
                    if ((cur - mouseLastPressed < CLICK_PERIOD) && (this.button == button)) {
                        clickCount++;
                    } else {
                        clickCount = 0;
                    }
                    this.button = button;
                    mouseLastPressed = cur;
                }
                if (!pressed) {
                    if (clickCount > 0) {
                        gform.clickEvent(button, mouseX, mouseY);
                        clickCount = 0;
                    }
                }
            }
            gform.flush();
            //System.out.println("flushed");
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    @Override
    public void scroll(long window, double scrollX, double scrollY) {
        try {
            if (gform == null) {
                return;
            }
            gform.scrollEvent((float) scrollX, (float) scrollY, mouseX, mouseY);
            gform.flush();
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    @Override
    public void cursorPos(long window, int x, int y) {
        try {
            if (gform == null) {
                return;
            }
            if (display == window) {
                mouseX = x;
                mouseY = y;
                gform.cursorPosEvent(x, y);
                if (drag) {
                    gform.dragEvent(buttonOnDrag, x - hoverX, y - hoverY, x, y);
                    hoverX = mouseX;
                    hoverY = mouseY;
                }
                gform.flush();
            }
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    @Override
    public boolean windowClose(long window) {
        try {
            if (gform == null) {
                return true;
            }
            gform.flush();
        } catch (Exception ex) {
            ex.printStackTrace();

        }
        return true;
    }

    private void reloadWindow() {
        try {
            winWidth = Glfw.glfwGetWindowWidth(display);
            winHeight = Glfw.glfwGetWindowHeight(display);
            fbWidth = glfwGetFramebufferWidth(display);
            fbHeight = glfwGetFramebufferHeight(display);
            // Calculate pixel ration for hi-dpi devices.
            pxRatio = (float) fbWidth / (float) winWidth;
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    @Override
    public void windowSize(long window, int width, int height) {
        try {
            reloadWindow();

            if (gform == null) {
                return;
            }
            gform.setSize(width, height);
            gform.onSizeChange(width, height);
            gform.flush();
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    @Override
    public void framebufferSize(long window, int x, int y) {
        try {
            if (gform == null) {
                return;
            }
            gform.getBoundle()[WIDTH] = x;
            gform.getBoundle()[HEIGHT] = y;
            reloadWindow();
            gform.flush();
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    @Override
    public void drop(long window, int count, String[] paths) {
        try {
            if (gform == null) {
                return;
            }
            gform.dropEvent(count, paths);
            gform.flush();
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    public void error(int error, String description) {
        System.out.println("error: " + error + " message: " + description);
    }

    @Override
    public void monitor(long monitor, boolean connected) {
    }

    @Override
    public void windowPos(long window, int x, int y) {
    }

    @Override
    public void windowRefresh(long window) {
    }

    @Override
    public void windowFocus(long window, boolean focused) {
    }

    @Override
    public void windowIconify(long window, boolean iconified) {
    }

    @Override
    public void cursorEnter(long window, boolean entered) {
    }

    //==============================
    @Override
    public String getAppSaveRoot() {
        return new File("./").getAbsolutePath();
    }

    @Override
    public String getAppResRoot() {
        return new File("./").getAbsolutePath();
    }

    @Override
    public GApplication getApplication() {
        return gapp;
    }


}
