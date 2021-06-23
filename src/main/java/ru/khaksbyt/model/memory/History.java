package ru.khaksbyt.model.memory;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
public class History {
    @Id
    @Column(name = "message_guid")
    private String messageGUID;

    private LocalDateTime date;

    private String action;

    public History() {
        this.messageGUID = "";
        this.date = null;
        this.action = "";
    }

    public History(String messageGUID, LocalDateTime date, String soapAction) {
        this.messageGUID = messageGUID;
        this.date = date;
        this.action = soapAction;
    }
}
