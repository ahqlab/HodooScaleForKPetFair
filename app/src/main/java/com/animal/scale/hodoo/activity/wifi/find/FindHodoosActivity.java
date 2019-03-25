package com.animal.scale.hodoo.activity.wifi.find;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.animal.scale.hodoo.R;
import com.animal.scale.hodoo.activity.home.activity.HomeActivity;
import com.animal.scale.hodoo.activity.pet.regist.basic.BasicInformationRegistActivity;
import com.animal.scale.hodoo.activity.pet.regist.disease.DiseaseInformationRegistActivity;
import com.animal.scale.hodoo.activity.pet.regist.physique.PhysiqueInformationRegistActivity;
import com.animal.scale.hodoo.activity.pet.regist.weight.WeightCheckActivity;
import com.animal.scale.hodoo.activity.setting.device.bowelplate.list.BowelPlateListActivity;
import com.animal.scale.hodoo.activity.wifi.WifiSearchActivity;
import com.animal.scale.hodoo.base.BaseActivity;
import com.animal.scale.hodoo.constant.HodooConstant;
import com.animal.scale.hodoo.databinding.ActivityFindHodoosBinding;
import com.animal.scale.hodoo.domain.ActivityInfo;
import com.animal.scale.hodoo.util.VIewUtil;
import com.cmmakerclub.iot.esptouch.EsptouchTask;
import com.cmmakerclub.iot.esptouch.IEsptouchListener;
import com.cmmakerclub.iot.esptouch.IEsptouchResult;
import com.cmmakerclub.iot.esptouch.IEsptouchTask;
import com.cmmakerclub.iot.esptouch.activity.EspWifiAdminSimple;
import com.cmmakerclub.iot.esptouch.activity.MainActivity;
import com.cmmakerclub.iot.esptouch.task.__IEsptouchTask;
/*import com.espressif.iot.esptouch.EsptouchTask;
import com.espressif.iot.esptouch.IEsptouchListener;
import com.espressif.iot.esptouch.IEsptouchResult;
import com.espressif.iot.esptouch.IEsptouchTask;
import com.espressif.iot.esptouch.util.ByteUtil;
import com.espressif.iot.esptouch.util.EspNetUtil;*/

import java.lang.ref.WeakReference;
import java.util.List;

public class FindHodoosActivity extends BaseActivity<FindHodoosActivity> implements FindHodoosIn.View{

    ProgressBar mprogressBar;

    public ActivityFindHodoosBinding binding;

   // private EsptouchAsyncTask4 mTask;

    String ssid;

    String bssid;

    String password;

    FindHodoosIn.Presenter presenter;

    WifiSearchActivity wifiSearchActivity;

    Button connectBtn;

    private EspWifiAdminSimple mWifiAdmin;

    private boolean inAppSettingState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        inAppSettingState = intent.getBooleanExtra(HodooConstant.IN_APP_SETTING_KEY, false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_find_hodoos);
        binding.setActivity(this);
        binding.setActivityInfo(new ActivityInfo(getString(R.string.find_hodoo_title)));
        wifiSearchActivity = (WifiSearchActivity) WifiSearchActivity.wifiSearchActivity;
        //mWifiAdmin = new EspWifiAdminSimple(this);
        presenter = new FindHodoosPresenter(this);
        presenter.loadData(FindHodoosActivity.this);
        presenter.loginModelData(FindHodoosActivity.this);
        binding.findDeviceLayout.setVisibility(View.GONE);
        ssid = intent.getStringExtra("ssid");
        bssid = intent.getStringExtra("bssid");
        password = intent.getStringExtra("password");
        createConnectBtn();
        super.setToolbarColor();
    }

    @Override
    protected BaseActivity<FindHodoosActivity> getActivityClass() {
        return FindHodoosActivity.this;
    }

    public void createConnectBtn(){
        VIewUtil vIewUtil = new VIewUtil(this);
        final int height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 58, getResources().getDisplayMetrics());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
        params.setMargins(vIewUtil.numberToDP(40),vIewUtil.numberToDP(0),vIewUtil.numberToDP(40),vIewUtil.numberToDP(31));
        connectBtn = new Button(this);
        connectBtn.setText(getString(R.string.wifi_connection));
        connectBtn.setTypeface(Typeface.DEFAULT_BOLD);
        connectBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
        connectBtn.setTextColor(Color.WHITE);
        connectBtn.setLayoutParams(params);
        connectBtn.setBackground( getResources().getDrawable(R.drawable.rounded_pink_btn));
        connectBtn.setStateListAnimator(null);
        ViewCompat.setElevation(connectBtn, vIewUtil.numberToDP(1));
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEsptouchInfo(ssid , bssid, password);
                connectBtn.setVisibility(View.GONE);
            }
        });
        binding.btnArea.addView(connectBtn);
    }


    public void createSaveBtn(final String bssid){
        VIewUtil vIewUtil = new VIewUtil(this);
        final int height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 58, getResources().getDisplayMetrics());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
        params.setMargins(vIewUtil.numberToDP(40),vIewUtil.numberToDP(0),vIewUtil.numberToDP(40),vIewUtil.numberToDP(31));
        Button btn = new Button(this);
        btn.setText(getString(R.string.istyle_regist));
        btn.setTypeface(Typeface.DEFAULT_BOLD);
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
        btn.setTextColor(Color.WHITE);
        btn.setLayoutParams(params);
        btn.setBackground( getResources().getDrawable(R.drawable.rounded_pink_btn));
        btn.setStateListAnimator(null);
        ViewCompat.setElevation(btn, vIewUtil.numberToDP(1));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.registDevice(bssid);
            }
        });
        binding.findHodooMessage2.setText(getString(R.string.istyle_plate_found_message));
        binding.findDeviceIndicatior.setVisibility(View.GONE);
        binding.wifiIcon.setVisibility(View.GONE);
        binding.findDeviceLayout.setVisibility(View.VISIBLE);
        binding.btnArea.removeAllViews();
        binding.btnArea.addView(btn);
    }

    public void createRetryBtn(){
        VIewUtil vIewUtil = new VIewUtil(this);
        final int height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 58, getResources().getDisplayMetrics());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
        params.setMargins(vIewUtil.numberToDP(40),vIewUtil.numberToDP(0),vIewUtil.numberToDP(40),vIewUtil.numberToDP(31));
        Button btn = new Button(this);
        btn.setText(getString(R.string.istyle_retry));
        btn.setTypeface(Typeface.DEFAULT_BOLD);
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
        btn.setTextColor(getResources().getColor(R.color.hodoo_midle_pink));
        btn.setLayoutParams(params);
        btn.setBackground( getResources().getDrawable(R.drawable.rounded_gray_btn));
        btn.setStateListAnimator(null);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEsptouchInfo(ssid , bssid, password);
            }
        });
        ViewCompat.setElevation(btn, vIewUtil.numberToDP(1));
        binding.btnArea.removeAllViews();
        binding.btnArea.addView(btn);
    }


    private void showEsptouchInfo(String pSsid, String pBssid, String pPassword) {
        String isSsidHiddenStr = "NO";
        String taskResultCountStr = "1";
        new EsptouchAsyncTask3().execute(pSsid, pBssid, pPassword, isSsidHiddenStr, taskResultCountStr);
    }

    @Override
    public void registDeviceResult(Integer result) {
        if(result != 0){
            if(result == 100){
                super.showBasicOneBtnPopup(null, getString(R.string.device_is_already_registered))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }else if(result == 1){
                presenter.confirmPetRegistration();
            }
        }else{
            //서버와의 연결이 끊어졌습니다.
        }
    }

    @Override
    public void successDevideResigt() {
        wifiSearchActivity.finish();
        if ( !inAppSettingState ) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.end_enter, R.anim.end_exit);
        } else {
            Intent intent = new Intent(getApplicationContext(), BowelPlateListActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.end_enter, R.anim.end_exit);
        }
        finish();
    }

    @Override
    public void goBasicRegistActivity(int petIdx) {
        Intent intent = new Intent(getApplicationContext(), BasicInformationRegistActivity.class);
        intent.putExtra("petIdx", petIdx);
        startActivity(intent);
        wifiSearchActivity.finish();
        finish();
    }

    @Override
    public void goDiseasesRegistActivity(int petIdx) {
        Intent intent = new Intent(getApplicationContext(), DiseaseInformationRegistActivity.class);
        intent.putExtra("petIdx", petIdx);
        startActivity(intent);
        wifiSearchActivity.finish();
        finish();
    }

    @Override
    public void goPhysicalRegistActivity(int petIdx) {
        Intent intent = new Intent(getApplicationContext(), PhysiqueInformationRegistActivity.class);
        intent.putExtra("petIdx", petIdx);
        startActivity(intent);
        wifiSearchActivity.finish();
        finish();
    }

    @Override
    public void goWeightRegistActivity(int petIdx) {
        Intent intent = new Intent(getApplicationContext(), WeightCheckActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.end_enter, R.anim.end_exit);
        wifiSearchActivity.finish();
        finish();
    }

    /*public class EsptouchAsyncTask4 extends AsyncTask<byte[], Void, List<IEsptouchResult>> {
        private WeakReference<FindHodoosActivity> mActivity;

        // without the lock, if the user tap confirm and cancel quickly enough,
        // the bug will arise. the reason is follows:
        // 0. task is starting created, but not finished
        // 1. the task is cancel for the task hasn't been created, it do nothing
        // 2. task is created
        // 3. Oops, the task should be cancelled, but it is running
        private final Object mLock = new Object();
        private ProgressDialog mProgressDialog;
        private android.support.v7.app.AlertDialog mResultDialog;
        private IEsptouchTask mEsptouchTask;

        EsptouchAsyncTask4(FindHodoosActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        void cancelEsptouch() {
            cancel(true);
            if (mEsptouchTask != null) {
                mEsptouchTask.interrupt();
            }
        }

        @Override
        protected void onPreExecute() {
            binding.circleView.spin();
        }

        @Override
        protected List<IEsptouchResult> doInBackground(byte[]... params) {
            FindHodoosActivity activity = mActivity.get();
            int taskResultCount;
            synchronized (mLock) {
                byte[] apSsid = params[0];
                byte[] apBssid = params[1];
                byte[] apPassword = params[2];
                byte[] deviceCountData = params[3];
                byte[] broadcastData = params[4];
                taskResultCount = deviceCountData.length == 0 ? -1 : Integer.parseInt(new String(deviceCountData));
                Context context = activity.getApplicationContext();
                mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, context);
                mEsptouchTask.setPackageBroadcast(broadcastData[0] == 1);
                mEsptouchTask.setEsptouchListener(activity.myListener);
            }
            return mEsptouchTask.executeForResults(taskResultCount);
        }

        @Override
        protected void onPostExecute(List<IEsptouchResult> result) {
            FindHodoosActivity activity = mActivity.get();
            if (result == null) {
//                Toast.makeText(activity ,"Create Esptouch task failed, the esptouch port could be used by other thread", Toast.LENGTH_SHORT).show();
                return;
            }
            IEsptouchResult firstResult = result.get(0);
            // check whether the task is cancelled and no results received
            if (!firstResult.isCancelled()) {
                int count = 0;
                // max results to be displayed, if it is more than maxDisplayCount,
                // just show the count of redundant ones
                final int maxDisplayCount = 5;
                // the task received some results including cancelled while
                // executing before receiving enough results

                if (firstResult.isSuc()) {
                    StringBuilder sb = new StringBuilder();
                    for (IEsptouchResult resultInList : result) {
                        sb.append("Esptouch success, bssid = ")
                                .append(resultInList.getBssid())
                                .append(", InetAddress = ")
                                .append(resultInList.getInetAddress().getHostAddress())
                                .append("\n");
                        count++;
                        if (count >= maxDisplayCount) {
                            break;
                        }
                    }
                    if (count < result.size()) {
                        sb.append("\nthere's ")
                                .append(result.size() - count)
                                .append(" more result(s) without showing\n");
                    }
                    //Toast.makeText(activity, sb.toString(), Toast.LENGTH_LONG).show();
                    createSaveBtn(result.get(0).getBssid());
                    binding.deviceInfo.setText(sb);
                } else {
//                    Toast.makeText(activity, "Esptouch fail", Toast.LENGTH_LONG).show();
                    createRetryBtn();
                }
            }
            activity.mTask = null;
            binding.circleView.stopSpinning();
        }
    }*/

    private void onEsptoucResultAddedPerform(final IEsptouchResult result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String text = result.getBssid() + " is connected to the wifi";
                /*Answers.getInstance().logCustom(new CustomEvent("ESPTOUCH wifi connected"));*/
                Toast.makeText(FindHodoosActivity.this, text,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private IEsptouchListener myListener = new IEsptouchListener() {

        @Override
        public void onEsptouchResultAdded(final IEsptouchResult result) {
            onEsptoucResultAddedPerform(result);
        }
    };

    private class EsptouchAsyncTask3 extends AsyncTask<String, Void, List<IEsptouchResult>> {

        private IEsptouchTask mEsptouchTask;
        // without the lock, if the user tap confirm and cancel quickly enough,
        // the bug will arise. the reason is follows:
        // 0. task is starting created, but not finished
        // 1. the task is cancel for the task hasn't been created, it do nothing
        // 2. task is created
        // 3. Oops, the task should be cancelled, but it is running
        private final Object mLock = new Object();

        @Override
        protected void onPreExecute() {
            binding.circleView.spin();
            /*mProgressDialog = new ProgressDialog(FindHodoosActivity.this);
            mProgressDialog
                    .setMessage("Esptouch is configuring, please wait for a moment...");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    synchronized (mLock) {
                        if (__IEsptouchTask.DEBUG) {
                            Log.i(TAG, "progress dialog is canceled");
                        }
                        if (mEsptouchTask != null) {
                            mEsptouchTask.interrupt();
                        }
                    }
                }
            });
            mProgressDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                    "Waiting...", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            mProgressDialog.show();
            mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                    .setEnabled(false);*/
        }

        @Override
        protected List<IEsptouchResult> doInBackground(String... params) {
            int taskResultCount = -1;
            synchronized (mLock) {
                String apSsid = params[0];
                String apBssid = params[1];
                String apPassword = params[2];
                String isSsidHiddenStr = params[3];
                String taskResultCountStr = params[4];
                boolean isSsidHidden = false;
                if (isSsidHiddenStr.equals("YES")) {
                    isSsidHidden = true;
                }
                taskResultCount = Integer.parseInt(taskResultCountStr);
                mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword,
                        isSsidHidden, FindHodoosActivity.this);
                mEsptouchTask.setEsptouchListener(myListener);

            }
            List<IEsptouchResult> resultList = mEsptouchTask.executeForResults(taskResultCount);
            return resultList;
        }

        @Override
        protected void onPostExecute(List<IEsptouchResult> result) {
            IEsptouchResult firstResult = result.get(0);
            // check whether the task is cancelled and no results received
            if (!firstResult.isCancelled()) {
                int count = 0;
                // max results to be displayed, if it is more than maxDisplayCount,
                // just show the count of redundant ones
                final int maxDisplayCount = 5;
                // the task received some results including cancelled while
                // executing before receiving enough results
                if (firstResult.isSuc()) {
                    StringBuilder sb = new StringBuilder();
                   /* Answers.getInstance()
                            .logCustom(new CustomEvent("ESPTOUCH[] success")
                                    .putCustomAttribute("SIZE", result.size()));*/
                    for (IEsptouchResult resultInList : result) {
                        sb.append("Esptouch success, bssid = "
                                + resultInList.getBssid()
                                + ",InetAddress = "
                                + resultInList.getInetAddress()
                                .getHostAddress() + "\n");
                        count++;
                        if (count >= maxDisplayCount) {
                            break;
                        }
                    }
                    if (count < result.size()) {
                        sb.append("\nthere's " + (result.size() - count)
                                + " more result(s) without showing\n");
                    }
                    //mProgressDialog.setMessage(sb.toString());
                    createSaveBtn(result.get(0).getBssid());
                    binding.deviceInfo.setText(sb);
                } else {
                    //mProgressDialog.setMessage("Esptouch fail");
                    createRetryBtn();
                }
            }
            //activity.mTask = null;
            binding.circleView.stopSpinning();
        }
    }

}
