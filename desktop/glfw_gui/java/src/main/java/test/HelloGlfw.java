
import org.mini.apploader.GuiLoader;
import org.mini.gui.GButton;
import org.mini.gui.GLabel;
import org.mini.gui.GPanel;
import org.mini.gui.GSoftKeyboard;
import org.mini.gui.GTextField;
import org.mini.util.wasm.UserAgent;

public class HelloGlfw
{

  public static void main (String args[])
  {
    System.out.println("HelloGlfw.main()");
    GuiLoader.getInstance().getForm().clear();
    float ratio = 1.5f;
    GuiLoader.getInstance().resizeForm(400, (int)(400*ratio));
    GuiLoader.getInstance().getForm().add(createPanel());
    GuiLoader.getInstance().repaintForm();
    GSoftKeyboard.getInstance().setActive(true, UserAgent.isMobileDevice());
  }

  private static GPanel createPanel()
  {
    GPanel panel = new GPanel();
      panel.setSize(300f, 200f);
      panel.setLocation(0f, 0f);
    GButton button = new GButton();
      button.setSize(150f, 30f);
      button.setLocation(20f, 150f);
      button.setText("Enter");
    GTextField tf = new GTextField();
      tf.setSize(250f, 30f);
      tf.setLocation(20f, 100f);
    GLabel lab = new GLabel();
      lab.setSize(90f, 30f);
      lab.setLocation(20f, 50f);
      lab.setText("Nothing pressed");
    panel.add(tf);
    panel.add(button);
    panel.add(lab);
    button.setActionListener((a) -> onPress(tf, lab));
    return panel;
  }
  
  private static void onPress(GTextField tf, GLabel lab)
  {
    lab.setText("Content: '" + tf.getText() + "'");
  }
}
