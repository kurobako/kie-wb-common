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

package org.kie.workbench.common.stunner.bpmn.backend.converters.events;

import java.util.List;

import org.eclipse.bpmn2.CancelEventDefinition;
import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.EscalationEventDefinition;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.TerminateEventDefinition;
import org.kie.workbench.common.stunner.bpmn.backend.converters.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Match;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.EventPropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BaseEndEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.ErrorEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.ErrorRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.ScopedSignalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalScope;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class EndEventConverter {

    private final TypedFactoryManager factoryManager;
    private final PropertyReaderFactory propertyReaderFactory;

    public EndEventConverter(TypedFactoryManager factoryManager, PropertyReaderFactory propertyReaderFactory) {
        this.factoryManager = factoryManager;
        this.propertyReaderFactory = propertyReaderFactory;
    }

    public Node<? extends View<? extends BPMNViewDefinition>, ?> convert(EndEvent event) {
        List<EventDefinition> eventDefinitions = event.getEventDefinitions();
        String nodeId = event.getId();

        switch (eventDefinitions.size()) {
            case 0: {
                Node<View<EndNoneEvent>, Edge> node = factoryManager.newNode(nodeId, EndNoneEvent.class);
                EndNoneEvent definition = node.getContent().getDefinition();
                EventPropertyReader p = propertyReaderFactory.of(event);

                definition.setGeneral(new BPMNGeneralSet(
                        new Name(p.getName()),
                        new Documentation(p.getDocumentation())
                ));

                node.getContent().setBounds(p.getBounds());

                definition.setDimensionsSet(p.getCircleDimensionSet());
                definition.setFontSet(p.getFontSet());
                definition.setBackgroundSet(p.getBackgroundSet());

                return node;
            }
            case 1:
                return Match.ofNode(EventDefinition.class, BaseEndEvent.class)
                        .when(TerminateEventDefinition.class, e -> {
                            Node<View<EndTerminateEvent>, Edge> node = factoryManager.newNode(nodeId, EndTerminateEvent.class);

                            EndTerminateEvent definition = node.getContent().getDefinition();
                            EventPropertyReader p = propertyReaderFactory.of(event);

                            definition.setGeneral(new BPMNGeneralSet(
                                    new Name(p.getName()),
                                    new Documentation(p.getDocumentation())
                            ));

                            node.getContent().setBounds(p.getBounds());

                            definition.setDimensionsSet(p.getCircleDimensionSet());
                            definition.setFontSet(p.getFontSet());
                            definition.setBackgroundSet(p.getBackgroundSet());

                            return node;
                        })
                        .when(SignalEventDefinition.class, e -> {
                            Node<View<EndSignalEvent>, Edge> node = factoryManager.newNode(nodeId, EndSignalEvent.class);

                            EndSignalEvent definition = node.getContent().getDefinition();
                            EventPropertyReader p = propertyReaderFactory.of(event);

                            definition.setGeneral(new BPMNGeneralSet(
                                    new Name(p.getName()),
                                    new Documentation(p.getDocumentation())
                            ));

                            definition.setDataIOSet(new DataIOSet(
                                    new AssignmentsInfo(p.getAssignmentsInfo())
                            ));

                            definition.setExecutionSet(new ScopedSignalEventExecutionSet(
                                    new SignalRef(p.getSignalRef()),
                                    new SignalScope(p.getSignalScope())
                            ));

                            node.getContent().setBounds(p.getBounds());

                            definition.setDimensionsSet(p.getCircleDimensionSet());
                            definition.setFontSet(p.getFontSet());
                            definition.setBackgroundSet(p.getBackgroundSet());

                            return node;
                        })
                        .when(MessageEventDefinition.class, e -> {
                            Node<View<EndMessageEvent>, Edge> node = factoryManager.newNode(nodeId, EndMessageEvent.class);

                            EndMessageEvent definition = node.getContent().getDefinition();
                            EventPropertyReader p = propertyReaderFactory.of(event);

                            definition.setGeneral(new BPMNGeneralSet(
                                    new Name(p.getName()),
                                    new Documentation(p.getDocumentation())
                            ));

                            definition.setDataIOSet(new DataIOSet(
                                    new AssignmentsInfo(p.getAssignmentsInfo())
                            ));

                            definition.setExecutionSet(new MessageEventExecutionSet(
                                    new MessageRef(e.getMessageRef().getName())
                            ));
                            node.getContent().setBounds(p.getBounds());

                            definition.setDimensionsSet(p.getCircleDimensionSet());
                            definition.setFontSet(p.getFontSet());
                            definition.setBackgroundSet(p.getBackgroundSet());

                            return node;
                        })
                        .when(ErrorEventDefinition.class, e -> {
                            Node<View<EndErrorEvent>, Edge> node = factoryManager.newNode(nodeId, EndErrorEvent.class);

                            EndErrorEvent definition = node.getContent().getDefinition();
                            EventPropertyReader p = propertyReaderFactory.of(event);

                            definition.setGeneral(new BPMNGeneralSet(
                                    new Name(p.getName()),
                                    new Documentation(p.getDocumentation())
                            ));

                            definition.setDataIOSet(new DataIOSet(
                                    new AssignmentsInfo(p.getAssignmentsInfo())
                            ));

                            definition.setExecutionSet(new ErrorEventExecutionSet(
                                    new ErrorRef(e.getErrorRef().getErrorCode())
                            ));

                            node.getContent().setBounds(p.getBounds());

                            definition.setDimensionsSet(p.getCircleDimensionSet());
                            definition.setFontSet(p.getFontSet());
                            definition.setBackgroundSet(p.getBackgroundSet());

                            return node;
                        })
                        .missing(EscalationEventDefinition.class)
                        .missing(CompensateEventDefinition.class)
                        .missing(CancelEventDefinition.class)
                        .apply(eventDefinitions.get(0)).asSuccess().value();
            default:
                throw new UnsupportedOperationException("Multiple event definitions not supported for end event");
        }
    }
}
