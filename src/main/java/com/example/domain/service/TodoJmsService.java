package com.example.domain.service;

import com.example.domain.model.Todo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;
import org.springframework.util.IdGenerator;
import org.springframework.validation.annotation.Validated;
import org.terasoluna.gfw.common.date.jodatime.JodaTimeDateFactory;

import javax.inject.Inject;
import java.util.UUID;

@Service
public class TodoJmsService {

    private static final Logger logger = LoggerFactory.getLogger(TodoJmsService.class);

    @Inject
    JodaTimeDateFactory dateFactory;

    @Inject
    IdGenerator idGenerator;

    @JmsListener(destination = "TodoQueue")
    @SendTo("ReplyTodoQueue")
    // TODO "Method of handling validation error"
    public Todo create(@Validated Todo todo, @Headers MessageHeaders headers) {

        logger.debug("Received Headers : {}", headers);
        logger.debug("Title : {}", todo.getTitle());
        logger.debug("Description : {}", todo.getDescription());

        todo.setTodoId(idGenerator.generateId().toString());
        todo.setCreatedAt(dateFactory.newDate());

        return todo;
    }

}
