package org.web4thejob.sandbox.web.panel;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndPerson;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatterBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.util.StringUtils;
import org.web4thejob.setting.SettingEnum;
import org.web4thejob.web.panel.base.zk.AbstractZkContentPanel;
import org.web4thejob.web.util.ZkUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.*;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@org.springframework.stereotype.Component
@Scope("prototype")
public class DefaultFeedReaderPanel extends AbstractZkContentPanel implements FeedReaderPanel {
    private final SyndFeedInput input = new SyndFeedInput();
    private final Vlayout vlayout = new Vlayout();
    private URL feedUrl;

    public DefaultFeedReaderPanel() {
        ZkUtil.setParentOfChild((Component) base, vlayout);
        vlayout.setVflex("true");
        vlayout.setStyle("overflow: auto;");
    }

    @Override
    protected void registerSettings() {
        super.registerSettings();
        registerSetting(SettingEnum.TARGET_URL, null);
    }

    @Override
    protected <T extends Serializable> void onSettingValueChanged(SettingEnum id, T oldValue, T newValue) {
        if (SettingEnum.TARGET_URL.equals(id)) {
            renderFeeed();
        } else {
            super.onSettingValueChanged(id, oldValue, newValue);
        }
    }

    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        ((org.zkoss.zul.Panel) base).getPanelchildren().setStyle("overflow: auto;");
        renderFeeed();
    }

    protected void renderFeeed() {
        if (!StringUtils.hasText(getSettingValue(SettingEnum.TARGET_URL, ""))) {
            vlayout.getChildren().clear();
            return;
        }
        if (feedUrl != null) {
            if (feedUrl.toExternalForm().equals(getSettingValue(SettingEnum.TARGET_URL, ""))) {
                return;
            }
        }

        vlayout.getChildren().clear();
        try {
            feedUrl = new URL(getSettingValue(SettingEnum.TARGET_URL, "").trim());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }


        try {
            SyndFeed feed = input.build(new XmlReader(feedUrl));

            Date now = new Date();
            DateTime lastPublish = null;
            Groupbox groupbox;
            Grid grid = null;
            int groupsno = 0;
            Iterator entryIter = feed.getEntries().iterator();
            while (entryIter.hasNext()) {
                SyndEntry entry = (SyndEntry) entryIter.next();

                if (lastPublish == null || DateTimeComparator.getDateOnlyInstance().compare(lastPublish,
                        new DateTime(getPublishDate(entry).getTime())) != 0) {
                    lastPublish = new DateTime(getPublishDate(entry).getTime());

                    groupsno += 1;
                    groupbox = new Groupbox();
                    groupbox.setOpen(groupsno <= 3);
                    groupbox.setMold("3d");
                    groupbox.setWidth("");
                    groupbox.setParent(vlayout);
                    Caption caption = new Caption(lastPublish.toString("MMM dd, yyyy"));
                    caption.setHflex("true");
                    caption.setParent(groupbox);

                    grid = new Grid();
                    grid.setParent(groupbox);
                    new Rows().setParent(grid);
                }

                Row row = new Row();
                row.setParent(grid.getRows());

                Vlayout vlayout = new Vlayout();
                vlayout.setParent(row);

                A title = new A(entry.getTitle());
                title.setHref(entry.getLink());
                title.setTarget("_blank");
                title.setParent(vlayout);

                Hlayout hlayout = new Hlayout();
                hlayout.setParent(vlayout);

                A user = new A(entry.getAuthor());
                user.setParent(hlayout);
                user.setTarget("_blank");
                user.setHref(((SyndPerson) entry.getAuthors().get(0)).getUri());
                user.setStyle("font-style:italic;");

                Period period = new Period(getPublishDate(entry).getTime(), now.getTime());
                Label hours = new Label(elapsedTime(period));
                hours.setParent(hlayout);


            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public Date getPublishDate(SyndEntry entry) {
        return entry.getPublishedDate() != null ? entry.getPublishedDate() : entry.getUpdatedDate();
    }

    private String elapsedTime(Period period) {
        String elapsed;

        if (period.getMonths() == 0 && period.toStandardHours().getHours() < 1) {
            elapsed = period.toString(new PeriodFormatterBuilder().appendMinutes().appendSuffix(" minute",
                    " minutes").toFormatter());
        } else if (period.getMonths() == 0 && period.toStandardDays().getDays() < 1) {
            elapsed = period.toString(new PeriodFormatterBuilder().appendHours().appendSuffix(" hour",
                    " hours").toFormatter());
        } else if (period.getMonths() == 0 && period.toStandardWeeks().getWeeks() < 1) {
            elapsed = period.toString(new PeriodFormatterBuilder().appendDays().appendSuffix(" day",
                    " days").toFormatter());
        } else if (period.getMonths() == 0 && period.toStandardWeeks().getWeeks() < 4) {
            elapsed = period.toString(new PeriodFormatterBuilder().appendWeeks().appendSuffix(" week",
                    " weeks").toFormatter());
        } else {
            elapsed = period.toString(new PeriodFormatterBuilder()
                    .appendYears().appendSuffix(" year", " years").appendSeparator(", ", " and ")
                    .appendMonths().appendSuffix(" month", " months").appendSeparator(", ", " and ")
                    .appendWeeks().appendSuffix(" week", " weeks").appendSeparator(", ", " and ")
                    .appendDays().appendSuffix(" day", " days").toFormatter());
        }

        return elapsed + " ago";
    }


    @Override
    public void render() {
        super.render();
        renderFeeed();
    }
}
