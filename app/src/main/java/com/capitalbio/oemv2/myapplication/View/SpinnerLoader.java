package com.capitalbio.oemv2.myapplication.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.capitalbio.oemv2.myapplication.Const.Const;
import com.capitalbio.oemv2.myapplication.R;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by liurongchan with love on 15/8/5.
 */
public class SpinnerLoader extends View {

    public static final String TAG = "SpinnerLoader";



    public void setDirectionAndDistance(int direction, float dis) {
        this.direction = direction;
        this.distance = dis;
    }


    //设备变化监听
    public ISelectedDeviceChange deviceChangeLisener;

    private int direction;
    private float distance = 0;
    private static final int POINTS_COUNT = 6;
    private static final float BIG_STEP = 0;
    private static final int DEFAULT_COLOR = Color.rgb(205, 240, 242);
    private static final float DEFAULT_RADUIS = 180;
    private static final float DEFAULT_CIRCLE_RADUIS = 40;
    private static final float DEAFULT_MOVE_RADUIS = 30;
    private static final int SPLIT_ANGLE = 30;


    /**
     * for save and restore instance of view.
     */
    private static final String INSTANCE_STATE = "saved_instance";
    private static final String ANGLE = "angle";
    private static final String BIGCIRCLECENTERX = "bigCircleCenterX";
    private static final String BIGCIRCLECENTERY = "bigCircleCenterY";
    private static final String RADUIS = "raduis";
    private static final String CIRCLERADUIS = "circleRaduis";
    private static final String MOVERADUIS = "moveRaduis";
    private static final String POINTCOLOR = "pointColor";
    private static final String STARTX1 = "startX1";
    private static final String STARTY1 = "startY1";
    private static final String ENDX1 = "endX1";
    private static final String ENDY1 = "endY1";
    private static final String STARTX2 = "startX2";
    private static final String STARTY2 = "startY2";
    private static final String ENDX2 = "endX2";
    private static final String ENDY2 = "endY2";
    private static final String CONTROLX1 = "controlX1";
    private static final String CONTROLY1 = "controlY1";
    private static final String BIGSTEP = "bigStep";
    private int smallCicle;
    private int middleCicle;
    private int bigCicle;
    private float smallToMiddle;
    private float middleToBig;
    public float alphAverageDistance;

    public int smallTextSize;
    public int bigTextSize;

    private float originX;
    private float moveOriginX;
    private MotionEvent currentEvent;
    private boolean isOutOfScreen = false;

    public float centerCircleRadius;


    public int[] smallToBigAlph = new int[11];

    private CirclePoint[] circlePoints = new CirclePoint[POINTS_COUNT];

    public CirclePoint[] getInitialPoints() {
        return initialPoints;
    }

    private CirclePoint[] initialPoints = new CirclePoint[POINTS_COUNT];

    public int angle = 0;


    private float bigStep;
    float bigCircleCenterX;
    float bigCircleCenterY;

    @Override
    public boolean isLaidOut() {
        return super.isLaidOut();
    }

    float raduis;
    float circleRaduis;
    float moveRaduis;
    int pointColor;


    private float startX1;
    private float startY1;
    private float startX2;
    private float startY2;

    private float controlX1;
    private float controlY1;

    private float endX1;
    private float endY1;
    private float endX2;
    private float endY2;

    private Path path1;

    private Paint big4Paint;


    private Paint middle3Paint;
    private Paint small3Paint;


    public static Paint bigCirclePaint;
    public static Paint middleCirclePaint;
    public static Paint smallCirclePaint;
    private Context mContext;
    private int mWidth;
    private int mHeight;


    private boolean isFirst = true;
    private boolean isFirstDraw = true;


    public SpinnerLoader(Context context) {
        this(context, null);
    }

    public SpinnerLoader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpinnerLoader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;


        smallCicle = getResources().getDimensionPixelSize(R.dimen.smallcicledp);
        middleCicle = getResources().getDimensionPixelSize(R.dimen.middlecicledp);
        bigCicle = getResources().getDimensionPixelSize(R.dimen.bigcicledp);

        //获取屏幕的宽度
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mWidth = wm.getDefaultDisplay().getWidth();

        final TypedArray attributes;
        attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SpinnerLoader,
                defStyleAttr, 0);
        pointColor = attributes.getColor(R.styleable.SpinnerLoader_point_color, DEFAULT_COLOR);
        boolean isdynamic = attributes.getBoolean(R.styleable.SpinnerLoader_isdynamic, true);
        isDynamic(isdynamic);
        attributes.recycle();

        smallTextSize = getResources().getDimensionPixelSize(R.dimen.main_view_small_text_size);
        bigTextSize = getResources().getDimensionPixelSize(R.dimen.main_view_big_text_size);

        //设置坐标和透明值的对应关系,11级透明度变换
        smallToBigAlph[0] = getResources().getColor(R.color.small_circle);
        smallToBigAlph[1] = getResources().getColor(R.color.small_to_middle_1);
        smallToBigAlph[2] = getResources().getColor(R.color.small_to_middle_2);
        smallToBigAlph[3] = getResources().getColor(R.color.small_to_middle_3);
        smallToBigAlph[4] = getResources().getColor(R.color.small_to_middle_4);
        smallToBigAlph[5] = getResources().getColor(R.color.middle_circle);
        smallToBigAlph[6] = getResources().getColor(R.color.middle_to_big_1);
        smallToBigAlph[7] = getResources().getColor(R.color.middle_to_big_2);
        smallToBigAlph[8] = getResources().getColor(R.color.middle_to_big_3);
        smallToBigAlph[9] = getResources().getColor(R.color.middle_to_big_4);
        smallToBigAlph[10] = getResources().getColor(R.color.big_circle);



    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec, true), measure(heightMeasureSpec, false));
    }

    private int measure(int measureSpec, boolean isWidth) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int padding = isWidth ? getPaddingLeft() + getPaddingRight() : getPaddingTop() + getPaddingBottom();
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = isWidth ? getSuggestedMinimumWidth() : getSuggestedMinimumHeight();
            result += padding;
            if (mode == MeasureSpec.AT_MOST) {
                if (isWidth) {
                    result = Math.max(result, size);
                } else {
                    result = Math.min(result, size);
                }
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (isFirst) {
            init();
            isFirst = false;
        }

        //每次滑动都会改变每个小圆的圆心的坐标
        for (int i = 1; i < POINTS_COUNT; i ++) {
            CirclePoint p = circlePoints[i];

            p.currentAngle = p.currentAngle + distance;
            centerCircleRadius = (float) (1.6 * raduis);

            //此处的xy是小圆圆心的坐标
            p.x = (float) (getPaddingLeft() + bigCircleCenterX + (float)Math.cos(Math.toRadians(p.currentAngle)) * centerCircleRadius);
            p.y = (float) (getPaddingTop() + bigCircleCenterY + (float)Math.sin(Math.toRadians(p.currentAngle)) * centerCircleRadius);
        }

        if (isFirstDraw) {
            //进行初始状态的绘制
            for (int i = 1; i < POINTS_COUNT; i++) {
                if (i == 1 || i == 5){
                    circlePoints[i].raduis = smallCicle;
                } else if (i == 2 || i == 4){
                    circlePoints[i].raduis = middleCicle;
                } else  if (i == 3) {
                    circlePoints[i].raduis = bigCicle;
                }
                circlePoints[i].drawSelfText(canvas, this);
            }

            //保存初始状态点
            for (int i = 1; i < circlePoints.length; i++) {
                initialPoints[i] = new CirclePoint();
                initialPoints[i].currentAngle = circlePoints[i].currentAngle;
                initialPoints[i].raduis = circlePoints[i].raduis;
                initialPoints[i].x = circlePoints[i].x;
                initialPoints[i].y = circlePoints[i].y;
                initialPoints[i].color = circlePoints[i].color;
            }

            isFirstDraw = false;
        }

        //计算平均距离
        caculateSpace();

        //滑动坐标变换
        slidCoordinateTranceform(direction);

        //点的绘制
        for (int i = 1; i < POINTS_COUNT; i++) {
            Log.d("circleState", " angle is " + circlePoints[i].currentAngle + " x is " + circlePoints[i].x + " y is " + circlePoints[i].y);
            circlePoints[i].drawNextState(canvas, this);
        }



    }

    protected void init() {
        float temp = getHeight() > getWidth() ? getWidth() / 2 : getHeight() / 2;
        raduis = temp - temp / DEFAULT_RADUIS * DEFAULT_CIRCLE_RADUIS;
        circleRaduis = DEFAULT_CIRCLE_RADUIS / DEFAULT_RADUIS * raduis;
        moveRaduis = DEAFULT_MOVE_RADUIS / DEFAULT_RADUIS * raduis;
        bigCircleCenterX = getPaddingLeft() + getWidth() / 2;
        bigCircleCenterY = getPaddingTop() + getHeight() / 2;

        path1 = new Path();
        initializePaints();
        initializePoints();
    }



    protected void initializePoints() {
        for (int i = 1; i < POINTS_COUNT; i++) {
            CirclePoint p = new CirclePoint();
            p.currentAngle = SPLIT_ANGLE * i;
            p.x = getPaddingLeft() + bigCircleCenterX + (float) Math.cos(Math.toRadians(p.currentAngle)) * raduis;
            p.y = getPaddingTop() + bigCircleCenterY + (float)Math.sin(Math.toRadians(p.currentAngle)) * raduis;
            p.color = pointColor;

            if (i == 1) {
                p.deviceName = getResources().getString(R.string.glycolipid);
            } else if (i == 2) {
                p.deviceName = getResources().getString(R.string.bracelete);
            } else if (i == 3) {
                p.deviceName = getResources().getString(R.string.aircat);
            } else if (i == 4) {
                p.deviceName = getResources().getString(R.string.bloodpress);
            } else if (i == 5) {
                p.deviceName = getResources().getString(R.string.bodyfat);
            }
            circlePoints[i] = p;
        }
    }

    protected void initializePaints() {
        bigCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bigCirclePaint.setColor(getResources().getColor(R.color.big_circle));

        middleCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        middleCirclePaint.setColor(getResources().getColor(R.color.middle_circle));

        smallCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        smallCirclePaint.setColor(getResources().getColor(R.color.small_circle));

        big4Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        big4Paint.setTextSize(70);
        big4Paint.setColor(getResources().getColor(R.color.blue));
        Typeface font = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD);
        big4Paint.setTypeface(font);


        middle3Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        middle3Paint.setTextSize(55);
        middle3Paint.setColor(getResources().getColor(R.color.blue));
        middle3Paint.setTypeface(font);

        small3Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        small3Paint.setTextSize(44);
        small3Paint.setColor(getResources().getColor(R.color.blue));
        small3Paint.setTypeface(font);
    }

    public void isDynamic(boolean dynamic) {
        if (dynamic) {
            bigStep = BIG_STEP;
        } else {
            bigStep = 0;
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE,super.onSaveInstanceState());
        bundle.putInt(ANGLE, angle);
        bundle.putFloat(BIGCIRCLECENTERX, bigCircleCenterX);
        bundle.putFloat(BIGCIRCLECENTERY, bigCircleCenterY);
        bundle.putFloat(RADUIS, raduis);
        bundle.putFloat(CIRCLERADUIS, circleRaduis);
        bundle.putFloat(MOVERADUIS, moveRaduis);
        bundle.putFloat(STARTX1, startX1);
        bundle.putFloat(STARTY1, startY1);
        bundle.putFloat(ENDX1, endX1);
        bundle.putFloat(ENDY1, endY1);
        bundle.putFloat(STARTX2, startX2);
        bundle.putFloat(STARTY2, startY2);
        bundle.putFloat(ENDX2, endX2);
        bundle.putFloat(ENDY2, endY2);
        bundle.putFloat(CONTROLX1, controlX1);
        bundle.putFloat(CONTROLY1, controlY1);
        bundle.putInt(POINTCOLOR, pointColor);
        bundle.putFloat(BIGSTEP, bigStep);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(state instanceof Bundle){
            final Bundle bundle = (Bundle)state;
            angle = bundle.getInt(ANGLE);
            bigCircleCenterX = bundle.getFloat(BIGCIRCLECENTERX);
            bigCircleCenterY = bundle.getFloat(BIGCIRCLECENTERY);
            raduis = bundle.getFloat(RADUIS);
            circleRaduis = bundle.getFloat(CIRCLERADUIS);
            moveRaduis = bundle.getFloat(MOVERADUIS);
            startX1 = bundle.getFloat(STARTX1);
            startY1 = bundle.getFloat(STARTY1);
            endX1 = bundle.getFloat(ENDX1);
            endY1 = bundle.getFloat(ENDY1);
            startX2 = bundle.getFloat(STARTX2);
            startY2 = bundle.getFloat(STARTY2);
            endX2 = bundle.getFloat(ENDX2);
            endY2 = bundle.getFloat(ENDY2);
            controlX1 = bundle.getFloat(CONTROLX1);
            controlY1 = bundle.getFloat(CONTROLY1);
            pointColor = bundle.getInt(POINTCOLOR);
            bigStep = bundle.getInt(BIGSTEP);
            init();
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }


    public static class CirclePoint  {
        public float currentAngle;
        public float raduis;
        public float x;
        public float y;
        public int color;
        public String deviceName;
        public Paint circleColorPaint = new Paint();
        public Paint textPaint = new Paint();
        public int textSize;


        public void drawNextState(Canvas canvas, SpinnerLoader parent) {
            setCirclePaint(parent);
            canvas.drawCircle(this.x, this.y - parent.getHeight() / 3, coordinateToRadius(parent), circleColorPaint);
            drawSelfText(canvas, parent);
        }

        private void drawSelfText(Canvas canvas, SpinnerLoader parent) {
            setTextSize(parent);
            canvas.drawText(deviceName, x - deviceName.length() * textSize/2, y - parent.getHeight()/3 + textSize/3, textPaint);
        }

        private float coordinateToRadius(SpinnerLoader parent) {

            if (x > parent.getInitialPoints()[1].x || x < parent.getInitialPoints()[5].x) {
                raduis = parent.smallCicle;
                return raduis;
            }
            if (x >= parent.getInitialPoints()[2].x && x < parent.getInitialPoints()[1].x) {
                raduis = parent.smallCicle + (parent.middleCicle - parent.smallCicle) * (parent.getInitialPoints()[1].x - x)/parent.smallToMiddle;
            } else if (x >= parent.getInitialPoints()[3].x && x < parent.getInitialPoints()[2].x) {
                raduis = parent.middleCicle + (parent.bigCicle - parent.middleCicle) * (parent.getInitialPoints()[2].x - x)/parent.smallToMiddle;
            } else if (x >= parent.getInitialPoints()[4].x && x < parent.getInitialPoints()[3].x) {
                return parent.bigCicle - (parent.bigCicle - parent.middleCicle) * (parent.getInitialPoints()[3].x - x)/parent.middleToBig;
            } else if (x >= parent.getInitialPoints()[5].x && x < parent.getInitialPoints()[4].x) {
                raduis = parent.middleCicle - (parent.middleCicle - parent.smallCicle) * (parent.getInitialPoints()[4].x - x)/parent.smallToMiddle;
            }

            return raduis;
        }

        private void setCirclePaint(SpinnerLoader parent) {

            int index;
            int color;
            circleColorPaint.setAntiAlias(true);
            if (x >= parent.getInitialPoints()[3].x) {
                index = (int) ((x - parent.getInitialPoints()[3].x)/parent.alphAverageDistance);
            } else {
                index = (int) ((x - parent.getInitialPoints()[5].x)/parent.alphAverageDistance);
            }

            //边界处理
            if (index <= 0) {
                index = 0;
            } else if (index >= 11) {
                index = 10;
            }

            if (x >= parent.getInitialPoints()[3].x) {
                color = parent.smallToBigAlph[10 - index];
            } else {
                color = parent.smallToBigAlph[index];
            }
            circleColorPaint.setColor(color);
        }

        private void setTextSize (SpinnerLoader parent) {

            if (currentAngle <= 90) {
                textSize = (int) (parent.smallTextSize + (parent.bigTextSize - parent.smallTextSize) * currentAngle/90);
            } else if (currentAngle > 90) {
                textSize = (int)(parent.bigTextSize - (parent.bigTextSize - parent.smallTextSize) * (currentAngle - 90)/90);
            }

            textPaint.setTextSize(textSize);
            textPaint.setColor(parent.getResources().getColor(R.color.circle_text_color));

        }


    }


    public void setDeviceChangeListener(ISelectedDeviceChange deviceChange) {
        deviceChangeLisener = deviceChange;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float distance = 0;
        int degree;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                originX = event.getX();
                moveOriginX = originX;
                break;

            case MotionEvent.ACTION_MOVE:
                handleTouch(event);
                break;

            case MotionEvent.ACTION_UP:
                distance = event.getX() - moveOriginX;
                distance = 120 * (distance)/(initialPoints[1].x - initialPoints[3].x);
                degree = (int) (distance/30);
                degree = degree * 30;
                if (event.getX() - originX < 0){
                    direction = Const.MAIN_VIEW_SLIP_DIRECTION_LEFT;
                    resetCoordinate(Const.MAIN_VIEW_SLIP_DIRECTION_LEFT);
                    setDirectionAndDistance(Const.MAIN_VIEW_SLIP_DIRECTION_LEFT, -degree);
                } else if (event.getX() - originX > 0) {
                    direction = Const.MAIN_VIEW_SLIP_DIRECTION_RIGHT;
                    resetCoordinate(Const.MAIN_VIEW_SLIP_DIRECTION_RIGHT);
                    setDirectionAndDistance(Const.MAIN_VIEW_SLIP_DIRECTION_RIGHT, -degree);
                }

                resetCoordinate(direction);

                for (int i = 1; i < circlePoints.length; i++) {
                    if (circlePoints[i].currentAngle == 90) {
                        deviceChangeLisener.selectDeviceChange(circlePoints[i]);
                    }
                }



                invalidate();
                break;
        }

        return true;
    }

    private void caculateSpace() {

        smallToMiddle = initialPoints[1].x - initialPoints[2].x;
        middleToBig = initialPoints[2].x - initialPoints[3].x;
        alphAverageDistance = (initialPoints[1].x - initialPoints[3].x)/11;
    }

    private void resetCoordinate(int dir) {
        boolean isSwaptoRight;
        for (int i = 1; i < circlePoints.length; i++) {
            Log.d("resetTest", "before reset: dir is " + dir + " i is " + i + " currentAngle is " + circlePoints[i].currentAngle);
        }

        for (int i = 1; i < circlePoints.length; i++) {
            if (circlePoints[i].currentAngle > initialPoints[1].currentAngle && circlePoints[i].currentAngle <= initialPoints[2].currentAngle) {
                isSwaptoRight = (circlePoints[i].currentAngle < initialPoints[1].currentAngle + 15);
//                swapCirclePoint(circlePoints[i], (dir == Const.MAIN_VIEW_SLIP_DIRECTION_LEFT) && isSwaptoRight ? initialPoints[1] : initialPoints[2]);
                swapRule(dir, i, 1, 2, isSwaptoRight);
            } else if (circlePoints[i].currentAngle >= initialPoints[2].currentAngle && circlePoints[i].currentAngle < initialPoints[3].currentAngle) {
                isSwaptoRight = (circlePoints[i].currentAngle < initialPoints[2].currentAngle + 15);
                swapRule(dir, i, 2, 3, isSwaptoRight);
//                swapCirclePoint(circlePoints[i], (dir == Const.MAIN_VIEW_SLIP_DIRECTION_LEFT) && isSwaptoRight ? initialPoints[2] : initialPoints[3]);
            } else if (circlePoints[i].currentAngle >= initialPoints[3].currentAngle && circlePoints[i].currentAngle < initialPoints[4].currentAngle) {
                isSwaptoRight = (circlePoints[i].currentAngle < initialPoints[3].currentAngle + 15);
                swapRule(dir, i, 3, 4, isSwaptoRight);
//                swapCirclePoint(circlePoints[i], (dir == Const.MAIN_VIEW_SLIP_DIRECTION_LEFT) && isSwaptoRight ? initialPoints[3] : initialPoints[4]);
            } else if (circlePoints[i].currentAngle >= initialPoints[4].currentAngle && circlePoints[i].currentAngle < initialPoints[5].currentAngle) {
                isSwaptoRight = (circlePoints[i].currentAngle < initialPoints[4].currentAngle + 15);
                swapRule(dir, i, 4, 5, isSwaptoRight);
//                swapCirclePoint(circlePoints[i], (dir == Const.MAIN_VIEW_SLIP_DIRECTION_LEFT) && isSwaptoRight ? initialPoints[4] : initialPoints[5]);
            } else if (circlePoints[i].currentAngle > initialPoints[5].currentAngle && circlePoints[i].currentAngle <= initialPoints[5].currentAngle + 15) {
                swapCirclePoint(circlePoints[i], initialPoints[5]);
            } else if (circlePoints[i].currentAngle < initialPoints[1].currentAngle && circlePoints[i].currentAngle >= initialPoints[1].currentAngle - 15) {
                swapCirclePoint(circlePoints[i], initialPoints[1]);
            }
        }

        //每次复位之后都需要将角度变化值设为0
        distance = 0;
        for (int i = 1; i < circlePoints.length; i++) {
            Log.d("resetTest", "after reset: dir is " + dir + " i is " + i + " currentAngle is " + circlePoints[i].currentAngle);
        }
    }

    private void  handleTouch(MotionEvent event) {
        distance = event.getX() - moveOriginX;

        //向左滑动
        if (distance < 0){
            distance = 60 * (-distance)/(initialPoints[1].x - initialPoints[3].x);
            setDirectionAndDistance(Const.MAIN_VIEW_SLIP_DIRECTION_LEFT, distance);
        } else  if (distance > 0){
            //向右滑动
            distance = 60 * (distance)/(initialPoints[1].x - initialPoints[3].x);
            setDirectionAndDistance(Const.MAIN_VIEW_SLIP_DIRECTION_RIGHT, -distance);
        }
        currentEvent = event;
        invalidate();
        //更新最新的x的下标
        moveOriginX = event.getX();
    }


    private void slidCoordinateTranceform(int dir) {

        int pointRange;
        int outScreenCount = 0;

        Arrays.sort(circlePoints, 1, 6, new MyComare());
        //向左边滑动
        if (direction == Const.MAIN_VIEW_SLIP_DIRECTION_LEFT) {
            //每次刷新都应该检查所有出去屏幕的点
            for (int i = 1; i < circlePoints.length; i++) {
                Log.d("beforeSwapPoint", " i is " + i + " angle is " + circlePoints[i].currentAngle + " dir is " + dir);
                if (circlePoints[i].currentAngle > initialPoints[5].currentAngle + 15) {
                    originX = currentEvent.getX();
                    outScreenCount++;
                    isOutOfScreen = true;
                }
            }

            for (int i = 1; i < outScreenCount + 1; i++) {
                float offset = (circlePoints[i].currentAngle - initialPoints[5].currentAngle)%15;
                Log.d("swapPoint", " i is " + i + " angle is " + circlePoints[i].currentAngle + ", offset is " + offset + ", outpoint is " + outScreenCount);

                swapCirclePoint(circlePoints[i], initialPoints[outScreenCount - i + 1]);
                circlePoints[i].currentAngle = circlePoints[i].currentAngle;
            }

            if (outScreenCount > 0) {
                for (int i = 1; i < circlePoints.length; i++) {
                    Log.d("afterSwapPoint", " i is " + i + " angle is " + circlePoints[i].currentAngle + " dir is " + dir);
                    /*if (circlePoints[i].currentAngle > initialPoints[5].currentAngle + 15) {
                        originX = currentEvent.getX();
                        outScreenCount++;
                        isOutOfScreen = true;
                    }*/
                }
            }

        } else if (direction == Const.MAIN_VIEW_SLIP_DIRECTION_RIGHT) {

            for (int i = 1; i < circlePoints.length; i++) {
                Log.d("beforeSwapPoint", " i is " + i + " angle is " + circlePoints[i].currentAngle);
                if (circlePoints[i].currentAngle < initialPoints[1].currentAngle - 15) {
                    originX = currentEvent.getX();
                    isOutOfScreen = true;
                    outScreenCount++;

                }
            }

            for (int i = 0; i < outScreenCount; i++) {
                float offset = (circlePoints[5 - i].currentAngle - initialPoints[1].currentAngle)%15;
                Log.d("swapPoint", " i is " + i + " angle is " + circlePoints[5 - i].currentAngle + ", offset is " + offset + ", outScreenCount is " + outScreenCount);
                swapCirclePoint(circlePoints[5 -i], initialPoints[5 - i]);
                circlePoints[5 - i].currentAngle = circlePoints[5 - i].currentAngle;
            }

            if (outScreenCount > 0) {
                for (int i = 1; i < circlePoints.length; i++) {
                    Log.d("afterSwapPoint", " i is " + i + " angle is " + circlePoints[i].currentAngle + " dir is " + dir);
                    /*if (circlePoints[i].currentAngle > initialPoints[5].currentAngle + 15) {
                        originX = currentEvent.getX();
                        outScreenCount++;
                        isOutOfScreen = true;
                    }*/
                }
            }


        }

        if (isOutOfScreen) {
            resetCoordinate(direction);
            isOutOfScreen = false;
        }
    }

    private void swapCirclePoint(CirclePoint p, CirclePoint q) {
        p.currentAngle = q.currentAngle;
        p.raduis = q.raduis;
        p.x = q.x;
        p.y = q.y;
        p.color = q.color;

    }

    public static class MyComare implements Comparator<CirclePoint> {

        @Override
        public int compare(CirclePoint c1, CirclePoint c2) {
            if (c1.currentAngle < c2.currentAngle) {
                return 1;
            } if (c1.currentAngle > c2.currentAngle){
                return -1;
            } else {
                return 0;
            }
        }
    }


    public int getBigCicle() {
        return bigCicle;
    }


    private void swapRule(int direction, int tmpCircleIndex, int lowInitCircleIndex, int higInitCircleIndex, boolean isToRight) {
        switch (direction) {
            case Const.MAIN_VIEW_SLIP_DIRECTION_LEFT:

                if (isToRight) {
                    swapCirclePoint(circlePoints[tmpCircleIndex], initialPoints[lowInitCircleIndex]);
                } else {
                    swapCirclePoint(circlePoints[tmpCircleIndex], initialPoints[higInitCircleIndex]);
                }


                break;
            case Const.MAIN_VIEW_SLIP_DIRECTION_RIGHT:

                if (isToRight) {
                    swapCirclePoint(circlePoints[tmpCircleIndex], initialPoints[lowInitCircleIndex]);
                } else {
                    swapCirclePoint(circlePoints[tmpCircleIndex], initialPoints[higInitCircleIndex]);
                }
                break;

            default:
                break;
        }
    }

}
