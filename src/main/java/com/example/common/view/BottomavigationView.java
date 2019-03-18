package com.example.common.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.common.CommonUtils;

import static com.example.common.CommonUtils.PixelConversion.Sp2Px;

/**
 * Created by 沐沐 on 2018/2/2.
 * 底部导航的封装
 */

public class BottomavigationView extends View {
    private Paint mPaint;
    private Paint onclickPaint;
    private String[] ary;
    private int[] imgArray;
    private int[] imgChageArray;
    private int chilNum;
    private int mH;
    private int mW;
    private int index = 0, widOfEach;
    private int changeColor;
    private ICommonFragment iCommonFragment;
    private int textSize;

    private void initView(Context mContext) {
        mPaint = new Paint();
        textSize = Sp2Px(mContext, 13);
        mPaint.setTextSize(textSize);
        onclickPaint = new Paint();
    }

    public void setAry(String[] ary, int[] imgAry, int[] imgChageAry) throws Exception {
        if (ary == null || ary.length != imgAry.length || imgChageAry == null)
            throw new Exception("200001");
        this.ary = ary;
        this.imgArray = imgAry;
        this.imgChageArray = imgChageAry;
        chilNum = this.ary.length;

    }


    public BottomavigationView(Context context) {
        this(context, null);
    }

    public BottomavigationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    public void setChangeColor(int changeColor) {
        this.changeColor = changeColor;
    }

    public void setLayoutType(int layoutType) {
        int layoutType1 = layoutType;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mH = MeasureSpec.getSize(heightMeasureSpec);
        mW = MeasureSpec.getSize(widthMeasureSpec);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (ary == null || chilNum == 0) {
            return;
        }
        int mWidth, imgMarginWidth = 0;
        Bitmap bitmap = null;
        float y = mH;
        float bot = mPaint.descent();
        float top = mPaint.ascent();
        float txtH = bot - top;
        mPaint.setARGB(255, 245, 245, 245);
        float topRect = y - txtH * 4 - 10;
        canvas.drawRect(0, topRect, mW, y, mPaint);
//        Paint p = new Paint();
//        p.setAntiAlias(true);//取消锯齿
//        p.setStyle(Paint.Style.FILL);//设置画圆弧的画笔的属性为描边(空心)，个人喜欢叫它描边，叫空心有点会引起歧义
//        p.setStrokeWidth(4);
//        p.setARGB(255, 245, 245, 245);

        float x = (mW - y / 2) / 2 - 15;
//        RectF rectF = new RectF(x, y - txtH * 5 - 25, mW - x, y - txtH * 2);
//        canvas.drawArc(rectF, 0, 360, true, p);
        mPaint.setARGB(255, 76, 76, 76);
        mPaint.setTextSize(textSize);

        for (int j = 0; j < chilNum; j++) {
            int txtWid = (int) mPaint.measureText(ary[j]); //文字的宽度
            widOfEach = mW / chilNum;//每一个导航的宽度
            int txtDiffer = widOfEach - txtWid;//每个导航减去文字所剩部分
            mWidth = txtDiffer / 2;//文字前后的边距

            if (index == j) {
                if (imgChageArray != null) {
                    bitmap = BitmapFactory.decodeResource(getResources(), imgChageArray[j]);
                    int width = bitmap.getWidth();
                    imgMarginWidth = (widOfEach - width) / 2;//图片左右边距，与文字获取方法一致
                }
                if (bitmap != null) {
//                    if (layoutType == 1 && j == 2) {
//                        canvas.drawBitmap(bitmap, j * widOfEach + imgMarginWidth, y - txtH * 5, onclickPaint);
//                    } else {
                    canvas.drawARGB(0, 245, 245, 245);
                    canvas.drawBitmap(bitmap, j * widOfEach + imgMarginWidth, y - txtH * 4, onclickPaint);

//                    }
                    onclickPaint.setColor(changeColor);
                    onclickPaint.setTextSize(textSize);
                    canvas.drawText(ary[j], j * widOfEach + mWidth, y - 20, onclickPaint);
                } else {

                    CommonUtils.LogUtils.e("BottomavigationView onDraw bitmap is null");
                }

            } else {
                onclickPaint.setColor(Color.BLACK);
                bitmap = BitmapFactory.decodeResource(getResources(), imgArray[j]);
                if (bitmap != null) {
                    int width = bitmap.getWidth();
                    imgMarginWidth = (widOfEach - width) / 2;//图片左右边距，与文字获取方法一致
//                    if (layoutType == 1 && j != 2) {
                    canvas.drawBitmap(bitmap, j * widOfEach + imgMarginWidth, y - txtH * 4, onclickPaint);
//                    } else {
//                        canvas.drawBitmap(bitmap, j * widOfEach + imgMarginWidth, y - txtH * 5, onclickPaint);
//                    }
                    canvas.drawText(ary[j], j * widOfEach + mWidth, y - 20, mPaint);
                } else {
                    CommonUtils.LogUtils.e("BottomavigationView onDraw bitmap is null");
                }


            }

        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int eventX = (int) event.getRawX();
                int eventY = (int) event.getRawY();
                if (eventX > 0) {
                    index = (eventX / (mW / chilNum));
                    notifyListener();
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:

                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return false;

    }

    private void notifyListener() {
        invalidate();
        iCommonFragment.getCurrentView(index);
    }

    public void setCommonFragment(ICommonFragment commonFragment) {
        this.iCommonFragment = commonFragment;
    }

    public void setIndex(int index) {
        this.index = index;
        invalidate();
    }


    public interface ICommonFragment {
        void getCurrentView(int tag);
    }

}
