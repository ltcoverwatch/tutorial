package com.example.zoom01.event;

import org.springframework.context.ApplicationEvent;

public class MyApplicationEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    public MyApplicationEvent(Object source) {
        super(source);
        System.out.println("触发 MyApplicationEvent 事件...");
    }
}
