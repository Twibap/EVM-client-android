package io.mystudy.tnn.myevmapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.lang.ref.WeakReference;

import io.mystudy.tnn.myevmapplication.Application.Dlog;
import io.mystudy.tnn.myevmapplication.Vending.Order;
import io.mystudy.tnn.myevmapplication.Vending.OrderRepository;
import io.mystudy.tnn.myevmapplication.websocket.PriceListener;
import kr.co.bootpay.Bootpay;
import kr.co.bootpay.BootpayAnalytics;
import kr.co.bootpay.CancelListener;
import kr.co.bootpay.CloseListener;
import kr.co.bootpay.ConfirmListener;
import kr.co.bootpay.DoneListener;
import kr.co.bootpay.ErrorListener;
import kr.co.bootpay.enums.Method;
import kr.co.bootpay.enums.PG;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int codeMkAddress = 1001;

    private RadioGroup btGroup;
    private Button btBuy;

    private TextView viewPrice;
    private int amount;
    private String account;

    // WebSocket
    private OkHttpClient httpClient;
    private WebSocket webSocket;
    private PriceListener priceListener;
    private PriceListener.Price price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarMainActivity);
        setSupportActionBar( toolbar );

        SharedPreferences sf = getSharedPreferences("Customer", MODE_PRIVATE);
        if ( !sf.contains("account" )){
            Intent intent = new Intent(this, AccountActivity.class);
            startActivityForResult(intent, codeMkAddress);
        } else {
            account = sf.getString("account", "Account Empty!!");
        }

        viewInit();

        // 초기설정 - 해당 프로젝트(안드로이드)의 application id 값을 설정합니다. 결제와 통계를 위해 꼭 필요합니다.
        BootpayAnalytics.init(this, "5bddbed2b6d49c480275bab1");

        // WebSocket
        httpClient = new OkHttpClient();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Request request = new Request.Builder()
                .url( getString( R.string.url_price_websocket ) )
                .build();
        priceListener= new PriceListener() {
            @Override
            public void showMessage(final PriceListener.Price _price) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        price = _price;
                        viewPrice.setText( "￦ "+_price.getEvm_price()+" / Ether" );
                    }
                });
            }
        };
        webSocket = httpClient.newWebSocket(request, priceListener);
        httpClient.dispatcher().executorService().shutdown();
    }

    @Override
    protected void onStop() {
        webSocket.close(PriceListener.NORMAL_CLOSURE_STATUS, TAG+" - onStop");

        super.onStop();
    }

    void viewInit(){
        btGroup = findViewById(R.id.chooseGroup);
        btBuy = findViewById(R.id.buttonBuy);

        viewPrice = findViewById(R.id.viewEtherPrice);

        btGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                // RadioGroup -> ConstraintLayout -> ToggleButton
                for(int j = 0; j < ((ConstraintLayout)btGroup.getChildAt(0)).getChildCount(); j++){
                    ToggleButton view = (ToggleButton) ((ConstraintLayout) radioGroup.getChildAt(0)).getChildAt(j);
                    view.setChecked( view.getId() == i );   // true
                }
            }
        });

        btBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( btGroup.getCheckedRadioButtonId() != -1)
                    onClick_request(view);
                else Toast.makeText(MainActivity.this, "결제 금액을 선택하세요", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // activity_main.xml 의 ToggleButton 에서 호출된다
    public void onToggle(View view) {   // view 클릭된 다음 상태로 전달된다.
        // RadioGroup -> ConstraintLayout -> ToggleButton
        ToggleButton theView = (ToggleButton) view;
        RadioGroup radioGroup = (RadioGroup) theView.getParent().getParent();

        if( theView.isChecked() ){
            radioGroup.check( view.getId() );
            amount = buttonId2Val( view.getId() );
        } else {
            radioGroup.clearCheck();
            amount = -1;
        }

        Toast.makeText(this, ((ToggleButton)view).getTextOn() , Toast.LENGTH_SHORT).show();
    }

    public void toggleClear(){
        btGroup.clearCheck();
    }

    public int buttonId2Val(int id){
        switch (id){
            case R.id.choose11000:
                return 11000;
            case R.id.choose22000:
                return 22000;
            case R.id.choose33000:
                return 33000;
            case R.id.choose55000:
                return 55000;
            case R.id.choose110000:
                return 110000;
            default:
                return -1;
        }

    }

    public void onClick_request(View v) {
        if(amount <= 0) {
            Dlog.e("onClick_request: Zero Price");
            return;
        }
        // 주문내용 생성
        Order order = new Order(account, amount, price.getId());
        Log.i(TAG, "onClick_request: order->"+order.toJson());

        // 주문번호 생성(서버 저장)
        PaymentTask task = new PaymentTask(this);
        task.execute(order);

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
                AlertDialog.Builder builder
                        = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog);
                builder.setTitle("Ethereum Account")
                        .setMessage(account)
                        .setPositiveButton("바꾸기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                                startActivityForResult(intent, codeMkAddress);
                            }
                        })
                        .setNegativeButton("닫기", null)
                        .show();
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
                account = data.getStringExtra("account");
                Toast.makeText(this, account, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private static class PaymentTask extends AsyncTask<Order, Order, Order>{
        private WeakReference<MainActivity> activityReference;

        PaymentTask(MainActivity context){
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected Order doInBackground(Order... orders) {
            OrderRepository repository = new OrderRepository();
            try {
                return repository.askOrder(orders[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Order order) {
//                super.onPostExecute(order);
            if( order == null ){
                Log.e(TAG, "onPostExecute: order is null");
                return;
            }
            // get a reference to the activity if it is still there
            final MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            // 결제호출
            Bootpay.init( activity.getFragmentManager())
                    .setApplicationId("5bddbed2b6d49c480275bab1") // 해당 프로젝트(안드로이드)의 application id 값
                    .setPG(PG.KAKAO) // 결제할 PG 사
                    //.setUserPhone("010-1234-5678") // 구매자 전화번호
                    .setMethod(Method.EASY) // 결제수단
                    .setName( order.itemName() ) // 결제할 상품명
                    .setOrderId( order.getOrder_id() ) //고유 주문번호로, 생성하신 값을 보내주셔야 합니다.
                    .setPrice( order.getAmount() ) // 결제할 금액
                    .onConfirm(new ConfirmListener() { // 결제가 진행되기 바로 직전 호출되는 함수로, 주로 재고처리 등의 로직이 수행
                        @Override
                        public void onConfirm(@Nullable String message) {
//                            if (0 < stuck) Bootpay.confirm(message); // 재고가 있을 경우.
//                            else Bootpay.removePaymentWindow(); // 재고가 없어 중간에 결제창을 닫고 싶을 경우
                            Log.d("confirm", message);
                        }
                    })
                    .onDone(new DoneListener() { // 결제완료시 호출, 아이템 지급 등 데이터 동기화 로직을 수행합니다
                        @Override
                        public void onDone(@Nullable String message) {
                            Log.d("done", message);
                        }
                    })
                    .onCancel(new CancelListener() { // 결제 취소시 호출
                        @Override
                        public void onCancel(@Nullable String message) {
                            activity.toggleClear();
                            Toast.makeText(activity, "결제가 취소되었습니다.", Toast.LENGTH_SHORT).show();
                            Log.d("cancel", message);
                        }
                    })
                    .onError(new ErrorListener() { // 에러가 났을때 호출되는 부분
                        @Override
                        public void onError(@Nullable String message) {
                            Log.d("error", message);
                        }
                    })
                    .onClose(new CloseListener() { //결제창이 닫힐때 실행되는 부분
                        @Override
                        public void onClose(String message) {
                            Log.d("close", "close");
                        }
                    })
                    .show();
        }
    }
}
