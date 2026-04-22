package com.bookshop.bookdispatchservice;

import com.bookshop.bookdispatchservice.event.OrderAccepted;
import com.bookshop.bookdispatchservice.event.OrderDispatched;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
public class FunctionsStreamIntegrationTests {

    @Autowired
    private InputDestination inputDestination;

    @Autowired
    private OutputDestination outputDestination;

    @Autowired
    private JsonMapper jsonMapper;

    @Test
    void whenOrderAcceptedThenDispatched() {
        long orderId = 123;
        Message<OrderAccepted> inputMessage =
                MessageBuilder.withPayload(new OrderAccepted(orderId, Instant.now())).build();
        Message<OrderDispatched> expectedOutputMessage =
                MessageBuilder.withPayload(new OrderDispatched(orderId, Instant.now())).build();
        inputDestination.send(inputMessage);
        var actualMessage = jsonMapper.readValue(outputDestination.receive(100).getPayload(),
                OrderDispatched.class);
        assertEquals(expectedOutputMessage.getPayload().orderId(), actualMessage.orderId());

    }
}
