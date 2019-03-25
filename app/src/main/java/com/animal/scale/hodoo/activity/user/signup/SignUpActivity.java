package com.animal.scale.hodoo.activity.user.signup;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.animal.scale.hodoo.R;
import com.animal.scale.hodoo.base.BaseActivity;
import com.animal.scale.hodoo.common.SharedPrefVariable;
import com.animal.scale.hodoo.custom.view.input.CommonTextWatcher;
import com.animal.scale.hodoo.databinding.ActivitySignUpBinding;
import com.animal.scale.hodoo.domain.Country;
import com.animal.scale.hodoo.domain.ResultMessageGroup;
import com.animal.scale.hodoo.domain.User;
import com.animal.scale.hodoo.util.RSA;
import com.animal.scale.hodoo.util.VIewUtil;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class SignUpActivity extends BaseActivity<SignUpActivity> implements SignUpIn.View {

    ActivitySignUpBinding binding;

    boolean isEmailVailed = false;

    SignUpIn.Presenter presenter;

    private boolean radioCheckState = false;
    private boolean emailState = false;
    private boolean pwState = false;
    private boolean pwCheckState = false;
    private boolean ninkState = false;
    private boolean countryState = false;
    private boolean agreeState = false;

    private int selectCountry = 0;
    private List<Country> countries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        super.setToolbarColor();
        presenter = new SignUpPresenter(this);
        presenter.loadData(SignUpActivity.this);
        binding.setActivity(this);
//        binding.setActivityInfo(new ActivityInfo(getString(R.string.signup_title)));
        binding.setUser(new User());

        binding.email.editText.addTextChangedListener(new CommonTextWatcher(binding.email, this, CommonTextWatcher.EMAIL_TYPE, R.string.vailed_email, new CommonTextWatcher.CommonTextWatcherCallback() {
            @Override
            public void onChangeState(boolean state) {
                emailState = state;
                Log.e(TAG, String.format("emailState : %b", emailState));
                vaildation();
            }
        }));
        binding.password.editText.addTextChangedListener(new CommonTextWatcher(
                binding.password,
                binding.passwordCheck,
                this,
                CommonTextWatcher.JOIN_PW_TYPE,
                new int[]{
                        R.string.istyle_enter_the_password,
                        R.string.istyle_password_is_incorrect
                },
                new CommonTextWatcher.CommonTextWatcherCallback() {
                    @Override
                    public void onChangeState(boolean state) {
                        pwState = state;
                        Log.e(TAG, String.format("pwState : %b", pwState));
                        vaildation();
                    }
                }
        ));
        binding.passwordCheck.editText.addTextChangedListener(new CommonTextWatcher(
                binding.passwordCheck,
                binding.password,
                this,
                CommonTextWatcher.PWCHECK_TYPE,
                new int[]{
                        R.string.istyle_enter_your_confirmation_password,
                        R.string.istyle_password_is_incorrect
                },
                new CommonTextWatcher.CommonTextWatcherCallback() {
                    @Override
                    public void onChangeState(boolean state) {
                        pwCheckState = state;
                        if ( pwCheckState )
                            pwState = true;
                        Log.e(TAG, String.format("pwState : %b", pwCheckState));
                        vaildation();
                    }
                }
        ));
        binding.nickName.editText.addTextChangedListener(new CommonTextWatcher(
                binding.nickName,
                this,
                CommonTextWatcher.EMPTY_TYPE,
                R.string.istyle_enter_your_nik_name,
                new CommonTextWatcher.CommonTextWatcherCallback() {
                    @Override
                    public void onChangeState(boolean state) {
                        ninkState = state;
                        Log.e(TAG, String.format("ninkState : %b", ninkState));
                        vaildation();
                    }
                }
        ));
        binding.from.editText.addTextChangedListener(new CommonTextWatcher(
                binding.from,
                this,
                CommonTextWatcher.EMPTY_TYPE,
                0,
                new CommonTextWatcher.CommonTextWatcherCallback() {
                    @Override
                    public void onChangeState(boolean state) {
                        countryState = state;
                        Log.e(TAG, String.format("countryState : %b", countryState));
                        vaildation();
                    }
                }
        ));
        binding.from.editText.setFocusable(false);
        binding.from.editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCountryEditTextClick(view);
            }
        });
        binding.radioGroupSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if ( !radioCheckState ) {
                    radioCheckState = true;
                    vaildation();
                }

            }
        });
        setBtnEnable(false);
        binding.singupAgreeCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                agreeState = b;
                vaildation();
            }
        });
     /*   binding.setErrorMsg(getString(R.string.vailed_email));
        binding.setEmailRule(new MyOwnBindingUtil.StringRule() {
            @Override
            public boolean validate(Editable s) {
                if (!ValidationUtil.isValidEmail(s.toString())) {
                    isEmailVailed = false;
                    return false;
                } else {
                    isEmailVailed = true;
                    return true;
                }
            }
        });*/
    }

    @Override
    protected BaseActivity<SignUpActivity> getActivityClass() {
        return SignUpActivity.this;
    }

    //ESP31
    public void onClickSubmitBtn(View view) {
//        if (!ValidationUtil.isValidEmail(binding.email.getText().toString())) {
//            //이메일 형식에 어긋납니다.
//            super.showBasicOneBtnPopup(null, getString(R.string.istyle_not_valid_email_format))
//                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    }).show();
//            return;
//        } else if (ValidationUtil.isEmpty(binding.password)) {
//            super.showBasicOneBtnPopup(null, getString(R.string.istyle_enter_the_password))
//                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    }).show();
//            return;
//        } else if (ValidationUtil.isEmpty(binding.passwordCheck)) {
//            super.showBasicOneBtnPopup(null, getString(R.string.istyle_enter_your_confirmation_password))
//                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    }).show();
//        } else if (!binding.passwordCheck.getText().toString().matches(binding.password.getText().toString())) {
//            super.showBasicOneBtnPopup(null, getString(R.string.istyle_password_is_incorrect))
//                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    }).show();
//        } else if (ValidationUtil.isEmpty(binding.nickName)) {
//            super.showBasicOneBtnPopup(null, getString(R.string.istyle_enter_your_nik_name))
//                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    }).show();
//        } else if (!binding.radioFemale.isChecked() && !binding.radioMale.isChecked()) {
//            super.showBasicOneBtnPopup(null, getString(R.string.istyle_select_gender))
//                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    }).show();
//        } else if (ValidationUtil.isEmpty(binding.from)) {
//            super.showBasicOneBtnPopup(null, getString(R.string.istyle_enter_your_place_of_residence))
//                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    }).show();
//        } else {
        registUser();

//        }
    }

   /* public void sendServer() {
        Call<ResultMessageGroup> result = NetRetrofit.getInstance().getUserService().registUser(binding.getUser());
        result.enqueue(new Callback<ResultMessageGroup>() {
            @Override
            public void onResponse(Call<ResultMessageGroup> call, Response<ResultMessageGroup> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if((int) Double.parseDouble(response.body().getDomain().toString()) == 1){
                            goNextPage();
                        }else{
                            Toast.makeText(getApplicationContext(), getString(R.string.user_regist_failed_message), Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.user_regist_failed_message), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ResultMessageGroup> call, Throwable t) {
            }
        });
    }*/

    @Override
    public void goNextPage() {
        Intent intent = new Intent(getApplicationContext(), SignUpFinishActivity.class);
        intent.putExtra(SharedPrefVariable.USER_EMAIL, binding.email.editText.getText().toString());
        startActivity(intent);
        overridePendingTransition(R.anim.end_enter, R.anim.end_exit);
        finish();
    }

    @Override
    public void registUser() {
        User user = binding.getUser();
        user.setEmail( binding.email.editText.getText().toString() );
        user.setPassword( binding.password.editText.getText().toString() );
        user.setNickname( binding.nickName.editText.getText().toString() );
        user.setCountry(selectCountry);
        if (binding.radioFemale.isChecked()) {
            binding.getUser().setSex("FEMALE");
        } else if (binding.radioMale.isChecked()) {
            binding.getUser().setSex("MALE");
        }
        setProgress(true);
        presenter.registUser(binding.getUser());
    }

    @Override
    public void registUserResult(ResultMessageGroup resultMessageGroup) {

    }

    @Override
    public void showPopup(String message) {
        super.showBasicOneBtnPopup(getString(R.string.message), message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }
                ).show();
    }

    @Override
    public void sendEmail(String userEmail) {
        presenter.userCertifiedMailSend(userEmail);
    }

    @Override
    public void setProgress(boolean state) {
        binding.signupProgress.setVisibility( state ? View.VISIBLE : View.GONE );
    }


    public void onClickCountryEditTextClick(View view) {
        if ( countries != null ) {
            String[] countreis = new String[countries.size()];
            for (int i = 0; i < countries.size(); i++) {
                countreis[i] = countries.get(i).getName();
            }
            final String[] values = countreis;

            AlertDialog.Builder builder = super.showBasicOneBtnPopup(getResources().getString(R.string.choice_country), null);
            builder.setTitle(getResources().getString(R.string.choice_country));
            // add a radio button list
            builder.setItems(values, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    binding.from.editText.setText(values[which]);
                    selectCountry = countries.get(which).getId();
                    dialog.dismiss();
                }
            });
            // add OK and Cancel buttons
       /* builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user clicked OK
            }
        });
        builder.setNegativeButton("Cancel", null);*/
            AlertDialog dialog = builder.create();
            ListView listView = dialog.getListView();
            listView.setDivider(new ColorDrawable(Color.parseColor("#e1e1e1")));
            listView.setDividerHeight(2);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_rect_white_radius_10);
            dialog.show();
        }
    }
    @Override
    public void setCountry(List<Country> countries) {
        this.countries = countries;
    }

    private boolean checkValidation( int type ) {
        return true;
    }
    private void setBtnEnable ( boolean state ) {
        binding.confirm.setEnabled(state);
        if ( binding.confirm.isEnabled() ) {
            binding.confirm.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        } else {
            binding.confirm.setTextColor(ContextCompat.getColor(this, R.color.mainRed));
        }
    }
    private void vaildation() {
        if (
                emailState &&
                pwState &&
                pwCheckState &&
                ninkState &&
                countryState &&
                radioCheckState &&
                agreeState
                ) {
            setBtnEnable(true);
        } else {
            setBtnEnable(false);
        }
    }
    private String mixToken ( String uId ) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmDDss");
        String mix = "";
        try {
            mix = RSA.Encrypt(uId + sdf.format(new Date()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return mix;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ( countries == null )
            presenter.getAllCountry(VIewUtil.getLocationCode(this));
    }
}
