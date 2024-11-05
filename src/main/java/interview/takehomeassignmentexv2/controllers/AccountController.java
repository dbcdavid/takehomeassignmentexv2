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
import java.util.List;
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
            return depositEvent(event);
        }
        else if (EventType.WITHDRAW.equals(event.getType())) {
            return withdrawEvent(event);
        }
        else if (EventType.TRANSFER.equals(event.getType())) {
            return transferEvent(event);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Map<String, Account>> depositEvent(Event event){
        if (event.getDestination() != null && event.getAmount() != null) {
            Account account = accountService.depositAmountById(event);
            Map<String, Account> response = new HashMap<>();
            response.put("destination", account);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Map<String, Account>> withdrawEvent(Event event){
        if (event.getOrigin() != null && event.getAmount() != null) {
            Optional<Account> account = accountService.withdrawAmountById(event);
            if (account.isPresent()) {
                Map<String, Account> response = new HashMap<>();
                response.put("origin", account.get());
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Map<String, Account>> transferEvent(Event event){
        if (event.getOrigin() != null && event.getDestination() != null && event.getAmount() != null) {
            Optional<List<Account>> result = accountService.transferAmount(event);
            if (result.isPresent()) {
                Map<String, Account> response = new HashMap<>();
                response.put("origin", result.get().get(0));
                response.put("destination", result.get().get(1));
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
