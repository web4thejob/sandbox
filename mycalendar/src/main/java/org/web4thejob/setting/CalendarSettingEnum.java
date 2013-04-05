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

package org.web4thejob.setting;

import org.web4thejob.util.L10nString;
import org.web4thejob.web.dialog.SettingsDialog;

/**
 * @author Veniamin Isaias
 * @since 2.0.0
 */
public class CalendarSettingEnum extends SettingEnum {
    public static final L10nString L10N_CATEGORY_3000_CALENDAR = new L10nString(SettingsDialog.class,
            "category_3000_calendar", "Calendar");
    public static final L10nString L10N_CATEGORY_3100_CALENDAR_EVENTS_MAPPING = new L10nString(SettingsDialog.class,
            "category_3100_calendar_events_mapping", "Events Mapping");

    public static final CalendarSettingEnum CALENDAR_DAYS = new CalendarSettingEnum("CALENDAR_DAYS", Integer.class,
            L10N_CATEGORY_3000_CALENDAR);
    public static final CalendarSettingEnum CALENDAR_TIMESLOTS = new CalendarSettingEnum("CALENDAR_TIMESLOTS",
            Integer.class,
            L10N_CATEGORY_3000_CALENDAR);
    public static final CalendarSettingEnum CALENDAR_TIMEZONE = new CalendarSettingEnum("CALENDAR_TIMEZONE",
            String.class,
            L10N_CATEGORY_3000_CALENDAR);
    public static final CalendarSettingEnum CALENDAR_WEEK_OF_YEAR = new CalendarSettingEnum("CALENDAR_WEEK_OF_YEAR",
            Boolean.class,
            L10N_CATEGORY_3000_CALENDAR);
    public static final CalendarSettingEnum CALENDAR_FIRST_DAY_OF_WEEK = new CalendarSettingEnum
            ("CALENDAR_FIRST_DAY_OF_WEEK", Integer.class,
                    L10N_CATEGORY_3000_CALENDAR);
    public static final CalendarSettingEnum CALENDAR_START_TIME = new CalendarSettingEnum("CALENDAR_START_TIME",
            Integer.class,
            L10N_CATEGORY_3000_CALENDAR);
    public static final CalendarSettingEnum CALENDAR_END_TIME = new CalendarSettingEnum("CALENDAR_END_TIME",
            Integer.class,
            L10N_CATEGORY_3000_CALENDAR);

    public static final CalendarSettingEnum CALENDAR_EVENT_START = new CalendarSettingEnum("CALENDAR_EVENT_START",
            String.class,
            L10N_CATEGORY_3100_CALENDAR_EVENTS_MAPPING);
    public static final CalendarSettingEnum CALENDAR_EVENT_END = new CalendarSettingEnum("CALENDAR_EVENT_END",
            String.class,
            L10N_CATEGORY_3100_CALENDAR_EVENTS_MAPPING);
    public static final CalendarSettingEnum CALENDAR_EVENT_TITLE = new CalendarSettingEnum("CALENDAR_EVENT_TITLE",
            String.class,
            L10N_CATEGORY_3100_CALENDAR_EVENTS_MAPPING);
    public static final CalendarSettingEnum CALENDAR_EVENT_TITLE_COLOR = new CalendarSettingEnum
            ("CALENDAR_EVENT_TITLE_COLOR", String.class,
                    L10N_CATEGORY_3100_CALENDAR_EVENTS_MAPPING);
    public static final CalendarSettingEnum CALENDAR_EVENT_CONTENT = new CalendarSettingEnum
            ("CALENDAR_EVENT_CONTENT", String.class,
                    L10N_CATEGORY_3100_CALENDAR_EVENTS_MAPPING);
    public static final CalendarSettingEnum CALENDAR_EVENT_CONTENT_COLOR = new CalendarSettingEnum
            ("CALENDAR_EVENT_CONTENT_COLOR", String.class,
                    L10N_CATEGORY_3100_CALENDAR_EVENTS_MAPPING);
    public static final CalendarSettingEnum CALENDAR_EVENT_LOCKED = new CalendarSettingEnum("CALENDAR_EVENT_LOCKED",
            String.class,
            L10N_CATEGORY_3100_CALENDAR_EVENTS_MAPPING);

    CalendarSettingEnum(String name, Class<?> type, L10nString category) {
        super(name, type, category);
    }
}
