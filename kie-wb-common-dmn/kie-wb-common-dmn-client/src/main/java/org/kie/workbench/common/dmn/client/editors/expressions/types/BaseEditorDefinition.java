/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions.types;

import javax.enterprise.event.Event;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.client.events.ExpressionEditorSelectedEvent;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;

public abstract class BaseEditorDefinition<T extends Expression> implements ExpressionEditorDefinition<T> {

    protected DMNGridPanel gridPanel;
    protected DMNGridLayer gridLayer;
    protected SessionManager sessionManager;
    protected SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    protected Event<ExpressionEditorSelectedEvent> editorSelectedEvent;
    protected CellEditorControlsView.Presenter cellEditorControls;
    protected TranslationService translationService;

    public BaseEditorDefinition() {
        //CDI proxy
    }

    public BaseEditorDefinition(final DMNGridPanel gridPanel,
                                final DMNGridLayer gridLayer,
                                final SessionManager sessionManager,
                                final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                final Event<ExpressionEditorSelectedEvent> editorSelectedEvent,
                                final CellEditorControlsView.Presenter cellEditorControls,
                                final TranslationService translationService) {
        this.gridPanel = gridPanel;
        this.gridLayer = gridLayer;
        this.sessionManager = sessionManager;
        this.sessionCommandManager = sessionCommandManager;
        this.editorSelectedEvent = editorSelectedEvent;
        this.cellEditorControls = cellEditorControls;
        this.translationService = translationService;
    }
}
