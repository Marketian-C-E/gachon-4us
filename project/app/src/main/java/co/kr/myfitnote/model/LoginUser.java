package co.kr.myfitnote.model;


public class LoginUser {
    private String phone;
    private String password;

    public LoginUser(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

}
