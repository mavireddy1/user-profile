package com.example.userprofile;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class UserProfileApplicationTests {

    @MockBean
    private RestTemplate restTemplate;

    @Test
    void contextLoads() {
    }
}
