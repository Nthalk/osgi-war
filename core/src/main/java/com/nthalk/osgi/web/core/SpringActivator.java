package com.nthalk.osgi.web.core;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@Component(immediate = true)
public class SpringActivator {
    @Activate
    public void activate() {
        System.out.println("Attempting to load context");
        try {
            ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
            System.out.println(context);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
