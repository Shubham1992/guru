package com.example.helperapp.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.helperapp.R;
import com.example.helperapp.service.ChatHeadService;


public class CustomView extends View {

    Paint p;
    int color;
    int x, y;
    int radius;
    Canvas mCanvas;
    String currentShape = "circle";
    Rect rect = null;
    private Context mContext;

    public CustomView(Context context) {
        this(context, null);
        mContext = context;

    }

    public CustomView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
        x = ((ChatHeadService) context).x_whatsapp_screen_1;
        y = ((ChatHeadService) context).y_whatsapp_screen_1;
        radius = ((ChatHeadService) context).radiusOfCircle;
        currentShape = ((ChatHeadService) context).currentShape;
        rect = ((ChatHeadService) context).rect;
    }

    public CustomView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;


    }


    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        if (canvas != null) {
            mCanvas = canvas;
            Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(Color.TRANSPARENT);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
            mPaint.setAntiAlias(true);

            canvas.drawColor(getResources().getColor(R.color.colorAccent));
            if (currentShape.equals("square")) {
                if (rect != null)
                    canvas.drawRect(rect, mPaint);
                else Log.e("Error", "Rect is null in custom view");
            } else {
                canvas.drawCircle(x, y, radius, mPaint);
            }

        }
    }


    @Override
    public void setOnTouchListener(OnTouchListener l) {
        super.setOnTouchListener(l);
        Log.e("touched", "touched");
    }
}