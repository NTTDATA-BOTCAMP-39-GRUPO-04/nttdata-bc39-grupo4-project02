package com.nttdata.bc39.grupo04.movements.service;

import com.nttdata.bc39.grupo04.api.utils.CodesEnum;
import com.nttdata.bc39.grupo04.movements.dto.MovementsDTO;
import com.nttdata.bc39.grupo04.movements.dto.MovementsReportDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovementsService {

    Mono<MovementsDTO> saveMovement(MovementsDTO dto, CodesEnum codesEnum);

    Flux<MovementsReportDTO> getAllMovementsByNumberAccount(String accountNumber);
}
