package ru.khaksbyt.util;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;

public class UtilsDate {
    public static LocalDateTime convert(XMLGregorianCalendar xmlGregorianCalendar) {
        return xmlGregorianCalendar.toGregorianCalendar().toZonedDateTime().toLocalDateTime();
    }
}
