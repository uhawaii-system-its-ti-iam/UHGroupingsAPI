package edu.hawaii.its.api.configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class StartupCheck {
    private static final Log logger = LogFactory.getLog(StartupCheck.class);

    @PostConstruct
    public void init() {
        logger.info("init; starting...");

        // System checks here.

        logger.info("init; started.");
    }
}
