package io.mystudy.tnn.myevmapplication;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.web3j.utils.Convert;

import java.math.BigInteger;

import io.mystudy.tnn.myevmapplication.websocket.Price;

/**
 * MainActivity의 구입 선택지를 전시하는 ViewPager에 데이터를 관리한다.
 */
public class ChoicePagerAdapter extends FragmentPagerAdapter {

    private final int[] choice = { 10000, 20000, 30000, 50000, 100000 };
    private final String[] amount = new String[ choice.length ];

    private final FragmentManager fragmentManager;

    ChoicePagerAdapter(FragmentManager fm){
        super( fm );
        this.fragmentManager = fm;
    }


    @Override
    public Fragment getItem(int position) {
        Fragment fragment = ChoiceHolderFragment.newInstance( position , choice[position], amount[position]);
        return fragment;
    }

    @Override
    public int getCount() {
        return choice.length;
    }

    /**
     * 다음 페이지가 보이도록 하기 위해 페이지 넓이를 줄인다.
     * 첫 페이지와 마지막 페이지를 제외한 나머지 페이지는 두배로 줄인다.
     * @param position
     * @return
     */
    @Override
    public float getPageWidth(int position) {
        if ( position == 0 || position == choice.length-1)
            return (0.95f);
        else
            return (0.9f);
    }

    @Override
    public void notifyDataSetChanged() {
        // Fragment에 데이터 업데이트 하기
        for (int i = 0; i < choice.length; i++) {
            String choicePageTag
                    = "android:switcher:"
                    + R.id.choice_purchase_amount+":"
                    + getItemId(i); // int->long
            ChoiceHolderFragment page = (ChoiceHolderFragment) fragmentManager
                    .findFragmentByTag( choicePageTag );

            if (page != null)
                page.updateBody(amount[i]);
        }

        super.notifyDataSetChanged();
    }

    void updateEtherPrice(Price price){
        int etherPrice = price.getEvm_price();
        for (int i = 0; i < choice.length; i++) {
            amount[i] = getEtherAmount( choice[i], etherPrice );
        }

        notifyDataSetChanged();
    }

    private String getEtherAmount(int choicePrice, int etherPrice){
        BigInteger unitEther = BigInteger.TEN.pow( 18 );
        BigInteger weiFromPrice = BigInteger.valueOf( etherPrice );
        BigInteger choice = BigInteger.valueOf( choicePrice ).multiply( unitEther );

        String result = choice.divide( weiFromPrice ).toString();
        return Convert.fromWei( result, Convert.Unit.ETHER ).toString();
    }
}
