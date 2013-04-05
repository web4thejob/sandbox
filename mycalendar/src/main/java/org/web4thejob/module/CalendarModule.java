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

import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.web4thejob.setting.CalendarSettingEnum;
import org.web4thejob.util.L10nString;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Veniamin Isaias
 * @since 2.0.0
 */

@SuppressWarnings("rawtypes")
@Component
public class CalendarModule extends AbstractModule implements LocalizableModule {

    @Override
    public int getOrdinal() {
        return 6;
    }

    @Override
    public Collection<L10nString> getLocalizableStrings(final Set<Class> classes) {
        final Set<Class> localizableModuleClasses = new HashSet<Class>();
        final List<L10nString> strings = new ArrayList<L10nString>();

        //add here all module classes that need to display localizable resources on external panels
        localizableModuleClasses.add(CalendarSettingEnum.class);

        for (Class<?> clazz : localizableModuleClasses) {
            ReflectionUtils.doWithFields(clazz, new ReflectionUtils.FieldCallback() {
                        @Override
                        public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                            strings.add((L10nString) field.get(null));
                        }
                    }, new ReflectionUtils.FieldFilter() {
                        @Override
                        public boolean matches(Field field) {
                            return ReflectionUtils.isPublicStaticFinal(field) && L10nString.class.equals(field
                                    .getType()) && classes.contains(((L10nString) ReflectionUtils.getField
                                    (field, null)).getDeclaringClass());
                        }
                    }
            );
        }


        return strings;
    }
}
