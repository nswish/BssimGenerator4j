package com.baosight.bssim.listeners;

import com.baosight.bssim.controllers.ApplicationController;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class AppServletContextListener implements ServletContextListener {
    public void contextInitialized(ServletContextEvent event) {
        ApplicationController.BASE_PATH = event.getServletContext().getRealPath("");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
