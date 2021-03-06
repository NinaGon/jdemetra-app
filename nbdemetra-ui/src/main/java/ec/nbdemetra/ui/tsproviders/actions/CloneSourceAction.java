/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.tsproviders.actions;

import ec.nbdemetra.ui.nodes.SingleNodeAction;
import ec.nbdemetra.ui.tsproviders.DataSourceNode;
import ec.nbdemetra.ui.tsproviders.DataSourceProviderBuddySupport;
import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.IDataSourceLoader;
import ec.tss.tsproviders.TsProviders;
import java.beans.IntrospectionException;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle.Messages;
import org.slf4j.LoggerFactory;

@ActionID(category = "Edit", id = "ec.nbdemetra.ui.nodes.actions.CloneSourceAction")
@ActionRegistration(displayName = "#CTL_CloneSourceAction", lazy = false)
@ActionReferences({
    @ActionReference(path = DataSourceNode.ACTION_PATH, position = 1320, separatorBefore = 1300)
})
@Messages("CTL_CloneSourceAction=Clone")
public final class CloneSourceAction extends SingleNodeAction<DataSourceNode> {

    public CloneSourceAction() {
        super(DataSourceNode.class);
    }

    @Override
    protected void performAction(DataSourceNode activatedNode) {
        try {
            clone(activatedNode.getLookup().lookup(DataSource.class));
        } catch (IntrospectionException ex) {
            LoggerFactory.getLogger(CloneSourceAction.class).error("While cloning", ex);
        }
    }

    static DataSource clone(DataSource dataSource) throws IntrospectionException {
        IDataSourceLoader loader = TsProviders.lookup(IDataSourceLoader.class, dataSource).get();
        final Object bean = loader.decodeBean(dataSource);
        if (DataSourceProviderBuddySupport.getDefault().get(loader).editBean("Clone data source", bean)) {
            DataSource newDataSource = loader.encodeBean(bean);
            return loader.open(newDataSource) ? newDataSource : null;
        }
        return null;
    }

    @Override
    protected boolean enable(DataSourceNode activatedNode) {
        return TsProviders.lookup(IDataSourceLoader.class, activatedNode.getLookup().lookup(DataSource.class)).isPresent();
    }

    @Override
    public String getName() {
        return Bundle.CTL_CloneSourceAction();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }
}
