package co.kr.myfitnote.cm.ui.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.List;

import co.kr.myfitnote.R;
import co.kr.myfitnote.account.data.model.Client;
import co.kr.myfitnote.client.ui.home.ClientHomeActivity;
import co.kr.myfitnote.core.utils.PreferencesManager;
import co.kr.myfitnote.measurement.data.ClientMeasurementData;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ClientViewHolder> {
    static final private String TAG = "ClientAdapter";
    private List<Client> clientList;
    private NavController navController;
    private Context context;

    public ClientAdapter(List<Client> clients, NavController navController, Context context) {
        this.clientList = clients;
        this.navController = navController;
        this.context = context;
    }

    @NonNull
    @Override
    public ClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cm_client_item, parent, false);
        return new ClientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientViewHolder holder, int position) {
        Client client = clientList.get(position);

        ClientMeasurementData measurementData = client.getLastMeasurement();

        String exercise = measurementData.getExercise();
        String createdAt = measurementData.getCreated_at();

        holder.recentMeasurement.setText(exercise);
        holder.recentMeasurementDate.setText(createdAt);
        holder.clientName.setText(client.getName());
        holder.clientBirthDate.setText(client.getBirth_date());
        holder.clientUsername.setText(client.getUsername());


        // Set other TextViews with client information as needed
    }

    @Override
    public int getItemCount() {
        return clientList.size();
    }

    // public static
    class ClientViewHolder extends RecyclerView.ViewHolder {
        TextView recentMeasurement;
        TextView recentMeasurementDate;
        TextView clientName;
        TextView clientBirthDate;
        TextView clientUsername;
        // 측정, 결과 조회 버튼
        Button measurementBtn, resultBtn, exerciseBtn;

        // Add other TextViews for birth date, gender, height, weight, address, and manager as needed

        public ClientViewHolder(@NonNull View itemView) {
            super(itemView);
            recentMeasurement = itemView.findViewById(R.id.recent_measure_item_tv);
            recentMeasurementDate = itemView.findViewById(R.id.recent_measure_date_tv);
            clientName = itemView.findViewById(R.id.client_name_tv);
            clientBirthDate = itemView.findViewById(R.id.client_birth_date_tv);
            clientUsername = itemView.findViewById(R.id.client_username_tv);

            measurementBtn = itemView.findViewById(R.id.measurement_btn);
            resultBtn = itemView.findViewById(R.id.result_btn);
            exerciseBtn = itemView.findViewById(R.id.exercise_btn);

            measurementBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Client client = clientList.get(position);
                    Log.e(TAG, client.getName() + " => 측정 버튼 클릭");

                    Bundle bundle = new Bundle();
                    bundle.putString("name", client.getName());
                    bundle.putString("phone", client.getPhone());
                    bundle.putString("birth", client.getBirth_date());
                    bundle.putString("weight", client.getWeight());
                    bundle.putString("height", client.getHeight());
                    bundle.putString("gender", client.getGender());
                    navController.navigate(R.id.action_clientSearchFragment_to_measurementMenuFragment, bundle);
                }
            });

            resultBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Client client = clientList.get(position);
                    Log.e(TAG, client.getName() + " => 결과 버튼 클릭");

                    Bundle bundle = new Bundle();
                    bundle.putString("name", client.getName());
                    bundle.putString("phone", client.getPhone());
                    bundle.putString("username", client.getUsername());
                    bundle.putString("token", client.getToken());
                    navController.navigate(R.id.action_clientSearchFragment_to_measurementResultListFragment, bundle);
                }
            });

            exerciseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Client client = clientList.get(position);
                    Intent it;
                    it = new Intent(context, ClientHomeActivity.class);
                    it.putExtra("client_id", client.getUsername());
                    PreferencesManager preferencesManager = PreferencesManager.getInstance(context);
                    preferencesManager.saveData("CLIENT_DATA", new Gson().toJson(client));
                    context.startActivity(it);
                }
            });
        }
    }

    public void updateData(List<Client> newData) {
        this.clientList = newData;
        notifyDataSetChanged();
    }
}