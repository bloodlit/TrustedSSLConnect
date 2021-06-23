package ru.khaksbyt.model.primary;

import lombok.Data;
import ru.khaksbyt.util.UtilsDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "nsi_ItemInfo")
public class NsiItemInfoDTO {
    @Id
    @Column(name = "registry_number")
    private Long registryNumber;

    private String name;

    @Column
    private LocalDateTime modified;

    public NsiItemInfoDTO(BigInteger registryNumber, String name, XMLGregorianCalendar modified) {
        this.registryNumber = registryNumber.longValue();
        this.name = name;

        this.modified = UtilsDate.convert(modified);
    }

    public NsiItemInfoDTO() {

    }
}
