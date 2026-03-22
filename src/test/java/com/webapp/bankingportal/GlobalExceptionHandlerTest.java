package com.webapp.bankingportal;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.MediaType;

import com.webapp.bankingportal.service.AccountService;
import com.webapp.bankingportal.util.ApiMessages;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class GlobalExceptionHandlerTest extends BaseTest {

    @MockBean
    private AccountService accountService;

    @Test
    void whenRedisConnectionFails_shouldReturn503() throws Exception {
        // create a real user and login to get a valid token
        val userDetails = createAndLoginUser();
        val token = userDetails.get("token");

        // mock AccountService to throw RedisConnectionFailureException
        Mockito.when(accountService.isPinCreated(Mockito.anyString()))
               .thenThrow(new RedisConnectionFailureException("Redis down"));

        mockMvc.perform(get("/api/account/pin/check")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message")
                .value(ApiMessages.REDIS_CONNECTION_FAILURE.getMessage()));
    }
}
