package co.kr.myfitnote.measurement.data;

import com.google.gson.annotations.SerializedName;

import co.kr.myfitnote.account.data.model.Client;

public class ClientFinalResultData {
    /**
     * 최종 결과 산출 데이터 모델
     */

    @SerializedName("client")
    private Client client;

    @SerializedName("measurement")
    private MeasurementData measurement;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public MeasurementData getMeasurement() {
        return measurement;
    }

    public void setMeasurement(MeasurementData measurement) {
        this.measurement = measurement;
    }
}
