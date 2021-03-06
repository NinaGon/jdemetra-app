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
package ec.ui.chart;

import ec.nbdemetra.ui.awt.KeyStrokes;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.TsFactory;
import ec.tss.datatransfer.TssTransferSupport;
import ec.tstoolkit.data.DescriptiveStatistics;
import ec.tstoolkit.timeseries.simplets.TsData;
import static ec.ui.ATsCollectionView.COPY_ACTION;
import ec.ui.ATsControl;
import ec.ui.ExtAction;
import ec.ui.view.JChartPanel;
import ec.util.chart.ColorScheme;
import ec.util.chart.swing.Charts;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 * Chart Panel used in popups of Revision History View
 * @author Mats Maggi
 */
public class RevisionChartPanel extends ATsControl implements ClipboardOwner {

    private JChartPanel panel;
    private XYLineAndShapeRenderer refRenderer;
    private XYLineAndShapeRenderer seriesRenderer;
    private static final int REF_INDEX = 0;
    private static final int SERIES_INDEX = 1;
    private TsData reference;
    private List<TsData> revs;
    private ActionMap am;
    private InputMap im;

    /**
     * Constructs a new RevisionChartPanel from a given chart
     * @param chart Chart used to construct the chart panel
     */
    public RevisionChartPanel(JFreeChart chart) {
        setLayout(new BorderLayout());
        panel = new JChartPanel(chart);

        refRenderer = new XYLineAndShapeRenderer();
        refRenderer.setBaseShapesVisible(false);
        refRenderer.setBasePaint(themeSupport.getLineColor(ColorScheme.KnownColor.RED));
        refRenderer.setBaseStroke(new BasicStroke(2.0f));

        seriesRenderer = new XYLineAndShapeRenderer();
        seriesRenderer.setBaseShapesVisible(false);
        seriesRenderer.setBasePaint(themeSupport.getLineColor(ColorScheme.KnownColor.BLUE));
        seriesRenderer.setBaseStroke(new BasicStroke(0.75f));
        
        panel.setPopupMenu(buildMenu().getPopupMenu());
        
        fillActionMap(getActionMap());
        fillInputMap(getInputMap());
        
        add(panel, BorderLayout.CENTER);
        onColorSchemeChange();
    }
    
    /**
     * Sets the title of the graph
     * @param title Title of the graph
     */
    public void setChartTitle(String title) {
        panel.getChart().setTitle(title);
    }

    /**
     * Sets the data of the graph
     * @param reference Reference serie used for the revisions
     * @param revisions Calculated list of revision's series
     */
    public void setTsData(TsData reference, List<TsData> revisions) {
        this.reference = reference;
        this.revs = revisions;

        TimeSeriesCollection ref = new TimeSeriesCollection();
        addSerie(ref, reference);

        XYPlot plot = panel.getChart().getXYPlot();
        plot.setDataset(REF_INDEX, ref);
        plot.setRenderer(REF_INDEX, refRenderer);
        refRenderer.setSeriesPaint(0, themeSupport.getLineColor(ColorScheme.KnownColor.RED));
        refRenderer.setSeriesStroke(0, new BasicStroke(2.0f));

        TimeSeriesCollection revCol = null;
        if (revisions != null && !revisions.isEmpty()) {
            revCol = new TimeSeriesCollection();
            for (TsData t : revisions) {
                addSerie(revCol, t);
            }

            plot.setDataset(SERIES_INDEX, revCol);
            plot.setRenderer(SERIES_INDEX, seriesRenderer);
            for (int i = 0; i < revCol.getSeriesCount(); i++) {
                seriesRenderer.setSeriesPaint(i, themeSupport.getLineColor(ColorScheme.KnownColor.BLUE));
                seriesRenderer.setSeriesStroke(i, new BasicStroke(0.75f));
            }
        } else {
            plot.setDataset(SERIES_INDEX, Charts.emptyXYDataset());
            refRenderer.setSeriesPaint(0, themeSupport.getLineColor(ColorScheme.KnownColor.ORANGE));
        }

        configureAxis(plot);

        setRange(ref, revCol);
    }
    
    private JMenu buildMenu() {
        am = new ActionMap();
        am.put(COPY_ACTION, new CopyAction());

        im = new InputMap();
        KeyStrokes.putAll(im, KeyStrokes.COPY, COPY_ACTION);
        
        JMenu result = new JMenu();

        JMenuItem item;
        
        item = new JMenuItem(am.get(COPY_ACTION));
        item.setText("Copy All");
        item.setAccelerator(KeyStrokes.COPY.get(0));
        ExtAction.hideWhenDisabled(item);
        result.add(item);
        
        return result;
    }

    private void configureAxis(XYPlot plot) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy");
        DateAxis dateAxis = new DateAxis();
        dateAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
        dateAxis.setDateFormatOverride(sdf);
        plot.setDomainAxis(dateAxis);
        NumberAxis yaxis = new NumberAxis();
        plot.setRangeAxis(yaxis);
    }

    private void setRange(TimeSeriesCollection ref, TimeSeriesCollection series) {
        double min, max;
        Range c = ref.getRangeBounds(true);

        min = c.getLowerBound();
        max = c.getUpperBound();

        if (series != null && series.getSeriesCount() != 0) {
            Range s = series.getRangeBounds(true);
            if (min > s.getLowerBound()) {
                min = s.getLowerBound();
            }

            if (max < s.getUpperBound()) {
                max = s.getUpperBound();
            }
        }

        min -= (Math.abs(min) * .03);
        max += (Math.abs(max) * .03);

        panel.getChart().getXYPlot().getRangeAxis().setRange(new Range(min, max));
    }

    private void addSerie(TimeSeriesCollection chartSeries, TsData data) {
        TimeSeries chartTs = new TimeSeries("");
        for (int i = 0; i < data.getDomain().getLength(); ++i) {
            if (DescriptiveStatistics.isFinite(data.get(i))) {
                Day day = new Day(data.getDomain().get(i).middle());
                chartTs.addOrUpdate(day, data.get(i));
            }
        }
        chartSeries.addSeries(chartTs);
    }

    protected Transferable transferableOnSelection() {
        TsCollection col = TsFactory.instance.createTsCollection();
        Ts ts = TsFactory.instance.createTs("Reference serie", null, reference);
        col.add(ts);
        if (revs != null) {
            for (int i = 0; i < revs.size(); i++) {
                ts = TsFactory.instance.createTs(
                        "Rev->" + ts.getTsData().getLastPeriod().toString(), 
                        null, 
                        revs.get(i));
                col.add(ts);
            }
        }
        return TssTransferSupport.getDefault().fromTsCollection(col);
    }

    @Override
    protected void onDataFormatChange() {
        // Do nothing
    }

    @Override
    protected void onColorSchemeChange() {
        refRenderer.setBasePaint(themeSupport.getLineColor(ColorScheme.KnownColor.RED));
        seriesRenderer.setBasePaint(themeSupport.getLineColor(ColorScheme.KnownColor.BLUE));

        XYPlot mainPlot = panel.getChart().getXYPlot();
        for (int i = 0; i < mainPlot.getSeriesCount(); i++) {
            seriesRenderer.setSeriesPaint(i, themeSupport.getLineColor(ColorScheme.KnownColor.BLUE));
        }

        mainPlot.setBackgroundPaint(themeSupport.getPlotColor());
        mainPlot.setDomainGridlinePaint(themeSupport.getGridColor());
        mainPlot.setRangeGridlinePaint(themeSupport.getGridColor());
        panel.getChart().setBackgroundPaint(themeSupport.getBackColor());
    }

    private class CopyAction extends AbstractAction {

        public CopyAction() {
            super(COPY_ACTION);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            setClipboardContents(transferableOnSelection());
        }
    }
}
