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
package ec.nbdemetra.ui.tsproviders.actions;

import com.google.common.base.Optional;
import ec.nbdemetra.ui.tsproviders.ProvidersNode;
import ec.tss.datatransfer.DataSourceTransferSupport;
import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.IDataSourceLoader;
import ec.tss.tsproviders.TsProviders;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.NodeAction;

@ActionID(category = "Edit", id = "ec.nbdemetra.ui.tsproviders.actions.PasteProvidersAction")
@ActionRegistration(displayName = "#CTL_PasteProvidersAction", lazy = false)
@ActionReferences({
    @ActionReference(path = ProvidersNode.ACTION_PATH, position = 1410, separatorBefore = 1400)
})
@Messages("CTL_PasteProvidersAction=Paste")
public final class PasteProvidersAction extends NodeAction {

    @Override
    protected void performAction(Node[] activatedNodes) {
        Optional<DataSource> dataSource = DataSourceTransferSupport.getDefault().getDataSource(getTransferable(activatedNodes));
        if (dataSource.isPresent()) {
            Optional<IDataSourceLoader> loader = TsProviders.lookup(IDataSourceLoader.class, dataSource.get());
            if (loader.isPresent()) {
                loader.get().open(dataSource.get());
            }
        }
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        return DataSourceTransferSupport.getDefault().canHandle(getTransferable(activatedNodes));
    }

    @Override
    public String getName() {
        return Bundle.CTL_PasteProvidersAction();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

    private static Transferable getTransferable(Node[] requestor) {
        return Toolkit.getDefaultToolkit().getSystemClipboard().getContents(requestor);
    }
}
