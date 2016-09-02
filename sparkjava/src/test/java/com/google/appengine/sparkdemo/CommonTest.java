package com.google.appengine.sparkdemo;

import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import slf4jtest.Settings;
import slf4jtest.TestLoggerFactory;

public class CommonTest {

    private TestLoggerFactory freshFactory(String className, Logger mockLogger) {
        Settings cfg = new Settings().delegate(className, mockLogger);
        TestLoggerFactory loggerFactory = new TestLoggerFactory(cfg);
        return loggerFactory;
    }

    @Test
    public void testAuthenticateSuccess() {
        String ip = "1.1.1.1";
        String authorization = "Basic c2FkbWluOnNhZG1pbnB3";

        Logger mockLogger = Mockito.mock(Logger.class);
        TestLoggerFactory loggerFactory = freshFactory(Common.class.getName(), mockLogger);

        Common.authenticate(loggerFactory, authorization, ip);
        Mockito.verify(mockLogger).info("sadmin auth succeed {}", "1.1.1.1");
    }

    @Test
    public void testAuthenticateWrong() {
        String ip = "1.1.1.1";
        String authorization = "Basic c2FkbWluOnNhZG1pbnB3eA==";

        Logger mockLogger = Mockito.mock(Logger.class);
        TestLoggerFactory loggerFactory = freshFactory(Common.class.getName(), mockLogger);

        Common.authenticate(loggerFactory, authorization, ip);
        Mockito.verify(mockLogger).info(
            "sadmin auth failed ({}:{}) {}",
            "sadmin",
            "sadminpwx",
            "1.1.1.1"
        );
    }

    @Test
    public void testAuthenticateInvalidFormat() {
        String ip = "1.1.1.1";
        String authorization = "Basic abc";

        Logger mockLogger = Mockito.mock(Logger.class);
        TestLoggerFactory loggerFactory = freshFactory(Common.class.getName(), mockLogger);

        Common.authenticate(loggerFactory, authorization, ip);
        Mockito.verify(mockLogger).info(
            "sadmin auth failed: invalid format ({}) {}",
            "Basic abc",
            "1.1.1.1"
        );
    }

    @Test
    public void testAuthenticateInvalidBasicFormat() {
        String ip = "1.1.1.1";
        String authorization = "abc";

        Logger mockLogger = Mockito.mock(Logger.class);
        TestLoggerFactory loggerFactory = freshFactory(Common.class.getName(), mockLogger);

        Common.authenticate(loggerFactory, authorization, ip);
        Mockito.verify(mockLogger).info(
            "sadmin auth failed: invalid basic format ({}) {}",
            "abc",
            "1.1.1.1"
        );
    }
}
