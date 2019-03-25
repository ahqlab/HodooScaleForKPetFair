package com.animal.scale.hodoo.activity.setting.user.account;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.animal.scale.hodoo.R;
import com.animal.scale.hodoo.base.BaseActivity;
import com.animal.scale.hodoo.common.CommonListener;
import com.animal.scale.hodoo.databinding.ActivityUserAccountBinding;
import com.animal.scale.hodoo.domain.ActivityInfo;
import com.animal.scale.hodoo.domain.User;

import java.util.List;

public class UserAccountActivity extends BaseActivity<UserAccountActivity> implements UserAccountIn.View {

    ActivityUserAccountBinding binding;

    UserAccountGridAdapter adapter;

    UserAccountIn.Presenter presenter;

    public static final int USER_REGIST = 0;

    private boolean editState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_account);
        binding.setActivity(this);
        binding.setActivityInfo(new ActivityInfo(getString(R.string.istyle_management_user)));
        super.setToolbarColor();

        presenter = new UserAccountPresenter(this);
        presenter.initUserData(getApplicationContext());
        presenter.getAccessType();
        presenter.getData();
    }

    @Override
    protected BaseActivity<UserAccountActivity> getActivityClass() {
        return UserAccountActivity.this;
    }


    @Override
    public void setEditBtn() {
        super.setSubBtn(getString(R.string.edit_btn_text), new OnSubBtnClickListener() {
            @Override
            public void onClick(View v) {
                ((Button) v).setText(!editState ? getString(R.string.compleate_btn_text) : getString(R.string.edit_btn_text));
                editState = !editState;
                adapter.setEditState(editState);
            }
        });
    }

    /*public void showPopup(final String SSID) {
            AlertDialog.Builder builder = new AlertDialog.Builder(WifiSearchActivity.this);
            builder.setTitleView(SSID);

            LayoutInflater inflater = this.getLayoutInflater();
            View viewInflated = inflater.inflate(R.layout.dialog_text_inpu_password, null);

            final EditText input = (EditText) viewInflated.findViewById(R.id.input);
            builder.setView(viewInflated);

            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String password = input.getText().toString();
                    connect(SSID, password);
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }
    */
    @Override
    public void showPopup(String title, String message) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_text_inpu_email, null);
        final EditText input = (EditText) view.findViewById(R.id.input);
        super.showBasicOneBtnPopup(title, null)
                .setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                showToast(input.getText().toString());
                                dialog.dismiss();

                            }
                        }
                )
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        showToast(input.getText().toString());
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public void showSinglePopup(String title, String message) {
        super.showBasicOneBtnPopup(title, message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }
                ).show();
    }

    @Override
    public void showPopup(String title, String message, final CommonListener.PopupClickListener listener) {
        super.showBasicOneBtnPopup(title, message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listener.onPositiveClick(dialog, which);
                            }
                        }
                )
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
    public void showPopup(int title, int message, final CommonListener.PopupClickListener listener) {
        super.showBasicOneBtnPopup(title, message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listener.onPositiveClick(dialog, which);
                            }
                        }
                )
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public void setAdapter(int idx, List<User> data) {
        if ( adapter == null ) {
            adapter = new UserAccountGridAdapter(UserAccountActivity.this, idx, data);
            adapter.setClickListener(new UserAccountGridAdapter.EditBtnClickListener() {
                @Override
                public void onClick(final User user) {
                    showPopup(getString(R.string.user_acoount__group_withdraw_title), user.getNickname() + getString(R.string.user_account__group_withdraw_suffix), new CommonListener.PopupClickListener() {
                        @Override
                        public void onPositiveClick(DialogInterface dialog, int which) {
                            presenter.withdrawGroup(UserAccountActivity.this, user);
                        }

                        @Override
                        public void onNegativeClick(DialogInterface dialog, int which) {

                        }
                    });
                }
            });
            binding.userGridView.setAdapter(adapter);
            binding.userGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                    if (position == USER_REGIST) {
//                        showPopup("+친구초대", "이메일");
//                    }
                }
            });
        } else {
            adapter.setData(data);
        }


    }
}
