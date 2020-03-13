package org.mini.xmlui;

import org.mini.gui.GObject;
import org.mini.gui.GViewPort;

public class XViewPort extends XContainer {
    static public final String XML_NAME = "viewport";
    GViewPort viewPort;

    public XViewPort(XContainer xc) {
        super(xc);
    }

    @Override
    String getXmlTag() {
        return XML_NAME;
    }

    @Override
    GObject getGui() {
        return viewPort;
    }

    void createGui() {
        if (viewPort == null) {
            viewPort = new GViewPort();
            viewPort.setLocation(x, y);
            viewPort.setSize(width, height);
            viewPort.setName(name);
            viewPort.setAttachment(this);
            for (int i = 0; i < size(); i++) {
                XObject xo = elementAt(i);
                GObject go = xo.getGui();
                if (go != null) viewPort.add(go);
            }
        } else {
            viewPort.setLocation(x, y);
            viewPort.setSize(width, height);
        }
    }
}
