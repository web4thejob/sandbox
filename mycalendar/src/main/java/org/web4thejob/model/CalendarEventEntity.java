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

import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.util.StringUtils;
import org.web4thejob.orm.Entity;
import org.web4thejob.setting.CalendarSettingEnum;
import org.web4thejob.setting.Setting;

import java.util.Date;
import java.util.Map;

/**
 * @author Veniamin Isaias
 * @since 2.0.0
 */

@SuppressWarnings("rawtypes")
public class CalendarEventEntity implements CalendarEventWrapper {
    private final Map<CalendarSettingEnum, Setting> mappings;
    private ConfigurablePropertyAccessor beanWrapper;
    private Entity entity;

    public CalendarEventEntity(Entity entity, Map<CalendarSettingEnum, Setting> mappings) {
        this.entity = entity;
        this.mappings = mappings;
        this.beanWrapper = PropertyAccessorFactory.forDirectFieldAccess(this.entity);
    }

    private Object getValue(CalendarSettingEnum id) {
        if (mappings.containsKey(id)) {
            Setting setting = mappings.get(id);
            if (setting.getValue() != null && StringUtils.hasText(setting.getValue().toString())) {
                return beanWrapper.getPropertyValue((String) setting.getValue());
            }
        }
        return null;
    }

    private void setValue(CalendarSettingEnum id, Object value) {
        if (mappings.containsKey(id)) {
            Setting setting = mappings.get(id);
            if (setting.getValue() != null && StringUtils.hasText(setting.getValue().toString())) {
                beanWrapper.setPropertyValue((String) setting.getValue(), value);
            }
        }
    }

    @Override
    public Date getBeginDate() {
        return (Date) getValue(CalendarSettingEnum.CALENDAR_EVENT_START);
    }

    @Override
    public Date getEndDate() {
        return (Date) getValue(CalendarSettingEnum.CALENDAR_EVENT_END);
    }

    @Override
    public String getTitle() {
        //String s = (String) getValue(CalendarSettingEnum.CALENDAR_EVENT_TITLE);
        //return s == null ? "" : s;
        return "";
    }

    @Override
    public String getContent() {
        String s = (String) getValue(CalendarSettingEnum.CALENDAR_EVENT_CONTENT);
        if (s != null) {
            s = s.replaceAll("<p>", "");
            s = s.replaceAll("</p>", "");
        }
        return s == null ? "" : s;
    }

    @Override
    public String getHeaderColor() {
        String s = (String) getValue(CalendarSettingEnum.CALENDAR_EVENT_TITLE_COLOR);
        return s == null ? "" : s;
    }

    @Override
    public String getContentColor() {
        String s = (String) getValue(CalendarSettingEnum.CALENDAR_EVENT_CONTENT_COLOR);
        return s == null ? "" : s;
    }

    @Override
    public String getZclass() {
        return null;
    }

    @Override
    public boolean isLocked() {
        Boolean locked = (Boolean) getValue(CalendarSettingEnum.CALENDAR_EVENT_LOCKED);
        if (locked != null) {
            return locked;
        }
        return false;
    }

    @Override
    public void setBeginDate(Date date) {
        setValue(CalendarSettingEnum.CALENDAR_EVENT_START, date);
    }

    @Override
    public void setEndDate(Date date) {
        setValue(CalendarSettingEnum.CALENDAR_EVENT_END, date);
    }

    @Override
    public void setTitle(String title) {
        setValue(CalendarSettingEnum.CALENDAR_EVENT_TITLE, title);
    }

    @Override
    public void setContent(String content) {
        setValue(CalendarSettingEnum.CALENDAR_EVENT_CONTENT, content);
    }

    @Override
    public void setHeaderColor(String headerColor) {
        setValue(CalendarSettingEnum.CALENDAR_EVENT_TITLE_COLOR, headerColor);
    }

    @Override
    public void setContentColor(String contentColor) {
        setValue(CalendarSettingEnum.CALENDAR_EVENT_CONTENT_COLOR, contentColor);
    }

    @Override
    public void setLocked(boolean locked) {
        setValue(CalendarSettingEnum.CALENDAR_EVENT_LOCKED, locked);
    }

    @Override
    public Entity getEntity() {
        return entity;
    }

    @Override
    public void update(Entity entity) {
        if (this.entity.equals(entity)) {
            this.entity = entity;
            beanWrapper = PropertyAccessorFactory.forDirectFieldAccess(this.entity);
        } else {
            throw new IllegalArgumentException("not the same entity.");
        }
    }

    @Override
    public int hashCode() {
        return entity.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        else if (this == obj) return true;
        else if (!CalendarEventWrapper.class.isInstance(obj)) return false;

        return entity.equals(((CalendarEventWrapper) obj).getEntity());
    }
}
