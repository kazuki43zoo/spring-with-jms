package com.example.app.todo;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.IdGenerator;
import org.springframework.util.StreamUtils;
import org.springframework.web.context.WebApplicationContext;
import org.terasoluna.gfw.common.date.jodatime.AbstractJodaTimeDateFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextHierarchy({
        @ContextConfiguration({"classpath:META-INF/spring/applicationContext.xml", "classpath:testContext.xml"})
        , @ContextConfiguration("classpath:META-INF/spring/spring-mvc.xml")
})
@WebAppConfiguration
public class TodoRestControllerTest {

    @Inject
    WebApplicationContext wac;

    private MockMvc mockMvc;

    /**
     * Set Up MockMvc object.
     */
    @Before
    public void setUpMockMvc() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    /**
     * Sequence :
     * <p/>
     * Controller(@RequestBody) -> JmsTemplate#send -> ListenerContainer -> @JmsListener -> JmsTemplate#recieve -> Controller(@ResponseBody)
     */
    @Test
    public void createUsingJmsTemplate() throws Exception {
        mockMvc.perform(post("/todos")
                // pre conditions
                .header("X-Using-Template", "jmsTemplate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(fileContent("Normal.json")))
                // assertions
                .andExpect(status().isCreated())
                .andExpect(content().string(fileContent("Normal-expected.json")));
    }

    /**
     * Sequence :
     * <p/>
     * Controller(@RequestBody) -> JmsMessageTemplate#send -> ListenerContainer -> @JmsListener -> JmsMessageTemplate#recieve -> Controller(@ResponseBody)
     */
    @Test
    public void createUsingJmsMessageTemplate() throws Exception {
        mockMvc.perform(post("/todos")
                // pre conditions
                .header("X-Using-Template", "jmsMessageTemplate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(fileContent("Normal.json")))
                // assertions
                .andExpect(status().isCreated())
                .andExpect(content().string(fileContent("Normal-expected.json")));
    }

    /**
     * Sequence :
     * <p/>
     * Controller(@RequestBody) -> JmsMessageTemplate#send -> ListenerContainer -> @Valid (Validation Error) -> ?(TODO)
     */
    @Test
    public void createValidationError() throws Exception {
        mockMvc.perform(post("/todos")
                // pre conditions
                .header("X-Using-Template", "jmsMessageTemplate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(fileContent("ValidationError.json")))
                // assertions
                .andExpect(status().isCreated()) // TODO Should assert 400(Bad Request)
                .andExpect(content().string(""));
    }

    /**
     * Helper method for reading input file and expected file.
     *
     * @param fileName file name
     * @return file content
     * @throws IOException
     */
    private static String fileContent(String fileName) throws IOException {
        return StreamUtils.copyToString(
                new ClassPathResource("com/example/app/todo/" + fileName).getInputStream(),
                StandardCharsets.UTF_8);
    }

    /**
     * Mock class for fixing current date time. (see testContext.xml)
     * <p/>
     * This class return "2015/11/07 00:00:00.000+0900" (e.g. JST).
     */
    public static class FixDateFactory extends AbstractJodaTimeDateFactory {
        @Override
        public DateTime newDateTime() {
            return new DateTime(2015, 11, 7, 0, 0);
        }
    }

    /**
     * Mock class for generating fixed id. (see testContext.xml)
     *
     * This class return "8d7e1990-84fc-11e5-8bcf-feff819cdc9f".
     */
    public static class FixIdGenerator implements IdGenerator {
        @Override
        public UUID generateId() {
            return UUID.fromString("8d7e1990-84fc-11e5-8bcf-feff819cdc9f");
        }
    }

}
