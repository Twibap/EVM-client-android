package io.mystudy.tnn.myevmapplication.wallet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;

import io.mystudy.tnn.myevmapplication.R;
import io.mystudy.tnn.myevmapplication.Vending.Order;
import io.mystudy.tnn.myevmapplication.Vending.OrderRepository;
import io.mystudy.tnn.myevmapplication.task.GetEtherBalanceTask;
import io.mystudy.tnn.myevmapplication.task.GetOrderHistoryTask;
import io.mystudy.tnn.myevmapplication.wallet.history.AccountInfoAdapter;

public class AccountActivity extends AppCompatActivity {

    private static final int CODE_CHANGE_ADDRESS = 1001;

    ImageView qrCodeView;

    private String address;

    RecyclerView mHistoryView;
    AccountInfoAdapter mHistoryAdapter;
    ArrayList<Order> mHistoryItems = new ArrayList<>();
    ProgressBar mHistoryProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        initData();

        initView();

    }

    private void initData(){
        address = getIntent().getStringExtra("account");
        mHistoryAdapter = new AccountInfoAdapter(mHistoryItems);

    }

    private void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle( getString(R.string.title_address));

        qrCodeView = findViewById(R.id.view_qr_code);

        mHistoryView = findViewById(R.id.view_purchase_history);
        mHistoryView.setLayoutManager(new LinearLayoutManager(this));
        mHistoryView.setAdapter(mHistoryAdapter);
        mHistoryView.addOnScrollListener(new OrderListOnScrollListener());

        mHistoryProgressBar = findViewById(R.id.history_progressBar);
    }

    private void setAddress(String address){
        if (address == null)
            throw new IllegalArgumentException("Account address is null");

        // get ethereum account balance
        getBalance(address);

        // to TextView
        mHistoryAdapter.setmAddress( address );

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
//            qrCodeView.setImageResource( R.drawable.logo_ethereum );
            qrCodeView.setImageBitmap( qrCodeImage );
        } catch (WriterException e) {
            e.printStackTrace();
        }

        mHistoryItems.clear();
        GetOrderHistoryTask task = new GetOrderHistoryTask(mHistoryAdapter, mHistoryProgressBar);
        task.execute(address);
    }

    private void getBalance(String address){
        GetEtherBalanceTask task = new GetEtherBalanceTask(GetEtherBalanceTask.NetworkType.ROPSTEN, new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what){
                    case 1: // true for showing loading message
                        mHistoryAdapter.setmBalance("Checking balance...");
                        break;
                    case 0: // false for showing loading message
                        mHistoryAdapter.setmBalance(msg.getData().getString("balance"));
                        break;
                }
                return false;
            }
        }));
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

    private class OrderListOnScrollListener extends RecyclerView.OnScrollListener{
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if ( !recyclerView.canScrollVertically(1) &&
                    OrderRepository.getInstance().getStatus() == OrderRepository.STATUS.HAS_MORE_ORDER){
                GetOrderHistoryTask task = new GetOrderHistoryTask(mHistoryAdapter, mHistoryProgressBar);
                task.execute(address, mHistoryItems.size());
            }
        }
    }
}
