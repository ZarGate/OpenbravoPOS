//    Openbravo POS is a point of sales application designed for touch screens.
//    Copyright (C) 2007-2009 Openbravo, S.L.
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
package com.openbravo.pos.payment;

import com.openbravo.pos.customers.CustomerInfoExt;
import java.awt.Component;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppProperties;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.payment.listeners.IListenForCancelButtonAction;
import java.util.Date;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import net.brennheit.mcashapi.MCashClient;
import net.brennheit.mcashapi.listener.IListenForPaymentUpdated;
import net.brennheit.mcashapi.listener.IListenForShortlinkScan;
import net.brennheit.mcashapi.resource.PaymentRequestOutcome;
import net.brennheit.mcashapi.resource.ResourceId;
import net.brennheit.mcashapi.resource.Shortlink;
import net.brennheit.mcashapi.resource.ShortlinkLastScan;

public class JPaymentMcash extends javax.swing.JPanel implements JPaymentInterface, IListenForShortlinkScan, IListenForPaymentUpdated, IListenForCancelButtonAction {

    private double m_dTotal;
    private double m_dPaid;
    private JPaymentNotifier m_notifier;
    private PaymentRequestOutcome m_paymentRequestOutcome;
    private ListModel m_infoList = new DefaultListModel();
    private MCashClient m_mCashClient;
    private ShortlinkLastScan m_shortlinkLastScan;
    private boolean m_bCanceled;
    private final String m_sLock = "synclock";
    private String m_sTicketID;
    private ResourceId m_resourceId;
    private String m_sPrevTicketID;
    private int m_iTicketTriesCounter;

    private String m_mCashBaseUri;
    private String m_mCashMerchantId;
    private String m_mCashUserId;
    private String m_mCashPosId;
    private String m_mCashAuthKey;
    private String m_mCashAuthMethod;
    private String m_mCashSerialNumber;
    private String m_mCashTestbedToken;
    private String m_mCashLedger;

    /**
     * Creates new form JPaymentFree
     */
    public JPaymentMcash(AppView app, JPaymentNotifier notifier) {
        m_notifier = notifier;
        initComponents();
        AppProperties config = app.getProperties();
        m_mCashBaseUri=config.getProperty("mcash.baseuri");
        m_mCashMerchantId=config.getProperty("mcash.merchantid");
        m_mCashUserId=config.getProperty("mcash.merchantuserid");
        m_mCashPosId=config.getProperty("mcash.posid");
        m_mCashAuthKey=config.getProperty("mcash.authkey");
        m_mCashAuthMethod=config.getProperty("mcash.authmethod");
        m_mCashSerialNumber=config.getProperty("mcash.serialnumber");
        m_mCashTestbedToken=config.getProperty("mcash.testbedtoken");
        if(m_mCashTestbedToken.equals("")){
            m_mCashTestbedToken = null;
        }
        m_mCashLedger=config.getProperty("mcash.ledger");
        
    }

    public void activate(CustomerInfoExt customerext, double dTotal, String transID) {
        m_dTotal = dTotal;
        m_notifier.setStatus(false, false, true);
        m_sTicketID = transID;
        if (!m_sTicketID.equals(m_sPrevTicketID)) {
            m_iTicketTriesCounter = 0;
        }
        clearJList(m_jListInfo);
        resetButtonsState();
    }

    public PaymentInfo executePayment() throws Exception {
        if (m_paymentRequestOutcome == null) {
            throw new Exception("Betalingen er ikke fullført");
        } else {
            return new PaymentInfoMcash(m_paymentRequestOutcome);
        }
    }

    public Component getComponent() {
        return this;
    }

    public String getInputString() {
        return "";
    }

    public void shortlinkScanned(ShortlinkLastScan sls) {
        addItemToJList("Scanning mottatt!", m_jListInfo);
        if (sls.id != null){
            addItemToJList("Fullfører automatisk betaling.", m_jListInfo);
            m_shortlinkLastScan = sls;
            executeMCashPayment();
        } else {
            addItemToJList("Noe feilet, prøv igjen.", m_jListInfo);
            resetButtonsState();
            closeMCash();
        }
        m_notifier.setStatus(false, false, true);
    }

    private void executeMCashPayment() {
        m_iTicketTriesCounter++;
        String tempTicketID = m_sTicketID + "-" + m_iTicketTriesCounter;
        m_resourceId = m_mCashClient.createPaymentRequest(tempTicketID, m_shortlinkLastScan.id, m_dTotal, "NOK", 0, false, null, true);
        if (m_resourceId == null || m_resourceId.id == null) {
            addItemToJList("Kunne ikke opprette betaling, prøv igjen.", m_jListInfo);
            resetButtonsState();
            return;
        }
        addItemToJList("Kjører betaling, vennligst vent.", m_jListInfo);
        m_mCashClient.addPaymentFinishedEventListener(this);
        m_mCashClient.startPaymentFinishedListener(m_resourceId.id);
        m_notifier.setStatus(false, false, false);

    }

    public void paymentFinished(PaymentRequestOutcome pro) {
        m_paymentRequestOutcome = pro;
        if (m_paymentRequestOutcome == null) {
            addItemToJList("Feilet ved venting på betaling, kotakt crew.", m_jListInfo);
            resetButtonsState();
            m_notifier.setStatus(false, false, true);
        } else if (m_paymentRequestOutcome.status.equals("fail")) {
            addItemToJList("Betaling feilet.", m_jListInfo);
            switch (m_paymentRequestOutcome.status_code) {
                case 4004:
                    addItemToJList("Fant ikke kunde.", m_jListInfo);
                    break;
                case 4019:
                    addItemToJList("Betaling avbrutt.", m_jListInfo);
                    break;
                case 5006:
                    addItemToJList("Betaling avslått.", m_jListInfo);
                    break;
                case 5011:
                    addItemToJList("Betaling fikk tidsavbrudd.", m_jListInfo);
                    break;
            }
            resetButtonsState();
            m_notifier.setStatus(false, false, true);
        } else {
            m_notifier.setStatus(true, true, true);
            m_notifier.clickOkButton();
        }
        closeMCash();
    }

    private void startListenForScanTokens() {
        closeMCash();
        m_mCashClient = new MCashClient(m_mCashBaseUri,
                m_mCashMerchantId, m_mCashUserId, m_mCashAuthKey, m_mCashAuthMethod,
                m_mCashPosId, m_mCashLedger, m_mCashTestbedToken);
        ResourceId shortlink = m_mCashClient.createShortlink(m_mCashSerialNumber, null);
        if(shortlink == null || shortlink.id == null){
            JOptionPane.showMessageDialog(this,
                        "En feil oppstod ved oppretting av mCASH Shortlink. Kontakt crew.",
                        "En feil oppstod", JOptionPane.ERROR_MESSAGE);
            resetButtonsState();
            closeMCash();
            return;
        }
        m_jButtonStartPayment.setEnabled(false);
        m_jButtonCancelPayment.setEnabled(true);
        addItemToJList("Venter på scanning", m_jListInfo);
        m_mCashClient.addShortlinkScannedEventListener(this);
        m_mCashClient.startShortlinkScannedListener(shortlink.id, new Date((new Date()).getTime() - 5000)); //Set time to live to 5 seconds
    }

    private void cancelMCashPayment() {
        if (m_mCashClient != null && m_resourceId != null) {
            m_mCashClient.abortPaymentRequest(m_resourceId.id, null);
        }
        addItemToJList("Avbrøt mCASH", m_jListInfo);
        closeMCash();
        resetButtonsState();
        m_notifier.setStatus(false, false, true);
    }

    private void closeMCash() {
        if (m_mCashClient == null) {
            return;
        }
        m_mCashClient.close();
        m_mCashClient = null;
    }

    private void addItemToJList(String item, javax.swing.JList jList) {
        DefaultListModel model = (DefaultListModel) jList.getModel();
        if (model == null) {
            throw new NullPointerException();
        }
        model.addElement(item);
        jList.setModel(model);
        jList.ensureIndexIsVisible(model.getSize() - 1);
    }

    private void clearJList(javax.swing.JList jList) {
        DefaultListModel model = (DefaultListModel) jList.getModel();
        if (model == null) {
            throw new NullPointerException();
        }
        model.removeAllElements();
        jList.setModel(model);
    }

    private void resetButtonsState() {
        m_jButtonCancelPayment.setEnabled(false);
        m_jButtonStartPayment.setEnabled(true);
    }

    @Override
    public void cancelButtonClicked() {
        cancelMCashPayment();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_jScrollPane = new javax.swing.JScrollPane();
        m_jListInfo = new javax.swing.JList();
        m_jButtonStartPayment = new javax.swing.JButton();
        m_jButtonCancelPayment = new javax.swing.JButton();

        m_jScrollPane.setPreferredSize(new java.awt.Dimension(150, 100));

        m_jListInfo.setModel(m_infoList);
        m_jListInfo.setFocusable(false);
        m_jScrollPane.setViewportView(m_jListInfo);

        m_jButtonStartPayment.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/mcash32.png"))); // NOI18N
        m_jButtonStartPayment.setText(AppLocal.getIntString("Button.Start")); // NOI18N
        m_jButtonStartPayment.setFocusPainted(false);
        m_jButtonStartPayment.setFocusable(false);
        m_jButtonStartPayment.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jButtonStartPayment.setRequestFocusEnabled(false);
        m_jButtonStartPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jButtonStartPaymentActionPerformed(evt);
            }
        });

        m_jButtonCancelPayment.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/button_cancel.png"))); // NOI18N
        m_jButtonCancelPayment.setText(AppLocal.getIntString("Button.Cancel")); // NOI18N
        m_jButtonCancelPayment.setEnabled(false);
        m_jButtonCancelPayment.setFocusPainted(false);
        m_jButtonCancelPayment.setFocusable(false);
        m_jButtonCancelPayment.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jButtonCancelPayment.setRequestFocusEnabled(false);
        m_jButtonCancelPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jButtonCancelPaymentActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_jButtonStartPayment, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jButtonCancelPayment, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(m_jButtonStartPayment, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(57, 57, 57)
                        .addComponent(m_jButtonCancelPayment, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(145, 145, 145))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void m_jButtonStartPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jButtonStartPaymentActionPerformed
        startListenForScanTokens();
    }//GEN-LAST:event_m_jButtonStartPaymentActionPerformed

    private void m_jButtonCancelPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jButtonCancelPaymentActionPerformed
        cancelMCashPayment();
    }//GEN-LAST:event_m_jButtonCancelPaymentActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton m_jButtonCancelPayment;
    private javax.swing.JButton m_jButtonStartPayment;
    private javax.swing.JList m_jListInfo;
    private javax.swing.JScrollPane m_jScrollPane;
    // End of variables declaration//GEN-END:variables
}
