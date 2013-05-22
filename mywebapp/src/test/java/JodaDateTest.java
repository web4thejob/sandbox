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

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatterBuilder;
import org.junit.Test;

import java.util.Date;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public class JodaDateTest {

    @Test
    public void doTest() {
        Period period = new Period(new DateTime(2011, 3, 1, 0, 0).getMillis(), new Date().getTime());
        System.out.println(period.toString(new PeriodFormatterBuilder().appendYears().appendSuffix(" year", " years")
                .appendSeparator(" ").appendMonths().appendSuffix(" month", " months")
                .appendSeparator(" ").appendWeeks().appendSuffix(" week", " weeks")
                .appendSeparator(" ").appendDays().appendSuffix(" day", " days")
                .appendSeparator(" ").appendHours().appendSuffix(" hour", " hours")
                .appendSeparator(" ").appendMinutes().appendSuffix(" minute", " minutes").toFormatter()));
    }

}
