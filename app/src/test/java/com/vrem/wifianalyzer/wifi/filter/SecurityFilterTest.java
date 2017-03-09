/*
 * WiFiAnalyzer
 * Copyright (C) 2017  VREM Software Development <VREMSoftwareDevelopment@gmail.com>
 *  
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.vrem.wifianalyzer.wifi.filter;

import com.vrem.util.EnumUtils;
import com.vrem.wifianalyzer.R;
import com.vrem.wifianalyzer.settings.Settings;
import com.vrem.wifianalyzer.wifi.model.Security;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SecurityFilterTest {
    @Mock
    private Settings settings;

    private SecurityFilter fixture;

    @Before
    public void setUp() {
        fixture = new SecurityFilter(EnumUtils.values(Security.class));
    }

    @Test
    public void testIsActive() throws Exception {
        assertFalse(fixture.isActive());
    }

    @Test
    public void testIsActivateWithChanges() throws Exception {
        // setup
        fixture.toggle(Security.WPA);
        // execute & validate
        assertTrue(fixture.isActive());
    }

    @Test
    public void testContains() throws Exception {
        for (Security security : Security.values()) {
            assertTrue(fixture.contains(security));
        }
    }

    @Test
    public void testToggleRemoves() throws Exception {
        // execute
        boolean actual = fixture.toggle(Security.WEP);
        // validate
        assertTrue(actual);
        assertFalse(fixture.contains(Security.WEP));
    }

    @Test
    public void testToggleAdds() throws Exception {
        // setup
        fixture.toggle(Security.WPA);
        // execute
        boolean actual = fixture.toggle(Security.WPA);
        // validate
        assertTrue(actual);
        assertTrue(fixture.contains(Security.WPA));
    }

    @Test
    public void testRemovingAllWillNotRemoveLast() throws Exception {
        // setup
        Security[] values = Security.values();
        // execute
        for (Security security : values) {
            fixture.toggle(security);
        }
        // validate
        int index = values.length - 1;
        for (int i = 0; i < index; i++) {
            assertFalse(fixture.contains(values[i]));
        }
        assertTrue(fixture.contains(Security.values()[index]));
    }

    @Test
    public void testGetColorWithExisting() throws Exception {
        // execute & validate
        assertEquals(R.color.connected, fixture.getColor(Security.WPA));
    }

    @Test
    public void testGetColorWithNonExisting() throws Exception {
        // setup
        fixture.toggle(Security.WPA);
        // execute & validate
        assertEquals(R.color.icons_color, fixture.getColor(Security.WPA));
    }

    @Test
    public void testSave() throws Exception {
        // setup
        Set<Security> expected = fixture.getValues();
        // execute
        fixture.save(settings);
        // execute
        verify(settings).saveSecurityFilter(expected);
    }
}