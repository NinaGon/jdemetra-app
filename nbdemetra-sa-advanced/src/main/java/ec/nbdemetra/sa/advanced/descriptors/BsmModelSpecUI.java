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

package ec.nbdemetra.sa.advanced.descriptors;

import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.descriptors.IPropertyDescriptors;
import ec.tstoolkit.structural.BsmSpecification;
import ec.tstoolkit.structural.ComponentUse;
import ec.tstoolkit.structural.ModelSpecification;
import ec.tstoolkit.structural.SeasonalModel;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean Palate
 */
public class BsmModelSpecUI implements IPropertyDescriptors {

    final ModelSpecification core;

    public BsmModelSpecUI(ModelSpecification spec) {
        core = spec;
    }

    public ComponentUse getLevel() {
        return core.getLevelUse();
    }

    public void setLevel(ComponentUse use) {
        core.useLevel(use);
    }

    public ComponentUse getSlope() {
        return core.getSlopeUse();
    }

    public void setSlope(ComponentUse use) {
        core.useSlope(use);
    }

    public ComponentUse getNoise() {
        return core.getNoiseUse();
    }

    public void setNoise(ComponentUse use) {
        core.useNoise(use);
    }

    public SeasonalModel getModel() {
        return core.getSeasonalModel();
    }

    public void setModel(SeasonalModel model) {
        core.setSeasonalModel(model);
    }


    private EnhancedPropertyDescriptor lDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("level", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, L_ID);
            desc.setDisplayName(L_NAME);
            desc.setShortDescription(L_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor sDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("slope", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, S_ID);
            desc.setDisplayName(S_NAME);
            desc.setShortDescription(S_DESC);
            edesc.setReadOnly(getLevel() == ComponentUse.Unused);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor smDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("model", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SM_ID);
            desc.setDisplayName(SM_NAME);
            desc.setShortDescription(SM_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor nDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("noise", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, N_ID);
            desc.setDisplayName(N_NAME);
            desc.setShortDescription(N_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = lDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = sDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = nDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = smDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }
    public static final int L_ID = 0, S_ID = 1, N_ID = 2, SM_ID = 3;
    public static final String L_NAME = "Level",
            S_NAME = "Slope",
            N_NAME = "Noise",
            SM_NAME = "Seasonal model";
    public static final String L_DESC = "Level",
            S_DESC = "Slope",
            N_DESC = "Noise",
            SM_DESC = "Seasonal model";

    public String getDisplayName() {
        return "Basic structural model";
    }
}
