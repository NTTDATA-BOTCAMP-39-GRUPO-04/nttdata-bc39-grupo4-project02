package com.nttdata.bc39.grupo04.composite.service;

import com.nttdata.bc39.grupo04.api.composite.CompositeService;
import com.nttdata.bc39.grupo04.api.movements.MovementsDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

@Service
public class CompositeServiceImpl implements CompositeService {

    private RestTemplate restTemplate;


    @Override
    public Mono<MovementsDTO> makeDepositATM(MovementsDTO movementsDTO) {
        return null;
    }

    @Override
    public Mono<MovementsDTO> makeWithdrawnATM(MovementsDTO movementsDTO) {
        return null;
    }

    @Override
    public Mono<MovementsDTO> makeTransferAccount(MovementsDTO movementsDTO) {
        return null;
    }
}
