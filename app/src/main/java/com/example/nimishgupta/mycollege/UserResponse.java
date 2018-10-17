package com.example.nimishgupta.mycollege;

public class UserResponse {
    private  String reason, slotChoosen, userName, whatBooked, status,uDate,uTime, uNextDate;
    private  int mike,projector;

    public UserResponse(String reason, String slotChoosen, String userName, int mike, int projector, String whatBooked, String status,
                        String uDate,String uTime,String uNextDate) {
        this.reason = reason;
        this.slotChoosen = slotChoosen;
        this.userName = userName;
        this.mike = mike;
        this.projector = projector;
        this.whatBooked = whatBooked;
        this.status = status;
        this.uDate = uDate;
        this.uTime = uTime;
        this.uNextDate = uNextDate;
    }

    public UserResponse(){

    }
//    public Button getAcceptBtn(){
//        return acceptBtn;



//    }

    public String getReason() {
        return reason;
    }

    public String getSlotChoosen() {
        return slotChoosen;
    }

    public String getUserName() {
        return userName;
    }

    public int getMike() {
        return mike;
    }

    public int getProjector() {
        return projector;
    }

    public String getWhatBooked(){
        return whatBooked;
    }

    public String getStatus(){
        return status;
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
