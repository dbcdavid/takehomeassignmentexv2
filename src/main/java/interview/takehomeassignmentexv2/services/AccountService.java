package interview.takehomeassignmentexv2.services;

import interview.takehomeassignmentexv2.model.Account;
import interview.takehomeassignmentexv2.model.Event;

import java.math.BigDecimal;
import java.util.Optional;

public interface AccountService {
    Account depositAmountById(Event event);

    Optional<BigDecimal> getBalanceById(Integer id);
}
