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

package org.web4thejob.sandbox;

import org.springframework.security.authentication.encoding.PlaintextPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.Entity;
import org.web4thejob.orm.Path;
import org.web4thejob.orm.query.Condition;
import org.web4thejob.orm.query.Query;
import org.web4thejob.security.UserIdentity;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.*;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public class UserNamesAndPasswords implements Composer<Window>, RowRenderer<UserIdentity> {
    private Window window;

    @Override
    public void doAfterCompose(Window comp) throws Exception {
        window = comp;

        Vbox vbox = new Vbox();
        vbox.setHflex("true");
        vbox.setParent(window);

/*
        Div div = new Div();
        div.setStyle("background-color:rgba(210,210,210,0.5);border:1px;border-color:rgba(210,210,210," +
                "0.5);border-style:solid;");
        div.setParent(vbox);
        Label label = new Label("Pick one of the following users to try the corresponding functionality.");
        label.setStyle("font-style:italic;font-weight:bold;font-size:15px;");
        label.setParent(div);
*/

        Grid grid = new Grid();
        grid.setRowRenderer(this);
        grid.setParent(vbox);
        grid.setSpan(true);

        Column column;
        new Columns().setParent(grid);
        column = new Column("User Name");
        column.setParent(grid.getColumns());
        column.setHflex("min");
        column = new Column("Password");
        column.setParent(grid.getColumns());
        column.setHflex("min");
        column = new Column("Description");
        column.setParent(grid.getColumns());
        column = new Column("More Info");
        column.setParent(grid.getColumns());
        column.setHflex("min");
        column.setAlign("center");

        Query query = ContextUtil.getEntityFactory().buildQuery(UserIdentity.class);
        query.addCriterion(new Path(UserIdentity.FLD_CODE), Condition.NE, UserIdentity.USER_ADMIN);
        query.setCached(true);
        grid.setModel(new ListModelList<Entity>(ContextUtil.getDRS().findByQuery(query)));
    }

    @Override
    public void render(Row row, UserIdentity data, int index) throws Exception {

        new Label(data.getCode()).setParent(row);
        new Label(data.getPassword()).setParent(row);
        new Label(data.getLastName()).setParent(row);

        Html html = new Html();
        html.setContent("<a href=\"about.html#" + data.getCode() + "\" target=\"_parent\"><img src=\"." +
                "./img/CMD_SESSION_INFO.png\"/></a>");
        html.setParent(row);
    }

}
