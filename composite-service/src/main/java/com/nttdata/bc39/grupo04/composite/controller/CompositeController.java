package com.nttdata.bc39.grupo04.composite.controller;

import com.nttdata.bc39.grupo04.api.composite.CompositeService;
import com.nttdata.bc39.grupo04.api.composite.TransactionAtmDTO;
import com.nttdata.bc39.grupo04.api.composite.TransactionTransferDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
}