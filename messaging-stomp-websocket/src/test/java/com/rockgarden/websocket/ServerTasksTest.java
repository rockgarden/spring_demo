package com.rockgarden.websocket;

import com.rockgarden.websocket.serverinfo.ServerTask;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import org.awaitility.Duration;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.awaitility.Awaitility.await;

@SpringBootTest
public class ServerTasksTest {

    @SpyBean
    ServerTask tasks;

    @Test
    public void reportCurrentTime() {
        await().atMost(Duration.TEN_SECONDS).untilAsserted(() -> {
            verify(tasks, atLeast(2)).reportCurrentTime();
        });
    }
}
