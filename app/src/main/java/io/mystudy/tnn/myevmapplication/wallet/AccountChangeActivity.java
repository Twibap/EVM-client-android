package io.mystudy.tnn.myevmapplication.wallet;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import io.mystudy.tnn.myevmapplication.Application.Dlog;
import io.mystudy.tnn.myevmapplication.Application.edittext.QRcodeEditText;
import io.mystudy.tnn.myevmapplication.R;

public class AccountChangeActivity
        extends AppCompatActivity
        implements
        View.OnClickListener,
        DialogInterface.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    TextInputLayout layoutAddress;
    QRcodeEditText addressField;
    Button btEnter;
    CheckBox checkBoxSaveAddr;
    SharedPreferences database;

    ImageView viewQrCode;

    String address;
    boolean isWillSave = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_change);

        initView();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle( getString(R.string.title_address_change) );

        database = getSharedPreferences("Customer", MODE_PRIVATE);
        if (database.contains("account")) {
            address = database.getString("account", null);
            isWillSave = true;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus && address != null)
            addressField.setText( address );

        if (hasFocus && isWillSave)
            checkBoxSaveAddr.setChecked(true);
    }

    private void initView(){
        layoutAddress = findViewById(R.id.editAddressLayout);
        addressField = findViewById(R.id.editAddress);
        addressField.addTextChangedListener(new AccountAddressWatcher());

        btEnter = findViewById(R.id.buttonEnter);
        btEnter.setOnClickListener(this);

        checkBoxSaveAddr = findViewById(R.id.checkBoxSaveAddress);
        checkBoxSaveAddr.setOnCheckedChangeListener(this);

        viewQrCode = findViewById(R.id.view_qr_code);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonEnter:
                enterAccount();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (database.contains("account") && isChecked)
            if (database.getString("account", null) != address)
                askOverwrite();
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

        AccountChangeActivity.this.finish();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                Dlog.e( "Selected Option Id : "+item.getItemId() );
                return super.onOptionsItemSelected(item);
        }
        return true;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Dlog.d("QR contents : "+ result.getContents());
                Dlog.d("QR img path : "+ result.getBarcodeImagePath());
                Dlog.d("QR format name : "+ result.getFormatName());
                Dlog.d("QR toString : "+ result.toString());

                String qrData = result.getContents();

                // Ethereum용 주소인지 확인한다.
                if ( qrData.startsWith("ethereum:") ){
                    Toast.makeText(this, "Scanned", Toast.LENGTH_LONG).show();
                    qrData = qrData.replace("ethereum:", "");

                    // QR 코드로 입력된 주소는 기본 주소 양식만 검사한다.
                    if( AddressUtils.isValidAddress(qrData) ){
                        // Checksum 되지 않은 주소는 Checksum 한다.
                        if ( !AddressUtils.isValidChecksumAddress(qrData))
                            qrData = AddressUtils.toChecksumAddress(qrData);

                        address = qrData;
                    }

                } else {
                    Dlog.e("QR data is not Ethereum address");
                    Toast.makeText(this, R.string.err_qr_msg_address, Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
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
            if ( validateAddress(editable.toString()) ){
                try {
                    String address = editable.toString();
                    BarcodeEncoder encoder = new BarcodeEncoder();

                    int view_width = viewQrCode.getWidth();
                    int view_height = viewQrCode.getHeight();
                    Dlog.e("Encode address : "+address);
                    Dlog.e("QR width  : "+view_width);
                    Dlog.e("QR height : "+view_height);

                    Bitmap qrCode = encoder.encodeBitmap(
                            address, BarcodeFormat.QR_CODE,
                            view_width, view_height);
                    // TODO DELETE Default image shown
                    viewQrCode.setImageBitmap( qrCode );

                } catch (WriterException e) {
                    e.printStackTrace();
                }
            } else {
                viewQrCode.setImageResource(R.drawable.logo_ethereum);
            }
        }
    }
}

