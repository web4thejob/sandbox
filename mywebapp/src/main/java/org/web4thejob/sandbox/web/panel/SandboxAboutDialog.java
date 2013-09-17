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
import org.springframework.security.authentication.encoding.PlaintextPasswordEncoder;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.module.Module;
import org.web4thejob.util.L10nMessages;
import org.web4thejob.web.dialog.DefaultAboutDialog;
import org.zkoss.zul.*;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@org.springframework.stereotype.Component
@Scope("prototype")
public class SandboxAboutDialog extends DefaultAboutDialog {

    @Override
    protected void prepareContent() {
        dialogContent.getPanelchildren().setStyle("overflow: auto;");

        Vbox vbox = new Vbox();
        vbox.setParent(dialogContent.getPanelchildren());
        vbox.setHflex("true");
        vbox.setVflex("true");
        vbox.setAlign("center");
        vbox.setPack("center");
        vbox.setSpacing("8px");

        Separator separator = new Separator();
        separator.setParent(vbox);

        A image = new A();
        image.setParent(vbox);
        image.setImage("img/w4tj_logo_vertical_full.png");
        image.setHref("http://web4thejob.org");
        image.setTarget("_blank");

        Label label = new Label();
        label.setParent(vbox);
        label.setStyle("font-size:16pt;color:rgb(85,85,85);");
        label.setValue("Sandbox web application v" + getSandboxVersion());

        Html html = new Html();
        html.setParent(vbox);
        html.setZclass("z-label");
        html.setStyle("font-size:12pt;color:rgb(85,85,85);");
        html.setContent("Copyright &copy; " + java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) + " " +
                L10nMessages.L10N_DEVELOPER_SIGNATURE.toString());

        html = new Html();
        html.setParent(vbox);
        html.setZclass("z-label");
        html.setStyle("font-size:12pt;color:rgb(85,85,85);");
        html.setContent("Licensed under <a target=\"_blank\" href=\"http://www.gnu.org/licenses/gpl.html\">GNU GPL v3</a>");

        html = new Html();
        html.setParent(vbox);
        html.setZclass("z-label");
        html.setStyle("font-size:12pt;color:rgb(85,85,85);");
        html.setContent("Powered by <a target=\"_blank\" href=\"http://sourceforge" +
                ".net/projects/web4thejob/files/webapp/" + ContextUtil.getModules().get(0).getVersion() + "\">" +
                "web4thejob v" + ContextUtil.getModules().get(0).getVersion() + "</a>");

        html = new Html();
        html.setParent(vbox);
        html.setZclass("z-label");
        html.setStyle("font-size:12pt;color:rgb(85,85,85);");
        html.setContent(L10nMessages.L10N_ICONS_SIGNATURE.toString());

        Separator space = new Separator("horizontal");
        space.setSpacing("10px");
        space.setBar(true);
        space.setParent(vbox);

        label = new Label();
        label.setParent(vbox);
        label.setStyle("font-size:12pt;color:rgb(85,85,85);text-decoration:underline;");
        label.setValue("Administrator credentials:");

        html = new Html();
        html.setParent(vbox);
        html.setZclass("z-label");
        html.setStyle("font-size:12pt;color:rgb(85,85,85);");
        html.setContent("User Name: <span style=\"color:rgb(75,75,75)\"><strong>admin</strong></span>");

        html = new Html();
        html.setParent(vbox);
        html.setZclass("z-label");
        html.setStyle("font-size:12pt;color:rgb(85,85,85);");
        String passwd = ContextUtil.getSecurityService().getAdministratorIdentity().getPassword();
        html.setContent("Password: <span style=\"color:rgb(75,75,75)\"><strong>" + passwd + "</strong></span>");


        btnOK.setFocus(true);

    }

    private String getSandboxVersion() {
        for (Module module : ContextUtil.getModules()) {
            if ("sandbox".equals(module.getName())) {
                return module.getVersion();
            }
        }

        return "";
    }

    @Override
    protected void prepareContentLayout() {
        super.prepareContentLayout();
        window.setHeight("600px");
    }
}
