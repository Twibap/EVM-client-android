package io.mystudy.tnn.myevmapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

import io.mystudy.tnn.myevmapplication.Application.BaseApplication;
import io.mystudy.tnn.myevmapplication.Application.Confidential;
import io.mystudy.tnn.myevmapplication.Application.Dlog;
import io.mystudy.tnn.myevmapplication.manual.ManualActivity;
import io.mystudy.tnn.myevmapplication.wallet.AccountActivity;
import io.mystudy.tnn.myevmapplication.wallet.AddressUtils;
import io.mystudy.tnn.myevmapplication.websocket.Price;
import io.mystudy.tnn.myevmapplication.websocket.PriceListener;
import kr.co.bootpay.BootpayAnalytics;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int codeMkAddress = 1001;

    private NumberFormat priceFormat;
    private TextView viewPrice;

    private TextView viewAddress;
    private String address;

    // WebSocket
    private OkHttpClient httpClient;
    private WebSocket webSocket;
    private PriceListener priceListener;

    private ChoiceViewPager choicePager;
    private ChoicePagerAdapter choicePagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarMainActivity);
        setSupportActionBar( toolbar );

        // 저장된 사용자 주소 가져오기 TODO BaseApplication의 생명 주기에 맞추기
        SharedPreferences sf = getSharedPreferences("Customer", MODE_PRIVATE);
        if( !sf.contains("account") ){
            Intent intent = new Intent(this, ManualActivity.class);
            startActivityForResult(intent, codeMkAddress);
        } else {
             address = sf.getString("account", null);
            ((BaseApplication) getApplicationContext()).setAddress( address );
        }

        viewInit();

        // 초기설정 - 해당 프로젝트(안드로이드)의 application id 값을 설정합니다. 결제와 통계를 위해 꼭 필요합니다.
        BootpayAnalytics.init(this, Confidential.Bootpay_Application_ID);

        Dlog.e("MainActivity onCreated!");

    }

    @Override
    protected void onStart() {
        super.onStart();

        priceFormat = NumberFormat.getCurrencyInstance(Locale.KOREA);

        Request request = new Request.Builder()
                .url( BaseApplication.getHost_websocket() )
                .build();
        priceListener= new PriceListener() {
            @Override
            public void showMessage(final Price _price) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((BaseApplication) getApplicationContext()).setEtherPrice(_price);

                        choicePagerAdapter.updateEtherPrice(_price);

                        String formattedPrice = priceFormat.format( _price.getEvm_price() );
                        String strPrice = formattedPrice.replace(priceFormat.getCurrency().getSymbol(),
                                priceFormat.getCurrency().getSymbol()+" ");
                        strPrice += " / Ether";
                        viewPrice.setText(strPrice);
                    }
                });
            }
        };
        // WebSocket
        httpClient = new OkHttpClient();
        webSocket = httpClient.newWebSocket(request, priceListener);
        httpClient.dispatcher().executorService().shutdown();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && address != null){
            viewAddress.setText(address);
        }
    }

    @Override
    protected void onStop() {
        webSocket.close(PriceListener.NORMAL_CLOSURE_STATUS, TAG+" - onStop");

        super.onStop();
    }

    void viewInit(){

        viewPrice = findViewById(R.id.view_price_body);
        viewAddress = findViewById(R.id.item_body_account);

        choicePagerAdapter = new ChoicePagerAdapter( getSupportFragmentManager() );
        choicePager = findViewById(R.id.choice_purchase_amount);
        choicePager.setAdapter( choicePagerAdapter );

        // 다음 페이지 보이게 하기
        // 출처: http://kkensu.tistory.com/2 [철스토리]
        int dp = 10; // DP 단위로 변환
        dp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
        choicePager.setPageMargin( dp );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_account:
                Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                intent.putExtra("account", address);    // Account 값 전달

                startActivityForResult(intent, codeMkAddress);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK){
            Dlog.e("onActivityResult: Code("+requestCode+") NOT OK");
            return;
        }

        switch (requestCode){
            case codeMkAddress:
                address = data.getStringExtra("account");
                address = AddressUtils.toChecksumAddress(address);
                ((BaseApplication) getApplicationContext()).setAddress( address );
                break;
        }
    }

}
