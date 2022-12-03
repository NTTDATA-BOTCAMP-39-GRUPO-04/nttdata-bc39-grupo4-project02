package com.nttdata.bc39.grupo04.account.controller;

import com.nttdata.bc39.grupo04.api.account.AccountDTO;
import com.nttdata.bc39.grupo04.api.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/account")
public class AccountRestController {
    @Autowired
    private AccountService service;

    @GetMapping(value = "/customer/{customerId}")
    Flux<AccountDTO> getAccountAllByCustomer(@PathVariable(value = "customerId") String customerId) {
        return service.getAllAccountByCustomer(customerId);
    }

    @GetMapping(value = "/{accountNumber}")
    Mono<AccountDTO> getAccountByNumber(@PathVariable(value = "accountNumber") String accountNumber) {
        return service.getByAccountNumber(accountNumber);
    }

    @PostMapping("/save")
    Mono<AccountDTO> createAccount(@RequestBody AccountDTO dto) {
        return service.createAccount(dto);
    }

    @GetMapping("/deposit/{accountNumber}")
    Mono<AccountDTO> makeDeposit(@PathVariable(value = "accountNumber") String accountNumber,
                                 @RequestParam(value = "amount") double amount) {
        return service.makeDepositAccount(amount, accountNumber);
    }

    @GetMapping("/withdrawal/{accountNumber}")
    Mono<AccountDTO> makeWithdrawal(@PathVariable(value = "accountNumber") String accountNumber,
                                    @RequestParam(value = "amount") double amount) {
        return service.makeWithdrawalAccount(amount, accountNumber);
    }

    @DeleteMapping("/{accountNumber}")
    Mono<Void> deleteAccount(@PathVariable(value = "accountNumber") String accountNumber) {
        return service.deleteAccount(accountNumber);
    }
}
