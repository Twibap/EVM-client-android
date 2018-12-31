package io.mystudy.tnn.myevmapplication.wallet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import io.mystudy.tnn.myevmapplication.R;
import io.mystudy.tnn.myevmapplication.task.GetEtherBalanceTask;

public class AccountActivity extends AppCompatActivity{

    private static final int CODE_CHANGE_ADDRESS = 1001;

    ImageView qrCodeView;
    TextView addressView;
    TextView balanceView;

    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        address = getIntent().getStringExtra("account");

        initView();

    }

    private void initView(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle( getString(R.string.title_address));

        qrCodeView = findViewById(R.id.view_qr_code);
        addressView = findViewById(R.id.view_address);
        balanceView = findViewById(R.id.view_balance);
    }

    private void setAddress(String address){
        if (address == null)
            throw new IllegalArgumentException("Account address is null");

        // get ethereum account balance
        getBalance(address);

        // to TextView
        addressView.setText( address );

        // to QR code Image View
        // TODO Make clear qr code image
        BarcodeEncoder encoder = new BarcodeEncoder();
        int view_width = qrCodeView.getWidth();
        int view_height = qrCodeView.getHeight();

        try {
            Bitmap qrCodeImage = encoder.encodeBitmap(
                    address, BarcodeFormat.QR_CODE,
                    view_width, view_height
            );
            qrCodeView.setImageBitmap( qrCodeImage );
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }

    private void getBalance(String address){
        GetEtherBalanceTask task = new GetEtherBalanceTask(GetEtherBalanceTask.NetworkType.ROPSTEN, balanceView);
        task.execute(address);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus)
            setAddress( address );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_account, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_account_change:
                Intent intent = new Intent(AccountActivity.this, AccountChangeActivity.class);
                startActivityForResult(intent, CODE_CHANGE_ADDRESS);
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // AccountChangeActivity에서 새 주소 값을 받는다.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode){
            case CODE_CHANGE_ADDRESS:
                address = data.getStringExtra("account");
                address = AddressUtils.toChecksumAddress(address);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // MainActivity로 Account 주소를 전달한다.
    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        intent.putExtra("account", address);

        setResult(RESULT_OK, intent);

        super.onBackPressed();
    }
}
