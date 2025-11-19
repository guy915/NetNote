/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.scenes;

import client.scenes.config.Config;
import client.scenes.config.ConfigService;
import client.utils.ServerUtils;
import commons.Note;
import commons.NoteCollection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class MainCtrlTest {
    private MainCtrl sut;
    private TestConfigService configService;
    private ServerUtils server;

    @BeforeEach
    public void setup() {
        configService = new TestConfigService();
        server = new ServerUtils();
        sut = new MainCtrl(configService, server);
        Locale.setDefault(new Locale("en", "US"));
    }

    @Test
    public void testInitialState() {
        assertNotNull(sut, "MainCtrl should be initialized");
        assertNull(sut.getActiveNote(), "Initial active note should be null");
    }

    @Test
    public void testActiveNoteManagement() {
        Note testNote = new Note("Test Title", "Test Content");

        sut.setActiveNote(testNote);

        assertEquals(testNote, sut.getActiveNote(),
                "Active note should be set correctly");
    }

    private static class TestConfigService extends ConfigService {
        private Config currentConfig;

        public TestConfigService() {
            this.currentConfig = Config.createDefault();
        }

        @Override
        public Config getConfig() {
            return currentConfig;
        }

        @Override
        public void updateConfig(Config config) {
            this.currentConfig = config;
        }
    }
}