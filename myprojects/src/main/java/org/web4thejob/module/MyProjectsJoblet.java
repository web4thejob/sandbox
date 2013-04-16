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

package org.web4thejob.module;

import job.myprojects.Country;
import job.myprojects.TaskAttachment;
import job.myprojects.VenueNotes;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.Entity;
import org.web4thejob.orm.EntityMetadata;
import org.web4thejob.orm.ORMUtil;
import org.web4thejob.web.panel.EntityViewPanel;
import org.web4thejob.web.panel.ListViewPanel;
import org.web4thejob.web.panel.MutableEntityViewPanel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Veniamin Isaias
 * @since 2.0.0
 */
@Component
public class MyProjectsJoblet extends AbstractJoblet {
    @Override
    public int getOrdinal() {
        return 99;
    }

    @Override
    public boolean isInstalled() {
        return true;
    }

    @Override
    public String[] getSchemas() {
        return new String[]{"mypm"};
    }

    @Override
    public <E extends Exception> List<E> setup() {

        sampleListAndEntityPanels();
        sampleCountries();

        return super.setup();
    }

    private void sampleListAndEntityPanels() {
        for (EntityMetadata emd : ContextUtil.getMRS().getEntityMetadatas()) {
            if (emd.getSchema().equals("mypm")) {

                if (emd.getEntityType().equals(TaskAttachment.class) || emd.getEntityType().equals(VenueNotes.class))
                    continue;

                ListViewPanel listViewPanel = ContextUtil.getDefaultPanel(ListViewPanel.class);
                listViewPanel.setTargetType(emd.getEntityType());
                ORMUtil.persistPanel(listViewPanel);

                EntityViewPanel entityViewPanel = ContextUtil.getDefaultPanel(MutableEntityViewPanel.class);
                entityViewPanel.setTargetType(emd.getEntityType());
                ORMUtil.persistPanel(entityViewPanel);
            }
        }
    }

    private void sampleCountries() {
        List<Entity> countries = new ArrayList<Entity>();
        Country country;

        country = new Country();
        country.setId("PT");
        country.setName("Portugal");
        countries.add(country);

        country = new Country();
        country.setId("IE");
        country.setName("Ireland");
        countries.add(country);

        country = new Country();
        country.setId("IT");
        country.setName("Italy");
        countries.add(country);

        country = new Country();
        country.setId("GR");
        country.setName("Greece");
        countries.add(country);

        country = new Country();
        country.setId("ES");
        country.setName("Spain");
        countries.add(country);

        ContextUtil.getDWS().save(countries);
    }

    @Override
    public List<Resource> getResources() {
        List<Resource> resources = new ArrayList<Resource>();

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            for (Resource resource : resolver.getResources("classpath*:job/myprojects/**/*.hbm.xml")) {
                resources.add(resource);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return resources;
    }

    @Override
    public String getBasePackage() {
        return "job.myprojects";
    }
}
