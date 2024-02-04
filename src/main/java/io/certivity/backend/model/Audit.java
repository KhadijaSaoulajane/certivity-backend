package io.certivity.backend.model;

import org.springframework.data.annotation.Id;

import java.sql.Timestamp;
import java.util.Date;

public class Audit {

    @Id
    private String id;
    private Date timestamp;
    private String actionType;
    private String oldValue;
    private String newValue;

    public Audit(Date timestamp, String actionType, String oldValue, String newValue) {
        this.timestamp = timestamp;
        this.actionType = actionType;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }



    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }
}
