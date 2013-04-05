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

import nu.xom.Document;
import nu.xom.Element;
import org.springframework.context.annotation.Scope;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.orm.ORMUtil;
import org.web4thejob.orm.PanelDefinition;
import org.web4thejob.util.XMLUtil;
import org.web4thejob.web.panel.DefaultMenuAuthorizationPanel;
import org.web4thejob.web.panel.Panel;
import org.web4thejob.web.panel.PlaceholderPanel;
import org.web4thejob.web.panel.UserMenuPanel;
import org.web4thejob.web.panel.base.zk.AbstractZkLayoutPanel;
import org.web4thejob.web.util.ZkUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.Collection;
import java.util.List;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@org.springframework.stereotype.Component
@Scope("prototype")
public class DefaultBookReaderPanel extends AbstractZkLayoutPanel implements BookReaderPanel,
        EventListener<Event> {
    private final static String ON_CLCIK_ECHO = Events.ON_CLICK + "Echo";
    private final Borderlayout blayout = new Borderlayout();
    private UserMenuPanel userMenuPanel;
    private Hbox lbox;
    private Hbox rbox;
    private Hbox dbox;
    private Hbox ubox;

    public DefaultBookReaderPanel() {
        ZkUtil.setParentOfChild((Component) base, blayout);
        blayout.setWidth("100%");
        blayout.setVflex("true");

        new Center().setParent(blayout);
        blayout.getCenter().setBorder("none");
        //blayout.getCenter().setStyle("margin-top:1px;");

        new South().setParent(blayout);
        blayout.getSouth().setHeight("50px");
        blayout.getSouth().setSplittable(false);
        blayout.getSouth().setCollapsible(false);
        blayout.getSouth().setBorder("normal");
        blayout.getSouth().setStyle("border-width:0px;border-top-width:1px;border-top-style:dotted;");

        new North().setParent(blayout);
        blayout.getNorth().setHeight("50px");
        blayout.getNorth().setSplittable(false);
        blayout.getNorth().setCollapsible(false);
        blayout.getNorth().setBorder("normal");
        blayout.getNorth().setStyle("border-style:dotted;background-color:rgb(250,250,250);");

/*
        new West().setParent(blayout);
        blayout.getWest().setWidth("10px");
        blayout.getWest().setSplittable(false);
        blayout.getWest().setCollapsible(false);
        blayout.getWest().setBorder("none");
        blayout.getWest().setStyle("background-color:blue;");
*/

        Hlayout hlayout = new Hlayout();
        hlayout.setParent(blayout.getSouth());
        hlayout.setVflex("true");
        hlayout.setHflex("true");

        lbox = new Hbox();
        lbox.setParent(hlayout);
        lbox.setHflex("true");
        lbox.setVflex("true");
        lbox.setPack("start");
        lbox.setAlign("center");
        lbox.setStyle("margin-left:10px;");

        ubox = new Hbox();
        ubox.setParent(hlayout);
        ubox.setHflex("true");
        ubox.setVflex("true");
        ubox.setPack("end");
        ubox.setAlign("center");
        ubox.setStyle("margin-left:5px;margin-right:15px;");

        dbox = new Hbox();
        dbox.setParent(hlayout);
        dbox.setHflex("true");
        dbox.setVflex("true");
        dbox.setPack("start");
        dbox.setAlign("center");
        dbox.setStyle("margin-left:15px;margin-right:5px;");

        rbox = new Hbox();
        rbox.setParent(hlayout);
        rbox.setHflex("true");
        rbox.setVflex("true");
        rbox.setPack("end");
        rbox.setAlign("center");
        rbox.setStyle("margin-right:10px;");


    }

    @Override
    protected Collection<Panel> getRenderedOrderOfChildren() {
        return subpanels;
    }

    @Override
    protected boolean isActive(Panel panel) {
        return !subpanels.isEmpty() && subpanels.first().equals(panel);
    }

    @Override
    public boolean accepts(Panel panel) {
        return blayout.getCenter().getChildren().isEmpty();
    }

    @Override
    protected void beforeAdd(Panel panel) {
        super.beforeAdd(panel);
        panel.attach(blayout.getCenter());
        panel.render();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();

        if (blayout.getCenter().getChildren().isEmpty()) {
            subpanels.add(ContextUtil.getDefaultPanel(PlaceholderPanel.class));
        }
    }

    @Override
    protected void afterRemove(Panel panel) {
        super.afterRemove(panel);
        panel.detach();
    }

    @Override
    public void render() {
        super.render();
        renderNavigation();
    }

    private UserMenuPanel getUserMenuPanel() {
        if (userMenuPanel == null || !userMenuPanel.isAttached()) {
            List<UserMenuPanel> menuPanels = ContextUtil.getSessionContext().getUserDesktop().getActiveInstances
                    (UserMenuPanel.class);
            if (!menuPanels.isEmpty()) {
                userMenuPanel = menuPanels.get(0);
            }
        }

        if (userMenuPanel == null) {
            userMenuPanel = ContextUtil.getDefaultPanel(UserMenuPanel.class);
        }

        return userMenuPanel;
    }

    private void renderNavigation() {
        getUserMenuPanel().select(subpanels.first().getBeanName());

        Element current;
        if (subpanels.first() instanceof ChapterPanel) {
            current = findElement(XMLUtil.getTextualValue(((ChapterPanel) subpanels.first()).getChapter()));
        } else {
            current = findElement(subpanels.first().getBeanName());
        }

        if (current == null) return;

        renderHeader(describeElement(current));
        Element nextid = findNext(current, true);
        Element previd = findPrevious(current);
        Element upid = findPreviousChapter(current);
        Element downid = findNextChapter(current);

        rbox.getChildren().clear();
        if (nextid != null) {
            new Label(describeElement(nextid)).setParent(rbox);
            A rarrow = new A(null, "img/CMD_MOVE_RIGHT.png");
            rarrow.setParent(rbox);
            rarrow.addEventListener(Events.ON_CLICK, this);
            rarrow.addEventListener(ON_CLCIK_ECHO, this);
            rarrow.setAttribute("beanid", nextid);
            rarrow.setTooltiptext("Next page");

        }

        lbox.getChildren().clear();
        if (previd != null) {
            A larrow = new A(null, "img/CMD_MOVE_LEFT.png");
            larrow.setParent(lbox);
            larrow.addEventListener(Events.ON_CLICK, this);
            larrow.addEventListener(ON_CLCIK_ECHO, this);
            larrow.setAttribute("beanid", previd);
            larrow.setTooltiptext("Previous page");
            new Label(describeElement(previd)).setParent(lbox);
        }

        ubox.getChildren().clear();
        if (upid != null) {
            new Label(describeElement(upid)).setParent(ubox);
            A uarrow = new A(null, "img/CMD_MOVE_UP.png");
            uarrow.setParent(ubox);
            uarrow.addEventListener(Events.ON_CLICK, this);
            uarrow.addEventListener(ON_CLCIK_ECHO, this);
            uarrow.setAttribute("beanid", upid);
            uarrow.setTooltiptext("Previous chapter");
        }

        dbox.getChildren().clear();
        if (downid != null) {
            A darrow = new A(null, "img/CMD_MOVE_DOWN.png");
            darrow.setParent(dbox);
            darrow.addEventListener(Events.ON_CLICK, this);
            darrow.addEventListener(ON_CLCIK_ECHO, this);
            darrow.setAttribute("beanid", downid);
            darrow.setTooltiptext("Next chapter");
            new Label(describeElement(downid)).setParent(dbox);
        }

    }

    private Element findNextChapter(Element element) {
        Element chapter = findNext(element, true);
        while (chapter != null) {
            if (DefaultMenuAuthorizationPanel.ELEMENT_MENU.equals(chapter.getLocalName())) {
                return chapter;
            }
            chapter = findNext(chapter, true);
        }

        return null;
    }

    private Element findPreviousChapter(Element element) {
        Element chapter = findPrevious(element);
        while (chapter != null) {
            if (DefaultMenuAuthorizationPanel.ELEMENT_MENU.equals(chapter.getLocalName())) {
                return chapter;
            }
            chapter = findPrevious(chapter);
        }

        return null;
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
    public String toString() {
/*
        if (!subpanels.isEmpty() && (!(subpanels.first() instanceof PlaceholderPanel))) {
            return subpanels.first().toString();
        }
*/
        return super.toString();
    }

    @Override
    public void processMessage(Message message) {
        if (message.getId() == MessageEnum.EXECUTE_SECURED_RESOURCE) {
            if (message.getArgs().get(MessageArgEnum.ARG_ITEM) instanceof Element) {
                Element element = message.getArg(MessageArgEnum.ARG_ITEM, Element.class);
                if (DefaultMenuAuthorizationPanel.ELEMENT_MENU.equals(element.getLocalName())) {
                    renderChapter(element);
                } else if (DefaultMenuAuthorizationPanel.ELEMENT_PANEL.equals(element.getLocalName())) {
                    renderTopic(element);
                }
            }
        } else {
            super.processMessage(message);
        }
    }

    private void renderHeader(String header) {
        blayout.getNorth().getChildren().clear();
        Vbox vbox = new Vbox();
        vbox.setParent(blayout.getNorth());
        vbox.setAlign("start");
        vbox.setPack("center");
        vbox.setHflex("true");
        vbox.setVflex("true");
        Label label = new Label(header);
        label.setParent(vbox);
        label.setStyle("margin-left:15px;font-weight:bold;font-size:120%");
    }

    @Override
    public void onEvent(Event event) throws Exception {
        if (event.getName().equals(Events.ON_CLICK)) {
            Element beanid = (Element) event.getTarget().getAttribute("beanid");
            if (beanid != null) {
                Clients.showBusy(null);
                Events.echoEvent(ON_CLCIK_ECHO, event.getTarget(), beanid);
            }
        } else {
            Clients.clearBusy();
            if (event.getData() instanceof Element) {
                Element element = (Element) event.getData();
                if (DefaultMenuAuthorizationPanel.ELEMENT_MENU.equals(element.getLocalName())) {
                    renderChapter(element);
                } else if (DefaultMenuAuthorizationPanel.ELEMENT_PANEL.equals(element.getLocalName())) {
                    renderTopic(element);
                }
            }
        }
    }

    private void renderTopic(Element topic) {
        Panel panel = ContextUtil.getPanel(XMLUtil.getTextualValue(topic));
        if (panel != null) {
            subpanels.replace(subpanels.first(), panel);
            panel.render();
            getUserMenuPanel().select(panel.getBeanName());
            dispatchMessage(ContextUtil.getMessage(MessageEnum.TITLE_CHANGED, this));
            renderNavigation();
        }
    }

    private void renderChapter(Element chapter) {
        ChapterPanel chapterPanel = ContextUtil.getDefaultPanel(ChapterPanel.class);
        if (chapterPanel != null) {
            subpanels.replace(subpanels.first(), chapterPanel);
            chapterPanel.setChapter(chapter);
            getUserMenuPanel().select(XMLUtil.getTextualValue(chapter));
            dispatchMessage(ContextUtil.getMessage(MessageEnum.TITLE_CHANGED, this));
            renderNavigation();
        }
    }

    private Element findPrevious(Element element) {
        if (element.getParent() == null) return null;

        //first look for previous sibling
        int index = element.getParent().indexOf(element);
        if (index > 0 && element.getParent().getChild(index - 1) instanceof Element) {
            Element sibling = (Element) element.getParent().getChild(index - 1);
            //then look for last child of sibling
            if (sibling.getChildElements().size() > 0) {
                Element child = sibling.getChildElements().get(sibling.getChildElements().size() - 1);
                while (child.getChildElements().size() > 0) {
                    child = child.getChildElements().get(child.getChildElements().size() - 1);
                }
                return child;
            } else {
                return sibling;
            }
        }

        if (element.getParent() instanceof Element && (!(element.getParent().getParent() instanceof Document))) {
            return (Element) element.getParent();
        }

        return null;
    }

    private Element findNext(Element element, boolean allowChildren) {
        if (element.getParent() == null) return null;

        //first look for first child
        if (allowChildren && element.getChildElements().size() > 0) {
            return element.getChildElements().get(0);
        }

        //first look for next sibling
        int index = element.getParent().indexOf(element);
        if (index < element.getParent().getChildCount() - 1) {
            return (Element) element.getParent().getChild(index + 1);
        }

        if (element.getParent() instanceof Element) {
            return findNext((Element) element.getParent(), false);
        }

        return null;
    }

    private Element findElement(String nodeid) {
        Element root = XMLUtil.getRootElement(ContextUtil.getSessionContext().getSecurityContext()
                .getAuthorizationMenu());

        if (nodeid.equals(XMLUtil.getTextualValue(root))) {
            return root;
        }

        Element node = null;
        for (int i = 0; i < root.getChildElements().size(); i++) {
            node = root.getChildElements().get(i);
            if (nodeid.equals(XMLUtil.getTextualValue(node))) {
                break;
            } else {
                node = findChildElement(node, nodeid);
                if (node != null) break;
            }
        }

        return node;
    }

    private Element findChildElement(Element parent, String nodeid) {
        Element node = null;
        for (int i = 0; i < parent.getChildElements().size(); i++) {
            node = parent.getChildElements().get(i);
            if (nodeid.equals(XMLUtil.getTextualValue(node))) {
                break;
            } else {
                node = findChildElement(node, nodeid);
                if (node != null) break;
            }
        }

        return node;
    }

}
