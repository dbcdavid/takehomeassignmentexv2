package interview.takehomeassignmentexv2.services;


import interview.takehomeassignmentexv2.model.Account;
import interview.takehomeassignmentexv2.model.Event;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class AccountServiceMemory implements AccountService {

    private HashMap<Integer, Account> accountMap;

    public AccountServiceMemory(){
        accountMap = new HashMap<>();
    }

    @Override
    public Account depositAmountById(Event event) {
        if (!accountMap.containsKey(event.getDestination())) {
            createAccount(event.getDestination());
        }

        Account account = accountMap.get(event.getDestination());
        account.setBalance(account.getBalance().add(event.getAmount()));
        return account;
    }

    @Override
    public Optional<Account> withdrawAmountById(Event event) {
        if (!accountMap.containsKey(event.getOrigin())) {
            return Optional.empty();
        }

        Account account = accountMap.get(event.getOrigin());
        account.setBalance(account.getBalance().subtract(event.getAmount()));
        return Optional.of(account);
    }

    @Override
    public Optional<List<Account>> transferAmount(Event event) {
        if (!accountMap.containsKey(event.getOrigin()) || !accountMap.containsKey(event.getDestination())) {
            return Optional.empty();
        }

        Account savedFrom = accountMap.get(event.getOrigin());
        Account savedTo = accountMap.get(event.getDestination());
        savedFrom.setBalance(savedFrom.getBalance().subtract(event.getAmount()));
        savedTo.setBalance(savedTo.getBalance().add(event.getAmount()));

        return Optional.of(new ArrayList<>(Arrays.asList(savedFrom, savedTo)));
    }

    @Override
    public Optional<BigDecimal> getBalanceById(Integer id) {
        if (!accountMap.containsKey(id)) {
            return Optional.empty();
        }

        Account account = accountMap.get(id);
        return Optional.of(account.getBalance());
    }

    private Account createAccount(Integer id){
        Account newAccount = Account.builder()
                .id(id)
                .balance(BigDecimal.ZERO)
                .build();

        accountMap.put(id, newAccount);
        return newAccount;
    }
}
