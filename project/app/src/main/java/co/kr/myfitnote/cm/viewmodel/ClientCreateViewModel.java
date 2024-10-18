package co.kr.myfitnote.cm.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ClientCreateViewModel extends ViewModel {

    private MutableLiveData<String> name = new MutableLiveData<>();
    private MutableLiveData<String> phone = new MutableLiveData<>();
    private MutableLiveData<String> birthDate = new MutableLiveData<>();

    public LiveData<String> getName() {
        return name;
    }

    public void setName(String newName) {
        name.setValue(newName);
    }

    public LiveData<String> getPhone() {
        return phone;
    }

    public void setPhone(String newPhone) {
        phone.setValue(newPhone);
    }

    public LiveData<String> getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String newBirthDate) {
        birthDate.setValue(newBirthDate);
    }
}
