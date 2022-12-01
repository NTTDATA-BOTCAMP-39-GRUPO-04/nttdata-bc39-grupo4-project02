package com.nttdata.bc39.grupo04.api.composite;

import com.nttdata.bc39.grupo04.api.movements.MovementsDTO;
import reactor.core.publisher.Mono;

public interface CompositeService {
    Mono<MovementsDTO> makeDepositATM(MovementsDTO movementsDTO);

    Mono<MovementsDTO> makeWithdrawnATM(MovementsDTO movementsDTO);

    Mono<MovementsDTO> makeTransferAccount(MovementsDTO movementsDTO);
}
