package interview.takehomeassignmentexv2.services;

import interview.takehomeassignmentexv2.model.Account;
import interview.takehomeassignmentexv2.model.Event;
import interview.takehomeassignmentexv2.model.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceMemoryTest {

    AccountServiceMemory accountServiceMemory;

    @BeforeEach
    void setUp() {
        accountServiceMemory = new AccountServiceMemory();
    }

    @Test
    void testDepositAmountByIdNonExistingAccount() {
        Account account = createSampleAccount(123456);
        Optional<BigDecimal> response = accountServiceMemory.getBalanceById(account.getId());
        assertTrue(response.isPresent());
        assert(response.get()).equals(new BigDecimal("10"));
    }

    @Test
    void testDepositAmountByIdExistingAccount() {
        Account account = createSampleAccount(123456);
        Event depositEvent = createSampleEvent(account.getId(), null, new BigDecimal("10"), EventType.DEPOSIT);
        accountServiceMemory.depositAmountById(depositEvent);
        Optional<BigDecimal> response = accountServiceMemory.getBalanceById(account.getId());
        assertTrue(response.isPresent());
        assert(response.get()).equals(new BigDecimal("20"));
    }

    @Test
    void testGetBalanceNonExistingAccount() {
        Optional<BigDecimal> response = accountServiceMemory.getBalanceById(123456);
        assertTrue(response.isEmpty());
    }

    @Test
    void testGetBalanceExistingAccount() {
        Account account = createSampleAccount(123456);
        Optional<BigDecimal> response = accountServiceMemory.getBalanceById(account.getId());
        assertTrue(response.isPresent());
        assert(response.get()).equals(BigDecimal.TEN);
    }

    @Test
    void testWithdrawAmountByIdNonExistingAccount() {
        Event withdrawEvent = createSampleEvent(null, 123456, new BigDecimal("10"), EventType.WITHDRAW);
        Optional<Account> response = accountServiceMemory.withdrawAmountById(withdrawEvent);
        assertTrue(response.isEmpty());
    }

    @Test
    void testWithdrawAmountByIdExistingAccount() {
        Account account = createSampleAccount(123456);
        Event withdrawEvent = createSampleEvent(null, account.getId(), new BigDecimal("10"), EventType.WITHDRAW);
        accountServiceMemory.withdrawAmountById(withdrawEvent);
        Optional<BigDecimal> response = accountServiceMemory.getBalanceById(account.getId());
        assertTrue(response.isPresent());
        assert(response.get()).equals(new BigDecimal("0"));
    }

    private Account createSampleAccount(Integer id) {
        Event depositEvent = createSampleEvent(id, null, new BigDecimal("10"), EventType.DEPOSIT);
        return accountServiceMemory.depositAmountById(depositEvent);
    }

    private Event createSampleEvent(Integer destination, Integer origin, BigDecimal amount, EventType type){
        return Event.builder()
                .destination(destination)
                .origin(origin)
                .amount(amount)
                .type(type)
                .build();
    }
}