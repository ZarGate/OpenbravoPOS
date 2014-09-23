//    Openbravo POS is a point of sales application designed for touch screens.
//    Copyright (C) 2008-2009 Openbravo, S.L.
//    http://www.openbravo.com/product/pos
//
//    This file is part of Openbravo POS.
//
//    Openbravo POS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Openbravo POS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Openbravo POS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.config;

import com.openbravo.data.user.DirtyManager;
import java.awt.Component;
import com.openbravo.pos.forms.AppConfig;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.payment.ConfigPaymentPanelCaixa;
import com.openbravo.pos.payment.ConfigPaymentPanelEmpty;
import com.openbravo.pos.payment.ConfigPaymentPanelGeneric;
import com.openbravo.pos.payment.ConfigPaymentPanelLinkPoint;
import com.openbravo.pos.payment.PaymentConfiguration;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author adrianromero
 * @author Mikel Irurita
 */
public class JPanelConfigPayment extends javax.swing.JPanel implements PanelConfig {

    private DirtyManager dirty = new DirtyManager();
    private Map<String, PaymentConfiguration> paymentsName = new HashMap<String, PaymentConfiguration>();
    private PaymentConfiguration pc;
    
    /** Creates new form JPanelConfigPayment */
    public JPanelConfigPayment() {
        
        initComponents();
                
        // dirty manager
        jcboCardReader.addActionListener(dirty);
        jcboPaymentGateway.addActionListener(dirty);
        jchkPaymentTest.addActionListener(dirty);
        
        // Payment Provider                
        initPayments("Not defined", new ConfigPaymentPanelEmpty());
        initPayments("external", new ConfigPaymentPanelEmpty());
        initPayments("PayPoint / SecPay", new ConfigPaymentPanelGeneric());
        initPayments("AuthorizeNet", new ConfigPaymentPanelGeneric());
        initPayments("Planetauthorize", new ConfigPaymentPanelGeneric());
        initPayments("Firs Data / LinkPoint / YourPay", new ConfigPaymentPanelLinkPoint());
        initPayments("PaymentsGateway.net", new ConfigPaymentPanelGeneric());
        initPayments("La Caixa (Spain)", new ConfigPaymentPanelCaixa());
        
        // Lector de tarjetas.
        jcboCardReader.addItem("Not defined");
        jcboCardReader.addItem("Generic");
        jcboCardReader.addItem("Alternative");
        jcboCardReader.addItem("Intelligent");
        jcboCardReader.addItem("Keyboard");
    }
    
    public boolean hasChanged() {
        return dirty.isDirty();
    }
    
    public Component getConfigComponent() {
        return this;
    }
   
    public void loadProperties(AppConfig config) {

        jcboCardReader.setSelectedItem(config.getProperty("payment.magcardreader"));
        jcboPaymentGateway.setSelectedItem(config.getProperty("payment.gateway"));
        jchkPaymentTest.setSelected(Boolean.valueOf(config.getProperty("payment.testmode")).booleanValue());    
        loadMCashProperties(config);
        pc.loadProperties(config);
        dirty.setDirty(false);
    }
   
    public void saveProperties(AppConfig config) {
        
        config.setProperty("payment.magcardreader", comboValue(jcboCardReader.getSelectedItem()));
        config.setProperty("payment.gateway", comboValue(jcboPaymentGateway.getSelectedItem()));
        config.setProperty("payment.testmode", Boolean.toString(jchkPaymentTest.isSelected()));
        setMCashProperties(config);
        pc.saveProperties(config);
        dirty.setDirty(false);
    }
    
    private void setMCashProperties(AppConfig config){
        config.setProperty("mcash.enabled", Boolean.toString(jchkMCashEnabled.isSelected()));
        config.setProperty("mcash.baseuri", jtxtMCashBaseUri.getText());
        config.setProperty("mcash.merchantid", jtxtMCashMerchantId.getText());
        config.setProperty("mcash.merchantuserid", jtxtMCashUserId.getText());
        config.setProperty("mcash.posid", jtxtMCashPosId.getText());
        config.setProperty("mcash.authkey", jtxtMCashAuthKey.getText());
        config.setProperty("mcash.authmethod", jtxtMCashAuthMethod.getText());
        config.setProperty("mcash.serialnumber", jtxtMCashSerialNumber.getText());
        config.setProperty("mcash.testbedtoken", jtxtMCashTestbedToken.getText());
        config.setProperty("mcash.ledger", jtxtMCashLedger.getText());
    }
    
    private void loadMCashProperties(AppConfig config){
        jchkMCashEnabled.setSelected(Boolean.valueOf(config.getProperty("mcash.enabled")).booleanValue());
        jtxtMCashBaseUri.setText(config.getProperty("mcash.baseuri"));
        jtxtMCashMerchantId.setText(config.getProperty("mcash.merchantid"));
        jtxtMCashUserId.setText(config.getProperty("mcash.merchantuserid"));
        jtxtMCashPosId.setText(config.getProperty("mcash.posid"));
        jtxtMCashAuthKey.setText(config.getProperty("mcash.authkey"));
        jtxtMCashAuthMethod.setText(config.getProperty("mcash.authmethod"));
        jtxtMCashSerialNumber.setText(config.getProperty("mcash.serialnumber"));
        jtxtMCashTestbedToken.setText(config.getProperty("mcash.testbedtoken"));
        jtxtMCashLedger.setText(config.getProperty("mcash.ledger"));
    }
    
    private void initPayments(String name, PaymentConfiguration pc) {
        jcboPaymentGateway.addItem(name);
        paymentsName.put(name, pc);
    }
     
    private String comboValue(Object value) {
        return value == null ? "" : value.toString();
    }   

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jcboCardReader = new javax.swing.JComboBox();
        jcboPaymentGateway = new javax.swing.JComboBox();
        jchkPaymentTest = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jtxtMCashBaseUri = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jchkMCashEnabled = new javax.swing.JCheckBox();
        jLabel15 = new javax.swing.JLabel();
        jtxtMCashMerchantId = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jtxtMCashUserId = new javax.swing.JTextField();
        jtxtMCashPosId = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jtxtMCashAuthKey = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jtxtMCashAuthMethod = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jtxtMCashSerialNumber = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jtxtMCashLedger = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jtxtMCashTestbedToken = new javax.swing.JTextField();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(AppLocal.getIntString("Label.Payment"))); // NOI18N

        jLabel13.setText(AppLocal.getIntString("label.paymentgateway")); // NOI18N

        jLabel11.setText(AppLocal.getIntString("label.magcardreader")); // NOI18N

        jcboPaymentGateway.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcboPaymentGatewayActionPerformed(evt);
            }
        });

        jchkPaymentTest.setText(AppLocal.getIntString("label.paymenttestmode")); // NOI18N

        jPanel2.setLayout(new java.awt.GridLayout(1, 1));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 664, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcboPaymentGateway, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jchkPaymentTest, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcboCardReader, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jcboCardReader, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jcboPaymentGateway, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jchkPaymentTest, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("Panel.Config.Mcash"))); // NOI18N

        jLabel12.setText(AppLocal.getIntString("label.mcash.baseuri")); // NOI18N

        jLabel14.setText(AppLocal.getIntString("label.mcash.integration")); // NOI18N

        jchkMCashEnabled.setText(bundle.getString("checkbox.active")); // NOI18N

        jLabel15.setText(AppLocal.getIntString("label.mcash.merchantid")); // NOI18N

        jLabel16.setText(AppLocal.getIntString("label.mcash.userid")); // NOI18N

        jLabel17.setText(AppLocal.getIntString("label.mcash.posid")); // NOI18N

        jLabel18.setText(AppLocal.getIntString("label.mcash.authkey")); // NOI18N

        jLabel19.setText(AppLocal.getIntString("label.mcash.authmethod")); // NOI18N

        jLabel20.setText(AppLocal.getIntString("label.mcash.serialnumber")); // NOI18N

        jLabel21.setText(AppLocal.getIntString("label.mcash.ledger")); // NOI18N

        jLabel22.setText(AppLocal.getIntString("label.mcash.testbedtoken")); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtMCashLedger, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtMCashUserId, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtMCashBaseUri, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jchkMCashEnabled, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtMCashMerchantId, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtMCashAuthMethod))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtMCashPosId, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtMCashAuthKey))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtMCashSerialNumber)
                            .addComponent(jtxtMCashTestbedToken))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jchkMCashEnabled)
                    .addComponent(jLabel14)
                    .addComponent(jtxtMCashPosId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtMCashBaseUri, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(jtxtMCashAuthKey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtMCashMerchantId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(jtxtMCashAuthMethod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtMCashUserId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(jtxtMCashSerialNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtMCashLedger, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21)
                    .addComponent(jtxtMCashTestbedToken, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.getAccessibleContext().setAccessibleName(bundle.getString("Panel.Config.Mcash")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

private void jcboPaymentGatewayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcboPaymentGatewayActionPerformed
    pc = paymentsName.get(comboValue(jcboPaymentGateway.getSelectedItem()));

    if (pc != null) {
        jPanel2.removeAll();
        jPanel2.add(pc.getComponent());
        jPanel2.revalidate();
        jPanel2.repaint(); 
    }
}//GEN-LAST:event_jcboPaymentGatewayActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JComboBox jcboCardReader;
    private javax.swing.JComboBox jcboPaymentGateway;
    private javax.swing.JCheckBox jchkMCashEnabled;
    private javax.swing.JCheckBox jchkPaymentTest;
    private javax.swing.JTextField jtxtMCashAuthKey;
    private javax.swing.JTextField jtxtMCashAuthMethod;
    private javax.swing.JTextField jtxtMCashBaseUri;
    private javax.swing.JTextField jtxtMCashLedger;
    private javax.swing.JTextField jtxtMCashMerchantId;
    private javax.swing.JTextField jtxtMCashPosId;
    private javax.swing.JTextField jtxtMCashSerialNumber;
    private javax.swing.JTextField jtxtMCashTestbedToken;
    private javax.swing.JTextField jtxtMCashUserId;
    // End of variables declaration//GEN-END:variables
    
}
