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

package job.myprojects;

import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.DateTimeConstants;
import org.joda.time.Duration;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.AbstractHibernateEntity;
import org.web4thejob.orm.Entity;
import org.web4thejob.orm.annotation.ColorHolder;
import org.web4thejob.orm.annotation.HtmlHolder;
import org.web4thejob.orm.annotation.UrlHolder;
import org.web4thejob.orm.validation.AdhocConstraintViolation;

import javax.validation.ConstraintViolation;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public class Task extends AbstractHibernateEntity {

    private long id;
    @NotNull
    private Project project;
    @NotBlank
    private String title;
    @NotBlank
    @ColorHolder
    private String color;
    @NotBlank
    @ColorHolder
    private String borderColor;
    @NotNull
    private Date startTime;
    @NotNull
    private Date endTime;
    private Venue location;
    @NotNull
    @DecimalMin("0.00")
    private BigDecimal rate = BigDecimal.ZERO;
    @HtmlHolder
    private String details;
    @UrlHolder
    private String link;
    private boolean locked;
    private boolean cancelled;
    @DecimalMin("0.00")
    @NotNull
    private BigDecimal notBillable = BigDecimal.ZERO;
    private BigDecimal duration = BigDecimal.ZERO;
    private TaskAttachment attachment;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getBorderColor() {
        return this.borderColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
        setDirty();
    }

    public Date getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
        setDirty();
    }

    public String getDetails() {
        return this.details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public boolean getLocked() {
        return this.locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean getCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public Serializable getIdentifierValue() {
        return id;
    }

    @Override
    public void setAsNew() {
        id = 0;
    }

    @Override
    public String toString() {
        return title;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;

        if (this.project != null) {
            this.project = ContextUtil.getMRS().deproxyEntity(this.project);

            this.title = this.project.getCode();
            this.color = this.project.getColor();
            this.borderColor = this.project.getBorderColor();
            this.rate = this.project.getRate();

            ContextUtil.getMRS().deproxyEntity(this.project.getLocation());
            this.location = this.project.getLocation();

            setDirty();
        }
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Venue getLocation() {
        return location;
    }

    public void setLocation(Venue location) {
        this.location = location;
    }

    public BigDecimal getNotBillable() {
        return notBillable;
    }

    public void setNotBillable(BigDecimal notBillable) {
        this.notBillable = notBillable;
    }

    private BigDecimal calcDuration() {
        if (startTime != null && endTime != null && startTime.before(endTime)) {
            Duration duration = new Duration(startTime.getTime(), endTime.getTime());
            return new BigDecimal(duration.getMillis() / (1.0 * DateTimeConstants.MILLIS_PER_HOUR));
        }
        return BigDecimal.ZERO;
    }

    @Override
    public void calculate() {
        duration = calcDuration();
    }

    public BigDecimal getDuration() {
        return duration;
    }

    public void setDuration(BigDecimal duration) {
        this.duration = duration;
    }

    @Override
    public Set<ConstraintViolation<Entity>> validate() {
        final Set<ConstraintViolation<Entity>> violations = super.validate();

        if (!isStartTimeValid()) {
            violations.add(new AdhocConstraintViolation(
                    "{org.web4thejob.model.constraint.NotBefore.message}", "startTime",
                    this, startTime));
        }

        if (!isEndTimeValid()) {
            violations.add(new AdhocConstraintViolation(
                    "{org.web4thejob.model.constraint.NotAfter.message}", "endTime",
                    this, endTime));
        }

        if (!isNotBillableValid()) {
            violations.add(new AdhocConstraintViolation(
                    "{org.web4thejob.model.constraint.LimitExcess.message}", "notBillable",
                    this, notBillable));
        }

        return violations;
    }

    private boolean isStartTimeValid() {
        if (startTime != null && endTime != null) {
            return startTime.before(endTime);
        }
        return true;
    }

    private boolean isEndTimeValid() {
        if (startTime != null && endTime != null) {
            return endTime.after(startTime);
        }
        return true;
    }

    public boolean isNotBillableValid() {
        if (notBillable != null) {
            return calcDuration().compareTo(notBillable) >= 0;
        }
        return true;
    }

    public TaskAttachment getAttachment() {
        return attachment;
    }

    public void setAttachment(TaskAttachment attachment) {
        this.attachment = attachment;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

}
