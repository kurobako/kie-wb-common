/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.client.editors.expressions.types.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.Context;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.commands.expressions.types.context.AddContextEntryCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.context.DeleteContextEntryCommand;
import org.kie.workbench.common.dmn.client.commands.general.ClearExpressionTypeCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionGrid;
import org.kie.workbench.common.dmn.client.events.ExpressionEditorSelectedEvent;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDHandlersState;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDHandlersState.GridWidgetHandlersOperation;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.impl.RowSelectionStrategy;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLayerRedrawManager;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ContextGridTest {

    private final static int INSERT_ROW_ABOVE = 0;

    private final static int INSERT_ROW_BELOW = 1;

    private final static int DELETE_ROW = 2;

    private final static int DIVIDER = 3;

    private final static int CLEAR_EXPRESSION_TYPE = 4;

    @Mock
    private DMNGridPanel gridPanel;

    @Mock
    private DMNGridLayer gridLayer;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private ClientSession session;

    @Mock
    private AbstractCanvasHandler handler;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    @Mock
    private EventSourceMock<ExpressionEditorSelectedEvent> editorSelectedEvent;

    @Mock
    private CellEditorControlsView.Presenter cellEditorControls;

    @Mock
    private TranslationService translationService;

    @Mock
    private ListSelectorView.Presenter listSelector;

    @Mock
    private GridCellTuple parent;

    @Mock
    private HasExpression hasExpression;

    @Mock
    private ExpressionEditorDefinition literalExpressionEditorDefinition;

    @Mock
    private BaseExpressionGrid literalExpressionEditor;

    @Mock
    private UndefinedExpressionEditorDefinition undefinedExpressionEditorDefinition;

    @Mock
    private UndefinedExpressionGrid undefinedExpressionEditor;

    @Mock
    private GridWidgetDnDHandlersState dndHandlersState;

    @Captor
    private ArgumentCaptor<AddContextEntryCommand> addContextEntryCommandCaptor;

    @Captor
    private ArgumentCaptor<DeleteContextEntryCommand> deleteContextEntryCommandCaptor;

    @Captor
    private ArgumentCaptor<ClearExpressionTypeCommand> clearExpressionTypeCommandCaptor;

    @Captor
    private ArgumentCaptor<GridLayerRedrawManager.PrioritizedCommand> redrawCommandCaptor;

    private LiteralExpression literalExpression = new LiteralExpression();

    private Optional<HasName> hasName = Optional.empty();

    private ContextGrid grid;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        final ContextEditorDefinition definition = new ContextEditorDefinition(gridPanel,
                                                                               gridLayer,
                                                                               sessionManager,
                                                                               sessionCommandManager,
                                                                               expressionEditorDefinitionsSupplier,
                                                                               editorSelectedEvent,
                                                                               cellEditorControls,
                                                                               translationService,
                                                                               listSelector);

        final Optional<Context> expression = definition.getModelClass();
        final ExpressionEditorDefinitions expressionEditorDefinitions = new ExpressionEditorDefinitions();
        expressionEditorDefinitions.add((ExpressionEditorDefinition) definition);
        expressionEditorDefinitions.add(literalExpressionEditorDefinition);
        expressionEditorDefinitions.add(undefinedExpressionEditorDefinition);

        doReturn(expressionEditorDefinitions).when(expressionEditorDefinitionsSupplier).get();
        doReturn(parent).when(literalExpressionEditor).getParentInformation();
        doReturn(Optional.of(literalExpression)).when(literalExpressionEditorDefinition).getModelClass();
        doReturn(Optional.of(literalExpressionEditor)).when(literalExpressionEditorDefinition).getEditor(any(GridCellTuple.class),
                                                                                                         any(HasExpression.class),
                                                                                                         any(Optional.class),
                                                                                                         any(Optional.class),
                                                                                                         anyBoolean());

        doReturn(parent).when(undefinedExpressionEditor).getParentInformation();
        doReturn(Optional.empty()).when(undefinedExpressionEditorDefinition).getModelClass();
        doReturn(Optional.of(undefinedExpressionEditor)).when(undefinedExpressionEditorDefinition).getEditor(any(GridCellTuple.class),
                                                                                                             any(HasExpression.class),
                                                                                                             any(Optional.class),
                                                                                                             any(Optional.class),
                                                                                                             anyBoolean());

        doReturn(session).when(sessionManager).getCurrentSession();
        doReturn(handler).when(session).getCanvasHandler();

        this.grid = spy((ContextGrid) definition.getEditor(parent,
                                                           hasExpression,
                                                           expression,
                                                           hasName,
                                                           false).get());

        doAnswer((i) -> i.getArguments()[0].toString()).when(translationService).format(anyString());
    }

    @Test
    public void testInitialSetupFromDefinition() {
        final GridData uiModel = grid.getModel();
        assertTrue(uiModel instanceof ContextGridData);

        assertEquals(3,
                     uiModel.getColumnCount());
        assertTrue(uiModel.getColumns().get(0) instanceof RowNumberColumn);
        assertTrue(uiModel.getColumns().get(1) instanceof NameColumn);
        assertTrue(uiModel.getColumns().get(2) instanceof ExpressionEditorColumn);

        assertEquals(2,
                     uiModel.getRowCount());

        assertEquals(1,
                     uiModel.getCell(0, 0).getValue().getValue());
        assertEquals(Name.DEFAULT_NAME,
                     uiModel.getCell(0, 1).getValue().getValue());
        assertTrue(uiModel.getCell(0, 2).getValue() instanceof ExpressionCellValue);
        final ExpressionCellValue dcv0 = (ExpressionCellValue) uiModel.getCell(0, 2).getValue();
        assertEquals(undefinedExpressionEditor,
                     dcv0.getValue().get());

        assertNotNull(uiModel.getCell(1, 0));
        assertNull(uiModel.getCell(1, 0).getValue().getValue());
        assertEquals(RowSelectionStrategy.INSTANCE,
                     uiModel.getCell(1, 0).getSelectionStrategy());
        assertEquals(ContextUIModelMapper.DEFAULT_ROW_CAPTION,
                     uiModel.getCell(1, 1).getValue().getValue());
        assertTrue(uiModel.getCell(1, 2).getValue() instanceof ExpressionCellValue);
        final ExpressionCellValue dcv1 = (ExpressionCellValue) uiModel.getCell(1, 2).getValue();
        assertEquals(literalExpressionEditor,
                     dcv1.getValue().get());
    }

    @Test
    public void testRowDragPermittedNotPendingRowMove() {
        doReturn(GridWidgetHandlersOperation.NONE).when(dndHandlersState).getOperation();
        assertTrue(grid.isRowDragPermitted(dndHandlersState));
    }

    @Test
    public void testRowDragPermittedPendingRowMoveNotLastRow() {
        assertRowDragPermitted(0,
                               true);
    }

    @Test
    public void testRowDragPermittedPendingRowMoveLastRow() {
        assertRowDragPermitted(1,
                               false);
    }

    private void assertRowDragPermitted(final int uiModelRowIndex,
                                        final boolean isPermitted) {
        final List<GridRow> rows = new ArrayList<>();
        rows.add(grid.getModel().getRow(uiModelRowIndex));

        doReturn(GridWidgetHandlersOperation.ROW_MOVE_PENDING).when(dndHandlersState).getOperation();
        doReturn(rows).when(dndHandlersState).getActiveGridRows();

        assertEquals(isPermitted,
                     grid.isRowDragPermitted(dndHandlersState));
    }

    @Test
    public void testGetItemsRowNumberColumn() {
        assertDefaultListItems(grid.getItems(0, 0));
    }

    @Test
    public void testOnItemSelectedNameColumn() {
        assertDefaultListItems(grid.getItems(0, 1));
    }

    @Test
    public void testOnItemSelectedExpressionColumnUndefinedExpressionType() {
        //The default model from ContextEditorDefinition has an undefined expression at (0, 2)
        assertDefaultListItems(grid.getItems(0, 2));
    }

    @Test
    public void testOnItemSelectedExpressionColumnDefinedExpressionType() {
        //Set an editor for expression at (0, 2)
        final BaseExpressionGrid editor = mock(BaseExpressionGrid.class);
        grid.getModel().setCellValue(0, 2, new ExpressionCellValue(Optional.of(editor)));

        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 2);

        assertThat(items.size()).isEqualTo(5);
        assertDefaultListItems(items.subList(0, 3));

        assertThat(items.get(DIVIDER)).isInstanceOf(HasListSelectorControl.ListSelectorDividerItem.class);
        assertListSelectorItem(items.get(CLEAR_EXPRESSION_TYPE),
                               DMNEditorConstants.ExpressionEditor_Clear);

        ((HasListSelectorControl.ListSelectorTextItem) items.get(CLEAR_EXPRESSION_TYPE)).getCommand().execute();
        verify(cellEditorControls).hide();
        verify(sessionCommandManager).execute(eq(handler),
                                              any(ClearExpressionTypeCommand.class));
    }

    private void assertDefaultListItems(final List<HasListSelectorControl.ListSelectorItem> items) {
        assertThat(items.size()).isEqualTo(3);
        assertListSelectorItem(items.get(INSERT_ROW_ABOVE),
                               DMNEditorConstants.ContextEditor_InsertContextEntryAbove);
        assertListSelectorItem(items.get(INSERT_ROW_BELOW),
                               DMNEditorConstants.ContextEditor_InsertContextEntryBelow);
        assertListSelectorItem(items.get(DELETE_ROW),
                               DMNEditorConstants.ContextEditor_DeleteContextEntry);
    }

    private void assertListSelectorItem(final HasListSelectorControl.ListSelectorItem item,
                                        final String text) {
        assertThat(item).isInstanceOf(HasListSelectorControl.ListSelectorTextItem.class);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) item;
        assertThat(ti.getText()).isEqualTo(text);
    }

    @Test
    public void testOnItemSelected() {
        final Command command = mock(Command.class);
        final HasListSelectorControl.ListSelectorTextItem listSelectorItem = mock(HasListSelectorControl.ListSelectorTextItem.class);
        when(listSelectorItem.getCommand()).thenReturn(command);

        grid.onItemSelected(listSelectorItem);

        verify(command).execute();
    }

    @Test
    public void testOnItemSelectedInsertRowAbove() {
        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(INSERT_ROW_ABOVE);

        grid.onItemSelected(ti);

        verify(cellEditorControls).hide();
        verify(grid).addContextEntry(eq(0));
    }

    @Test
    public void testOnItemSelectedInsertRowBelow() {
        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(INSERT_ROW_BELOW);

        grid.onItemSelected(ti);

        verify(cellEditorControls).hide();
        verify(grid).addContextEntry(eq(1));
    }

    @Test
    public void testOnItemSelectedDeleteRow() {
        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(DELETE_ROW);

        grid.onItemSelected(ti);

        verify(cellEditorControls).hide();
        verify(grid).deleteContextEntry(eq(0));
    }

    @Test
    public void testOnItemSelectedDeleteRowEnabled() {
        //Grid has two rows from Context model. Neither can be deleted.
        assertDeleteRowEnabled(0, false);
        assertLastRowHasNoListItems();

        //Grid has three rows. Rows 1 and 2 can be deleted.
        grid.getModel().appendRow(new BaseGridRow());
        assertDeleteRowEnabled(0, true);
        assertDeleteRowEnabled(1, true);
        assertLastRowHasNoListItems();
    }

    private void assertDeleteRowEnabled(final int uiRowIndex, final boolean enabled) {
        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(uiRowIndex, 0);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(DELETE_ROW);
        assertThat(ti.isEnabled()).isEqualTo(enabled);
    }

    private void assertLastRowHasNoListItems() {
        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(grid.getModel().getRowCount() - 1, 0);
        assertThat(items).isEmpty();
    }

    @Test
    public void testAddContextEntry() {
        grid.addContextEntry(0);

        verify(sessionCommandManager).execute(eq(handler),
                                              addContextEntryCommandCaptor.capture());

        final AddContextEntryCommand addContextEntryCommand = addContextEntryCommandCaptor.getValue();
        addContextEntryCommand.execute(handler);

        verify(parent).onResize();
        verify(gridPanel).refreshScrollPosition();
        verify(gridPanel).updatePanelSize();
        verify(gridLayer).batch(redrawCommandCaptor.capture());

        final GridLayerRedrawManager.PrioritizedCommand redrawCommand = redrawCommandCaptor.getValue();
        redrawCommand.execute();

        verify(gridLayer).draw();
    }

    @Test
    public void testDeleteContextEntry() {
        grid.deleteContextEntry(0);

        verify(sessionCommandManager).execute(eq(handler),
                                              deleteContextEntryCommandCaptor.capture());

        final DeleteContextEntryCommand deleteContextEntryCommand = deleteContextEntryCommandCaptor.getValue();
        deleteContextEntryCommand.execute(handler);

        verify(parent).onResize();
        verify(gridPanel).refreshScrollPosition();
        verify(gridPanel).updatePanelSize();
        verify(gridLayer).batch(redrawCommandCaptor.capture());

        final GridLayerRedrawManager.PrioritizedCommand redrawCommand = redrawCommandCaptor.getValue();
        redrawCommand.execute();

        verify(gridLayer).draw();
    }

    @Test
    public void testClearExpressionType() {
        grid.clearExpressionType(0);

        verify(sessionCommandManager).execute(eq(handler),
                                              clearExpressionTypeCommandCaptor.capture());

        final ClearExpressionTypeCommand clearExpressionTypeCommand = clearExpressionTypeCommandCaptor.getValue();
        clearExpressionTypeCommand.execute(handler);

        verify(parent).onResize();
        verify(gridPanel).refreshScrollPosition();
        verify(gridPanel).updatePanelSize();
        verify(gridLayer).batch(redrawCommandCaptor.capture());

        final GridLayerRedrawManager.PrioritizedCommand redrawCommand = redrawCommandCaptor.getValue();
        redrawCommand.execute();

        verify(gridLayer).draw();
    }
}
