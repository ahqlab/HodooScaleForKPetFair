package com.animal.scale.hodoo.activity.home.activity;

import android.content.Context;

import com.animal.scale.hodoo.common.AbstractAsyncTaskOfList;
import com.animal.scale.hodoo.common.AsyncTaskCancelTimerTask;
import com.animal.scale.hodoo.common.CommonModel;
import com.animal.scale.hodoo.common.SharedPrefManager;
import com.animal.scale.hodoo.common.SharedPrefVariable;
import com.animal.scale.hodoo.domain.InvitationUser;
import com.animal.scale.hodoo.domain.PetAllInfos;
import com.animal.scale.hodoo.service.NetRetrofit;

import java.util.List;

import retrofit2.Call;

public class HomeActivityModel extends CommonModel{

    Context context;

    public SharedPrefManager mSharedPrefManager;

    public void loadData(Context context){
        this.context = context;
        mSharedPrefManager = SharedPrefManager.getInstance(context);
    }

    public void getPetAllInfo(final DomainListCallBackListner<PetAllInfos> domainListCallBackListner) {
        Call<List<PetAllInfos>> call = NetRetrofit.getInstance().getPetBasicInfoService().getAboutMyPetList(mSharedPrefManager.getStringExtra(SharedPrefVariable.GROUP_CODE));
        new AsyncTaskCancelTimerTask(new AbstractAsyncTaskOfList<PetAllInfos>() {
            @Override
            protected void doPostExecute(List<PetAllInfos> petAllInfos) {
                domainListCallBackListner.doPostExecute(petAllInfos);
            }

            @Override
            protected void doPreExecute() {
                domainListCallBackListner.doPreExecute();
            }

            @Override
            protected void doCancelled() {
                domainListCallBackListner.doCancelled();
            }
        }.execute(call), limitedTime , interval, true).start();
    }

    public void getInvitationCount( final HomeActivityModel.DomainListCallBackListner<InvitationUser> callback ) {
        int idx = mSharedPrefManager.getIntExtra(SharedPrefVariable.USER_UNIQUE_ID);
        Call<List<InvitationUser>> call = NetRetrofit.getInstance().getInvitationService().getInvitationUser(idx);
        new AsyncTaskCancelTimerTask(new AbstractAsyncTaskOfList<InvitationUser>() {
            @Override
            protected void doPostExecute(List<InvitationUser> users) {
//                for (int i = 0; i < users.size(); i++)
//                    if ( users.get(i).getState() > 0 )
//                        users.remove(i);


                callback.doPostExecute(users);
            }

            @Override
            protected void doPreExecute() {

            }

            @Override
            protected void doCancelled() {

            }
        }.execute(call), limitedTime, interval, true).start();
    }
    public String getPetAllInfo() {
        return mSharedPrefManager.getStringExtra(SharedPrefVariable.PET_ALL_INFO);
    }
    public void setPetAllInfo ( String petAllInfo ) {
        mSharedPrefManager.putStringExtra(SharedPrefVariable.PET_ALL_INFO, petAllInfo);
    }
    public void setNotiCount ( int count ) {
        mSharedPrefManager.putIntExtra(SharedPrefVariable.BADGE_COUNT, count);
    }
    public int getUserIdx () {
        return mSharedPrefManager.getIntExtra(SharedPrefVariable.USER_UNIQUE_ID);
    }
}
