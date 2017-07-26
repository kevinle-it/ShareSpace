package com.lmtri.sharespace.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

import com.lmtri.sharespace.R;

/**
 * Created by lmtri on 6/14/2017.
 */

public class SquareImageView extends AppCompatImageView {

    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Drawable drawable = getDrawable();

            if (drawable != null) {
                int width = getWidth(), height = getHeight();
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);

//                Bitmap bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

//                Bitmap roundBitmap = getRoundedCornerBitmap(getContext(), bitmap, 4, width, height, false, true, false, true);
//                canvas.drawBitmap(roundBitmap, 0, 0, null);

                BitmapShader bitmapShader;
                bitmapShader = new BitmapShader(scaledBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

                Paint paint = new Paint();
                paint.setAntiAlias(true);   // Smooth out the Edges.
                paint.setShader(bitmapShader);

                // Round all corners.
                RectF rectF = new RectF(0.0f, 0.0f, width, height);
                canvas.drawRoundRect(rectF, getResources().getDimension(R.dimen.fragment_housing_item_card_view_corner_radius), getResources().getDimension(R.dimen.fragment_housing_item_card_view_corner_radius), paint);
                // Square top right.
                rectF = new RectF(width / 2, 0.0f, width, height / 2);
                canvas.drawRect(rectF, paint);
                // Square bottom right.
                rectF = new RectF(width / 2, height / 2, width, height);
                canvas.drawRect(rectF, paint);
            }
        } else {
            super.onDraw(canvas);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }

    public Bitmap getRoundedCornerBitmap(Context context, Bitmap input, int cornerRadiusInDP , int width, int height, boolean squareTopLeft, boolean squareTopRight, boolean squareBottomLeft, boolean squareBottomRight) {

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        // Make sure that our rounded corner is scaled appropriately.
        final float cornerRadiusInPixel = cornerRadiusInDP * densityMultiplier;

        paint.setAntiAlias(true);   // Smooth out the Edges.
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, cornerRadiusInPixel, cornerRadiusInPixel, paint);


        // Draw rectangles over the corners we want to be square.
        if (squareTopLeft){
            canvas.drawRect(0, 0, width/2, height/2, paint);
        }
        if (squareTopRight){
            canvas.drawRect(width/2, 0, width, height/2, paint);
        }
        if (squareBottomLeft){
            canvas.drawRect(0, height/2, width/2, height, paint);
        }
        if (squareBottomRight){
            canvas.drawRect(width/2, height/2, width, height, paint);
        }


        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(input, 0, 0, paint);

        return output;
    }
}
