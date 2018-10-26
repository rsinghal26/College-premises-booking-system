package com.example.nimishgupta.mycollege;

public class SAC_response {

    private  String reason, userName, status,fromTime,toTime,uDate,uTime, uNextDate;

    public SAC_response(String reason, String userName, String status, String fromTime,String toTime, String uDate,String uTime,String uNextDate) {
        this.reason = reason;
        this.userName = userName;
        this.status = status;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.uDate = uDate;
        this.uTime = uTime;
        this.uNextDate = uNextDate;
    }

    public SAC_response(){

    }

    public String getReason() {
        return reason;
    }

    public String getUserName() {
        return userName;
    }

    public String getStatus() {
        return status;
    }

    public String getFromTime() {
        return fromTime;
    }

    public String getToTime() {
        return toTime;
    }

    public String getuDate() {
        return uDate;
    }

    public String getuTime() {
        return uTime;
    }

    public String getuNextDate() {
        return uNextDate;
    }
}
