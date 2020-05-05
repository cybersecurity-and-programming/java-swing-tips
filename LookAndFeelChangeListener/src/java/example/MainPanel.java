// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.stream.Stream;
import javax.swing.*;

public final class MainPanel extends JPanel {
  // private static final String DRAWS_FOCUS_BORDER_AROUND_ICON = "Tree.drawsFocusBorderAroundIcon";
  // private static final String DRAW_DASHED_FOCUS_INDICATOR = "Tree.drawDashedFocusIndicator";
  private enum TreeDraws {
    DRAWS_FOCUS_BORDER_AROUND_ICON("Tree.drawsFocusBorderAroundIcon"),
    DRAW_DASHED_FOCUS_INDICATOR("Tree.drawDashedFocusIndicator");
    private final String key;
    /* default */ TreeDraws(String key) {
      this.key = key;
    }

    @Override public String toString() {
      return key;
    }
  }

  private final JCheckBox check1 = new ActionCommandCheckBox(TreeDraws.DRAWS_FOCUS_BORDER_AROUND_ICON);
  private final JCheckBox check2 = new ActionCommandCheckBox(TreeDraws.DRAW_DASHED_FOCUS_INDICATOR);
  private final JTextArea textArea = new JTextArea();

  private MainPanel() {
    super(new BorderLayout());

    append("MainPanel: init");
    // updateCheckBox("MainPanel: init");

    UIManager.addPropertyChangeListener(e -> {
      if (e.getPropertyName().equals("lookAndFeel")) {
        // String lnf = e.getNewValue().toString();
        updateCheckBox("UIManager: propertyChange");
      }
    });

    EventQueue.invokeLater(() -> {
      ActionListener al = e -> {
        append("JMenuItem: actionPerformed");
        Object o = e.getSource();
        if (o instanceof JRadioButtonMenuItem && ((JRadioButtonMenuItem) o).isSelected()) {
          updateCheckBox("JMenuItem: actionPerformed: invokeLater");
        }
      };
      JMenuBar menuBar = new JMenuBar();
      menuBar.add(LookAndFeelUtil.createLookAndFeelMenu());
      getRootPane().setJMenuBar(menuBar);
      descendants(menuBar)
          .filter(JRadioButtonMenuItem.class::isInstance)
          .map(JRadioButtonMenuItem.class::cast)
          .forEach(mi -> mi.addActionListener(al));
    });

    JPanel np = new JPanel(new GridLayout(2, 1));
    np.add(check1);
    np.add(check2);

    JPanel p = new JPanel(new GridLayout(2, 1));
    JTree tree = new JTree();
    p.add(new JScrollPane(tree));
    p.add(new JScrollPane(textArea));

    add(np, BorderLayout.NORTH);
    add(p);
    setPreferredSize(new Dimension(320, 240));
  }

  @Override public void updateUI() {
    super.updateUI();
    System.out.println("JPanel: updateUI");
    updateCheckBox("JPanel: updateUI: invokeLater");
  }

  // public static void searchAllMenuElements(MenuElement me, List<JRadioButtonMenuItem> list) {
  //   if (me instanceof JRadioButtonMenuItem) {
  //     list.add((JRadioButtonMenuItem) me);
  //   }
  //   MenuElement[] sub = me.getSubElements();
  //   if (sub.length != 0) {
  //     for (MenuElement e: sub) {
  //       searchAllMenuElements(e, list);
  //     }
  //   }
  // }

  // private static Stream<MenuElement> descendants(MenuElement me) {
  //   return Stream.of(me.getSubElements())
  //       .map(MainPanel::descendants).reduce(Stream.of(me), Stream::concat);
  // }

  private static Stream<MenuElement> descendants(MenuElement me) {
    return Stream.of(me.getSubElements())
        .flatMap(m -> Stream.concat(Stream.of(m), descendants(m)));
  }

  private void updateCheckBox(String str) {
    EventQueue.invokeLater(() -> {
      append("--------\n" + str);

      String focusKey = TreeDraws.DRAWS_FOCUS_BORDER_AROUND_ICON.toString();
      append(focusKey + ": " + UIManager.getBoolean(focusKey));
      check1.setSelected(UIManager.getBoolean(focusKey));

      String dashedKey = TreeDraws.DRAW_DASHED_FOCUS_INDICATOR.toString();
      append(dashedKey + ": " + UIManager.getBoolean(dashedKey));
      check2.setSelected(UIManager.getBoolean(dashedKey));
    });
  }

  private void append(String str) {
    textArea.append(str + "\n");
  }

  public static void main(String[] args) {
    EventQueue.invokeLater(MainPanel::createAndShowGui);
  }

  private static void createAndShowGui() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
      ex.printStackTrace();
      Toolkit.getDefaultToolkit().beep();
    }
    JFrame frame = new JFrame("@title@");
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.getContentPane().add(new MainPanel());
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  private static class ActionCommandCheckBox extends JCheckBox {
    protected ActionCommandCheckBox(TreeDraws key) {
      super(key.toString());
      setAction(new AbstractAction(key.toString()) {
        @Override public void actionPerformed(ActionEvent e) {
          JCheckBox c = (JCheckBox) e.getSource();
          UIManager.put(key.toString(), c.isSelected());
          SwingUtilities.updateComponentTreeUI(c.getRootPane());
        }
      });
    }
  }
}

// @see https://java.net/projects/swingset3/sources/svn/content/trunk/SwingSet3/src/com/sun/swingset3/SwingSet3.java
final class LookAndFeelUtil {
  private static String lookAndFeel = UIManager.getLookAndFeel().getClass().getName();

  private LookAndFeelUtil() {
    /* Singleton */
  }

  public static JMenu createLookAndFeelMenu() {
    JMenu menu = new JMenu("LookAndFeel");
    ButtonGroup lafGroup = new ButtonGroup();
    for (UIManager.LookAndFeelInfo lafInfo: UIManager.getInstalledLookAndFeels()) {
      menu.add(createLookAndFeelItem(lafInfo.getName(), lafInfo.getClassName(), lafGroup));
    }
    return menu;
  }

  private static JMenuItem createLookAndFeelItem(String lafName, String lafClassName, ButtonGroup lafGroup) {
    JRadioButtonMenuItem lafItem = new JRadioButtonMenuItem(lafName, lafClassName.equals(lookAndFeel));
    lafItem.setActionCommand(lafClassName);
    lafItem.setHideActionText(true);
    lafItem.addActionListener(e -> {
      ButtonModel m = lafGroup.getSelection();
      try {
        setLookAndFeel(m.getActionCommand());
      } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
        ex.printStackTrace();
        Toolkit.getDefaultToolkit().beep();
      }
    });
    lafGroup.add(lafItem);
    return lafItem;
  }

  private static void setLookAndFeel(String lookAndFeel) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
    String oldLookAndFeel = LookAndFeelUtil.lookAndFeel;
    if (!oldLookAndFeel.equals(lookAndFeel)) {
      UIManager.setLookAndFeel(lookAndFeel);
      LookAndFeelUtil.lookAndFeel = lookAndFeel;
      updateLookAndFeel();
      // firePropertyChange("lookAndFeel", oldLookAndFeel, lookAndFeel);
    }
  }

  private static void updateLookAndFeel() {
    for (Window window: Window.getWindows()) {
      SwingUtilities.updateComponentTreeUI(window);
    }
  }
}
