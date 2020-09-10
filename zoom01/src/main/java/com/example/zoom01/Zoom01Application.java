package com.example.zoom01;

import com.example.zoom01.component.MyApplicationListener;
import com.example.zoom01.event.MyApplicationEvent;
import kong.unirest.Unirest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class Zoom01Application {

	public static void main(String[] args) {

		Unirest.config()
				.connectTimeout(3000)
				.proxy(new kong.unirest.Proxy("clowd-proxy.chemi-con.co.jp", 8080));


		ConfigurableApplicationContext context = SpringApplication.run(Zoom01Application.class, args);

		//不用写下面这句，讲listener注入即可，在类上用component注解
		//context.addApplicationListener(new MyApplicationListener());
		// 发布 MyApplicationEvent 事件
		context.publishEvent(new MyApplicationEvent(new Object()));
		// context.getBean(MyEventHandler.class).publishMyApplicationEvent();
	}

}
