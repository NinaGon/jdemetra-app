/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import ec.tstoolkit.algorithm.IProcDocument;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.InformationExtractor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * @author pcuser
 */
public interface IProcDocumentViewFactory<D extends IProcDocument> {

    @Nonnull
    IProcDocumentView<D> create(@Nonnull D document);

    @Deprecated
    <I> void register(
            @Nonnull Id id,
            @Nullable InformationExtractor<? super D, I> info,
            @Nonnull ItemUI<? extends IProcDocumentView<D>, I> ui);

    @Deprecated
    void unregister(@Nonnull Id id);
}
