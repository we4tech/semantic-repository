/**
 * $Id$
 * *****************************************************************************
 *    Copyright (C) 2005 - 2007 somewhere in .Net ltd.
 *    All Rights Reserved.  No use, copying or distribution of this
 *    work may be made except in accordance with a valid license
 *    agreement from somewhere in .Net LTD.  This notice must be included on
 *    all copies, modifications and derivatives of this work.
 * *****************************************************************************
 * $LastChangedBy$
 * $LastChangedDate$
 * $LastChangedRevision$
 * *****************************************************************************
 */

package com.ideabase.repository.test.visual;

import com.ideabase.repository.test.BaseTestCase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/*import org.apache.bsf.BSFManager;
import org.apache.bsf.BSFException;*/

/**
 * Initiate base environment and execute ruby script to access internal
 * object space or to experiment new stuffs.
 *
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class VisualRubyBasedTestTool extends JFrame implements ActionListener {

  private static final String CMD_EXECUTE = "cmd_execute";
  private final JTextArea rubyScriptArea = new JTextArea("puts('write your script here')");
  private final JTextArea outputArea = new JTextArea("output");
//  private BSFManager bsfManager;

  public VisualRubyBasedTestTool() throws HeadlessException {
    super();
    setTitle("visual ruby based testing tool");

    initComponents();
  }

  private void initComponents() {
    outputArea.setSize(100, 200);
    outputArea.setWrapStyleWord(true);
    
    final JButton executeButton = new JButton("execute");
    final JSplitPane outputAndScriptArea = new JSplitPane();
    final JPanel buttonArea = new JPanel();

    outputAndScriptArea.setOrientation(JSplitPane.VERTICAL_SPLIT);
    outputAndScriptArea.add(JSplitPane.TOP, new JScrollPane(rubyScriptArea));
    outputAndScriptArea.add(JSplitPane.BOTTOM, new JScrollPane(outputArea));

    buttonArea.setLayout(new BorderLayout());
    buttonArea.add(executeButton, BorderLayout.WEST);

    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(outputAndScriptArea, BorderLayout.CENTER);
    getContentPane().add(buttonArea, BorderLayout.EAST);

    // set event listeners
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    executeButton.setActionCommand(CMD_EXECUTE);
    executeButton.addActionListener(this);
  }

  public static void main(final String[] pArgs) {
    final VisualRubyBasedTestTool tool = new VisualRubyBasedTestTool();
    tool.pack();
    tool.setSize(500, 500);
    tool.setVisible(true);
  }

  public void actionPerformed(final ActionEvent pEvent) {
    if (pEvent.getActionCommand().equals(CMD_EXECUTE)) {
      executeRubyScript();
    }
  }

  private void executeRubyScript() {
    /*final BaseTestCase baseTestCase = new BaseTestCase();
    baseTestCase.initiate();*/

    // execute ruby script
//    SwingUtilities.invokeLater(new Runnable() {
//      public void run() {
//        if (bsfManager == null) {
//          bsfManager = new BSFManager();
//          bsfManager.setClassLoader(getClass().getClassLoader());
//        }
//        try {
//          outputArea.setText(String.valueOf(bsfManager.
//              eval("ruby", "test", 0, 0, rubyScriptArea.getText())));
//        } catch (BSFException e) {
//          e.printStackTrace();
//        }
//      }
//    });

  }
}
