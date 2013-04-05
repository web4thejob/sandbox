/*
 * Copyright (c) 2013 Veniamin Isaias
 *
 * This file is part of web4thejob-sandbox.
 *
 * Web4thejob-sandbox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Web4thejob-sandbox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with web4thejob-sandbox.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.web4thejob.sandbox.web.panel;

import nu.xom.Element;
import org.springframework.context.annotation.Scope;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.orm.ORMUtil;
import org.web4thejob.orm.PanelDefinition;
import org.web4thejob.util.XMLUtil;
import org.web4thejob.web.panel.DefaultMenuAuthorizationPanel;
import org.web4thejob.web.panel.base.zk.AbstractZkContentPanel;
import org.web4thejob.web.util.ZkUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zul.A;
import org.zkoss.zul.Vbox;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@org.springframework.stereotype.Component
@Scope("prototype")
public class DefaultChapterPanel extends AbstractZkContentPanel implements ChapterPanel, EventListener<MouseEvent> {
    private Vbox vbox = new Vbox();
    private Element chapter;

    public DefaultChapterPanel() {
        ZkUtil.setParentOfChild((Component) base, vbox);
        vbox.setHflex("true");
        vbox.setVflex("true");
        vbox.setSpacing("10px");
    }

    @Override
    public Element getChapter() {
        return this.chapter;
    }

    @Override
    public void setChapter(Element chapter) {
        this.chapter = chapter;
        vbox.getChildren().clear();

        for (int i = 0; i < chapter.getChildElements().size(); i++) {
            Element element = chapter.getChildElements().get(i);
            A a = new A(describeElement(element));
            a.setParent(vbox);
            a.setAttribute("element", element);
            a.addEventListener(Events.ON_CLICK, this);
        }

    }

    @Override
    public String toString() {
        if (this.chapter != null) {
            return XMLUtil.getTextualValue(chapter);
        }
        return super.toString();
    }

    private String describeElement(Element element) {
        if (DefaultMenuAuthorizationPanel.ELEMENT_MENU.equals(element.getLocalName())) {
            return XMLUtil.getTextualValue(element);
        } else if (DefaultMenuAuthorizationPanel.ELEMENT_PANEL.equals(element.getLocalName())) {
            PanelDefinition panelDefinition = ORMUtil.getPanelDefinition(XMLUtil.getTextualValue(element));
            return panelDefinition.getName();
        }

        return null;
    }

    @Override
    public void onEvent(MouseEvent event) throws Exception {
        if (event.getTarget().getAttribute("element") instanceof Element) {
            dispatchMessage(ContextUtil.getMessage(MessageEnum.EXECUTE_SECURED_RESOURCE, this,
                    MessageArgEnum.ARG_ITEM, event.getTarget().getAttribute("element")));
        }
    }
}
