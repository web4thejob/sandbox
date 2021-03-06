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

package org.web4thejob.web.panel;

import org.springframework.context.annotation.Scope;
import org.springframework.util.StringUtils;
import org.web4thejob.command.CalendarCommandEnum;
import org.web4thejob.command.Command;
import org.web4thejob.command.CommandEnum;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.message.MessageListener;
import org.web4thejob.model.CalendarEventEntity;
import org.web4thejob.model.CalendarEventWrapper;
import org.web4thejob.orm.Entity;
import org.web4thejob.orm.Path;
import org.web4thejob.orm.PropertyMetadata;
import org.web4thejob.orm.query.Condition;
import org.web4thejob.orm.query.Criterion;
import org.web4thejob.orm.query.OrderBy;
import org.web4thejob.orm.query.Query;
import org.web4thejob.setting.CalendarSettingEnum;
import org.web4thejob.setting.Setting;
import org.web4thejob.setting.SettingEnum;
import org.web4thejob.util.CoreUtil;
import org.web4thejob.util.L10nMessages;
import org.web4thejob.web.dialog.DefaultEntityPersisterDialog;
import org.web4thejob.web.dialog.EntityPersisterDialog;
import org.web4thejob.web.dialog.QueryDialog;
import org.web4thejob.web.panel.base.zk.AbstractZkBindablePanel;
import org.web4thejob.web.util.ZkUtil;
import org.zkoss.calendar.Calendars;
import org.zkoss.calendar.api.CalendarEvent;
import org.zkoss.calendar.api.CalendarModel;
import org.zkoss.calendar.event.CalendarDropEvent;
import org.zkoss.calendar.event.CalendarsEvent;
import org.zkoss.calendar.impl.SimpleCalendarModel;
import org.zkoss.calendar.impl.SimpleDateFormatter;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listitem;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.web4thejob.message.MessageEnum.ENTITY_UPDATED;
import static org.web4thejob.message.MessageEnum.QUERY;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */


@SuppressWarnings("rawtypes")
@org.springframework.stereotype.Component
@Scope("prototype")
public class DefaultCalendarViewPanel extends AbstractZkBindablePanel implements CalendarViewPanel {
    private static final String ON_EVENT_UPDATE_ECHO = CalendarsEvent.ON_EVENT_UPDATE + "Echo";
    private static final String ON_DAY_CLICK_ECHO = CalendarsEvent.ON_DAY_CLICK + "Echo";
    private static final String ON_WEEK_CLICK_ECHO = CalendarsEvent.ON_WEEK_CLICK + "Echo";
    private static final String ATTRIB_BEGIN_DATE = "beginDate";
    private static final String ATTRIB_END_DATE = "endDate";
    private final Calendars calendar = new Calendars();
    private final CalendarsEventHandler calendarsEventHandler = new CalendarsEventHandler();
    private DialogListener dialogListener = new DialogListener();
    private Query activeQuery;
    private Entity targetEntity;
    private Date minBeginDate = new Date();
    private Date maxEndDate = minBeginDate;

    public DefaultCalendarViewPanel() {
        ZkUtil.setParentOfChild((Component) base, calendar);
        calendar.setWidth("100%");
        calendar.setVflex("true");
        calendar.setBeginTime(7);
        calendar.setDateFormatter(new MyDateFormatter());
        calendar.setWeekOfYear(true);

        calendar.addEventListener(CalendarsEvent.ON_DAY_CLICK, calendarsEventHandler);
        calendar.addEventListener(CalendarsEvent.ON_WEEK_CLICK, calendarsEventHandler);
        calendar.addEventListener(CalendarsEvent.ON_EVENT_CREATE, calendarsEventHandler);
        calendar.addEventListener(CalendarsEvent.ON_EVENT_EDIT, calendarsEventHandler);
        calendar.addEventListener(CalendarsEvent.ON_EVENT_UPDATE, calendarsEventHandler);
        calendar.addEventListener(ON_EVENT_UPDATE_ECHO, calendarsEventHandler);
        calendar.addEventListener(ON_DAY_CLICK_ECHO, calendarsEventHandler);
        calendar.addEventListener(ON_WEEK_CLICK_ECHO, calendarsEventHandler);
    }

    @Override
    protected void arrangeForMasterEntity() {
        if (getMasterEntity() == null) {
            clear();
        } else if (getBindProperty() != null) {
            refresh();
        }
    }

    @Override
    protected void arrangeForTargetEntity(Entity targetEntity) {
        boolean hasChanged = (this.targetEntity != null && !this.targetEntity.equals(targetEntity)) || (targetEntity !=
                null && !targetEntity.equals(this.targetEntity));

        if (hasChanged && this.targetEntity != null) {
            dispatchMessage(ContextUtil.getMessage(MessageEnum.ENTITY_DESELECTED, this, MessageArgEnum.ARG_ITEM,
                    this.targetEntity));
        }

/*
        if (targetEntity == null) {
            ((ListModelList) listbox.getModel()).clearSelection();
        } else {
            if (!((ListModelList) listbox.getModel()).getSelection().contains(targetEntity)) {
                ((ListModelList) listbox.getModel()).addToSelection(targetEntity);
            }
        }
*/
        this.targetEntity = targetEntity;
        if (this.targetEntity != null) {
            arrangeForState(PanelState.FOCUSED);
            if (hasChanged) {
                dispatchMessage(ContextUtil.getMessage(MessageEnum.ENTITY_SELECTED, this, MessageArgEnum.ARG_ITEM,
                        this.targetEntity));
            }
        } else {
            if (isMasterDetail() && !hasMasterEntity()) {
                arrangeForState(PanelState.UNDEFINED);
            } else {
                arrangeForState(PanelState.BROWSING);
            }
        }
    }

    @SuppressWarnings({"rawtypes"})
    @Override
    public void clear() {
        calendar.setModel(new SimpleCalendarModel());
        setTargetEntity(null);
        setMasterEntity(null);
    }

    @Override
    public Date getCurrentDate() {
        return calendar.getCurrentDate();
    }

    @Override
    public void setCurrentDate(Date date) {
        calendar.setCurrentDate(date);
    }

    @Override
    protected boolean processEntityDeselection(Entity entity) {
        return !(isMasterDetail() && hasMasterEntity() && getMasterEntity().equals(entity)) || processEntityDeletion
                (entity);
    }

    @Override
    protected boolean processEntityInsertion(Entity entity) {
        if (hasTargetType() && getTargetType().isInstance(entity)) {
            if (calendar.getModel() == null) {
                calendar.setModel(new SimpleCalendarModel());
            }
            ((SimpleCalendarModel) calendar.getModel()).add(new CalendarEventEntity(entity, getMappings()));
            setTargetEntity(entity);
            return true;
        }
        return false;
    }

    @Override
    protected boolean processEntityUpdate(Entity entity) {
        if (hasTargetType() && getTargetType().isInstance(entity)) {
            if (calendar.getModel() != null) {
                CalendarEventWrapper ce = new CalendarEventEntity(entity, getMappings());

                int index = ((SimpleCalendarModel) calendar.getModel()).indexOf(ce);
                if (index >= 0) {
                    ((SimpleCalendarModel) calendar.getModel()).update(ce);

                    //it seems that Calendars holds another internal list of ce besides the model!!!!
                    String id = calendar.getCalendarEventId(ce);
                    ((CalendarEventWrapper) calendar.getCalendarEventById(id)).update(entity);

                    if (hasTargetEntity() && getTargetEntity().equals(entity)) {
                        targetEntity = entity;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected boolean processEntityDeletion(Entity entity) {
        if (canBind(entity)) {
            if (hasMasterEntity() && getMasterEntity().equals(entity)) {
                setMasterEntity(null);
                return true;
            } else if (calendar.getModel() != null) {
                CalendarEventWrapper ce = new CalendarEventEntity(entity, getMappings());

                int index = ((SimpleCalendarModel) calendar.getModel()).indexOf(ce);
                if (index >= 0) {
                    if (hasTargetEntity() && getTargetEntity().equals(entity)) {
                        setTargetEntity(null);
                    }
                    ((SimpleCalendarModel) calendar.getModel()).remove(index);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void arrangeForNullTargetType() {
        super.arrangeForNullTargetType();
        activeQuery = null;
        setTargetEntity(null);
        calendar.setDroppable("false");
        unregisterCommand(CommandEnum.QUERY);
        unregisterCommand(CommandEnum.REFRESH);
        unregisterCommand(CommandEnum.ADDNEW);
        unregisterCommand(CommandEnum.UPDATE);
        unregisterCommand(CommandEnum.DELETE);
        unregisterCommand(CommandEnum.MOVE_LEFT);
        unregisterCommand(CommandEnum.MOVE_RIGHT);
        unregisterCommand(CalendarCommandEnum.CALENDAR_VIEW);
        arrangeForState(PanelState.UNDEFINED);
    }

    @Override
    protected void arrangeForState(PanelState newState) {
        super.arrangeForState(newState);
        activateCommand(CalendarCommandEnum.CALENDAR_VIEW, true);
        activateCommand(CommandEnum.MOVE_LEFT, true);
        activateCommand(CommandEnum.MOVE_RIGHT, true);
        activateCommand(CommandEnum.REFRESH, true);
        activateCommand(CalendarCommandEnum.CALENDAR_MONTH_VIEW, true);
        arrangeForMold();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        calendar.setDays(getSettingValue(CalendarSettingEnum.CALENDAR_DAYS, 7));
        calendar.setTimeslots(getSettingValue(CalendarSettingEnum.CALENDAR_TIMESLOTS, 2));
        calendar.setFirstDayOfWeek(getSettingValue(CalendarSettingEnum.CALENDAR_FIRST_DAY_OF_WEEK, 1));

        arrangeForNullTargetType();
        if (hasTargetType()) {
            arrangeForTargetType();
            arrangeForState(PanelState.READY);
        } else {
            arrangeForState(PanelState.UNDEFINED);
        }

    }

    @Override
    protected void arrangeForTargetType() {
        registerCommand(ContextUtil.getDefaultCommand(CommandEnum.REFRESH, this));
        registerCommand(ContextUtil.getDefaultCommand(CommandEnum.ADDNEW, this));
        registerCommand(ContextUtil.getDefaultCommand(CommandEnum.UPDATE, this));
        registerCommand(ContextUtil.getDefaultCommand(CommandEnum.DELETE, this));
        registerCommand(ContextUtil.getDefaultCommand(CommandEnum.MOVE_LEFT, this));
        registerCommand(ContextUtil.getDefaultCommand(CommandEnum.MOVE_RIGHT, this));
        registerCommand(ContextUtil.getDefaultCommand(CalendarCommandEnum.CALENDAR_VIEW, this));
        arrangeForState(PanelState.READY);
        arrangeForMold();
        calendar.setDroppable("true");
        calendar.removeEventListener(Events.ON_DROP, calendarsEventHandler); //its called many times
        calendar.addEventListener(Events.ON_DROP, calendarsEventHandler);
    }

    @Override
    public boolean hasTargetEntity() {
        return targetEntity != null;
    }

    @Override
    public Entity getTargetEntity() {
        return targetEntity;
    }

    @Override
    protected <T extends Serializable> void onSettingValueChanged(SettingEnum id, T oldValue, T newValue) {
        if (SettingEnum.MOLD.equals(id)) {
            calendar.setMold((String) newValue);
            arrangeForMold();
        } else if (CalendarSettingEnum.CALENDAR_DAYS.equals(id)) {
            calendar.setDays((Integer) newValue);
        } else if (CalendarSettingEnum.CALENDAR_TIMESLOTS.equals(id)) {
            calendar.setTimeslots((Integer) newValue);
        } else if (CalendarSettingEnum.CALENDAR_TIMEZONE.equals(id)) {
            for (Object timeZone : calendar.getTimeZones().keySet()) {
                calendar.removeTimeZone((TimeZone) timeZone);
            }
            if (StringUtils.hasText((String) newValue)) {
                calendar.setTimeZone((String) newValue);
            }
        } else if (CalendarSettingEnum.CALENDAR_WEEK_OF_YEAR.equals(id)) {
            calendar.setWeekOfYear((Boolean) newValue);
        } else if (CalendarSettingEnum.CALENDAR_FIRST_DAY_OF_WEEK.equals(id)) {
            calendar.setFirstDayOfWeek((Integer) newValue);
        } else if (CalendarSettingEnum.CALENDAR_START_TIME.equals(id)) {
            calendar.setBeginTime((Integer) newValue);
        } else if (CalendarSettingEnum.CALENDAR_END_TIME.equals(id)) {
            calendar.setEndTime((Integer) newValue);
        }

        super.onSettingValueChanged(id, oldValue, newValue);

    }

    @Override
    protected void registerSettings() {
        super.registerSettings();
        registerSetting(SettingEnum.MOLD, null);

        registerSetting(CalendarSettingEnum.CALENDAR_DAYS, 7);
        registerSetting(CalendarSettingEnum.CALENDAR_TIMESLOTS, 2);
        registerSetting(CalendarSettingEnum.CALENDAR_TIMEZONE, null);
        registerSetting(CalendarSettingEnum.CALENDAR_FIRST_DAY_OF_WEEK, Calendar.MONDAY);
        registerSetting(CalendarSettingEnum.CALENDAR_WEEK_OF_YEAR, false);
        registerSetting(CalendarSettingEnum.CALENDAR_START_TIME, 7);
        registerSetting(CalendarSettingEnum.CALENDAR_END_TIME, 24);

        registerSetting(CalendarSettingEnum.CALENDAR_EVENT_START, null);
        registerSetting(CalendarSettingEnum.CALENDAR_EVENT_END, null);
        registerSetting(CalendarSettingEnum.CALENDAR_EVENT_TITLE, null);
        registerSetting(CalendarSettingEnum.CALENDAR_EVENT_TITLE_COLOR, null);
        registerSetting(CalendarSettingEnum.CALENDAR_EVENT_CONTENT, null);
        registerSetting(CalendarSettingEnum.CALENDAR_EVENT_CONTENT_COLOR, null);
        registerSetting(CalendarSettingEnum.CALENDAR_EVENT_LOCKED, null);
    }

    @Override
    public Set<CommandEnum> getSupportedCommands() {
        Set<CommandEnum> supported = new HashSet<CommandEnum>(super.getSupportedCommands());
        supported.add(CommandEnum.REFRESH);
        supported.add(CommandEnum.ADDNEW);
        supported.add(CommandEnum.UPDATE);
        supported.add(CommandEnum.DELETE);
        supported.add(CommandEnum.MOVE_LEFT);
        supported.add(CommandEnum.MOVE_RIGHT);

        supported.add(CalendarCommandEnum.CALENDAR_VIEW);
        return Collections.unmodifiableSet(supported);
    }

    private void arrangeForMold() {
        boolean monthView = "month".equals(calendar.getMold());
        activateCommand(CalendarCommandEnum.CALENDAR_1DAY_VIEW, !monthView);
        activateCommand(CalendarCommandEnum.CALENDAR_5DAY_VIEW, !monthView);
        activateCommand(CalendarCommandEnum.CALENDAR_7DAY_VIEW, !monthView);

        if (hasCommand(CalendarCommandEnum.CALENDAR_MONTH_VIEW)) {
            getCommand(CalendarCommandEnum.CALENDAR_MONTH_VIEW).setValue(monthView);
        }

        if (!monthView) {
            switch (calendar.getDays()) {
                case 1:
                    if (hasCommand(CalendarCommandEnum.CALENDAR_1DAY_VIEW)) {
                        getCommand(CalendarCommandEnum.CALENDAR_1DAY_VIEW).setValue(true);
                        activateCommand(CalendarCommandEnum.CALENDAR_1DAY_VIEW, false);
                    }
                    if (hasCommand(CalendarCommandEnum.CALENDAR_5DAY_VIEW)) {
                        getCommand(CalendarCommandEnum.CALENDAR_5DAY_VIEW).setValue(false);
                        activateCommand(CalendarCommandEnum.CALENDAR_5DAY_VIEW, true);
                    }
                    if (hasCommand(CalendarCommandEnum.CALENDAR_7DAY_VIEW)) {
                        getCommand(CalendarCommandEnum.CALENDAR_7DAY_VIEW).setValue(false);
                        activateCommand(CalendarCommandEnum.CALENDAR_7DAY_VIEW, true);
                    }
                    break;
                case 5:
                    if (hasCommand(CalendarCommandEnum.CALENDAR_5DAY_VIEW)) {
                        getCommand(CalendarCommandEnum.CALENDAR_5DAY_VIEW).setValue(true);
                        activateCommand(CalendarCommandEnum.CALENDAR_5DAY_VIEW, false);
                    }
                    if (hasCommand(CalendarCommandEnum.CALENDAR_1DAY_VIEW)) {
                        getCommand(CalendarCommandEnum.CALENDAR_1DAY_VIEW).setValue(false);
                        activateCommand(CalendarCommandEnum.CALENDAR_1DAY_VIEW, true);
                    }
                    if (hasCommand(CalendarCommandEnum.CALENDAR_7DAY_VIEW)) {
                        getCommand(CalendarCommandEnum.CALENDAR_7DAY_VIEW).setValue(false);
                        activateCommand(CalendarCommandEnum.CALENDAR_7DAY_VIEW, true);
                    }
                    break;
                case 7:
                    if (hasCommand(CalendarCommandEnum.CALENDAR_7DAY_VIEW)) {
                        getCommand(CalendarCommandEnum.CALENDAR_7DAY_VIEW).setValue(true);
                        activateCommand(CalendarCommandEnum.CALENDAR_7DAY_VIEW, false);
                    }
                    if (hasCommand(CalendarCommandEnum.CALENDAR_1DAY_VIEW)) {
                        getCommand(CalendarCommandEnum.CALENDAR_1DAY_VIEW).setValue(false);
                        activateCommand(CalendarCommandEnum.CALENDAR_1DAY_VIEW, true);
                    }
                    if (hasCommand(CalendarCommandEnum.CALENDAR_5DAY_VIEW)) {
                        getCommand(CalendarCommandEnum.CALENDAR_5DAY_VIEW).setValue(false);
                        activateCommand(CalendarCommandEnum.CALENDAR_5DAY_VIEW, true);
                    }
                    break;
            }
        }

        //refresh();
    }

    @Override
    protected void processValidCommand(Command command) {
        if (CalendarCommandEnum.CALENDAR_MONTH_VIEW.equals(command.getId())) {
            if ((Boolean) command.getValue()) {
                calendar.setMold("month");
                arrangeForMold();
            } else {
                calendar.setMold("default");
                arrangeForMold();
            }
            refresh();
        } else if (CalendarCommandEnum.CALENDAR_1DAY_VIEW.equals(command.getId())) {
            calendar.setDays(1);
            arrangeForMold();
            refresh();
        } else if (CalendarCommandEnum.CALENDAR_5DAY_VIEW.equals(command.getId())) {
            calendar.setDays(5);
            arrangeForMold();
            refresh();
        } else if (CalendarCommandEnum.CALENDAR_7DAY_VIEW.equals(command.getId())) {
            calendar.setDays(7);
            arrangeForMold();
            refresh();
        } else if (CommandEnum.MOVE_LEFT.equals(command.getId())) {
            calendar.previousPage();
            refresh();
            setTargetEntity(null);
        } else if (CommandEnum.MOVE_RIGHT.equals(command.getId())) {
            calendar.nextPage();
            refresh();
            setTargetEntity(null);
        } else if (CommandEnum.REFRESH.equals(command.getId())) {
            calendar.setModel(null);
            minBeginDate = new Date();
            maxEndDate = minBeginDate;
            refresh();
        } else if (CommandEnum.ADDNEW.equals(command.getId())) {
            if (hasTargetType()) {
                Entity templEntity = prepareMutableInstance(MutableMode.INSERT);

                if (command.getArg(ATTRIB_BEGIN_DATE, Date.class) != null || command.getArg(ATTRIB_END_DATE,
                        Date.class) != null) {
                    CalendarEventWrapper CalendarEventWrapper = getCalendarEventWrapper(templEntity, getMappings());
                    CalendarEventWrapper.setBeginDate(command.getArg(ATTRIB_BEGIN_DATE, Date.class));
                    CalendarEventWrapper.setEndDate(command.getArg(ATTRIB_END_DATE, Date.class));
                    templEntity.calculate();
                    command.removeArgs();
                }

                EntityPersisterDialog dialog = ContextUtil.getDefaultDialog(EntityPersisterDialog.class,
                        templEntity, getSettings(), MutableMode.INSERT);
                dialog.setL10nMode(getL10nMode());
                dialog.setDirty(true);
                dialog.show(dialogListener);
            }

        } else if (CommandEnum.UPDATE.equals(command.getId())) {
            if (hasTargetEntity()) {
                EntityPersisterDialog dialog = ContextUtil.getDefaultDialog(EntityPersisterDialog.class,
                        prepareMutableInstance(MutableMode.UPDATE), getSettings(), MutableMode.UPDATE);
                dialog.setL10nMode(getL10nMode());
                dialog.show(dialogListener);
            }
        } else {
            super.processValidCommand(command);
        }
    }

    @Override
    public void processMessage(Message message) {
        switch (message.getId()) {
            case QUERY:
                Query query = message.getArg(MessageArgEnum.ARG_ITEM, Query.class);
                if (hasTargetType() && query != null && getTargetType().equals(query.getTargetType())) {
                    activeQuery = query;
                    refresh();
                }
                break;
            case ENTITY_INSERTED:
                // lists process entity inserts sent from them only because this is
                // the only
                // way to make sure the added entity matches tne active query
                break;
            case BIND_DIRECT:
                bindEcho(message.getArg(MessageArgEnum.ARG_ITEM, Entity.class));
                break;
            default:
                super.processMessage(message);
                break;
        }
    }

    private Map<CalendarSettingEnum, Setting> getMappings() {
        Map<CalendarSettingEnum, Setting> mappings = new HashMap<CalendarSettingEnum, Setting>();
        mappings.put(CalendarSettingEnum.CALENDAR_EVENT_START, getSetting(CalendarSettingEnum
                .CALENDAR_EVENT_START));
        mappings.put(CalendarSettingEnum.CALENDAR_EVENT_END, getSetting(CalendarSettingEnum.CALENDAR_EVENT_END));
        mappings.put(CalendarSettingEnum.CALENDAR_EVENT_TITLE, getSetting(CalendarSettingEnum
                .CALENDAR_EVENT_TITLE));
        mappings.put(CalendarSettingEnum.CALENDAR_EVENT_TITLE_COLOR, getSetting(CalendarSettingEnum
                .CALENDAR_EVENT_TITLE_COLOR));
        mappings.put(CalendarSettingEnum.CALENDAR_EVENT_CONTENT, getSetting(CalendarSettingEnum
                .CALENDAR_EVENT_CONTENT));
        mappings.put(CalendarSettingEnum.CALENDAR_EVENT_CONTENT_COLOR, getSetting(CalendarSettingEnum
                .CALENDAR_EVENT_CONTENT_COLOR));
        mappings.put(CalendarSettingEnum.CALENDAR_EVENT_LOCKED, getSetting(CalendarSettingEnum
                .CALENDAR_EVENT_LOCKED));
        return mappings;
    }

    private CalendarModel toCalendarModel(List<Entity> entities) {
        SimpleCalendarModel model = (SimpleCalendarModel) calendar.getModel();
        if (model == null) {
            model = new SimpleCalendarModel();
        }

        for (Entity entity : entities) {
            CalendarEvent calendarsEvent = getCalendarEventWrapper(entity, getMappings());

            model.remove(calendarsEvent);
            model.add(calendarsEvent);
        }

        return model;
    }

    protected CalendarEventWrapper getCalendarEventWrapper(Entity entity, Map<CalendarSettingEnum,
            Setting> mappings) {
        return new CalendarEventEntity(entity, mappings);
    }

    @Override
    public void render() {
        super.render();
        refresh();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void refresh() {
        if (calendar.getPage() != null && (calendar.getBeginDate().before(minBeginDate) || calendar.getEndDate().after
                (maxEndDate)) || hasMasterEntity()) {
            if (calendar.getBeginDate().before(minBeginDate)) {
                minBeginDate = calendar.getBeginDate();
            }
            if (calendar.getEndDate().after(maxEndDate)) {
                maxEndDate = calendar.getEndDate();
            }

            activeQuery = null;
            Query query = getFinalQuery(calendar.getBeginDate(), calendar.getEndDate());
            if (query != null) {
                setTargetEntity(null);
                calendar.setModel(toCalendarModel(ContextUtil.getDRS().findByQuery(query)));
                arrangeForState(PanelState.BROWSING);
            } else {
                minBeginDate = new Date();
                maxEndDate = minBeginDate;
            }
        }
    }

    private Query getFinalQuery(Date startDate, Date endDate) {
        if (activeQuery == null && hasTargetType()) {
            String startProperty = getSettingValue(CalendarSettingEnum
                    .CALENDAR_EVENT_START, null);
            String endProperty = getSettingValue(CalendarSettingEnum
                    .CALENDAR_EVENT_END, null);

            if (StringUtils.hasText(startProperty) && StringUtils.hasText(endProperty)) {
                activeQuery = ContextUtil.getEntityFactory().buildQuery(getTargetType());
                activeQuery.addCriterion(new Path(startProperty), Condition.GTE, startDate);
                activeQuery.addCriterion(new Path(endProperty), Condition.LT, endDate);
            }
        }

        if (activeQuery != null) {
            Query finalQuery;
            if (isMasterDetail()) {
                if (!hasMasterEntity()) {
                    return null;
                }

                finalQuery = (Query) activeQuery.clone();
                for (Criterion criterion : activeQuery.getCriteria()) {
                    finalQuery.addCriterion(criterion);
                }
                for (OrderBy orderBy : activeQuery.getOrderings()) {
                    finalQuery.addOrderBy(orderBy);
                }

                finalQuery.addCriterion(new Path(getBindProperty()), Condition.EQ, getMasterEntity(), true);

                return finalQuery;
            } else {
                return activeQuery;
            }
        }
        return null;
    }

    private class CalendarsEventHandler implements EventListener<Event> {

        @Override
        public void onEvent(Event event) throws Exception {
            if (CalendarsEvent.ON_EVENT_CREATE.equals(event.getName())) {
                if (hasCommand(CommandEnum.ADDNEW)) {
                    CalendarsEvent cevt = (CalendarsEvent) event;
                    Command addnew = getCommand(CommandEnum.ADDNEW);
                    addnew.setArg(ATTRIB_BEGIN_DATE, adjustStartTime(cevt.getBeginDate()));
                    addnew.setArg(ATTRIB_END_DATE, adjustEndTime(cevt.getEndDate()));
                    processValidCommand(addnew);
                    addnew.removeArgs();
                }
            } else if (CalendarsEvent.ON_EVENT_EDIT.equals(event.getName())) {
                CalendarsEvent cevt = (CalendarsEvent) event;
                Panel entityView = CoreUtil.getEntityViewPanel(((CalendarEventWrapper) cevt
                        .getCalendarEvent()).getEntity());
                if (entityView != null) {
                    dispatchMessage(ContextUtil.getMessage(MessageEnum.ADOPT_ME,
                            entityView));
                }
            } else if (CalendarsEvent.ON_EVENT_UPDATE.equals(event.getName())) {
                if (hasCommand(CommandEnum.UPDATE)) {
                    Clients.showBusy((Component) base, null);
                    CalendarsEvent cevt = (CalendarsEvent) event;
                    cevt.stopClearGhost();
                    CalendarEventWrapper calendarEventWrapper = (CalendarEventWrapper) cevt.getCalendarEvent();
                    Entity entityBefore = calendarEventWrapper.getEntity().clone();
                    Entity entityAfter = calendarEventWrapper.getEntity();
                    calendarEventWrapper.setBeginDate(cevt.getBeginDate());
                    calendarEventWrapper.setEndDate(cevt.getEndDate());
                    if (entityAfter.validate().isEmpty()) {
                        processEntityUpdate(entityAfter);
                        Events.echoEvent(ON_EVENT_UPDATE_ECHO, event.getTarget(), entityAfter);
                    } else {
                        calendarEventWrapper.update(entityBefore);  //rollback
                        ZkUtil.displayMessage(L10nMessages.L10N_VALIDATION_ERRORS.toString(),
                                true, calendar);
                        Clients.clearBusy((Component) base);
                        cevt.clearGhost();
                    }
                }
            } else if (ON_EVENT_UPDATE_ECHO.equals(event.getName())) {
                Clients.clearBusy((Component) base);
                Entity entity = (Entity) event.getData();

                try {
                    ContextUtil.getDWS().save(entity);
                } catch (Exception e) {
                    ZkUtil.displayMessage(DefaultEntityPersisterDialog.L10N_MESSAGE_UNEXPECTED_ERRORS.toString(),
                            true, calendar);
                }

                dispatchMessage(ContextUtil.getMessage(ENTITY_UPDATED, DefaultCalendarViewPanel.this,
                        MessageArgEnum.ARG_ITEM, entity));

            } else if (CalendarsEvent.ON_DAY_CLICK.equals(event.getName())) {
                Clients.showBusy(null);
                Events.echoEvent(ON_DAY_CLICK_ECHO, event.getTarget(), event.getData());
            } else if (CalendarsEvent.ON_WEEK_CLICK.equals(event.getName())) {
                Clients.showBusy(null);
                Events.echoEvent(ON_WEEK_CLICK_ECHO, event.getTarget(), event.getData());
            } else if (ON_DAY_CLICK_ECHO.equals(event.getName())) {
                focus(((Date) event.getData()), 1);
            } else if (ON_WEEK_CLICK_ECHO.equals(event.getName())) {
                focus(((Date) event.getData()), 7);
            } else if (Events.ON_DROP.equals(event.getName())) {
                handleEntityDrop((CalendarDropEvent) event);
            }
        }

        private void handleEntityDrop(CalendarDropEvent event) {
            if (event.getDragged() instanceof Listitem && hasCommand(CommandEnum.ADDNEW)) {

                final Listitem draggedItem = (Listitem) event.getDragged();
                final ListModelList model = (ListModelList) draggedItem.getListbox().getModel();
                final Entity draggedEntity = (Entity) model.getElementAt(draggedItem.getIndex());
                Entity selectedEntity = null;
                if (!model.getSelection().isEmpty()) {
                    selectedEntity = (Entity) model.getSelection().iterator().next();
                }

                if (selectedEntity == null) {
                    draggedItem.setSelected(true);
                } else if (draggedEntity.equals(getTargetEntity())) {
                    return;
                } else if (draggedEntity.getEntityType().equals(getMasterType())) {
                    return;
                }

                Entity trgentity = prepareMutableInstance(MutableMode.INSERT);
                if (trgentity == null) {
                    return;
                }
                if (getTargetType().equals(draggedEntity.getEntityType())) {
                    trgentity = draggedEntity.clone();
                } else {
                    applyCurrentCritriaValues(getFinalQuery(calendar.getBeginDate(), calendar.getEndDate()), trgentity);
                    for (final PropertyMetadata propertyMetadata : ContextUtil.getMRS().getEntityMetadata
                            (getTargetType()
                            ).getPropertiesMetadata()) {
                        if (propertyMetadata.isInsertable() && !propertyMetadata.getName().equals(getBindProperty()) &&
                                propertyMetadata.isAssociationType() && propertyMetadata.getAssociatedEntityMetadata()
                                .getEntityType().equals(draggedEntity.getEntityType())) {

                            propertyMetadata.setValue(trgentity, draggedEntity);
                            // break;
                        }
                    }
                }

                Date start = adjustStartTime(event.getDate());
                Date end = adjustEndTime(event.getDate());

                if (!end.after(start)) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(end);
                    c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY) + 1);
                    end = c.getTime();
                }

                //Set start time
                ContextUtil.getMRS().getPropertyMetadata(trgentity.getEntityType(),
                        getMappings().get(CalendarSettingEnum.CALENDAR_EVENT_START).getValue().toString()).setValue
                        (trgentity, start);

                //Set end time
                ContextUtil.getMRS().getPropertyMetadata(trgentity.getEntityType(),
                        getMappings().get(CalendarSettingEnum.CALENDAR_EVENT_END).getValue().toString()).setValue
                        (trgentity, end);


                trgentity.setAsNew();
                EntityPersisterDialog dialog = ContextUtil.getDefaultDialog(EntityPersisterDialog.class, trgentity,
                        getSettings(), MutableMode.INSERT, false);
                dialog.setL10nMode(getL10nMode());
                dialog.setDirty(true);
                dialog.show(dialogListener);
            }
        }

        private Date adjustStartTime(Date start) {
            Calendar c = Calendar.getInstance();
            c.setTime(start);

            if (c.get(Calendar.HOUR_OF_DAY) < getSettingValue(CalendarSettingEnum.CALENDAR_START_TIME, 0)) {
                c.set(Calendar.HOUR_OF_DAY, getSettingValue(CalendarSettingEnum.CALENDAR_START_TIME, 0));
            }

            return c.getTime();

        }

        private Date adjustEndTime(Date end) {
            Calendar c = Calendar.getInstance();
            c.setTime(end);

            if (c.get(Calendar.HOUR_OF_DAY) < getSettingValue(CalendarSettingEnum.CALENDAR_START_TIME, 0)) {
                c.set(Calendar.HOUR_OF_DAY, getSettingValue(CalendarSettingEnum.CALENDAR_START_TIME, 0) + 1);
            }

            return c.getTime();

        }

    }

    private void focus(Date date, int span) {
        Clients.clearBusy();
        CalendarViewPanel panel = ContextUtil.getDefaultPanel(CalendarViewPanel.class);
        panel.setSettings(getSettings());
        panel.setCurrentDate(date);
        panel.setSettingValue(SettingEnum.PANEL_NAME, panel.getSettingValue(SettingEnum.PANEL_NAME,
                "") + " (Focused)");
        panel.setSettingValue(SettingEnum.MOLD, "default");
        panel.setSettingValue(CalendarSettingEnum.CALENDAR_DAYS, span);
        if (hasMasterEntity()) {
            panel.setMasterEntity(getMasterEntity());
        }
        dispatchMessage(ContextUtil.getMessage(MessageEnum.ADOPT_ME, panel));
    }


    private class DialogListener implements MessageListener {
        @Override
        public void processMessage(Message message) {
            switch (message.getId()) {
                case AFFIRMATIVE_RESPONSE:
                    if (QueryDialog.class.isInstance(message.getSender())) {
                        DefaultCalendarViewPanel.this.processMessage(ContextUtil.getMessage(QUERY, this,
                                message.getArgs()));
                    }
                    break;
                case ENTITY_UPDATED:
                    //processEntityUpdate(message.getArg(MessageArgEnum.ARG_ITEM, Entity.class));
                    dispatchMessage(message);
                    break;
                case ENTITY_INSERTED:
                    processEntityInsertion(message.getArg(MessageArgEnum.ARG_ITEM, Entity.class));
                    break;
            }
        }
    }

    private class MyDateFormatter extends SimpleDateFormatter {
        private SimpleDateFormat _df;
        private String _dayFormat = "EEE, MMM d";

        public String getCaptionByDate(Date date, Locale locale, TimeZone timezone) {
            if (_df == null) {
                _df = new SimpleDateFormat(_dayFormat, locale);
            }
            _df.setTimeZone(timezone);
            return _df.format(date);
        }
    }

}