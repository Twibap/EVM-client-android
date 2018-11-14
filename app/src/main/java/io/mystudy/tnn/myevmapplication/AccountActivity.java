package io.mystudy.tnn.myevmapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import io.mystudy.tnn.myevmapplication.Application.Dlog;

public class AccountActivity
        extends AppCompatActivity
        implements View.OnClickListener, DialogInterface.OnClickListener {

    TextInputLayout layoutAddress;
    EditText addressField;
    Button btEnter;
    CheckBox checkBoxSaveAddr;
    SharedPreferences database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        initView();

        database = getSharedPreferences("Customer", MODE_PRIVATE);
    }

    private void initView(){
        layoutAddress = findViewById(R.id.editAddressLayout);
        addressField = findViewById(R.id.editAddress);
        addressField.addTextChangedListener(new AccountAddressWatcher());

        btEnter = findViewById(R.id.buttonEnter);
        btEnter.setOnClickListener(this);

        checkBoxSaveAddr = findViewById(R.id.checkBoxSaveAddress);
        checkBoxSaveAddr.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonEnter:
                enterAccount();
                break;
            case R.id.checkBoxSaveAddress:
                if(database.contains("account"))
                    askOverwrite();
                break;
        }
    }

    private void enterAccount(){
        if( addressField.getText().length() == 0 ){
            Toast.makeText(this, "Account 주소를 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        // 입력된 주소 값 가져오기
        String account = addressField.getText().toString();

        // 이더리움 주소 유효성 체크
        if( !validateAddress(account) )
            return;

        // 임시 저장
        Intent intent = getIntent();
        intent.putExtra("account", account);

        // 영구 저장
        checkBoxSaveAddr = findViewById(R.id.checkBoxSaveAddress);
        if ( checkBoxSaveAddr.isChecked() ) {
            SharedPreferences.Editor sfEditor = database.edit();
            sfEditor.putString("account", account).apply();

            Dlog.d("account saved");
        }
        Dlog.d("account returned: " + account);
        setResult(RESULT_OK, intent);

        AccountActivity.this.finish();
    }

    // 주소 조건에 따라 에러 메세지
    private boolean validateAddress(String account) {
        if( !AddressUtils.isValidAddress(account)) {
            layoutAddress.setError(getString(R.string.err_msg_address));
            requestFocus(addressField);
            return false;
        } else if( !AddressUtils.isValidChecksumAddress(account) ) {
            layoutAddress.setError(getString(R.string.err_msg_check_address));
            requestFocus(addressField);
            return false;
        } else {
            layoutAddress.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if( view.requestFocus() )
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void askOverwrite() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog);
        builder.setMessage("저장된 Account 주소를 바꾸시겠습니까??")
                .setPositiveButton("네", this)
                .setNegativeButton("아니오",this)
                .setCancelable(false)
                .show();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        switch (i){
            case DialogInterface.BUTTON_POSITIVE:
                checkBoxSaveAddr.setChecked(true);
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                checkBoxSaveAddr.setChecked(false);
                break;
        }
    }

    @Override
    public void finish() {
        if( !database.contains("account") && !getIntent().hasExtra("account")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog);
            builder
                    .setMessage("종료하시겠습니까?")
                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finishAffinity();
                }
            })
                    .setNegativeButton("아니오", null)
                    .show();
        } else {
            super.finish();
        }
    }

    private class AccountAddressWatcher implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            validateAddress(editable.toString());
        }
    }
}

