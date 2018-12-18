package io.mystudy.tnn.myevmapplication.Application.edittext;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import io.mystudy.tnn.myevmapplication.R;

/**
 * 내용을 삭제할 수 있는 버튼을 가진 EditText이다.
 */
public class ClearEditText extends AppCompatEditText implements TextWatcher, View.OnTouchListener, View.OnFocusChangeListener {

    private Drawable clearDrawable;
    private OnTouchListener onTouchListener;
    private OnFocusChangeListener onFocusChangeListener;

    public ClearEditText(Context context) {
        super(context);
        init();
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        Drawable tempDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_clear_cancle_close);
        clearDrawable = DrawableCompat.wrap(tempDrawable);
        DrawableCompat.setTintList(clearDrawable, getHintTextColors());
        clearDrawable.setBounds(0, 0, clearDrawable.getIntrinsicWidth(), clearDrawable.getIntrinsicHeight());

        setClearIconVisible(false);

        addTextChangedListener(this);
        super.setOnTouchListener(this);
        super.setOnFocusChangeListener(this);
    }

    private void setClearIconVisible(boolean visible) {
        clearDrawable.setVisible(visible, false);
        setCompoundDrawables(null, null, visible ? clearDrawable : null, null);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (isFocused()){
            setClearIconVisible( s.length() > 0 );
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int x = (int) event.getX();

        // EditText 내용 삭제
        if ( clearDrawable.isVisible() &&
                x > getWidth() - getPaddingRight() - clearDrawable.getIntrinsicWidth() ){
            if ( event.getAction() == MotionEvent.ACTION_UP ){
                setError(null);
                setText(null);
            }

            return true;
        }

        if ( onTouchListener != null ){
            return onTouchListener.onTouch(v, event);
        }

        return false;
    }

    @Override
    public void setOnTouchListener(OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if ( hasFocus ){
            setClearIconVisible( getText().length() > 0 );
        } else {
            setClearIconVisible( false );
        }

        if ( onFocusChangeListener != null ){
            onFocusChangeListener.onFocusChange(v, hasFocus);
        }
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
        this.onFocusChangeListener = onFocusChangeListener;
    }
}
