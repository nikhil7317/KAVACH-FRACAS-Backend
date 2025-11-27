package com.railbit.tcasanalysis.config;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class ActiveUserSessionListener implements HttpSessionListener {
    private static final Set<String> activeUsers = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        activeUsers.add(se.getSession().getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        activeUsers.remove(se.getSession().getId());
    }

    public static int getActiveSessions() {
        return activeUsers.size();
    }
}
