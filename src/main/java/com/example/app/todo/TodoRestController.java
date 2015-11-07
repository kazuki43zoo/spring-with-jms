package com.example.app.todo;

import com.example.domain.model.Todo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsMessageOperations;
import org.springframework.jms.core.JmsOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@RequestMapping("/todos")
@RestController
@Transactional("jmsTransactionManager")
public class TodoRestController {

    private static final Logger logger = LoggerFactory.getLogger(TodoRestController.class);

    @Inject
    JmsMessageOperations jmsMessageOperations;

    @Inject
    JmsOperations jmsOperations;

    @RequestMapping(method = RequestMethod.POST, headers = "X-Using-Template=jmsTemplate")
    @ResponseStatus(HttpStatus.CREATED)
    public Todo createTodoUsingJmsTemplate(@RequestBody Todo todo) {

        logger.debug("start sending.");
        jmsOperations.convertAndSend("TodoQueue", todo);
        logger.debug("end sending.");

        logger.debug("start receiving.");
        Todo createdTodo = (Todo) jmsOperations.receiveAndConvert("ReplyTodoQueue");
        logger.debug("end receiving.");

        return createdTodo;
    }

    @RequestMapping(method = RequestMethod.POST, headers = "X-Using-Template=jmsMessageTemplate")
    @ResponseStatus(HttpStatus.CREATED)
    public Todo createTodoUsingJmsMessageTemplate(@RequestBody Todo todo) {

        logger.debug("start sending.");
        jmsMessageOperations.convertAndSend("TodoQueue", todo);
        logger.debug("end sending.");

        logger.debug("start receiving.");
        Todo createdTodo = jmsMessageOperations.receiveAndConvert("ReplyTodoQueue", Todo.class);
        logger.debug("end receiving.");

        return createdTodo;
    }

}
