package co.kr.myfitnote.account.data.model;

import java.util.Date;

import co.kr.myfitnote.measurement.data.ClientMeasurementData;

public class Client {
    String name;
    String phone;
    String birth_date;
    String gender;
    String height;
    String weight;
    String address;
    String username;
    String token;
    CompanyManager manager;
    ClientMeasurementData last_measurement;
    String disease_type;

    public Client(){}

    public Client(
            String name, String phone, String birth_date, String gender, String height, String weight, String address, CompanyManager manager, String diseaseType){
        this.name = name;
        this.phone = phone;
        this.birth_date = birth_date;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.manager = manager;
        this.address = address;
        this.disease_type = diseaseType;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public CompanyManager getManager() {
        return manager;
    }

    public void setManager(CompanyManager manager) {
        this.manager = manager;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ClientMeasurementData getLastMeasurement() {
        return last_measurement;
    }

    public void setLastMeasurement(ClientMeasurementData lastMeasurement) {
        this.last_measurement = lastMeasurement;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
