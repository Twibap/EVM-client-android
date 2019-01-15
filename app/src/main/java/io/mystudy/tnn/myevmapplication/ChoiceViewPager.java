package io.mystudy.tnn.myevmapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import io.mystudy.tnn.myevmapplication.Application.Dlog;

/**
 * 가운데 정렬 기능이 있는 ViewPager
 * 첫/마지막 page가 아닌 경우 화면 가운데에 위치한다.
 */
class ChoiceViewPager extends ViewPager implements ViewPager.PageTransformer {
    public ChoiceViewPager(@NonNull Context context) {
        super(context);
        setPageTransformer(false, this);
    }

    public ChoiceViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setPageTransformer(false, this);
    }

    @Override
    public void transformPage(@NonNull View page, float position) {
        // 양 끝의 position이 아니면 가운데로 보이게 한다.
        int currentItemIndex = getCurrentItem();
        if ( currentItemIndex == 0 ||
                currentItemIndex == getAdapter().getCount() - 1 ) {
            page.setTranslationX( 0 );
        } else {
            // Adapter에서 수정된 PageWidth 를 구한다.
            float reduceWeight = 1 - getAdapter().getPageWidth( (int) position );
            Dlog.e("Reduce weight = "+ reduceWeight);

            // 줄어든 만큼 X축 이동 한다.
            int widthDisplay = getContext().getResources().getDisplayMetrics().widthPixels;
            Dlog.e("Width Display = "+ widthDisplay);
            float reducedPixels = widthDisplay * reduceWeight;
            Dlog.e("Reduced Pixels = "+ reducedPixels);

            page.setTranslationX( reducedPixels );
        }
    }

}
