package io.mystudy.tnn.myevmapplication;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;

/**
 * MainActivity의 구입 선택지를 전시하는 ViewPager에 데이터를 관리한다.
 */
public class ChoicePagerAdapter extends FragmentPagerAdapter {

    private final int[] choice = { 11000, 22000, 33000, 55000, 110000 };
    private final String[] amount = new String[ choice.length ];

    Context context;

    ChoicePagerAdapter(Context context){
        super( ((AppCompatActivity) context).getSupportFragmentManager() );
        this.context = context;
    }


    @Override
    public Fragment getItem(int position) {
        return ChoiceHolderFragment.newInstance( choice[ position ] );
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
}
