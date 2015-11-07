package com.example.domain.service;

import com.example.domain.model.Todo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TodoJmsService {

    private static final Logger logger = LoggerFactory.getLogger(TodoJmsService.class);

    @JmsListener(destination = "TodoQueue")
    @SendTo("ReplyTodoQueue")
    public Todo create(Todo todo, @Headers MessageHeaders headers) throws InterruptedException {

        logger.debug("Received Headers : {}", headers);
        logger.debug("Title : {}", todo.getTitle());
        logger.debug("Description : {}", todo.getDescription());

        TimeUnit.SECONDS.sleep(1);

        todo.setTodoId(UUID.randomUUID().toString());
        todo.setCreatedAt(new Date());

        return todo;
    }

}
