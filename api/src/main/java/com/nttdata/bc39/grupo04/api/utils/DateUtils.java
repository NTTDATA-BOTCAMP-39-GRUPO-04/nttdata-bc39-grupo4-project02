package com.nttdata.bc39.grupo04.api.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public final class DateUtils {

    public static Date getDateWithFormatddMMyyyy(Date date) {
        if (Objects.isNull(date)) return null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateWithFormat = simpleDateFormat.format(date);
        try {
            return simpleDateFormat.parse(dateWithFormat);
        } catch (ParseException e) {
            return null;
        }
    }
}
