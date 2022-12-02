package com.nttdata.bc39.grupo04.composite.controller;

import com.nttdata.bc39.grupo04.api.composite.CompositeService;
import com.nttdata.bc39.grupo04.api.movements.MovementsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/composite")
public class CompositeController {

    @Autowired
    private CompositeService service;

    @PutMapping("/deposit/{account}")
    Mono<MovementsDTO> makeDepositeATM(@PathVariable("account") String account,
                                       @RequestParam("amount") double amount) {
        return service.makeDepositATM(account, amount);
    }

    @PutMapping("/withdrawal/{account}")
    Mono<MovementsDTO> makeWithdrawlATM(@PathVariable("account") String account,
                                        @RequestParam("amount") double amount) {
        return service.makeWithdrawnATM(account, amount);
    }
}
