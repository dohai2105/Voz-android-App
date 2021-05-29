package com.example.squareImage;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.ImageView;

public class ResizeableImageViewIcon extends ImageView {
	public ResizeableImageViewIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override 
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        Drawable d = getDrawable();
        if(d!=null){
                // ceil not round - avoid thin vertical gaps along the left/right edges
            	int width = 80;
                int height = (int) Math.ceil((float) width * (float) d.getIntrinsicHeight() / (float) d.getIntrinsicWidth());
                setMeasuredDimension(width, height);
        }else{
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
   }

}
