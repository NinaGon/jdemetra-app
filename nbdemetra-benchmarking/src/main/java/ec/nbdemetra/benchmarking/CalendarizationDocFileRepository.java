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
package ec.nbdemetra.benchmarking;

import ec.nbdemetra.ws.DefaultFileItemRepository;
import ec.nbdemetra.ws.IWorkspaceItemRepository;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tss.disaggregation.documents.CalendarizationDocument;
import ec.tstoolkit.MetaData;
import java.util.Date;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Mats Maggi
 */
@ServiceProvider(service = IWorkspaceItemRepository.class)
public final class CalendarizationDocFileRepository extends DefaultFileItemRepository<CalendarizationDocument> {

    public static final String REPOSITORY = "CalendarizationDoc";
    
    @Override
    public String getRepository() {
        return REPOSITORY;
    }

    @Override
    public Class<CalendarizationDocument> getSupportedType() {
        return CalendarizationDocument.class;
    }
    
    @Override
    public boolean save(WorkspaceItem<CalendarizationDocument> doc) {
        CalendarizationDocument element = doc.getElement();
        element.getMetaData().put(MetaData.DATE, new Date().toString());
        return super.save(doc);
    }

}
