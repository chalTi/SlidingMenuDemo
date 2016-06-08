package com.example.wentongwang.slidingmenudemo;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by Wentong WANG on 2016/3/25.
 */
public class SlidingMenu extends ViewGroup {

    final static int MENU_LEFT = 0;
    final static int MENU_RIGHT = 1;

    private float mDownX;
    private float mDownY;
    //中间界面部分
    private View mContentView;
    private int contentWidth;
    private int contentHeight;
    //左侧菜单部分
    private View mLeftView;
    private int leftWidth;
    private boolean leftMenuOpened = false;
    private boolean isUseLeftMenu = false;

    //右侧菜单部分
    private View mRightView;
    private int rightWidth;
    private boolean rightMenuOpened = false;
    private boolean isUseRightMenu = false;

    private Scroller mScroller;

    public SlidingMenu(Context context) {
        this(context, null);
    }

    public SlidingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);

        mScroller = new Scroller(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //XML加载完成时的回调
        mLeftView = getChildAt(0);
        mContentView = getChildAt(1);
        mRightView = getChildAt(2);

        LayoutParams layoutParamsL = mLeftView.getLayoutParams();
        leftWidth = layoutParamsL.width;
        LayoutParams layoutParamsR = mRightView.getLayoutParams();
        rightWidth = layoutParamsR.width;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //测量孩子
        //父和子的测量关系
        //child.measure(): 期望孩子大小该怎么设置
        //测量左侧菜单
        mLeftView.measure(MeasureSpec.makeMeasureSpec(leftWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
        //测量右侧
        mContentView.measure(widthMeasureSpec, heightMeasureSpec);
        //测量右侧菜单
        mRightView.measure(MeasureSpec.makeMeasureSpec(rightWidth, MeasureSpec.EXACTLY), heightMeasureSpec);


        contentWidth = mContentView.getMeasuredWidth();
        contentHeight = mContentView.getMeasuredHeight();
        //设置自己的宽度跟高度
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(measureWidth, measureHeight);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //左侧布局
//        int width = mLeftView.getMeasuredWidth();
//        int height = mLeftView.getMeasuredHeight();

        int left, top, right, bottom;
        left = -leftWidth;
        top = 0;
        right = 0;
        bottom = contentHeight;

        mLeftView.layout(left, top, right, bottom);
        //右侧布局
        mContentView.layout(0, 0, contentWidth, contentHeight);

        mRightView.layout(contentWidth, 0, contentWidth + rightWidth, contentHeight);
    }

    //拦截touch，让listView也能识别左右滑动
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = ev.getX();
                float moveY = ev.getY();
                if (Math.abs(moveX - mDownX) > 100 && Math.abs(moveX - mDownX) > Math.abs(moveY - mDownY)) {
                    //水平滑动 拦截掉
                    return true;
                }
                break;
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                mDownY = ev.getY();
                break;

        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //获取手指初始位置
                mDownX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                //获取移动时候的位置
                float moveX = event.getX();
                int diffX = (int) (mDownX - moveX + 0.5f);//四舍五入
                int scrollX = getScrollX() + diffX;//将要移动到的位置


                if (scrollX < 0 && isUseLeftMenu) {
                    //屏幕向左滑动越界，滚动到指定位置
                    if (scrollX < -leftWidth) scrollTo(-leftWidth, 0);
                    else scrollBy(diffX, 0);
                } else if (scrollX > 0 && isUseRightMenu) {
                    //屏幕向右滑动越界，滚动到指定位置
                    if (scrollX > rightWidth) scrollTo(rightWidth, 0);
                    else scrollBy(diffX, 0);
                } else {
                    scrollTo(0, 0);
                }

                //重置
                mDownX = moveX;
                break;
            case MotionEvent.ACTION_UP:
                //松开时候的
                float upX = event.getX(); //手指在屏幕当前的位置
                int currentX = getScrollX();//屏幕当前位置值
                float isleftOpenPosition = -leftWidth / 2f; //判断是否展开菜单的中间值
                float isrightOpenPosition = rightWidth / 2f;

                if (currentX < 0) {
                    //滑出左侧菜单的操作
                    if (!leftMenuOpened) {
                        if (currentX > isleftOpenPosition) {
                            //并不展开菜单
                            closeLeftMenu(currentX);
                        } else {
                            openLeftMenu(currentX);
                        }
                    } else {
                        //实现点击菜单外部分，直接收回菜单
                        if (upX > mLeftView.getMeasuredWidth()) {
                            closeLeftMenu(currentX);
                        } else if (currentX > isleftOpenPosition) {
                            closeLeftMenu(currentX);
                        } else {
                            openLeftMenu(currentX);
                        }
                    }
                } else {
                    //滑出右侧菜单的操作
                    if (!rightMenuOpened) {
                        if (currentX < isrightOpenPosition) {
                            //并不展开菜单
                            closeRightMenu(currentX);
                        } else {
                            openRightMenu(currentX);
                        }
                    } else {
                        //实现点击菜单外部分，直接收回菜单
                        if (upX < contentWidth - rightWidth) {
                            closeRightMenu(currentX);
                        } else if (currentX < isrightOpenPosition) {
                            closeRightMenu(currentX);
                        } else {
                            openRightMenu(currentX);
                        }
                    }
                }
                invalidate(); //刷新UI -->需要重写 --draw()--drawChild()之后的computeScroll(),否则动画出不来
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            //更新位置
            scrollTo(mScroller.getCurrX(), 0);
            invalidate();
        }
    }

    private void openLeftMenu(int currentX) {
        scrollTo(-leftWidth, 0);
        //放缓滑动，提高用户体验
        int startX = currentX;
        int dx = -leftWidth - startX;
        int duration = Math.abs(dx) * 10; //时长
        if (duration >= 400) {
            duration = 400;
        }
        //这个只是模拟数据，具体还要刷新UI
        mScroller.startScroll(startX, 0, dx, 0, duration);
        leftMenuOpened = true;
    }

    private void closeLeftMenu(int currentX) {
        scrollTo(0, 0);
        //放缓滑动，提高用户体验
        int startX = currentX;
        int dx = 0 - startX;
        int duration = Math.abs(dx) * 10; //时长
        if (duration >= 400) {
            duration = 400;
        }
        mScroller.startScroll(startX, 0, dx, 0, duration);
        leftMenuOpened = false;
    }

    private void openRightMenu(int currentX) {
        scrollTo(rightWidth, 0);
        //放缓滑动，提高用户体验
        int startX = currentX;
        int dx = rightWidth - startX;
        int duration = Math.abs(dx) * 10; //时长
        if (duration >= 400) {
            duration = 400;
        }
        //这个只是模拟数据，具体还要刷新UI
        mScroller.startScroll(startX, 0, dx, 0, duration);
        rightMenuOpened = true;
    }

    private void closeRightMenu(int currentX) {
        scrollTo(0, 0);
        //放缓滑动，提高用户体验
        int startX = currentX;
        int dx = 0 - startX;
        int duration = Math.abs(dx) * 10; //时长
        if (duration >= 400) {
            duration = 400;
        }
        mScroller.startScroll(startX, 0, dx, 0, duration);
        rightMenuOpened = false;
    }

    /**
     * 外部调用的打开菜单
     * @param which 0为左侧， 1为右侧
     */
    public void openMenu(int which) {
       if (which == MENU_LEFT){
           openLeftMenu(0);
       }else{
           openRightMenu(0);
       }
    }

    /**
     * 给外部使用的关闭菜单
     *
     * @param which 0为左侧菜单，1为右侧菜单
     */
    public void closeMenu(int which) {
        if (which == MENU_LEFT)
            closeLeftMenu(-leftWidth);
        else
            closeRightMenu(rightWidth);
    }


    public boolean isLeftMenuOpened() {
        return this.leftMenuOpened;
    }

    /**
     * 设置是否使用左侧菜单
     *
     * @param is
     */
    public void useLeftMenu(boolean is) {
        this.isUseLeftMenu = is;
    }

    /**
     * 设置是否使用右侧菜单
     *
     * @param is
     */
    public void useRightMenu(boolean is) {
        this.isUseRightMenu = is;
    }

}
