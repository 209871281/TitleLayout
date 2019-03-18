package com.example.common.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.example.common.CommonUtils;

import static com.example.common.CommonUtils.PixelConversion.Sp2Px;

/**
 * 自绘控件 - 标题栏
 */

public class TitleLayout extends View {
    private Paint mPaint;//定义画笔
    private Rect mBounds;//获取文字的宽高
    private int width, height, chilNum, textSize, widOfEach, bgColor, txtColor,index;
    private String[] ary;
    private Context mContext;
    private IOnClickListener onClickListener;

    private void init(Context context) {
        this.mContext = context;
        mPaint = new Paint();
        mBounds = new Rect();
    }

    /**
     * 标题背景颜色
     *
     * @param color int
     */
    public void setBgColor(int color) {
        this.bgColor = color;
    }

    /**
     * 标题文字颜色，目前是为同一个颜色
     *
     * @param color int
     */
    public void setTextColor(int color) {
        this.txtColor = color;
    }

    public TitleLayout(Context context) {
        this(context, null);
    }

    public TitleLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT && getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(width, height);
        } else if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(width, height);
        } else if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(width, height / 11);
        }

    }

    public void setAry(String[] ary, int[] imgAry) throws Exception {
        if (ary == null)
            throw new Exception("200001");
        this.ary = ary;
        chilNum = ary.length;

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = getMeasuredWidth();
        if (getMeasuredHeight() > 190) {
            height = 190;
        } else {
            height = getMeasuredHeight();
        }
        CommonUtils.LogUtils.e("onDraw" + width + ":" + height);
        if (ary == null || chilNum == 0) {
            return;
        }
        int mWidth = 0;
        float y = height;

        mPaint.setColor(bgColor);
        canvas.drawRect(0, 0, width, y, mPaint);
        mPaint.setColor(txtColor);
        float bot = mPaint.descent();
        float top = mPaint.ascent();
        float txtH = bot - top;
        for (int j = 0; j < chilNum; j++) {
            int txtWid = (int) mPaint.measureText(ary[j]); //文字的宽度
            widOfEach = width / chilNum;//每一个导航的宽度
            int txtDiffer = widOfEach - txtWid;//每个导航减去文字所剩部分
            mWidth = txtDiffer / 2;//文字前后的边距
            if (j == 0) {
                textSize = Sp2Px(mContext, 13);
                mPaint.setTextSize(textSize);
                canvas.drawText(ary[0], 30, y / 2 + txtH / 2, mPaint);
            }
            if (j == 1) {
                textSize = Sp2Px(mContext, 17);
                mPaint.setTextSize(textSize);
                canvas.drawText(ary[1], width / 2 - txtWid / 2, y / 2 + txtH / 2, mPaint);
            }
            if (j == 2) {
                textSize = Sp2Px(mContext, 13);
                mPaint.setTextSize(textSize);
                canvas.drawText(ary[2], width - txtWid - 30, y / 2 + txtH / 2, mPaint);

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
                    index = (eventX / (width / chilNum));
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
//        invalidate();
        requestLayout();
        onClickListener.getCurrentView(index);
    }

    public void setOnclickListener(IOnClickListener commonFragment) {
        this.onClickListener = commonFragment;
    }

    public void setIndex(int index) {
        this.index = index;
//        invalidate();
        requestLayout();
    }


    public interface IOnClickListener {
        void getCurrentView(int tag);
    }

}
