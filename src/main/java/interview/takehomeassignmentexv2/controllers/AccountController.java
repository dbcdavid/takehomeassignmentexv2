package interview.takehomeassignmentexv2.controllers;

import interview.takehomeassignmentexv2.model.Account;
import interview.takehomeassignmentexv2.model.Event;
import interview.takehomeassignmentexv2.model.EventType;
import interview.takehomeassignmentexv2.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class AccountController {
    public static final String BALANCE_PATH = "/balance";
    public static final String EVENT_PATH = "/event";
    public static final String RESET_PATH = "/reset";

    private final AccountService accountService;

    @GetMapping(RESET_PATH)
    public ResponseEntity reset() {
        accountService.reset();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(BALANCE_PATH)
    public BigDecimal getBalanceById(@RequestParam Integer account_id) {
        return accountService.getBalanceById(account_id).orElseThrow(NotFoundException::new);
    }

    @PostMapping(EVENT_PATH)
    public ResponseEntity<Map<String, Account>> processEvent(@RequestBody Event event) {
        if (EventType.DEPOSIT.equals(event.getType())) {
            Optional<Account> account = depositEvent(event);
            if (account.isPresent()) {
                Map<String, Account> response = new HashMap<>();
                response.put("destination", account.get());
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public Optional<Account> depositEvent(Event event){
        if (event.getDestination() != null && event.getAmount() != null) {
            Account account = accountService.depositAmountById(event);
            return Optional.of(account);
        }

        return Optional.empty();
    }
}
