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
package ec.util.grid.swing;

import static ec.util.various.swing.ModernUI.withEmptyBorders;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import javax.swing.JComponent;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.OverlayLayout;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * A grid component for Swing that differs from a JTable by adding a row header.
 *
 * @author Jeremy Demortier
 * @author Philippe Charles
 * @author Mats Maggi
 */
public class JGrid extends JComponent {

    final JScrollPane scrollPane;
    final XTable noDataPanel;
    final XTable main;
    final FixedColumnTable fct;
    final InternalTableModel internalModel;
    static final int COLUMN_WIDTH;
    static final int ROW_HEIGTH;
    float zoomRatio = 1.0f;
    static final float FONT_SIZE;

    static {
        // Setting constants used to implement zoom
        JTable t = new JTable();
        COLUMN_WIDTH = new TableColumn().getPreferredWidth();
        ROW_HEIGTH = t.getRowHeight();
        FONT_SIZE = t.getFont().getSize2D();
    }

    public JGrid() {
        setLayout(new OverlayLayout(this));

        this.scrollPane = withEmptyBorders(new JScrollPane());
        scrollPane.setVisible(false);
        add(scrollPane);

        this.noDataPanel = new XTable();
        add(withEmptyBorders(new JScrollPane(noDataPanel)));

        this.internalModel = new InternalTableModel();
        internalModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
//                fct.getFixedTable().getColumnModel().getColumn(0).setWidth(300);
                boolean tmp = internalModel.hasData();
                scrollPane.setVisible(tmp);
                noDataPanel.setVisible(!tmp);
            }
        });

        this.main = new XTable();
        main.setModel(internalModel);
        main.setNoDataRenderer(new XTable.DefaultNoDataRenderer("", ""));

        // Adding resize support of newly added columns
        main.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    Enumeration<TableColumn> cols = main.getTableHeader().getColumnModel().getColumns();
                    while (cols.hasMoreElements()) {
                        TableColumn c = cols.nextElement();
                        c.setPreferredWidth((int) (COLUMN_WIDTH * zoomRatio));
                    }
                }
            }
        });

        // This makes sure user can drop on all component and not only on present cell
        main.setFillsViewportHeight(true);

        main.getTableHeader().setReorderingAllowed(false);
        main.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // click on header select the column
        main.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int selection = main.getTableHeader().columnAtPoint(e.getPoint());
                main.getColumnModel().getSelectionModel().setSelectionInterval(selection, selection);
            }
        });
        addDragOnHeader(main.getTableHeader());

        scrollPane.setViewportView(main);

        // This makes the viewport background same as table background
        // http://www.jroller.com/santhosh/date/20050524#jtable_becomes_uglier_with_auto
        scrollPane.getViewport().setBackground(main.getBackground());

        // This splits the original table into two distinct tables. The first
        // one has only one column used for the headers,
        // while the other has all the remaining columns. They share the same
        // model but not the columns model!
        fct = new FixedColumnTable(1, scrollPane);
        fct.getFixedTable().getTableHeader().setReorderingAllowed(false);
        fct.getFixedTable().setFillsViewportHeight(true);

        // click on header select/unselect all the columns
        fct.getFixedTable().getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                ListSelectionModel columnSelectionModel = main.getColumnModel().getSelectionModel();
                ListSelectionModel rowSelectionModel = main.getSelectionModel();
                int columnCount = main.getColumnCount();
                int rowCount = main.getRowCount();

                if (columnCount == getCount(columnSelectionModel) || rowCount == getCount(rowSelectionModel)) {
                    columnSelectionModel.clearSelection();
                    rowSelectionModel.clearSelection();
                } else {
                    columnSelectionModel.setSelectionInterval(0, columnCount - 1);
                    rowSelectionModel.setSelectionInterval(0, rowCount - 1);
                }
            }
        });
        addDragOnHeader(fct.getFixedTable().getTableHeader());

        Color newGridColor = UIManager.getColor("control");
        if (newGridColor != null) {
            main.setGridColor(newGridColor);
            // fct.getFixedTable().setGridColor(newGridColor);
        }
        setRowRenderer(new GridRowHeaderRenderer());

        // InputMap and ActionMap
        //setActionMap(main.getActionMap());
        setInputMap(WHEN_FOCUSED, main.getInputMap(WHEN_FOCUSED));
        setInputMap(WHEN_IN_FOCUSED_WINDOW, main.getInputMap(WHEN_IN_FOCUSED_WINDOW));
        setInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, main.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT));
    }

    // setFont() method now scales all elements when the font has the size changed
    @Override
    public void setFont(Font font) {
        super.setFont(font);

        zoomRatio = font.getSize2D() / FONT_SIZE;
        main.setRowHeight((int) (ROW_HEIGTH * zoomRatio));
        fct.getFixedTable().setRowHeight(main.getRowHeight());

        // Resize of data columns according to the zoom ratio
        Enumeration<TableColumn> cols = main.getTableHeader().getColumnModel().getColumns();
        while (cols.hasMoreElements()) {
            TableColumn c = cols.nextElement();
            c.setPreferredWidth((int) (COLUMN_WIDTH * zoomRatio));
        }

        // Resize of row headers according to the zoom ratio
        JTable j = fct.getFixedTable();
        Dimension dim = new Dimension((int) (COLUMN_WIDTH * zoomRatio), (int) (ROW_HEIGTH * zoomRatio));
        j.setPreferredScrollableViewportSize(dim);
        j.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth((int) (COLUMN_WIDTH * zoomRatio));

        // Resize of the fonts according to the zoom ratio
        noDataPanel.setFont(font);
        main.setFont(font);
        main.getTableHeader().setFont(font);
        fct.getFixedTable().setFont(font);
    }

    public void setDragEnabled(boolean dragEnabled) {
        main.setDragEnabled(dragEnabled);
        fct.getFixedTable().setDragEnabled(dragEnabled);
    }

    @Override
    public void setTransferHandler(TransferHandler newHandler) {
        super.setTransferHandler(newHandler);
        main.setTransferHandler(newHandler);
        main.getTableHeader().setTransferHandler(newHandler);
        fct.getFixedTable().setTransferHandler(newHandler);
        fct.getFixedTable().getTableHeader().setTransferHandler(newHandler);
        noDataPanel.setTransferHandler(newHandler);
    }

    public void setRowHeight(int rowHeight) {
        main.setRowHeight(rowHeight);
    }

    public int getRowHeight() {
        return main.getRowHeight();
    }

    public JTableHeader getTableHeader() {
        return main.getTableHeader();
    }

    public TableCellRenderer getDefaultRenderer(Class<?> aClass) {
        return main.getDefaultRenderer(aClass);
    }

    public void setDefaultRenderer(Class<?> aClass, TableCellRenderer tableCellRenderer) {
        main.setDefaultRenderer(aClass, tableCellRenderer);
    }

    public void setColumnRenderer(TableCellRenderer renderer) {
        main.getTableHeader().setDefaultRenderer(renderer);
    }

    public void setRowRenderer(TableCellRenderer renderer) {
        fct.getFixedTable().setDefaultRenderer(Object.class, renderer);
    }

    public XTable.NoDataRenderer getNoDataRenderer() {
        return this.noDataPanel.getNoDataRenderer();
    }

    public void setNoDataRenderer(XTable.NoDataRenderer renderer) {
        this.noDataPanel.setNoDataRenderer(renderer);
    }

    public ListSelectionModel getSelectionModel() {
        return main.getSelectionModel();
    }

    public TableColumnModel getColumnModel() {
        return main.getColumnModel();
    }

    public GridModel getModel() {
        return internalModel.getGridModel();
    }

    public void setModel(GridModel model) {
        internalModel.setGridModel(model);
    }

    @Override
    public synchronized void addMouseListener(MouseListener l) {
        super.addMouseListener(l);
        main.addMouseListener(l);
        noDataPanel.addMouseListener(l);
    }

    @Override
    public synchronized void removeMouseListener(MouseListener l) {
        noDataPanel.removeMouseListener(l);
        main.removeMouseListener(l);
        super.removeMouseListener(l);
    }

    public void setRowSelectionAllowed(boolean rowSelectionAllowed) {
        main.setRowSelectionAllowed(rowSelectionAllowed);
    }

    public void setColumnSelectionAllowed(boolean columnSelectionAllowed) {
        main.setColumnSelectionAllowed(columnSelectionAllowed);
    }

    public int[] getSelectedColumns() {
        return main.getSelectedColumns();
    }

    public int[] getSelectedRows() {
        return main.getSelectedRows();
    }

    public void setOddBackground(Color oddBackground) {
        main.setOddBackground(oddBackground);
    }

    public void setGridColor(Color gridColor) {
        main.setGridColor(gridColor);
    }

    static class InternalTableModel extends AbstractTableModel implements TableModelListener {

        GridModel gridModel;

        InternalTableModel() {
            this.gridModel = GridModels.empty();
        }

        public void setGridModel(GridModel gridModel) {
            this.gridModel.removeTableModelListener(this);
            this.gridModel = gridModel != null ? gridModel : GridModels.empty();
            this.gridModel.addTableModelListener(this);
            fireTableStructureChanged();
        }

        public GridModel getGridModel() {
            return gridModel;
        }

        public boolean hasData() {
            return gridModel.getRowCount() > 0 || gridModel.getColumnCount() > 0;
        }

        @Override
        public int getRowCount() {
            return gridModel.getRowCount();
        }

        @Override
        public int getColumnCount() {
            return gridModel.getColumnCount() + 1;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return columnIndex == 0 ? gridModel.getRowName(rowIndex) : gridModel.getValueAt(rowIndex, columnIndex - 1);
        }

        @Override
        public String getColumnName(int columnIndex) {
            return columnIndex == 0 ? null : gridModel.getColumnName(columnIndex - 1);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 0 ? String.class : gridModel.getColumnClass(columnIndex - 1);
        }

        @Override
        public void tableChanged(TableModelEvent e) {
            fireTableChanged(e);
        }
    }

    static void addDragOnHeader(final JTableHeader tableHeader) {
        DragSource dragSource = DragSource.getDefaultDragSource();
        dragSource.createDefaultDragGestureRecognizer(tableHeader, DnDConstants.ACTION_COPY_OR_MOVE, new DragGestureListener() {
            @Override
            public void dragGestureRecognized(DragGestureEvent dge) {
                TransferHandler transferHandler = tableHeader.getTransferHandler();
                if (transferHandler != null) {
                    transferHandler.exportAsDrag(tableHeader, dge.getTriggerEvent(), TransferHandler.COPY);
                }
            }
        });
    }

    private static int getCount(ListSelectionModel m) {
        return !m.isSelectionEmpty() ? (m.getMaxSelectionIndex() - m.getMinSelectionIndex() + 1) : 0;
    }
}
