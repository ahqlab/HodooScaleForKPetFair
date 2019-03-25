package com.animal.scale.hodoo.activity.meal.regist;

import android.content.Context;

import com.animal.scale.hodoo.common.AbstractAsyncTask;
import com.animal.scale.hodoo.common.AsyncTaskCancelTimerTask;
import com.animal.scale.hodoo.common.CommonModel;
import com.animal.scale.hodoo.common.SharedPrefManager;
import com.animal.scale.hodoo.common.SharedPrefVariable;
import com.animal.scale.hodoo.domain.Feed;
import com.animal.scale.hodoo.domain.MealHistory;
import com.animal.scale.hodoo.domain.PetAllInfos;
import com.animal.scale.hodoo.service.NetRetrofit;

import retrofit2.Call;

public class MealRegistrationModel extends CommonModel {

    Context context;

    public SharedPrefManager sharedPrefManager;

    public void loadData(Context context) {
        this.context = context;
        sharedPrefManager = SharedPrefManager.getInstance(context);
    }

    public void getFeedInfo(int feedId, final DomainCallBackListner<Feed> domainCallBackListner) {
        Call<Feed> call = NetRetrofit.getInstance().getFeedService().getFeedInfo(feedId);
        new AsyncTaskCancelTimerTask(new AbstractAsyncTask<Feed>() {
            @Override
            protected void doPostExecute(Feed feed) {
                domainCallBackListner.doPostExecute(feed);
            }

            @Override
            protected void doPreExecute() {

            }

            @Override
            protected void doCancelled() {

            }
        }.execute(call), limitedTime, interval, true).start();
    }

    public void saveMeal(MealHistory mealHistory, final DomainCallBackListner<Integer> domainCallBackListner) {
        Call<Integer> call =  NetRetrofit.getInstance().getMealHistoryService().insert(mealHistory);
        new AsyncTaskCancelTimerTask(new AbstractAsyncTask<Integer>() {
            @Override
            protected void doPostExecute(Integer result) {
                domainCallBackListner.doPostExecute(result);
            }

            @Override
            protected void doPreExecute() {

            }

            @Override
            protected void doCancelled() {

            }
        }.execute(call), limitedTime, interval, true).start();
    }

    public void getPetAllInfo(final DomainCallBackListner<PetAllInfos> domainCallBackListner) {
        int petIdx = sharedPrefManager.getIntExtra(SharedPrefVariable.CURRENT_PET_IDX);
        Call<PetAllInfos> call = NetRetrofit.getInstance().getPetService().petAllInfos(sharedPrefManager.getIntExtra(SharedPrefVariable.CURRENT_PET_IDX));
        new AsyncTaskCancelTimerTask(new AbstractAsyncTask<PetAllInfos>() {
            @Override
            protected void doPostExecute(PetAllInfos petAllInfos) {
                domainCallBackListner.doPostExecute(petAllInfos);
            }
            @Override
            protected void doPreExecute() {

            }

            @Override
            protected void doCancelled() {

            }
        }.execute(call), limitedTime, interval, true).start();
    }

    public void getTodaySumCalorie(String date, final DomainCallBackListner<MealHistory> domainCallBackListner) {
        Call<MealHistory> call = NetRetrofit.getInstance().getMealHistoryService().getTodaySumCalorie(sharedPrefManager.getIntExtra(SharedPrefVariable.CURRENT_PET_IDX), date);
        new AsyncTaskCancelTimerTask(new AbstractAsyncTask<MealHistory>() {
            @Override
            protected void doPostExecute(MealHistory mealHistory) {
                domainCallBackListner.doPostExecute(mealHistory);
            }

            @Override
            protected void doPreExecute() {

            }

            @Override
            protected void doCancelled() {

            }
        }.execute(call), limitedTime, interval, true).start();
    }
}
