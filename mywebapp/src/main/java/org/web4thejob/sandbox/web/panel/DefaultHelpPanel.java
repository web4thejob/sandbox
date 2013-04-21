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

import org.springframework.context.annotation.Scope;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.orm.ORMUtil;
import org.web4thejob.orm.PanelDefinition;
import org.web4thejob.orm.Path;
import org.web4thejob.orm.query.Condition;
import org.web4thejob.orm.query.Criterion;
import org.web4thejob.orm.query.Query;
import org.web4thejob.sandbox.orm.Document;
import org.web4thejob.setting.SettingEnum;
import org.web4thejob.web.panel.DefaultHtmlViewPanel;
import org.web4thejob.web.panel.Panel;

import java.util.StringTokenizer;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@org.springframework.stereotype.Component
@Scope("prototype")
public class DefaultHelpPanel extends DefaultHtmlViewPanel implements HelpPanel {

    @Override
    public void processMessage(Message message) {
        if (message.getId() == MessageEnum.ACTIVATED && message.getArg(MessageArgEnum.ARG_ITEM,
                Object.class) instanceof Panel) {

            Panel panel = message.getArg(MessageArgEnum.ARG_ITEM, Panel.class);

            Query query = ContextUtil.getEntityFactory().buildQuery(Document.class);
            Criterion criterion = query.addCriterion(new Path(Document.FLD_CODE), Condition.EQ, panel.getBeanName());
            Document doc = ContextUtil.getDRS().findUniqueByQuery(query);
            if (doc != null) {
                bind(doc);
            } else {
                PanelDefinition panelDefinition = ORMUtil.getPanelDefinition(panel.getBeanName());
                if (panelDefinition != null) {
                    StringTokenizer tokenizer = new StringTokenizer(panelDefinition.getType(), ", ");
                    while (tokenizer.hasMoreTokens()) {
                        criterion.setValue(tokenizer.nextToken());
                        doc = ContextUtil.getDRS().findUniqueByQuery(query);
                        if (doc != null) {
                            bind(doc);
                            break;
                        }
                    }
                }

                if (doc == null && activeQuery != null) {
                    doc = ContextUtil.getDRS().findUniqueByQuery(activeQuery);
                    if (doc != null) {
                        bind(doc);
                    }
                }
            }

        }

        super.processMessage(message);
    }

    @Override
    protected void registerSettings() {
        super.registerSettings();
        setSettingValue(SettingEnum.TARGET_TYPE, Document.class);
        setSettingValue(SettingEnum.HTML_PROPERTY, Document.FLD_BODY);
    }
}
