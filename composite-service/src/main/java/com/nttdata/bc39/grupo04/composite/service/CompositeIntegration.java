package com.nttdata.bc39.grupo04.composite.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nttdata.bc39.grupo04.api.account.AccountDTO;
import com.nttdata.bc39.grupo04.api.account.AccountService;
import com.nttdata.bc39.grupo04.api.account.HolderDTO;
import com.nttdata.bc39.grupo04.api.exceptions.BadRequestException;
import com.nttdata.bc39.grupo04.api.exceptions.HttpErrorInfo;
import com.nttdata.bc39.grupo04.api.exceptions.InvaliteInputException;
import com.nttdata.bc39.grupo04.api.exceptions.NotFoundException;
import com.nttdata.bc39.grupo04.api.movements.MovementsDTO;
import com.nttdata.bc39.grupo04.api.movements.MovementsReportDTO;
import com.nttdata.bc39.grupo04.api.movements.MovementsService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.nttdata.bc39.grupo04.api.utils.Constants.*;

@Component
public class CompositeIntegration implements MovementsService, AccountService {

    private final RestTemplate restTemplate;
    private static final Logger logger = Logger.getLogger(CompositeIntegration.class);
    private final String urlMovementsService;
    private final String urlAccountService;
    private final ObjectMapper mapper;

    public CompositeIntegration(RestTemplate restTemplate,
                                ObjectMapper mapper,
                                @Value("${app.movements-service.host}") String movementsServiceHost,
                                @Value("${app.movements-service.port}") String movementsServicePort,
                                @Value("${app.account-service.host}") String accountServiceHost,
                                @Value("${app.account-service.port}") String accountServicePort) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.urlAccountService = String.format("http://%s:%s/%s", accountServiceHost, accountServicePort, "account");
        this.urlMovementsService = String.format("http://%s:%s/%s", movementsServiceHost, movementsServicePort, "movements");
    }

    //Movements
    @Override
    public Mono<MovementsDTO> saveDepositMovement(MovementsDTO dto) {
        String url = urlMovementsService + "/deposit";
        try {
            MovementsDTO movementsDTO = restTemplate.postForObject(url, dto, MovementsDTO.class);
            return Mono.just(movementsDTO);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while saveDepositMovement: " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }


    @Override
    public Mono<MovementsDTO> saveWithdrawlMovement(MovementsDTO dto) {
        String url = urlMovementsService + "/withdrawl";
        try {
            MovementsDTO movementsDTO = restTemplate.postForObject(url, dto, MovementsDTO.class);
            return Mono.just(movementsDTO);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while make saveWithdrawlMovement " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Flux<MovementsReportDTO> getAllMovementsByNumberAccount(String accountNumber) {
        String url = urlMovementsService + "/" + accountNumber;
        try {
            List<MovementsReportDTO> list = restTemplate.exchange(url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<MovementsReportDTO>>() {
                    }).getBody();

            Mono<List<MovementsReportDTO>> monoList = Mono.just(list);
            return monoList.flatMapMany(Flux::fromIterable);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while make getAllMovementsByNumberAccount:  " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }

    //Acount
    @Override
    public Mono<AccountDTO> getByAccountNumber(String accountNumber) {
        String url = urlAccountService + "/" + accountNumber;
        try {
            AccountDTO accountDTO = restTemplate.getForObject(url, AccountDTO.class);
            return Mono.just(accountDTO);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while make getByAccountNumber " + ex.getMessage());
            if (accountNumber.equals(ACCOUNT_NUMBER_OF_ATM)) {
                AccountDTO atmAccount = new AccountDTO();
                atmAccount.setAccount(ACCOUNT_NUMBER_OF_ATM);
                atmAccount.setHolders(List.of(new HolderDTO("20100047218", "BANCO DE CREDITO DEL PERU")));
                atmAccount.setCustomerId("20100047218");
                atmAccount.setProductId(CODE_PRODUCT_CUENTA_CORRIENTE);
                atmAccount.setAvailableBalance(INITIAL_AMOUNT_OF_ATM);
                createAccount(atmAccount);
                return Mono.just(atmAccount);
            } else {
                throw handleHttpClientException(ex);
            }
        }
    }

    @Override
    public Flux<AccountDTO> getAllAccountByCustomer(String customerId) {
        return null;
    }

    @Override
    public Mono<AccountDTO> createAccount(AccountDTO dto) {
        String url = urlAccountService + "/save";
        try {
            AccountDTO accountDTO = restTemplate.postForObject(url, dto, AccountDTO.class);
            return Mono.just(accountDTO);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while createAccount: " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Mono<AccountDTO> makeDepositAccount(double amount, String accountNumber) {
        String url = urlAccountService + "/deposit/" + accountNumber + "?amount=" + amount;
        try {
            AccountDTO accountDTO = restTemplate.getForObject(url, AccountDTO.class);
            return Mono.just(accountDTO);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while makeDepositAccount: " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Mono<AccountDTO> makeWithdrawalAccount(double amount, String accountNumber) {
        String url = urlAccountService + "/withdrawal/" + accountNumber + "?amount=" + amount;
        try {
            AccountDTO accountDTO = restTemplate.getForObject(url, AccountDTO.class);
            return Mono.just(accountDTO);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while makeWithdrawalAccount: " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Mono<Void> deleteAccount(String accountNumber) {
        return null;
    }

    //privates methods
    private String getErrorMessage(HttpClientErrorException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException io) {
            return io.getMessage();
        }
    }

    public RuntimeException handleHttpClientException(HttpClientErrorException ex) {
        switch (ex.getStatusCode()) {
            case NOT_FOUND:
                return new NotFoundException(getErrorMessage(ex));
            case BAD_REQUEST:
                return new BadRequestException(getErrorMessage(ex));
            case UNPROCESSABLE_ENTITY:
                return new InvaliteInputException(getErrorMessage(ex));
            default:
                return ex;
        }
    }


}
