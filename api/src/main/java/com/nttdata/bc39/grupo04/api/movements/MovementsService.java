package com.nttdata.bc39.grupo04.api.movements;

import com.nttdata.bc39.grupo04.api.utils.CodesEnum;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovementsService {

    Mono<MovementsDTO> saveMovement(MovementsDTO dto, CodesEnum codesEnum);

    Flux<MovementsReportDTO> getAllMovementsByNumberAccount(String accountNumber);
}
