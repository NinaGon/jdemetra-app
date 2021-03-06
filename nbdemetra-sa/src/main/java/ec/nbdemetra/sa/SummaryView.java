/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa;

import com.google.common.collect.Maps;
import ec.nbdemetra.ui.ComponentFactory;
import ec.nbdemetra.ui.NbComponents;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tss.html.HtmlUtil;
import ec.tss.html.implementation.HtmlRegArimaReport;
import ec.tss.sa.RegArimaReport;
import ec.tstoolkit.algorithm.AlgorithmDescriptor;
import ec.ui.AHtmlView;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.windows.TopComponent;

/**
 *
 * @author Philippe Charles
 */
public class SummaryView extends AbstractSaProcessingTopComponent implements MultiViewElement {

    // main components
    private final JComponent visualRepresentation;
    private final JToolBar toolBarRepresentation;
    // data
    private Map<Integer, Map<AlgorithmDescriptor, RegArimaReport>> reports;
    // subcomponents
    private final JComboBox<Map.Entry<Integer, AlgorithmDescriptor>> comboBox;
    private final AHtmlView reportTB_;

    public SummaryView(WorkspaceItem<MultiProcessingDocument> doc, MultiProcessingController controller) {
        super(doc, controller);
        this.reports = new HashMap<>();
        this.reportTB_ = ComponentFactory.getDefault().newHtmlView();

        this.comboBox = new JComboBox<>();
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel result = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    Map.Entry<Integer, AlgorithmDescriptor> item = (Map.Entry<Integer, AlgorithmDescriptor>) value;
                    result.setText(TaggedTreeNode.freqName(item.getKey()) + " > " + item.getValue().name);
                }
                return result;
            }
        });
        comboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED && e.getItem() != null) {
                    Map.Entry<Integer, AlgorithmDescriptor> item = (Map.Entry<Integer, AlgorithmDescriptor>) e.getItem();
                    HtmlRegArimaReport report = new HtmlRegArimaReport(item.getValue().name, reports.get(item.getKey()).get(item.getValue()));
                    reportTB_.loadContent(HtmlUtil.toString(report));
                } else {
                    reportTB_.loadContent("");
                }
            }
        });

        toolBarRepresentation = NbComponents.newInnerToolbar();
        toolBarRepresentation.addSeparator();
        toolBarRepresentation.add(comboBox);

        visualRepresentation = reportTB_;

        setData(Collections.<Integer, Map<AlgorithmDescriptor, RegArimaReport>>emptyMap());

        setLayout(new BorderLayout());
        add(toolBarRepresentation, BorderLayout.NORTH);
        add(visualRepresentation, BorderLayout.CENTER);
    }

    @Override
    protected void onSaProcessingStateChange() {
        super.onSaProcessingStateChange();
        if (controller.getState().isFinished()) {
            setData(getCurrentProcessing().createRegArimaReports());
        } else {
            setData(Collections.<Integer, Map<AlgorithmDescriptor, RegArimaReport>>emptyMap());
        }
    }

    // MultiViewElement >
    @Override
    public JComponent getVisualRepresentation() {
        return visualRepresentation;
    }

    @Override
    public JComponent getToolbarRepresentation() {
        return toolBarRepresentation;
    }

    @Override
    public void componentOpened() {
    }

    @Override
    public void componentClosed() {
    }

    @Override
    public void componentShowing() {
    }

    @Override
    public void componentHidden() {
    }

    @Override
    public void componentActivated() {
    }

    @Override
    public void componentDeactivated() {
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback callback) {
    }

    @Override
    public CloseOperationState canCloseElement() {
        return CloseOperationState.STATE_OK;
    }
    // < MultiViewElement

    private static ComboBoxModel<Map.Entry<Integer, AlgorithmDescriptor>> asComboBoxModel(Map<Integer, Map<AlgorithmDescriptor, RegArimaReport>> reports) {
        DefaultComboBoxModel<Map.Entry<Integer, AlgorithmDescriptor>> result = new DefaultComboBoxModel<>();
        for (Map.Entry<Integer, Map<AlgorithmDescriptor, RegArimaReport>> item : reports.entrySet()) {
            for (AlgorithmDescriptor ritem : item.getValue().keySet()) {
                result.addElement(Maps.immutableEntry(item.getKey(), ritem));
            }
        }
        return result;
    }

    public void setData(Map<Integer, Map<AlgorithmDescriptor, RegArimaReport>> reports) {
        reportTB_.loadContent("");
        this.reports = reports;
        comboBox.setVisible(reports.size() > 1);
        comboBox.setModel(asComboBoxModel(reports));
        comboBox.setSelectedIndex(-1);
        if (!reports.isEmpty()) {
            comboBox.setSelectedIndex(0);
        }
    }
}
