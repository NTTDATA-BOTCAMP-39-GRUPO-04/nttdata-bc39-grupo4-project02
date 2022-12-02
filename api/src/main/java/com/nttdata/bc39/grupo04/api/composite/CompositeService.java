package com.nttdata.bc39.grupo04.api.composite;

import com.nttdata.bc39.grupo04.api.movements.MovementsDTO;
import reactor.core.publisher.Mono;

public interface CompositeService {
    Mono<MovementsDTO> makeDepositATM(String accountNumber, double amount);

    Mono<MovementsDTO> makeWithdrawnATM(String accountNumber, double amount);

    Mono<MovementsDTO> makeTransferAccount(MovementsDTO movementsDTO);
}
