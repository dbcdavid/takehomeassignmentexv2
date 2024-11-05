package interview.takehomeassignmentexv2.services;

import interview.takehomeassignmentexv2.model.Account;
import interview.takehomeassignmentexv2.model.Event;
import interview.takehomeassignmentexv2.model.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
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
        Account account = createSampleAccount("123456");
        Optional<BigDecimal> response = accountServiceMemory.getBalanceById(account.getId());
        assertTrue(response.isPresent());
        assert(response.get()).equals(new BigDecimal("10"));
    }

    @Test
    void testDepositAmountByIdExistingAccount() {
        Account account = createSampleAccount("123456");
        Event depositEvent = createSampleEvent(null, account.getId(), new BigDecimal("10"), EventType.DEPOSIT);
        accountServiceMemory.depositAmountById(depositEvent);
        Optional<BigDecimal> response = accountServiceMemory.getBalanceById(account.getId());
        assertTrue(response.isPresent());
        assert(response.get()).equals(new BigDecimal("20"));
    }

    @Test
    void testGetBalanceNonExistingAccount() {
        Optional<BigDecimal> response = accountServiceMemory.getBalanceById("123456");
        assertTrue(response.isEmpty());
    }

    @Test
    void testGetBalanceExistingAccount() {
        Account account = createSampleAccount("123456");
        Optional<BigDecimal> response = accountServiceMemory.getBalanceById(account.getId());
        assertTrue(response.isPresent());
        assert(response.get()).equals(BigDecimal.TEN);
    }

    @Test
    void testWithdrawAmountByIdNonExistingAccount() {
        Event withdrawEvent = createSampleEvent("123456", null, new BigDecimal("10"), EventType.WITHDRAW);
        Optional<Account> response = accountServiceMemory.withdrawAmountById(withdrawEvent);
        assertTrue(response.isEmpty());
    }

    @Test
    void testWithdrawAmountByIdExistingAccount() {
        Account account = createSampleAccount("123456");
        Event withdrawEvent = createSampleEvent(account.getId(), null, new BigDecimal("10"), EventType.WITHDRAW);
        accountServiceMemory.withdrawAmountById(withdrawEvent);
        Optional<BigDecimal> response = accountServiceMemory.getBalanceById(account.getId());
        assertTrue(response.isPresent());
        assert(response.get()).equals(new BigDecimal("0"));
    }

    @Test
    void testTransferAmountExistingAccounts() {
        Account origin = createSampleAccount("123456");
        Account destination = createSampleAccount("654321");
        Event transferEvent = createSampleEvent(origin.getId(), destination.getId(), new BigDecimal("10"), EventType.TRANSFER);
        Optional<List<Account>> response = accountServiceMemory.transferAmount(transferEvent);
        assertTrue(response.isPresent());

        Optional<BigDecimal> responseFrom = accountServiceMemory.getBalanceById(origin.getId());
        assertTrue(responseFrom.isPresent());
        assert(responseFrom.get()).equals(new BigDecimal("0"));

        Optional<BigDecimal> responseTo = accountServiceMemory.getBalanceById(destination.getId());
        assertTrue(responseTo.isPresent());
        assert(responseTo.get()).equals(new BigDecimal("20"));
    }

    @Test
    void testTransferAmountNonExistingOrigin() {
        Account destination = createSampleAccount("654321");
        Event transferEvent = createSampleEvent("123456", destination.getId(), new BigDecimal("10"), EventType.TRANSFER);
        Optional<List<Account>> response = accountServiceMemory.transferAmount(transferEvent);
        assertTrue(response.isEmpty());
    }

    @Test
    void testTransferAmountNonExistingDestination() {
        Account origin = createSampleAccount("654321");
        Event transferEvent = createSampleEvent(origin.getId(), "123456", new BigDecimal("10"), EventType.TRANSFER);
        Optional<List<Account>> response = accountServiceMemory.transferAmount(transferEvent);
        assertTrue(response.isEmpty());
    }

    private Account createSampleAccount(String id) {
        Event depositEvent = createSampleEvent(null, id, new BigDecimal("10"), EventType.DEPOSIT);
        return accountServiceMemory.depositAmountById(depositEvent);
    }

    private Event createSampleEvent(String origin, String destination, BigDecimal amount, EventType type){
        return Event.builder()
                .destination(destination)
                .origin(origin)
                .amount(amount)
                .type(type)
                .build();
    }
}