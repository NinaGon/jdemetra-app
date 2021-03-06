/*
 * Copyright 2013 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
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
package ec.nbdemetra.ui.tsaction;

import com.google.common.base.Optional;
import ec.nbdemetra.ui.MonikerUI;
import ec.nbdemetra.ui.NbComponents;
import ec.nbdemetra.ui.ns.AbstractNamedService;
import ec.nbdemetra.ui.tools.ChartTopComponent;
import ec.nbdemetra.ui.tools.GridTopComponent;
import ec.nbdemetra.ui.tsproviders.DataSourceProviderBuddySupport;
import ec.tss.Ts;
import ec.tss.TsMoniker;
import ec.tss.tsproviders.DataSet;
import ec.tss.tsproviders.IDataSourceProvider;
import ec.tss.tsproviders.TsProviders;
import ec.ui.interfaces.ITsChart.LinesThickness;
import ec.ui.interfaces.ITsCollectionView.TsUpdateMode;
import ec.ui.interfaces.ITsGrid;
import java.awt.Image;
import java.beans.BeanInfo;
import java.io.Serializable;
import javax.swing.Icon;
import org.netbeans.core.spi.multiview.*;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = ITsAction.class)
public class ChartGridTsAction extends AbstractNamedService implements ITsAction {

    public static final String NAME = "ChartGridTsAction";

    public ChartGridTsAction() {
        super(ITsAction.class, NAME);
    }

    @Override
    public String getDisplayName() {
        return "Chart & grid";
    }

    @Override
    public void open(Ts ts) {
        String name = NAME + ts.getMoniker().toString();
        TopComponent c = NbComponents.findTopComponentByName(name);
        if (c == null) {
            MultiViewDescription[] descriptions = {new ChartTab(ts), new GridTab(ts)};
            c = MultiViewFactory.createMultiView(descriptions, descriptions[0], null);
            c.setName(name);

            Optional<IDataSourceProvider> provider = TsProviders.lookup(IDataSourceProvider.class, ts.getMoniker());
            if (provider.isPresent()) {
                DataSet dataSet = provider.get().toDataSet(ts.getMoniker());
                if (dataSet != null) {
                    c.setIcon(getIcon(ts.getMoniker()));
                    c.setDisplayName(provider.get().getDisplayNodeName(dataSet));
                }
            } else {
                c.setDisplayName(ts.getName());
            }
            c.open();
        }
        c.requestActive();
    }

    private static Image getIcon(TsMoniker moniker) {
        return DataSourceProviderBuddySupport.getDefault().get(moniker).getIcon(moniker, BeanInfo.ICON_COLOR_16x16, false);
    }

    static class ChartTab implements MultiViewDescription, Serializable {

        final Ts ts;

        public ChartTab(Ts ts) {
            this.ts = ts;
        }

        @Override
        public int getPersistenceType() {
            return TopComponent.PERSISTENCE_NEVER;
        }

        @Override
        public String getDisplayName() {
            return "Chart";
        }

        @Override
        public Image getIcon() {
            Icon icon = MonikerUI.getDefault().getIcon(ts.getMoniker());
            return icon != null ? ImageUtilities.icon2Image(icon) : null;
        }

        @Override
        public HelpCtx getHelpCtx() {
            return null;
        }

        @Override
        public String preferredID() {
            return "Chart";
        }

        @Override
        public MultiViewElement createElement() {
            ChartTopComponent result = new ChartTopComponent();
            result.getChart().getTsCollection().add(ts);
            result.getChart().setTsUpdateMode(TsUpdateMode.None);
            result.getChart().setLegendVisible(true);
            result.getChart().setTitleVisible(false);
            result.getChart().setLinesThickness(LinesThickness.Thick);
            return result;
        }
    }

    static class GridTab implements MultiViewDescription, Serializable {

        final Ts ts;

        public GridTab(Ts ts) {
            this.ts = ts;
        }

        @Override
        public int getPersistenceType() {
            return TopComponent.PERSISTENCE_NEVER;
        }

        @Override
        public String getDisplayName() {
            return "Grid";
        }

        @Override
        public Image getIcon() {
            Icon icon = MonikerUI.getDefault().getIcon(ts.getMoniker());
            return icon != null ? ImageUtilities.icon2Image(icon) : null;
        }

        @Override
        public HelpCtx getHelpCtx() {
            return null;
        }

        @Override
        public String preferredID() {
            return "Grid";
        }

        @Override
        public MultiViewElement createElement() {
            GridTopComponent result = new GridTopComponent();
            result.getGrid().getTsCollection().add(ts);
            result.getGrid().setTsUpdateMode(TsUpdateMode.None);
            result.getGrid().setMode(ITsGrid.Mode.SINGLETS);
            return result;
        }
    }
}
