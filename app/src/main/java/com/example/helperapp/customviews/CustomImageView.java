package com.example.helperapp.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;

import androidx.appcompat.widget.AppCompatImageView;

public class CustomImageView extends AppCompatImageView {
    public CustomImageView(Context context) {
        super(context);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        Log.e("back", "back press dabaya");
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
        {
            // handle back press
            // if (event.getAction() == KeyEvent.ACTION_DOWN)
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}