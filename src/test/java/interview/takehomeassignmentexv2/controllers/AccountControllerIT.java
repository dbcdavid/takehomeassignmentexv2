package interview.takehomeassignmentexv2.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import interview.takehomeassignmentexv2.model.Event;
import interview.takehomeassignmentexv2.model.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AccountControllerIT {
    @Autowired
    AccountController accountController;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        accountController.reset();
    }

    @Test
    void testGetBalanceNonExistingAccount() throws Exception {
        mockMvc.perform(get(AccountController.BALANCE_PATH)
                        .queryParam("account_id", "654321"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetBalanceExistingAccount() throws Exception {
        createSampleAccount(123456, new BigDecimal("25"));
        MvcResult result = mockMvc.perform(get(AccountController.BALANCE_PATH)
                        .queryParam("account_id", "123456"))
                .andExpect(status().isOk())
                .andReturn();

        assert(result.getResponse().getContentAsString()).equals("25");
    }

    @Test
    void testCreateAccount() throws Exception {
        Event event = createSampleEvent(EventType.DEPOSIT, null, 123456, new BigDecimal("25"));

        mockMvc.perform(post(AccountController.EVENT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isCreated());
    }

    @Test
    void testDeposit() throws Exception {
        Event event = createSampleEvent(EventType.DEPOSIT, null, 123456, new BigDecimal("25"));

        createSampleAccount(123456, new BigDecimal("25"));
        mockMvc.perform(post(AccountController.EVENT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.destination.balance", is(50)))
                .andReturn();
    }

    @Test
    void testWithdrawExistingAccount() throws Exception {
        Event event = createSampleEvent(EventType.WITHDRAW, 123456, null, new BigDecimal("10"));

        createSampleAccount(123456, new BigDecimal("25"));
        mockMvc.perform(post(AccountController.EVENT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.origin.balance", is(15)))
                .andReturn();
    }

    @Test
    void testWithdrawNonExistingAccount() throws Exception {
        Event event = createSampleEvent(EventType.WITHDRAW, 123456, null, new BigDecimal("10"));

        mockMvc.perform(post(AccountController.EVENT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testTransferExistingAccounts() throws Exception {
        Event event = createSampleEvent(EventType.TRANSFER, 123456, 654321, new BigDecimal("25"));
        createSampleAccount(123456, new BigDecimal("25"));
        createSampleAccount(654321, new BigDecimal("25"));

        mockMvc.perform(post(AccountController.EVENT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(jsonPath("$.origin.balance", is(0)))
                .andExpect(jsonPath("$.destination.balance", is(50)));
    }

    @Test
    void testTransferNonExistingFromAccount() throws Exception {
        Event event = createSampleEvent(EventType.TRANSFER, 123456, 654321, new BigDecimal("25"));
        createSampleAccount(654321, new BigDecimal("25"));

        mockMvc.perform(post(AccountController.EVENT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testTransferNonExistingToAccount() throws Exception {
        Event event = createSampleEvent(EventType.TRANSFER, 123456, 654321, new BigDecimal("25"));
        createSampleAccount(123456, new BigDecimal("25"));

        mockMvc.perform(post(AccountController.EVENT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testBadEvent() throws Exception {
        Event event = createSampleEvent(null, null, null, null);

        mockMvc.perform(post(AccountController.EVENT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isBadRequest());
    }

    void createSampleAccount(Integer id, BigDecimal balance) {
        Event event = createSampleEvent(EventType.DEPOSIT, null, id, balance);
        accountController.processEvent(event);
    }

    Event createSampleEvent(EventType type, Integer origin, Integer destination, BigDecimal amount) {
        return Event.builder()
                .type(type)
                .origin(origin)
                .destination(destination)
                .amount(amount)
                .build();
    }
}