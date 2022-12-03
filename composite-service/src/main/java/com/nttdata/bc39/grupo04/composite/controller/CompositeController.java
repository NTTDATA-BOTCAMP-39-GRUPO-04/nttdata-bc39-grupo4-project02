package com.nttdata.bc39.grupo04.composite.controller;

import com.nttdata.bc39.grupo04.api.account.AccountDTO;
import com.nttdata.bc39.grupo04.api.composite.CompositeService;
import com.nttdata.bc39.grupo04.api.composite.TransactionAtmDTO;
import com.nttdata.bc39.grupo04.api.composite.TransactionTransferDTO;
import com.nttdata.bc39.grupo04.api.movements.MovementsReportDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/composite")
public class CompositeController {

    @Autowired
    private CompositeService service;

    @PutMapping("/depositAtm/{account}")
    Mono<TransactionAtmDTO> makeDepositeATM(@PathVariable("account") String account,
                                            @RequestParam("amount") double amount) {
        return service.makeDepositATM(account, amount);
    }

    @PutMapping("/withdrawalAtm/{account}")
    Mono<TransactionAtmDTO> makeWithdrawlATM(@PathVariable("account") String account,
                                             @RequestParam("amount") double amount) {
        return service.makeWithdrawnATM(account, amount);
    }

    @PutMapping("/tranference")
    Mono<TransactionAtmDTO> makeTransference(@RequestBody TransactionTransferDTO dto) {
        return service.makeTransferAccount(dto);
    }

    @GetMapping("/movements/{account}")
    Flux<MovementsReportDTO> getAllMovementsByNumber(@PathVariable("account") String account) {
        return service.getAllMovementsByAccount(account);
    }

    @GetMapping("account/customer/{customerId}")
    Flux<AccountDTO> getAccountAllByCustomer(@PathVariable(value = "customerId") String customerId) {
        return service.getAccountAllByCustomer(customerId);
    }

    @GetMapping(value = "/account/{accountNumber}")
    Mono<AccountDTO> getAccountByNumber(@PathVariable(value = "accountNumber") String accountNumber) {
        return service.getAccountByNumber(accountNumber);
    }

    @PostMapping("/account/save")
    Mono<AccountDTO> createAccount(@RequestBody AccountDTO dto) {
        return service.createAccount(dto);
    }

}