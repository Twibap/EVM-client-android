package io.mystudy.tnn.myevmapplication.task;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.ref.WeakReference;

import io.mystudy.tnn.myevmapplication.MainActivity;
import io.mystudy.tnn.myevmapplication.Vending.Bill;
import io.mystudy.tnn.myevmapplication.Vending.Order;
import io.mystudy.tnn.myevmapplication.Vending.OrderRepository;
import kr.co.bootpay.Bootpay;
import kr.co.bootpay.CancelListener;
import kr.co.bootpay.CloseListener;
import kr.co.bootpay.ConfirmListener;
import kr.co.bootpay.DoneListener;
import kr.co.bootpay.ErrorListener;
import kr.co.bootpay.enums.Method;
import kr.co.bootpay.enums.PG;

import static android.content.ContentValues.TAG;

public class PaymentTask extends AsyncTask<Order, Order, Order>{
    private WeakReference<MainActivity> activityReference;

    public PaymentTask(MainActivity context){
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
                        Bootpay.confirm(message);   // TODO 재고 검토 후 confirm 할 것
                    }
                })
                .onDone(new DoneListener() { // 결제완료시 호출, 아이템 지급 등 데이터 동기화 로직을 수행합니다
                    @Override
                    public void onDone(@Nullable String message) {
                        Log.d("done", message);
                        Gson gson = new Gson();
                        Bill bill = gson.fromJson(message, Bill.class);

                        // 결제 정보 서버로 전송
                        VerifyPaymentTask task = new VerifyPaymentTask();
                        task.execute( bill );
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
