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

package org.web4thejob.model;

import org.web4thejob.orm.Entity;
import org.zkoss.calendar.api.CalendarEvent;

import java.util.Date;

/**
 * @author Veniamin Isaias
 * @since 2.0.0
 */
public interface CalendarEventWrapper extends CalendarEvent {
    public void setBeginDate(Date date);

    public void setEndDate(Date date);

    public void setTitle(String title);

    public void setContent(String content);

    public void setHeaderColor(String headerColor);

    public void setContentColor(String contentColor);

    public void setLocked(boolean locked);

    public Entity getEntity();

    public void update(Entity entity);

    @Override
    public int hashCode();

    @Override
    public boolean equals(Object obj);


}
