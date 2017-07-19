package org.cpswt.hla;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.joda.time.DateTime;

/**
 * FederateInfo
 */
public class FederateInfo {
    private String federateId;
    private String federateType;
    private boolean isLateJoiner;

    DateTime joinTime;
    DateTime resignTime;

    boolean timedOutResign;

    public String getFederateId() {
        return federateId;
    }

    public String getFederateType() {
        return federateType;
    }

    public boolean isLateJoiner() {
        return isLateJoiner;
    }

    public boolean isTimedOutResign() {
        return timedOutResign;
    }

    public DateTime getJoinTime() {
        return joinTime;
    }

    public DateTime getResignTime() {
        return resignTime;
    }

    public void setJoinTime(DateTime joinTime) {
        this.joinTime = joinTime;
    }

    public void setResignTime(DateTime resignTime) {
        this.resignTime = resignTime;
    }

    public void setTimedOutResign(boolean timedOutResign) {
        this.timedOutResign = timedOutResign;
    }

    @JsonIgnore
    public boolean hasJoined() {
        return this.joinTime != null;
    }

    @JsonIgnore
    public boolean hasResigned() {
        return this.resignTime != null;
    }

    public FederateInfo(String federateId, String federateType, boolean isLateJoiner) {
        this.federateId = federateId;
        this.federateType = federateType;
        this.isLateJoiner = isLateJoiner;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FederateInfo) {
            return this.federateId.equalsIgnoreCase(((FederateInfo) obj).federateId);
        }
        return false;
    }
}
