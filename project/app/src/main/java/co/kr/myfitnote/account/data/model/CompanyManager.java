package co.kr.myfitnote.account.data.model;

public class CompanyManager {
    private Company company;
    private String id_number;
    private String phone;
    private String name;
    private int total_client_cnt;
    private int new_clint_cnt;

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getId_number() {
        return id_number;
    }

    public void setId_number(String id_number) {
        this.id_number = id_number;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotal_client_cnt() {
        return total_client_cnt;
    }

    public void setTotal_client_cnt(int total_client_cnt) {
        this.total_client_cnt = total_client_cnt;
    }

    public int getNew_clint_cnt() {
        return new_clint_cnt;
    }

    public void setNew_clint_cnt(int new_clint_cnt) {
        this.new_clint_cnt = new_clint_cnt;
    }
}
