package com.nttdata.bc39.grupo04.composite.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nttdata.bc39.grupo04.api.account.AccountDTO;
import com.nttdata.bc39.grupo04.api.account.AccountService;
import com.nttdata.bc39.grupo04.api.exceptions.BadRequestException;
import com.nttdata.bc39.grupo04.api.exceptions.HttpErrorInfo;
import com.nttdata.bc39.grupo04.api.exceptions.InvaliteInputException;
import com.nttdata.bc39.grupo04.api.exceptions.NotFoundException;
import com.nttdata.bc39.grupo04.api.movements.MovementsDTO;
import com.nttdata.bc39.grupo04.api.movements.MovementsReportDTO;
import com.nttdata.bc39.grupo04.api.movements.MovementsService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Objects;

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
            if (Objects.isNull(movementsDTO)) {
                throw new BadRequestException("Error, no se pudo establer conexión con movements-service");
            }
            return Mono.just(movementsDTO);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while make deposit " + ex.getMessage());
            switch (ex.getStatusCode()) {
                case NOT_FOUND:
                    throw new NotFoundException(getErrorMessage(ex));
                case UNPROCESSABLE_ENTITY:
                    throw new InvaliteInputException(getErrorMessage(ex));
                case BAD_REQUEST:
                    throw new BadRequestException(getErrorMessage(ex));
                default:
                    logger.debug("Error status:" + ex.getStatusCode());
                    logger.warn("Error body: " + ex.getResponseBodyAsString());
                    throw ex;
            }
        }
    }


    @Override
    public Mono<MovementsDTO> saveWithdrawlMovement(MovementsDTO dto) {
        String url = urlMovementsService + "/withdrawl";
        try {
            MovementsDTO movementsDTO = restTemplate.postForObject(url, dto, MovementsDTO.class);
            if (Objects.isNull(movementsDTO)) {
                throw new BadRequestException("Error, no se pudo establer conexión con movements-service");
            }
            return Mono.just(movementsDTO);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while make withdrawl " + ex.getMessage());
            switch (ex.getStatusCode()) {
                case NOT_FOUND:
                    throw new NotFoundException(getErrorMessage(ex));
                case UNPROCESSABLE_ENTITY:
                    throw new InvaliteInputException(getErrorMessage(ex));
                case BAD_REQUEST:
                    throw new BadRequestException(getErrorMessage(ex));
                default:
                    logger.debug("Error status:" + ex.getStatusCode());
                    logger.warn("Error body: " + ex.getResponseBodyAsString());
                    throw ex;
            }
        }
    }

    @Override
    public Flux<MovementsReportDTO> getAllMovementsByNumberAccount(String accountNumber) {
        return null;
    }

    //Acount
    @Override
    public Mono<AccountDTO> getByAccountNumber(String accountNumber) {
        return null;
    }

    @Override
    public Flux<AccountDTO> getAllAccountByCustomer(String customerId) {
        return null;
    }

    @Override
    public Mono<AccountDTO> createAccount(AccountDTO dto) {
        return null;
    }

    @Override
    public Mono<AccountDTO> makeDepositAccount(double amount, String accountNumber) {
        return null;
    }

    @Override
    public Mono<AccountDTO> makeWithdrawal(double amount, String accountNumber) {
        return null;
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
}
