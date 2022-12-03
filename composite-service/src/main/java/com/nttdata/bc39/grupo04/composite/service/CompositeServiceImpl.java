package com.nttdata.bc39.grupo04.composite.service;

import com.nttdata.bc39.grupo04.api.account.AccountDTO;
import com.nttdata.bc39.grupo04.api.composite.CompositeService;
import com.nttdata.bc39.grupo04.api.composite.TransactionAtmDTO;
import com.nttdata.bc39.grupo04.api.composite.TransactionTransferDTO;
import com.nttdata.bc39.grupo04.api.exceptions.NotFoundException;
import com.nttdata.bc39.grupo04.api.movements.MovementsDTO;
import com.nttdata.bc39.grupo04.api.movements.MovementsReportDTO;
import com.nttdata.bc39.grupo04.api.utils.CodesEnum;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import static com.nttdata.bc39.grupo04.api.utils.Constants.*;

@Service
public class CompositeServiceImpl implements CompositeService {

    private final CompositeIntegration integration;
    private final Logger logger = Logger.getLogger(CompositeServiceImpl.class);

    @Autowired
    public CompositeServiceImpl(CompositeIntegration integration) {
        this.integration = integration;
    }

    @Override
    public Mono<TransactionAtmDTO> makeDepositATM(String destinationAccountNumber, double amount) {
        return takeTransference(ACCOUNT_NUMBER_OF_ATM,
                destinationAccountNumber, amount, CodesEnum.TYPE_DEPOSIT);
    }

    @Override
    public Mono<TransactionAtmDTO> makeWithdrawnATM(String destinationAccountNumber, double amount) {
        return takeTransference(ACCOUNT_NUMBER_OF_ATM,
                destinationAccountNumber, amount, CodesEnum.TYPE_WITHDRAWL);
    }

    @Override
    public Mono<TransactionAtmDTO> makeTransferAccount(TransactionTransferDTO body) {
        return takeTransference(body.getSourceAccount()
                , body.getDestinationAccount()
                , body.getAmount()
                , CodesEnum.TYPE_TRANSFER);
    }

    private Mono<TransactionAtmDTO> takeTransference(
            String sourceAccountNumber, String destinationAccountNumber, double amount, CodesEnum codesEnum) {
        integration.getByAccountNumber(sourceAccountNumber);
        integration.getByAccountNumber(destinationAccountNumber);
        validationLimitAmount(sourceAccountNumber, destinationAccountNumber, codesEnum, amount);
        Flux<MovementsReportDTO> movements = integration.getAllMovementsByNumberAccount(codesEnum == CodesEnum.TYPE_TRANSFER ? sourceAccountNumber : destinationAccountNumber);
        double newAmount = amount;
        double newComission = 0;
        if (Objects.requireNonNull(movements.collectList().block()).size() >= MAX_TRANSACCION_FREE) {
            newAmount = amount - COMISSION_AMOUNT_PER_TRANSACTION;
            newComission = COMISSION_AMOUNT_PER_TRANSACTION * -1;
        }

        MovementsDTO sourceMovement = new MovementsDTO();
        sourceMovement.setAccount(sourceAccountNumber);
        sourceMovement.setComission(codesEnum == CodesEnum.TYPE_TRANSFER ? newComission : Math.abs(newComission));
        sourceMovement.setTransferAccount(destinationAccountNumber);
        sourceMovement.setAmount(newAmount);

        MovementsDTO destinationMovement = new MovementsDTO();
        destinationMovement.setAccount(destinationAccountNumber);
        destinationMovement.setTransferAccount(sourceAccountNumber);
        destinationMovement.setAmount(newAmount);
        destinationMovement.setComission(codesEnum == CodesEnum.TYPE_TRANSFER ? 0 : newComission);

        Mono<AccountDTO> sourceAccountMono = Mono.just(new AccountDTO());
        Mono<AccountDTO> destinationAccountMono = Mono.just(new AccountDTO());
        switch (codesEnum) {
            case TYPE_DEPOSIT:
                sourceAccountMono = integration.makeDepositAccount(amount, sourceAccountNumber);
                destinationAccountMono = integration.makeDepositAccount(newAmount, destinationAccountNumber);
                sourceMovement.setAvailableBalance(Objects.requireNonNull(sourceAccountMono.block()).getAvailableBalance());
                integration.saveDepositMovement(sourceMovement);
                break;
            case TYPE_WITHDRAWL:
                sourceAccountMono = integration.makeWithdrawalAccount(newAmount, sourceAccountNumber);
                destinationAccountMono = integration.makeWithdrawalAccount(newAmount, destinationAccountNumber);
                sourceMovement.setAvailableBalance(Objects.requireNonNull(sourceAccountMono.block()).getAvailableBalance());
                integration.saveWithdrawlMovement(sourceMovement);
                break;
            case TYPE_TRANSFER:
                sourceAccountMono = integration.makeWithdrawalAccount(amount, sourceAccountNumber);
                destinationAccountMono = integration.makeDepositAccount(newAmount, destinationAccountNumber);
                sourceMovement.setAvailableBalance(Objects.requireNonNull(sourceAccountMono.block()).getAvailableBalance());
                integration.saveWithdrawlMovement(sourceMovement);
                break;
        }

        destinationMovement.setAvailableBalance(Objects.requireNonNull(destinationAccountMono.block()).getAvailableBalance());
        if (codesEnum == CodesEnum.TYPE_WITHDRAWL) {
            integration.saveWithdrawlMovement(destinationMovement);
        } else {
            integration.saveDepositMovement(destinationMovement);
        }
        TransactionAtmDTO transactionAtmDTO = new TransactionAtmDTO();
        transactionAtmDTO.setSourceAccount(sourceAccountNumber);
        transactionAtmDTO.setDestinationAccount(destinationAccountNumber);
        transactionAtmDTO.setAmount(newAmount);
        transactionAtmDTO.setComission(Math.abs(newComission));
        transactionAtmDTO.setTotalAmount(amount);
        transactionAtmDTO.setDate(Calendar.getInstance().getTime());
        return Mono.just(transactionAtmDTO);
    }

    private void validationLimitAmount(String sourceAccount, String destinationAccount,
                                       CodesEnum codesEnum, double amount) {

        switch (codesEnum) {
            case TYPE_DEPOSIT:
                if (amount < MIN_DEPOSIT_AMOUNT || amount > MAX_DEPOSIT_AMOUNT) {
                    logger.debug("Error, limites de deposita hacia la cuenta nro:" + destinationAccount + " ,amount: " + amount);
                    throw new NotFoundException(String.format(Locale.getDefault(),
                            "Error, limites de monto de operacion , cuenta nro: " + destinationAccount + " (min: %d PEN y max: %d PEN) ",
                            MIN_DEPOSIT_AMOUNT, MAX_DEPOSIT_AMOUNT));
                }
                break;
            case TYPE_WITHDRAWL:
                if (amount < MIN_WITHDRAWAL_AMOUNT || amount > MAX_WITHDRAWAL_AMOUNT) {
                    logger.debug("Error,  limites de retiro  en cuenta nro: " + destinationAccount + " ,monto: " + amount);
                    throw new NotFoundException(String.format(Locale.getDefault(),
                            "Error, limites de monto de operacion , cuenta nro:" + destinationAccount + " (min: %d PEN y max: %d PEN)",
                            MIN_WITHDRAWAL_AMOUNT, MAX_WITHDRAWAL_AMOUNT));
                }
                break;
            case TYPE_TRANSFER:
                if (amount < MIN_TRANSFERENCE_AMOUNT || amount > MAX_TRANSFERENCE_AMOUNT) {
                    logger.debug("Error,  limites de transferencia ,  amount: " + amount);
                    throw new NotFoundException(String.format(Locale.getDefault(),
                            "Error, limites de monto de operacion , cuenta nro: " + sourceAccount + " (min: %d PEN y max: %d PEN)",
                            MIN_TRANSFERENCE_AMOUNT, MAX_TRANSFERENCE_AMOUNT));
                }
                break;
        }
    }
}
