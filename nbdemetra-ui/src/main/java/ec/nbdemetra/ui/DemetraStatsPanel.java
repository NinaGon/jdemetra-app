/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package ec.nbdemetra.ui;

import ec.nbdemetra.ws.ui.SpecSelectionComponent;
import ec.satoolkit.ISaSpecification;
import ec.satoolkit.tramoseats.TramoSeatsSpecification;
import ec.tss.sa.EstimationPolicyType;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPopupMenu;
import org.openide.awt.DropDownButtonFactory;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Mats Maggi
 */
final class DemetraStatsPanel extends javax.swing.JPanel {

    private final DemetraStatsOptionsPanelController controller;
    private JPopupMenu specPopup = new JPopupMenu();
    private SpecSelectionComponent specComponent = new SpecSelectionComponent(true);

    private EstimationPolicyType[] types = {EstimationPolicyType.Complete,
        EstimationPolicyType.FreeParameters,
        EstimationPolicyType.None};

    /**
     * Creates new form DemetraBehaviourPanel
     */
    DemetraStatsPanel(DemetraStatsOptionsPanelController controller) {
        this.controller = controller;
        initComponents();
        initSpecButton();
    }

    void load() {
        DemetraUI demetraUI = DemetraUI.getDefault();
        spectralLastYears.setValue(demetraUI.getSpectralLastYears());

        estimationPolicyComboBox.setModel(new DefaultComboBoxModel(types));
        estimationPolicyComboBox.setSelectedItem(demetraUI.getEstimationPolicyType());

        stabilityLength.setValue(demetraUI.getStabilityLength());
        
        specComponent.setSpecification(demetraUI.getDefaultSASpec());
        selectedSpecLabel.setText(demetraUI.getDefaultSASpec().toLongString());
    }

    void store() {
        DemetraUI demetraUI = DemetraUI.getDefault();
        demetraUI.setSpectralLastYears((Integer) spectralLastYears.getValue());
        demetraUI.setEstimationPolicyType((EstimationPolicyType) estimationPolicyComboBox.getSelectedItem());
        demetraUI.setStabilityLength((Integer) stabilityLength.getValue());
        
        if (specComponent.getSpecification() instanceof TramoSeatsSpecification) {
            demetraUI.setDefaultSASpec("tramoseats." + specComponent.getSpecification().toString());
        } else {
            demetraUI.setDefaultSASpec("x13." + specComponent.getSpecification().toString());
        }
        
    }

    boolean valid() {
        // TODO check whether form is consistent and complete
        return true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lastYearsPanel = new javax.swing.JPanel();
        spectralLastYears = new javax.swing.JSpinner();
        spectralLabel = new javax.swing.JLabel();
        stabilityLabel = new javax.swing.JLabel();
        stabilityLength = new javax.swing.JSpinner();
        saPanel = new javax.swing.JPanel();
        defaultSpecLabel = new javax.swing.JLabel();
        specButton = DropDownButtonFactory.createDropDownButton(ImageUtilities.loadImageIcon("ec/nbdemetra/sa/blog_16x16.png", false), specPopup);
        selectedSpecLabel = new javax.swing.JLabel();
        revisionHistoryPanel = new javax.swing.JPanel();
        estimationLabel = new javax.swing.JLabel();
        estimationPolicyComboBox = new javax.swing.JComboBox();

        lastYearsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(DemetraStatsPanel.class, "DemetraStatsPanel.lastYearsPanel.border.title"))); // NOI18N

        spectralLastYears.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));

        org.openide.awt.Mnemonics.setLocalizedText(spectralLabel, org.openide.util.NbBundle.getMessage(DemetraStatsPanel.class, "DemetraStatsPanel.spectralLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(stabilityLabel, org.openide.util.NbBundle.getMessage(DemetraStatsPanel.class, "DemetraStatsPanel.stabilityLabel.text")); // NOI18N

        stabilityLength.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(8), Integer.valueOf(1), null, Integer.valueOf(1)));

        javax.swing.GroupLayout lastYearsPanelLayout = new javax.swing.GroupLayout(lastYearsPanel);
        lastYearsPanel.setLayout(lastYearsPanelLayout);
        lastYearsPanelLayout.setHorizontalGroup(
            lastYearsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lastYearsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lastYearsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(stabilityLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(spectralLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(lastYearsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spectralLastYears, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stabilityLength, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        lastYearsPanelLayout.setVerticalGroup(
            lastYearsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lastYearsPanelLayout.createSequentialGroup()
                .addGroup(lastYearsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spectralLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                    .addGroup(lastYearsPanelLayout.createSequentialGroup()
                        .addComponent(spectralLastYears, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(lastYearsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stabilityLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                    .addComponent(stabilityLength, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        saPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(DemetraStatsPanel.class, "DemetraStatsPanel.saPanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(defaultSpecLabel, org.openide.util.NbBundle.getMessage(DemetraStatsPanel.class, "DemetraStatsPanel.defaultSpecLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(specButton, org.openide.util.NbBundle.getMessage(DemetraStatsPanel.class, "DemetraStatsPanel.specButton.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(selectedSpecLabel, org.openide.util.NbBundle.getMessage(DemetraStatsPanel.class, "DemetraStatsPanel.selectedSpecLabel.text")); // NOI18N

        javax.swing.GroupLayout saPanelLayout = new javax.swing.GroupLayout(saPanel);
        saPanel.setLayout(saPanelLayout);
        saPanelLayout.setHorizontalGroup(
            saPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(saPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(defaultSpecLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(specButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(selectedSpecLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        saPanelLayout.setVerticalGroup(
            saPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(saPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(defaultSpecLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(specButton)
                .addComponent(selectedSpecLabel))
        );

        revisionHistoryPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(DemetraStatsPanel.class, "DemetraStatsPanel.revisionHistoryPanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(estimationLabel, org.openide.util.NbBundle.getMessage(DemetraStatsPanel.class, "DemetraStatsPanel.estimationLabel.text")); // NOI18N

        javax.swing.GroupLayout revisionHistoryPanelLayout = new javax.swing.GroupLayout(revisionHistoryPanel);
        revisionHistoryPanel.setLayout(revisionHistoryPanelLayout);
        revisionHistoryPanelLayout.setHorizontalGroup(
            revisionHistoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(revisionHistoryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(estimationLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(estimationPolicyComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(119, Short.MAX_VALUE))
        );
        revisionHistoryPanelLayout.setVerticalGroup(
            revisionHistoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(revisionHistoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(estimationLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                .addComponent(estimationPolicyComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lastYearsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(saPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(revisionHistoryPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lastYearsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(revisionHistoryPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 26, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel defaultSpecLabel;
    private javax.swing.JLabel estimationLabel;
    private javax.swing.JComboBox estimationPolicyComboBox;
    private javax.swing.JPanel lastYearsPanel;
    private javax.swing.JPanel revisionHistoryPanel;
    private javax.swing.JPanel saPanel;
    private javax.swing.JLabel selectedSpecLabel;
    private javax.swing.JButton specButton;
    private javax.swing.JLabel spectralLabel;
    private javax.swing.JSpinner spectralLastYears;
    private javax.swing.JLabel stabilityLabel;
    private javax.swing.JSpinner stabilityLength;
    // End of variables declaration//GEN-END:variables

    private void initSpecButton() {
        specPopup.add(specComponent);
        specComponent.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String p = evt.getPropertyName();
                if (p.equals(SpecSelectionComponent.SPECIFICATION_PROPERTY) && evt.getNewValue() != null) {
                    selectedSpecLabel.setText(((ISaSpecification) evt.getNewValue()).toLongString());
                } else if (p.equals(SpecSelectionComponent.ICON_PROPERTY) && evt.getNewValue() != null) {
                    specButton.setIcon(ImageUtilities.image2Icon((Image) evt.getNewValue()));
                }
            }
        });
    }
}
