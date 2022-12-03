package com.nttdata.bc39.grupo04.api.composite;

import com.nttdata.bc39.grupo04.api.movements.MovementsDTO;
import reactor.core.publisher.Mono;

public interface CompositeService {
    Mono<TransactionAtmDTO> makeDepositATM(String destinationAccountNumber, double amount);

    Mono<TransactionAtmDTO> makeWithdrawnATM(String destinationAccountNumber, double amount);

    Mono<TransactionAtmDTO> makeTransferAccount(TransactionTransferDTO body);
}
