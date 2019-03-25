package com.animal.scale.hodoo.activity.pet.regist.disease;

import android.content.Context;

import com.animal.scale.hodoo.common.AbstractAsyncTask;
import com.animal.scale.hodoo.common.AsyncTaskCancelTimerTask;
import com.animal.scale.hodoo.common.CommonModel;
import com.animal.scale.hodoo.common.SharedPrefManager;
import com.animal.scale.hodoo.common.SharedPrefVariable;
import com.animal.scale.hodoo.domain.PetChronicDisease;
import com.animal.scale.hodoo.service.NetRetrofit;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class DiseaseInformationModel extends CommonModel {

    public Context context;

    public SharedPrefManager mSharedPrefManager;

    DiseaseInformationIn.View view;

    public void loadData(Context context){
        this.context = context;
        mSharedPrefManager = SharedPrefManager.getInstance(context);
    };

    public void getDiseaseformation(int petIdx, final DomainCallBackListner<PetChronicDisease> domainCallBackListner) {
        Call<PetChronicDisease> call = NetRetrofit.getInstance().getPetChronicDiseaseService().getDiseaseformation(mSharedPrefManager.getStringExtra(SharedPrefVariable.GROUP_CODE), petIdx);
        new AsyncTaskCancelTimerTask(new AbstractAsyncTask<PetChronicDisease>(){
            @Override
            protected void doPostExecute(PetChronicDisease petChronicDisease) {
                domainCallBackListner.doPostExecute(petChronicDisease);
            }

            @Override
            protected void doPreExecute() {
                domainCallBackListner.doPreExecute();
            }

            @Override
            protected void doCancelled() {

            }
        }.execute(call), limitedTime, interval, true).start();
    }

    public List<PetChronicDisease> stringToListConversion(String diseaseName) {
        List<PetChronicDisease> petChronicDiseases = new ArrayList<PetChronicDisease>();
        String[] array = diseaseName.split(",");
        for(int i=0;i<array.length;i++) {
            if(!array[i].matches(",")){
                petChronicDiseases.add(new PetChronicDisease(array[i]));
            }
        }
        return petChronicDiseases;
    }

    public void deleteDiseaseformation(int petIdx, int diseaseIdx,  final DomainCallBackListner<Integer> domainCallBackListner) {
        Call<Integer> call = NetRetrofit.getInstance().getPetChronicDiseaseService().delete(petIdx, diseaseIdx);
        new AsyncTaskCancelTimerTask(new AbstractAsyncTask<Integer>(){

            @Override
            protected void doPostExecute(Integer result) {
                domainCallBackListner.doPostExecute(result);
            }

            @Override
            protected void doPreExecute() {
                domainCallBackListner.doPreExecute();
            }

            @Override
            protected void doCancelled() {

            }
        }.execute(call), limitedTime, interval, true).start();
    }
    public void registDiseaseformation(PetChronicDisease domain, int petIdx,  final DomainCallBackListner<Integer> domainCallBackListner) {
        Call<Integer> call = NetRetrofit.getInstance().getPetChronicDiseaseService().registDiseaseformation(domain, petIdx);
        new AsyncTaskCancelTimerTask(new AbstractAsyncTask<Integer>(){

            @Override
            protected void doPostExecute(Integer result) {
                domainCallBackListner.doPostExecute(result);
            }

            @Override
            protected void doPreExecute() {
                domainCallBackListner.doPreExecute();
            }

            @Override
            protected void doCancelled() {

            }
        }.execute(call), limitedTime, interval, true).start();
    }

    public interface PetDiseaseInformationResultListner {
        void doPostExecute(PetChronicDisease petChronicDisease);
        void doPreExecute();
    }

    public interface deleteInfoResultListner {
        void doPostExecute(Integer result);
        void doPreExecute();
    }

    public interface registResultListner {
        void doPostExecute(Integer result);
        void doPreExecute();
    }
}
