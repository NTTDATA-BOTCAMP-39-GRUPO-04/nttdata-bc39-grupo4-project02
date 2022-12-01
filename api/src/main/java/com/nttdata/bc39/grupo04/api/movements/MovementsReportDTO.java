package com.nttdata.bc39.grupo04.api.movements;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovementsReportDTO {
    private String account;
    private String transferAccount;
    private double amount;
    private Date date;
}
