package co.kr.myfitnote.model;

import android.util.Patterns;

public class User {
    private String phone;
    private String password;
    private String name;
    private String birth_date;
    private String height;
    private String weight;
    private String gender;
    private String token;

    public User(
            String password,
            String phone,
            String birthDate,
            String weight,
            String height,
            String name,
            String gender,
            String token
    ){
        this.phone = phone;
        this.password = password;
        this.birth_date = birthDate;
        this.weight = weight;
        this.height = height;
        this.name = name;
        this.gender = gender;
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getBirthDate() {
        return birth_date;
    }

    public String getHeight() {
        return height;
    }

    public String getWeight() {
        return weight;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBirthDate(String birthDate) {
        this.birth_date = birthDate;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isPhoneValid() {
        return Patterns.PHONE.matcher(getPhone()).matches();
    }

    public void setSex(String sex) {
        this.gender = sex;
    }

    public String getSex() {
        return gender;
    }
}
