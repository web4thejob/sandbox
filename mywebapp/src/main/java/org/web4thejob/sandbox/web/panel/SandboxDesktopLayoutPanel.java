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

import org.web4thejob.command.Command;
import org.web4thejob.command.CommandEnum;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.web.dialog.AboutDialog;
import org.web4thejob.web.panel.DefaultDesktopLayoutPanel;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public class SandboxDesktopLayoutPanel extends DefaultDesktopLayoutPanel {

    @Override
    protected void processValidCommand(Command command) {
        if (CommandEnum.ABOUT.equals(command.getId())) {
            AboutDialog aboutDialog = ContextUtil.getDialog(SandboxAboutDialog.class);
            aboutDialog.show(null);
        } else {
            super.processValidCommand(command);
        }
    }
}
