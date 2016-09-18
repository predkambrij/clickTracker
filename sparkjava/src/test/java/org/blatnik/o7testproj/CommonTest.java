package org.blatnik.o7testproj;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.blatnik.o7testproj.Common;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Common.class)
public class CommonTest {
    // helpers
    static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);
        // remove final modifier from field
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }
    static Object[] commonSetup(Map<String, String> settingsOveride) throws Exception {
        Map<String, String> settings = new HashMap<String, String>() {{
            put("checkPasswordResponse", "true");
        }};
        if (settingsOveride != null) {
            settings.putAll(settingsOveride);
        }

        Logger mockLogger = Mockito.mock(Logger.class);
        setFinalStatic(Common.class.getField("logger"), mockLogger);

        PowerMockito.spy(Common.class);
        PowerMockito
            .doReturn(Boolean.parseBoolean(settings.get("checkPasswordResponse")))
            .when(Common.class, "checkPassword", "sadminpw", Config.sadminPw);

        return new Object[]{mockLogger};
    }

    @Test
    public void testAuthenticateSuccess() throws Exception {
        Object[] obs = commonSetup(null);
        assertTrue(Common.authenticate("Basic c2FkbWluOnNhZG1pbnB3", "1.1.1.1"));
        Mockito.verify((Logger)obs[0]).info(Config.strings.get("sadminAuthSucceed"), "1.1.1.1");
    }
    @Test
    public void testAuthenticateWrong() throws Exception {
        Map<String, String> settings = new HashMap<String, String>();
        settings.put("checkPasswordResponse", "false");
        Object[] obs = commonSetup(settings);

        assertFalse(Common.authenticate("Basic c2FkbWluOnNhZG1pbnB3", "1.1.1.1"));
        Mockito.verify((Logger)obs[0]).info(Config.strings.get("sadminAuthFailed"), "sadmin", "sadminpw", "1.1.1.1");
    }
    @Test
    public void testAuthenticateInvalidFormat() throws Exception {
        Map<String, String> settings = new HashMap<String, String>();
        settings.put("checkPasswordResponse", "false");
        Object[] obs = commonSetup(settings);

        assertFalse(Common.authenticate("Basic abc", "1.1.1.1"));
        Mockito.verify((Logger)obs[0]).info(Config.strings.get("sadminAuthFailedInvalidFormat"), "Basic abc", "1.1.1.1");
    }
    @Test
    public void testAuthenticateInvalidBasicFormat() throws Exception {
        Map<String, String> settings = new HashMap<String, String>();
        settings.put("checkPasswordResponse", "false");
        Object[] obs = commonSetup(settings);

        assertFalse(Common.authenticate("abc", "1.1.1.1"));
        Mockito.verify((Logger)obs[0]).info(Config.strings.get("sadminAuthFailedInvalidBasicFormat"), "abc", "1.1.1.1");
    }
}
