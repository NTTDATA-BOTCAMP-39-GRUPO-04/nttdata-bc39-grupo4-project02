package com.nttdata.bc39.grupo04.composite.service;

import com.nttdata.bc39.grupo04.api.composite.CompositeService;
import com.nttdata.bc39.grupo04.api.movements.MovementsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.nttdata.bc39.grupo04.api.utils.Constants.ACCOUNT_NUMBER_OF_ATM;
import static com.nttdata.bc39.grupo04.api.utils.Constants.COMISSION_AMOUNT_PER_TRANSACTION;

@Service
public class CompositeServiceImpl implements CompositeService {

    private final CompositeIntegration integration;

    @Autowired
    public CompositeServiceImpl(CompositeIntegration integration) {
        this.integration = integration;
    }

    @Override
    public Mono<MovementsDTO> makeDepositATM(String accountNumber, double amount) {
        MovementsDTO sourceMovement = new MovementsDTO();
        sourceMovement.setAccount(ACCOUNT_NUMBER_OF_ATM);
        sourceMovement.setTransferAccount(accountNumber);
        sourceMovement.setAmount(amount);
        sourceMovement.setComission(COMISSION_AMOUNT_PER_TRANSACTION);
        sourceMovement.setAvailableBalance(2400);

        MovementsDTO destinationMovement = new MovementsDTO();
        destinationMovement.setAccount(accountNumber);
        destinationMovement.setTransferAccount(ACCOUNT_NUMBER_OF_ATM);
        destinationMovement.setAmount(amount);
        destinationMovement.setComission(0);
        sourceMovement.setAvailableBalance(1400);

        integration.makeDepositAccount(amount, accountNumber);
        integration.saveWithdrawlMovement(sourceMovement);
        return integration.saveDepositMovement(destinationMovement);
    }

    @Override
    public Mono<MovementsDTO> makeWithdrawnATM(String accountNumber, double amount) {
        return null;
    }

    @Override
    public Mono<MovementsDTO> makeTransferAccount(MovementsDTO movementsDTO) {
        return null;
    }
}
