package co.kr.myfitnote.cm.ui.my;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.polar.sdk.api.PolarBleApi;
import com.polar.sdk.api.PolarBleApiDefaultImpl;
import com.polar.sdk.api.model.PolarDeviceInfo;

import co.kr.myfitnote.R;
import co.kr.myfitnote.core.polar.HeartRateManager;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;

public class ConnectBluetoothDeviceFragment extends Fragment {
    private final String TAG = "ConnectBluetoothDeviceFragment";

    private RecyclerView rv_bluetooth_device_list;
    private Disposable scanDisposable;
    private PolarBleApi api;

    public ConnectBluetoothDeviceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_connect_bluetooth_device, container, false);

        rv_bluetooth_device_list = view.findViewById(R.id.rv_bluetooth_device_list);
        rv_bluetooth_device_list.setLayoutManager(new LinearLayoutManager(getContext()));
        BluetoothAdapter bluetoothAdapter = new BluetoothAdapter();
        rv_bluetooth_device_list.setAdapter(bluetoothAdapter);

        api = PolarBleApiDefaultImpl.defaultImplementation(getContext(), PolarBleApi.ALL_FEATURES);
        scanDisposable = api.searchForDevice()
                        .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(PolarDeviceInfo -> {
                                    Log.e(TAG, PolarDeviceInfo.getDeviceId());
                                    // Add to recycler view
                                    bluetoothAdapter.addDevice(PolarDeviceInfo);
                                }, throwable -> {
                                    Log.e(TAG, throwable.getMessage());
                                }, () -> {
                                    Log.e(TAG, "complete");
                                });

        return view;
    }

    @Override
    public void onDestroy() {

        if (!scanDisposable.isDisposed()){
            // Dispose the disposable
            Log.e(TAG, "Disposable's dispose is called!");
            scanDisposable.dispose();
        }

        super.onDestroy();
    }
}