package com.nttdata.bc39.grupo04.movements.service;

import com.nttdata.bc39.grupo04.movements.dto.MovementsDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovementsService {

    Mono<MovementsDTO> saveDepositMovement(MovementsDTO dto);

    Mono<MovementsDTO> saveWithdrawlMovement(MovementsDTO dto);

    Flux<MovementsDTO> getAllMovementsByNumber(String number);
}
