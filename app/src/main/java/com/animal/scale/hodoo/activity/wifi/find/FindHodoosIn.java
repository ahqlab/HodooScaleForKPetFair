package com.animal.scale.hodoo.activity.wifi.find;

import android.content.Context;

public interface FindHodoosIn {

    interface View{

        void registDeviceResult(Integer result);

        void successDevideResigt();

        void goBasicRegistActivity(int i);

        void goDiseasesRegistActivity(int petIdx);

        void goPhysicalRegistActivity(int petIdx);

        void goWeightRegistActivity(int petIdx);
    }

    interface Presenter{

        void loadData(Context context);

        void registDevice(String bssid);

        void confirmPetRegistration();

        void loginModelData(Context context);
    }
}
