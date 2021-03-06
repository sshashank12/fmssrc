/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.virtualvehicle.inputcomponents;

import javax.swing.text.Document;

/**
 * General input panel with three text fields and optionally:
 * <ul>
 * <li>a message/text</li>
 * <li>labels for the text fields</li>
 * <li>unit labels for the text fields</li>
 * </ul>
 * The input of the text fields can be validated 
 * (see {@link Builder#enableValidation enableValidation}).
 * For instanciation the contained 
 * {@link TripleTextInputPanel.Builder Builder}-class must be used.
 * The <code>Object</code> that is returned by {@link InputPanel#getInput} is
 * an <code>Array</code> of three <code>Strings</code>.
 *
 * @author zishan
 */
public final class IpAddressTextInputPanel
    extends TextInputPanel {

  /**
   * If the panel is resetable this is the value the input is set to when
   * doReset() is called.
   */
  private Object resetValue;
  /**
   * Flag indicating if the content of the first text field is a valid input.
   */
  private boolean inputField1Valid = true;
 
  /** 
   * Create a new <code>TripleTextInputPanel</code>.
   * @param title Title of the panel
   */
  private IpAddressTextInputPanel(String title) {
    super(title);
    initComponents();
  }

  /**
   * Enable input validation against the given regular expressions. 
   * If a format string is <code>null</code> the related text field is not 
   * validated.
   * @see InputPanel#addValidationListener
   * @param format1 A regular expression for the first text field.
   * @param format2 A regular expression for the second text field.
   * @param format3 A regular expression for the third text field.
   */
  private void enableInputValidation(String format1) {
    if (format1 != null) {
      Ipaddresstext.getDocument().addDocumentListener(new TextInputValidator(format1));
    }
    
  }

  @Override
  protected void captureInput() {
    input = new String[] {Ipaddresstext.getText()};
  }

  @Override
  public void doReset() {
    input = resetValue;
  }

  /**
   * Mark the input of the specified <code>Document</code> as valid, if this
   * Document belongs to one of the three text fields in this panel.
   * If <code>valid</code> is <code>true</code> the validity of the other
   * two documents is checked, too. Only if all three text fields contain valid
   * input, the whole input of the panel is marked as valid.
   *
   * @see TextInputPanel#setInputValid(boolean, javax.swing.text.Document)  
   * @param valid true, if the content of the <code>Document</code> is valid
   * @param doc the <code>Document</code>
   */
  @Override
  protected void setInputValid(boolean valid, Document doc) {
    // Find out to which input field the document belongs and check the others
    boolean allValid = valid;
    if (doc == Ipaddresstext.getDocument()) {
      inputField1Valid = valid;
      
    }
   
    else {
      allValid = false;
    }
    setInputValid(allValid);
  }

  // CHECKSTYLE:OFF
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;

    label3 = new javax.swing.JLabel();
    Ipaddresstext = new javax.swing.JTextField();
    messageLabel = new javax.swing.JLabel();

    setLayout(new java.awt.GridBagLayout());

    label3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
    label3.setText("Label");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
    add(label3, gridBagConstraints);

    Ipaddresstext.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
    Ipaddresstext.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    Ipaddresstext.setText("initial Value");
    Ipaddresstext.setName("Ipaddresstext"); // NOI18N
    Ipaddresstext.setPreferredSize(new java.awt.Dimension(70, 20));
    Ipaddresstext.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent evt) {
        IpaddresstextFocusGained(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
    add(Ipaddresstext, gridBagConstraints);
    Ipaddresstext.getAccessibleContext().setAccessibleName("");

    messageLabel.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
    messageLabel.setText("Message");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
    add(messageLabel, gridBagConstraints);
  }// </editor-fold>//GEN-END:initComponents

  private void IpaddresstextFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_IpaddresstextFocusGained
    Ipaddresstext.selectAll();
  }//GEN-LAST:event_IpaddresstextFocusGained
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JTextField Ipaddresstext;
  private javax.swing.JLabel label3;
  private javax.swing.JLabel messageLabel;
  // End of variables declaration//GEN-END:variables
  //CHECKSTYLE:ON

  /**
   * See {@link InputPanel.Builder}.
   */
  public static class Builder
      implements InputPanel.Builder {

    /**
     * The panel's title.
     */
    private final String title;
    /**
     * Message to be displayed in the panel.
     */
    private String message;
    /**
     * Labels of for the text fields.
     */
    private final String[] labels = new String[3];
    /**
     * Unit labels of the text fields.
     */
    
    /**
     * Initial values for the text fields.
     */
    private final String[] initialValues = new String[3];
    /**
     * Regular expressions for validation of the text field contents.
     */
    private final String[] formats = new String[3];
    /**
     * Show a reset button in the panel.
     * Default is <code>false</code>. 
     */
    private boolean resetButton;
    /**
     * Value the input is reset to when the reset button is used.
     */
    private Object resetValue;

    /**
     * Create a new builder.
     * @param title Title of the panel.
     */
    public Builder(String title) {
      this.title = title;
    }

    @Override
    public InputPanel build() {
      IpAddressTextInputPanel panel = new IpAddressTextInputPanel(title);
      panel.enableInputValidation(formats[0]);
     
      panel.label3.setText(labels[0]);
      
     
      panel.Ipaddresstext.setText(initialValues[0]);
      
      panel.messageLabel.setText(message);
      panel.resetable = resetButton;
      if (panel.resetable) {
        panel.resetValue = resetValue;
      }
      return panel;
    }

    /**
     * Set the labels for the text fields. 
     * Passing <code>null</code> means there should not be such a label.
     * @param label1 The label of the first text field
     * @param label2 The label of the second text field
     * @param label3 The label of the third text field
     * @return the instance of this <code>Builder</code>
     */
    public Builder setLabels(String label1) {
      labels[0] = label1;
      return this;
    }

    /**
     * Set the same initial value for all three text fields.
     * @param value The initial value.
     * @return the isntance of this <code>Builder</code>
     */
    

    /**
     * Set the initial values for the text fields of the panel
     * Passing <code>null</code> means there should not be an initial value
     * for this text field.
     * @param val1 The initial value of the first text field
     * @param val2 The initial value of the second text field
     * @param val3 The initial value of the third text field
     * @return the instance of this <code>Builder</code>
     */
    public Builder setInitialValues(String val1) {
      initialValues[0] = val1;
      
      return this;
    }

    /**
     * Set the same text for the unit label for all three text fields.
     * @param unit The unit 
     * @return the isntance of this <code>Builder</code>
     */
   

    /**
     * Set the text for the unit labels of the panel.
     * Passing <code>null</code> means there should not be a unit label for
     * the corresponding text field.
     * @param unit1 The unit of the first text field
     * @param unit2 The unit of the second text field
     * @param unit3 The unit of the third text field
     * @return the instance of this <code>Builder</code>
     */
    

    /**
     * Set the message of the panel.
     * The user of this method must take care for the line breaks in the message,
     * as it is not wrapped automatically!
     * @param message the message
     * @return the instance of this <code>Builder</code>
     */
    public Builder setMessage(String message) {
      this.message = message;
      return this;
    }

   

    /**
     * Make the panel validate it's input by using the specified regular
     * expressions for the text fields.
     * Passing null as an argument means that the corresponding text field
     * should not be validated.
     * @param format1 The regular expression that will be used for validation in
     *                the first text field.
     * @param format2 The regular expression that will be used for validation in
     *                the second text field.
     * @param format3 The regular expression that will be used for validation in
     *                the third text field.
     * @return the instance of this <code>Builder</code>
     */
    public Builder enableValidation(String format1) {
      formats[0] = format1;
       return this;
    }

    /**
     * Set a value the panel's input can be reset to.
     * @param resetValue the reset value
     * @return the instance of this <code>Builder</code>
     */
    public Builder enableResetButton(Object resetValue) {
      this.resetButton = true;
      this.resetValue = "127.0.0.1";
      return this;
    }
  }
}
