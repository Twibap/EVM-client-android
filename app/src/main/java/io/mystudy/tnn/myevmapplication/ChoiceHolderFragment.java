package io.mystudy.tnn.myevmapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

import io.mystudy.tnn.myevmapplication.Application.BaseApplication;
import io.mystudy.tnn.myevmapplication.Vending.Order;
import io.mystudy.tnn.myevmapplication.task.PaymentTask;
import io.mystudy.tnn.myevmapplication.websocket.Price;

import static android.content.Context.MODE_PRIVATE;

public class ChoiceHolderFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_CHOICE_PRICE = "choice_price";
    private static final String ARG_SELECTION_NUMBER = "selection_number";
    private static final String ARG_ETHER_AMOUNT = "ether_amount";

    private static NumberFormat priceFormat = NumberFormat.getCurrencyInstance( Locale.KOREA );

    private int choicedPrice;
    private String etherAmount;

    private TextView titleView;
    private TextView bodyView;
    private Button buyButton;

    public ChoiceHolderFragment() {
    }

    public static ChoiceHolderFragment newInstance(int position, int choicedPrice, String etherAmount){
        ChoiceHolderFragment fragment = new ChoiceHolderFragment();
        Bundle arguments = new Bundle();
        arguments.putInt( ARG_SELECTION_NUMBER, position);
        arguments.putInt( ARG_CHOICE_PRICE, choicedPrice);
        arguments.putString( ARG_ETHER_AMOUNT, etherAmount);
        fragment.setArguments( arguments );
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // attachToRoot 를 false 하지 않으면 !!! FAILED BINDER TRANSACTION !!! 에러 발생
        View rootView = inflater.inflate(R.layout.item_purchase, container, false);

        // init view
        titleView = rootView.findViewById(R.id.item_purchase_title);
        bodyView = rootView.findViewById(R.id.item_purchase_amount);
        buyButton = rootView.findViewById(R.id.item_purchase_button);
        buyButton.setOnClickListener( this );

        // get data
        choicedPrice = getArguments().getInt( ARG_CHOICE_PRICE );
        etherAmount = getArguments().getString( ARG_ETHER_AMOUNT );

        // set data
        titleView.setText( moneyFormat( choicedPrice ) );
        String bodyMsg = String.format( getString(R.string.card_purchase_amount), etherAmount);
        bodyView.setText( bodyMsg );

        return rootView;
    }

    @Override
    public void onClick(View v) {
        // viewPager - fragment - button
        ChoiceViewPager pager = (ChoiceViewPager) v.getParent().getParent();
        int position = getArguments().getInt(ARG_SELECTION_NUMBER);

        // 옆에 살짝 보이는 다음 화면 버튼 누를 경우 해당 View로 이동
        if (pager.getCurrentItem() == position ){

            BaseApplication application = ((BaseApplication) getContext().getApplicationContext());
            String address = application.getAddress();
            Price price = application.getEtherPrice();

            mkOrder( address, choicedPrice, price);
        } else {
            pager.setCurrentItem( position , true);
        }
    }

    String moneyFormat(int money){
        String formatted = priceFormat.format( money );
        formatted = formatted.replace(
                priceFormat.getCurrency().getSymbol(),
                priceFormat.getCurrency().getSymbol()+" "
        );
        return formatted;
    }

    /**
     * 구매 주문을 서버로 전송한다.
     * @param address 이더 받을 주소
     * @param amount 구입 금액
     * @param price 현재 이더 시세
     */
    public void mkOrder(String address, int amount, Price price) {
        SharedPreferences sf = getContext().getSharedPreferences("Customer", MODE_PRIVATE);
        String token = null;
        if(sf.contains("firebaseToken"))
            token = sf.getString("firebaseToken", null);

        // 주문내용 생성
        Order order = new Order( address, token, amount, price.getId());

        // 주문 전송
        PaymentTask task = new PaymentTask( getContext() );
        task.execute(order); // 전송 후 결제 진행

    }

    void updateBody(String body){
        String bodyMsg = String.format( getString(R.string.card_purchase_amount), body);
        bodyView.setText( bodyMsg );
    }
}
