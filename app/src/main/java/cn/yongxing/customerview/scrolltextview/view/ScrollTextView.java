package cn.yongxing.customerview.scrolltextview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.yongxing.customerview.scrolltextview.R;


/**
 * Created by jiangyongxing on 2016/7/25.
 * 描述：竖直滚动的跑马灯控件
 */
public class ScrollTextView extends TextView {

    private int mDuration; //文字在跑动的时候多久刷新一次界面
    private int mInterval; //文字停留在中间的时长切换的间隔
    private List<String> mTexts; //显示文字的数据源
    private int mY = 0; //文字的Y坐标
    private int mIndex = 0; //当前的数据下标
    private Paint mPaintBack; //绘制内容的画笔
    private boolean isMove = true; //文字是否移动
    private Timer timer;
    private TimerTask timerTask;
    private boolean isStart = false;//文字是否开始跑动
    private Rect indexBound;
    private String content;
    private int mMovingPixelsPerMillisecond = 6;//每毫秒移动的像素值


    public ScrollTextView(Context context) {
        this(context, null);
    }

    public ScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public boolean getIsStart() {
        return isStart;
    }

    //设置数据源
    public void setTextList(List<String> mTexts) {
        this.mTexts = mTexts;
    }

    //设置广告文字的停顿时间
    public void setmInterval(int mInterval) {
        this.mInterval = mInterval;
    }

    //设置文字从出现到消失的时长
    public void setmDuration(int mDuration) {
        this.mDuration = mDuration;
    }

    //设置正文内容的颜色
    public void setBackColor(int mBackColor) {
        mPaintBack.setColor(mBackColor);
    }

    //初始化默认值
    private void init() {
        mDuration = 10;
        mInterval = 2500;
        mIndex = 0;

        mPaintBack = new Paint();
        mPaintBack.setAntiAlias(true);
        mPaintBack.setDither(true);
        mPaintBack.setTextSize(dip2px(13));//设置字体大小
        mPaintBack.setColor(getResources().getColor(R.color.c525563));//设置文字的颜色

    }

    public void start() {
        isStart = true;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (mTexts != null && mTexts.size() > 0) {
            content = mTexts.get(mIndex);
            //绘制内容
            indexBound = new Rect();
            mPaintBack.getTextBounds(content, 0, content.length(), indexBound);
            //移动到最上面
            if (mY == removeTheRemainder(0 - indexBound.bottom)) {
                mY = removeTheRemainder(getMeasuredHeight() - indexBound.top);
                mIndex++;
            }
            canvas.drawText(content, 0, content.length(), 10, mY, mPaintBack);
            //移动到中间
            if (mY == removeTheRemainder(getMeasuredHeight() / 2 - (indexBound.top + indexBound.bottom) / 2) && isStart) {
                isMove = false;
                timer = new Timer();
                timer.schedule(timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        postInvalidate();
                        isMove = true;
                    }
                }, mInterval);
            }
            if (isStart)//此处的作用是为了当用户在Onpause的时候stop，然后在onstart或者onresume的时候开启并且是开关屏的状态下，start()方法走两遍，导致的my不在与removeTheRemainder(getMeasuredHeight() / 2 - (indexBound.top + indexBound.bottom) / 2)相等，文字就会停在中间不走
                mY -= mMovingPixelsPerMillisecond;
            //循环使用数据
            if (mIndex == mTexts.size()) {
                mIndex = 0;
            }
            //如果是处于移动状态时的,则延迟绘制
            //计算公式为一个比例,一个时间间隔移动组件高度,则多少毫秒来移动1像素
            if (isMove && isStart) {
                postInvalidateDelayed(mDuration);//间隔mDuration好眠来刷新一次界面
            }
        }

    }

    /**
     * 停止滚动文字
     */
    public void stop() {
        isStart = false;
        if (timer != null) {
            timer.cancel();
        }
        if (timerTask != null) {
            timerTask.cancel();
        }
        if (indexBound != null) {
            mY = removeTheRemainder(getMeasuredHeight() / 2 - (indexBound.top + indexBound.bottom) / 2);
        }
    }

    /**
     * 为的是适配 mMovingPixelsPerMillisecond 不是1的时候  ，mY的值与（0 - indexBound.bottom）或者
     * （getMeasuredHeight() / 2 - (indexBound.top + indexBound.bottom) / 2）不相等的问题。
     * 因为可以知道mY是mMovingPixelsPerMillisecond的整数倍，如果（0 - indexBound.bottom）不是整数倍的话
     * 那么就不会相等，达不到边界值的要求
     * @param size
     * @return
     */
    private int removeTheRemainder(int size) {
        return size - size % mMovingPixelsPerMillisecond;
    }

    /**
     * dip转换px
     */
    public int dip2px(int dip) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }
}
