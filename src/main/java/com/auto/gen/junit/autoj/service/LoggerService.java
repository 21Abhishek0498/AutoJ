package com.auto.gen.junit.autoj.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.read.ListAppender;

@Service
public class LoggerService {

	public List<String> getLogHistory() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Appender<?> appender = loggerContext.getLogger("ROOT").getAppender("CONSOLE");
        
        if (appender instanceof ListAppender<?>) {
            ListAppender<?> listAppender = (ListAppender<?>) appender;
            return listAppender.list.stream().map(Object::toString).collect(Collectors.toList());
        } else {
            return new ArrayList<String>(); // Handle other appenders if needed
        }
    }
	
	/**
	 * This is purely for testing purpose.
	 * @return List<String> mockLogHistory
	 */
	public List<String> getMockLogHistory(){
		List<String> mockLogHistory = new ArrayList<>();
		for(int i = 0; i < 20; i++) {
			mockLogHistory.add("This is a mock log line. Line number: " + (i + 1));
		}
		return mockLogHistory;
	}
}
