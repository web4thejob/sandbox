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

package org.web4thejob.sandbox.orm;

import job.myprojects.Task;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.Path;
import org.web4thejob.orm.query.Query;

import java.util.Date;
import java.util.List;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */


public class MyProjectsSampleDataInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private static Logger logger = Logger.getLogger(MyProjectsSampleDataInitializer.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() != null) {
            return;// do nothing if this is not the root context
        }

        Date now = new Date();
        Query query = ContextUtil.getEntityFactory().buildQuery(Task.class);
        query.addOrderBy(new Path("startTime"));
        List<Task> tasks = ContextUtil.getDRS().findByQuery(query);
        if (tasks.isEmpty()) return;

        Task olderTask = tasks.get(0);
        if (olderTask.getStartTime().after(now)) return;

        int num = 0;
        int daysOffset = (int) new Duration(olderTask.getStartTime().getTime(), now.getTime()).getStandardDays();
        for (Task task : tasks) {
            num += 1;
            DateTime start = new DateTime(task.getStartTime().getTime());
            task.setStartTime(start.plusDays(daysOffset).toDate());

            DateTime end = new DateTime(task.getEndTime().getTime());
            task.setEndTime(end.plusDays(daysOffset).toDate());

            ContextUtil.getDWS().save(task);
        }

        logger.info(num + " tasks have been offset successfully.");
    }
}
