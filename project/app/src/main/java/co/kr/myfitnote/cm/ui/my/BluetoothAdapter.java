package co.kr.myfitnote.cm.ui.my;

import android.graphics.BlendMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.polar.sdk.api.model.PolarDeviceInfo;

import java.util.ArrayList;

import co.kr.myfitnote.R;
import co.kr.myfitnote.core.utils.PreferencesManager;

public class BluetoothAdapter extends RecyclerView.Adapter<BluetoothAdapter.ViewHolder> {

    private ArrayList<PolarDeviceInfo> mDataset;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final String TAG = "BluetoothAdapter";
        private TextView device_id, is_saved;

        public ViewHolder(View view) {
            // Define click listener for the ViewHolder's View
            super(view);
            device_id = view.findViewById(R.id.device_id);
            is_saved = view.findViewById(R.id.is_saved);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String clickedDeviceId = mDataset.get(getAdapterPosition()).getDeviceId();
                    // Add bluetooth device id to shared preferences
                    PreferencesManager.getInstance(view.getContext()).saveData("BLE_DEVICE_ID", clickedDeviceId);
                    // Set check mark text
                    is_saved.setText("\uD83D\uDFE2");
                }
            });
        }

        public TextView getDeviceIdTv(){
            return device_id;
        }

    }

    public BluetoothAdapter(){
        mDataset = new ArrayList<>();
    }

    public void addDevice(PolarDeviceInfo device){
        mDataset.add(device);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bluetooth_device, parent, false);

        return new ViewHolder((ViewGroup) view);
    }

    @Override
    public void onBindViewHolder(@NonNull BluetoothAdapter.ViewHolder holder, int position) {
        holder.getDeviceIdTv().setText(mDataset.get(position).getDeviceId());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
