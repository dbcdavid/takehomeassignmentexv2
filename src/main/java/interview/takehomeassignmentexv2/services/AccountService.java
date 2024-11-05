package interview.takehomeassignmentexv2.services;

import interview.takehomeassignmentexv2.model.Account;
import interview.takehomeassignmentexv2.model.Event;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountService {
    Account depositAmountById(Event event);

    Optional<BigDecimal> getBalanceById(String id);

    Optional<Account> withdrawAmountById(Event event);

    Optional<List<Account>> transferAmount(Event event);

    void reset();
}
