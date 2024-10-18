package co.kr.myfitnote.core.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import co.kr.myfitnote.account.data.model.Client;
import co.kr.myfitnote.account.data.model.CompanyManager;
import co.kr.myfitnote.account.data.model.User;

public class PreferencesManager {

    private static final String PREF_NAME = "co.kr.myfitnote";

    private static PreferencesManager instance;
    private SharedPreferences preferences;

    public PreferencesManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized PreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferencesManager(context.getApplicationContext());
        }
        return instance;
    }

    public void saveData(String key, String data) {
        preferences.edit().putString(key, data).apply();
    }

    public String getData(String key) {
        return preferences.getString(key, null);
    }

    public void clearData(String key) {
        /**
         * Clear data from SharedPreferences
         */
        preferences.edit().remove(key).apply();
    }

    public void clearData(String[] keys){
        /**
         * Clear data from SharedPreferences
         */
        SharedPreferences.Editor editor = preferences.edit();
        for (String key : keys){
            editor.remove(key);
        }
        editor.apply();
    }

    public User getUser(){
        /**
         * 저장된 데이터를 바탕으로 처리하여 User 객체 반환
         */
        Gson gson = new Gson();
        User user = null;
        String userJsonData = getData("USER_DATA");

        if (userJsonData != null){
            user = gson.fromJson(userJsonData, User.class);
            this.getData("USER_DATA");
        }

        return user;
    }

    public CompanyManager getCompanyManager(){
        /**
         * 저장된 데이터를 바탕으로 처리하여 회사 관리자 객체 반환
         */
        Gson gson = new Gson();
        CompanyManager companyManager = null;
        String managerJsonData = getData("MANAGER_DATA");

        if (managerJsonData != null){
            companyManager = gson.fromJson(managerJsonData, CompanyManager.class);
            this.getData("MANAGER_DATA");
        }

        return companyManager;
    }

    public Client getClient(){
        /**
         * 저장된 데이터를 바탕으로 처리하여 Client 객체 반환
         */
        Gson gson = new Gson();
        Client client = null;
        String clientJsonData = getData("CLIENT_DATA");

        if (clientJsonData != null){
            client = gson.fromJson(clientJsonData, Client.class);
            this.getData("CLIENT_DATA");
        }

        return client;
    }

}
