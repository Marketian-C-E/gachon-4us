package co.kr.myfitnote.prescription.data;

import co.kr.myfitnote.account.data.model.Client;

public class Prescription {
    private int id;
    private Client client;
    private Comparable manager;
    private String prescription_date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Comparable getManager() {
        return manager;
    }

    public void setManager(Comparable manager) {
        this.manager = manager;
    }

    public String getPrescription_date() {
        return prescription_date;
    }

    public void setPrescription_date(String prescription_date) {
        this.prescription_date = prescription_date;
    }
}
