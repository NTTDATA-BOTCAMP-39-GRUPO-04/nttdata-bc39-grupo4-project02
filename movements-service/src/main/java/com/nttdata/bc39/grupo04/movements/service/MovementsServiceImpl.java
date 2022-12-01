package com.nttdata.bc39.grupo04.movements.service;

import com.nttdata.bc39.grupo04.api.exceptions.InvaliteInputException;
import com.nttdata.bc39.grupo04.api.exceptions.NotFoundException;
import com.nttdata.bc39.grupo04.api.utils.CodesEnum;
import com.nttdata.bc39.grupo04.movements.dto.MovementsDTO;
import com.nttdata.bc39.grupo04.movements.dto.MovementsReportDTO;
import com.nttdata.bc39.grupo04.movements.persistence.MovementsEntity;
import com.nttdata.bc39.grupo04.movements.persistence.MovementsRepository;
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
public class MovementsServiceImpl implements MovementsService {
    private final MovementsRepository repository;
    private final MovementMapper mapper;
    private final Logger logger = Logger.getLogger(MovementsServiceImpl.class);

    @Autowired
    public MovementsServiceImpl(MovementsRepository repository, MovementMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Mono<MovementsDTO> saveMovement(MovementsDTO dto, CodesEnum codesEnum) {
        validationMovement(dto, codesEnum);
        if (codesEnum == CodesEnum.TYPE_WITHDRAWL) {
            dto.setAmount(dto.getAmount() * -1);
            dto.setComission(dto.getComission() * -1);
        }
        MovementsEntity entity = mapper.dtoToEntity(dto);
        entity.setTotalAmount(dto.getAmount() + dto.getComission());
        entity.setDate(Calendar.getInstance().getTime());
        return repository.save(entity)
                .map(mapper::entityToDto);
    }

    @Override
    public Flux<MovementsReportDTO> getAllMovementsByNumberAccount(String accountNumber) {
        return repository.findByAccount(accountNumber).map(mapper::entityToReportDto);
    }

    private void validationMovement(MovementsDTO dto, CodesEnum codesEnum) {
        if (Objects.isNull(dto)) {
            throw new InvaliteInputException("Error, el formato de los datos es invalido");
        }
        if (Objects.isNull(dto.getAccount())) {
            throw new InvaliteInputException("Error,el numero de cuenta es invalido");
        }
        if (Objects.isNull(dto.getTransferAccount())) {
            throw new InvaliteInputException("Error , cuenta transferencia es invalida.");
        }
        double amount = dto.getAmount();
        if (codesEnum == CodesEnum.TYPE_DEPOSIT) {
            if (amount < MIN_DEPOSIT_AMOUNT || amount > MAX_DEPOSIT_AMOUNT) {
                logger.debug("Error, limites de deposito ,  data: " + dto);
                throw new NotFoundException(String.format(Locale.getDefault(), "Error, se excedió los limites de deposito (min: %d PEN y max: %d PEN) ", MIN_DEPOSIT_AMOUNT, MAX_DEPOSIT_AMOUNT));
            }
        }
        if (codesEnum == CodesEnum.TYPE_WITHDRAWL) {
            if (amount < MIN_WITHDRAWAL_AMOUNT || amount > MAX_WITHDRAWAL_AMOUNT) {
                logger.debug("Error,  limites de retiro ,  data: " + dto);
                throw new NotFoundException(String.format(Locale.getDefault(), "Error, se excedió los limites de retiro (min: %d PEN y max: %d PEN)", MIN_WITHDRAWAL_AMOUNT, MAX_WITHDRAWAL_AMOUNT));
            }
        }
    }

}
