/**
 * Copyright (c) 2009, Benjamin Adams
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of Benjamin Adams nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

 * THIS SOFTWARE IS PROVIDED BY Benjamin Adams ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Benjamin Adams BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * @author Ben
 */

/*
 * EditQDimContextDialog.java
 *
 * Created on Aug 11, 2009, 10:02:53 AM
 */

package edu.geog.geocog.csml.gui;

import edu.geog.geocog.csml.model.CSMLModel;
import edu.geog.geocog.csml.model.CSMLDomain;
import edu.geog.geocog.csml.model.CSMLQualityDimension;
import edu.geog.geocog.csml.model.CSMLQualityDimensionTypeContext;
import edu.geog.geocog.csml.utils.JTextFieldFilter;

/**
 *
 * @author Ben
 */
public class EditQDimContextDialog extends javax.swing.JDialog {

    public static final int CANCEL = 0;
    public static final int OK = 1;

    private CSMLDomain domain;
    private CSMLModel model;
    private CSMLQualityDimensionTypeContext context;

    private int result = CANCEL;

    /** Creates new form EditQDimContextDialog */
    public EditQDimContextDialog(java.awt.Frame parent, boolean modal, CSMLDomain domain, CSMLModel model, CSMLQualityDimensionTypeContext context) {
        super(parent, modal);
        initComponents();
        this.domain = domain;
        this.model = model;
        this.context = context;

        qDimTypeContextsComboBox.addItem("");
        CSMLQualityDimensionTypeContext[] qdtcArray = model.getQualityDimensionTypeContexts(domain.getId());
        for (CSMLQualityDimensionTypeContext qdtc : qdtcArray) {
            if (qdtc.getDomainId().equals(domain.getId()))
                qDimTypeContextsComboBox.addItem(qdtc);
        }
        qDimTypeContextsComboBox.setSelectedItem(context);

        int qDimNum = domain.numberOfDimensions();

        weightsPanel = new javax.swing.JPanel();
        weightsPanel.setLayout(new java.awt.GridLayout(qDimNum, 2));
        qDimWeights = new javax.swing.JTextField[qDimNum];
        for (int i = 0; i < qDimNum; i++) {
            qDimWeights[i] = new javax.swing.JTextField();
        }
        qDimLabels = new javax.swing.JLabel[qDimNum];
        for (int i = 0; i < qDimNum; i++) {
            qDimLabels[i] = new javax.swing.JLabel();
        }

        String[] qdIds = domain.getQualityDimensionIds();
        CSMLQualityDimension[] qdArray = new CSMLQualityDimension[qdIds.length];
        for (int i = 0; i < qdArray.length; i++) {
            qDimLabels[i].setText(domain.getQualityDimension(qdIds[i]).toString());
        }

        JTextFieldFilter[] filters = new JTextFieldFilter[qDimNum];
        for (int i = 0; i < qDimNum; i++) {
            filters[i] = new JTextFieldFilter(JTextFieldFilter.FLOAT);
            filters[i].setNegativeAccepted(false);
        }

        Object o = qDimTypeContextsComboBox.getSelectedItem();
        if (o instanceof CSMLQualityDimensionTypeContext) {
            for (int i = 0; i < qDimNum; i++) {
                qDimWeights[i].setDocument(filters[i]);
                CSMLQualityDimensionTypeContext qdtc = (CSMLQualityDimensionTypeContext) o;
                qDimWeights[i].setText(Double.toString(qdtc.getWeight(qdIds[i])));
            }
        } else {
            for (int i = 0; i < qDimNum; i++) {
                qDimWeights[i].setDocument(filters[i]);
                qDimWeights[i].setText(Double.toString(1.0 / qDimNum));
            }
        }

        for (int i = 0; i < qDimNum; i++) {
            weightsPanel.add(qDimWeights[i]);
            weightsPanel.add(qDimLabels[i]);
        }

        contextScrollPane.setViewportView(weightsPanel);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        qDimTypeContextsComboBox = new javax.swing.JComboBox();
        contextScrollPane = new javax.swing.JScrollPane();
        slidersPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quality Dimension weights");

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        slidersPanel.setLayout(new java.awt.GridLayout(0, 2));
        contextScrollPane.setViewportView(slidersPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(497, Short.MAX_VALUE)
                .addComponent(qDimTypeContextsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(483, Short.MAX_VALUE)
                .addComponent(okButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton)
                .addContainerGap())
            .addComponent(contextScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 667, Short.MAX_VALUE)
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(qDimTypeContextsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(contextScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        result = OK;
        this.dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        result = CANCEL;
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    public int getResult() {
        return result;
    }

    private javax.swing.JPanel weightsPanel;
    private javax.swing.JLabel[] qDimLabels;
    private javax.swing.JTextField[] qDimWeights;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JScrollPane contextScrollPane;
    private javax.swing.JButton okButton;
    private javax.swing.JComboBox qDimTypeContextsComboBox;
    private javax.swing.JPanel slidersPanel;
    // End of variables declaration//GEN-END:variables

}
