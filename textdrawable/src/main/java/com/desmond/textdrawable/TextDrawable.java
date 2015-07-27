package com.desmond.textdrawable;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.support.annotation.ColorInt;
import android.util.TypedValue;

/**
 * Created by desmond on 27/7/15.
 */
public class TextDrawable extends ShapeDrawable {

    private static final float SHADE_FACTOR = 0.9f;

    private final Paint mTextPaint;
    private final Paint mBorderPaint;

    private final String mText;
    private final RectShape mShape;

    @ColorInt private final int mColor;

    private final float mHeight;
    private final float mWidth;
    private final float mFontSize;
    private final float mBorderThickness;

    private final float mRadius;

    private TextDrawable(Builder builder) {

        mShape = builder.shape;
        mHeight = builder.height;
        mWidth = builder.width;
        mRadius = builder.radius;

        mText = builder.toUpperCase ? builder.text.toUpperCase() : builder.text;
        mColor = builder.color;

        mFontSize = builder.fontSize;
        mTextPaint = new Paint();
        mTextPaint.setColor(builder.textColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setFakeBoldText(builder.isBold);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTypeface(builder.font);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setStrokeWidth(builder.borderThickness);

        // border paint settings
        mBorderThickness = builder.borderThickness;
        mBorderPaint = new Paint();
        mBorderPaint.setColor(builder.textColor);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(mBorderThickness);

        // drawable paint color
        Paint paint = getPaint();
        paint.setColor(mColor);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        Rect bound = getBounds();

        drawBorder(canvas);

        int count = canvas.save();
        canvas.translate(bound.left, bound.top);

        drawText(canvas);

        canvas.restoreToCount(count);
    }

    private void drawBorder(Canvas canvas) {
        if (mBorderThickness <= 0) return;

        RectF rectF = new RectF(getBounds());
        rectF.inset(mBorderThickness/2, mBorderThickness/2);

        if (mShape instanceof OvalShape) {
            canvas.drawOval(rectF, mBorderPaint);
        } else if (mShape instanceof RoundRectShape) {
            canvas.drawRoundRect(rectF, mRadius, mRadius, mBorderPaint);
        } else {
            canvas.drawRect(rectF, mBorderPaint);
        }
    }

    private void drawText(Canvas canvas) {
        Rect rect = getBounds();

        float width = mWidth < 0 ? rect.width() : mWidth;
        float height = mHeight < 0 ? rect.height() : mHeight;
        mTextPaint.setTextSize(mFontSize);
        canvas.drawText(mText, width / 2, height / 2 - ((mTextPaint.descent() + mTextPaint.ascent()) / 2), mTextPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mTextPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mTextPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicWidth() {
        return (int) mWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return (int) mHeight;
    }

    public static class Builder {

        private String text;

        @ColorInt private int color;
        @ColorInt private int textColor;

        private float borderThickness;
        private float width;
        private float height;

        public float radius;

        private Typeface font;

        private RectShape shape;

        private float fontSize;
        private boolean isBold;

        private boolean toUpperCase;

        private Context mContext;

        public Builder(Context context) {
            Resources resources = context.getResources();
            mContext = context;

            text = "";

            color = Color.GRAY;
            textColor = Color.WHITE;

            borderThickness = 0;
            width = height = -1;

            font = Typeface.create("sans-serif-light", Typeface.NORMAL);

            shape = new RectShape();

            fontSize = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_SP, 8, resources.getDisplayMetrics());

            isBold = false;
            toUpperCase = false;
        }

        public Builder width(float width) {
            Resources resources = mContext.getResources();
            this.width = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, width, resources.getDisplayMetrics());
            return this;
        }

        public Builder height(float height) {
            Resources resources = mContext.getResources();
            this.height = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, height, resources.getDisplayMetrics());
            return this;
        }

        /**
         *
         * @param fontName font.ttf is inside assets/fonts/
         * @return
         */
        public Builder setFont(String fontName) {
            if (fontName != null) {
                font = Typeface.createFromAsset(mContext.getAssets(), "fonts/" + fontName);
            }
            return this;
        }

        public Builder setFontSize(int size) {
            Resources resources = mContext.getResources();
            this.fontSize = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_SP, size, resources.getDisplayMetrics());
            return this;
        }

        public Builder bold() {
            this.isBold = true;
            return this;
        }

        public Builder toUpperCase() {
            this.toUpperCase = true;
            return this;
        }

        public Builder setTextColor(@ColorInt int textColor) {
            this.textColor = textColor;
            return this;
        }

        public Builder setBorderThickness(float borderThickness) {
            Resources resources = mContext.getResources();
            this.borderThickness = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, borderThickness, resources.getDisplayMetrics());

            return this;
        }

        public Builder rect() {
            shape = new RectShape();
            return this;
        }

        public Builder round() {
            shape = new OvalShape();
            return this;
        }

        public Builder roundRect(int radius) {
            this.radius = radius;
            float[] radii = {radius, radius, radius, radius, radius, radius, radius, radius};
            shape = new RoundRectShape(radii, null, null);
            return this;
        }

        public TextDrawable buildRect(String text, int color) {
            rect();
            return build(text, color);
        }

        public TextDrawable buildRoundRect(String text, int color, int radius) {
            roundRect(radius);
            return build(text, color);
        }

        public TextDrawable buildRound(String text, int color) {
            round();
            return build(text, color);
        }

        public TextDrawable build(String text, @ColorInt int color) {
            this.color = color;
            this.text = text;
            return new TextDrawable(this);
        }
    }
}
