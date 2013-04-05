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

package org.web4thejob.command;

/**
 * @author Veniamin Isaias
 * @since 2.0.0
 */
public class CalendarCommandEnum extends CommandEnum {

    public static final CalendarCommandEnum CALENDAR_MONTH_VIEW = new CalendarCommandEnum("CALENDAR_MONTH_VIEW",
            CommandEnum.CATEGORY_DEFAULT, true, true, null, false);
    public static final CalendarCommandEnum CALENDAR_1DAY_VIEW = new CalendarCommandEnum("CALENDAR_1DAY_VIEW",
            false);
    public static final CalendarCommandEnum CALENDAR_5DAY_VIEW = new CalendarCommandEnum("CALENDAR_5DAY_VIEW",
            false);
    public static final CalendarCommandEnum CALENDAR_7DAY_VIEW = new CalendarCommandEnum("CALENDAR_7DAY_VIEW",
            false);

    public static final CalendarCommandEnum CALENDAR_VIEW = new CalendarCommandEnum("CALENDAR_VIEW",
            CommandEnum.CATEGORY_DEFAULT, true, true,
            new CalendarCommandEnum[]{CALENDAR_MONTH_VIEW,
                    CALENDAR_1DAY_VIEW, CALENDAR_5DAY_VIEW, CALENDAR_7DAY_VIEW});

    CalendarCommandEnum(String name, String category, boolean requiresStartSeparator, boolean requiresEndSeparator,
                        CommandEnum[] subcommands) {
        super(name, category, requiresStartSeparator, requiresEndSeparator, subcommands);
    }

    CalendarCommandEnum(String name) {
        super(name);
    }

    CalendarCommandEnum(String name, Object value) {
        super(name, value);
    }

    CalendarCommandEnum(String name, String category, boolean requiresStartSeparator, boolean requiresEndSeparator,
                        CommandEnum[] subcommands, Object value) {
        super(name, category, requiresStartSeparator, requiresEndSeparator, subcommands, value);
    }
}
