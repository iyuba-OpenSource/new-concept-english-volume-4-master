package com.iyuba.conceptEnglish.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.entity.PassDetail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Relin on 2018/5/7.
 * Game break mode control.
 */

public class CircleGameStageView extends View {

    /**
     * Item's brush tool.
     */
    private Paint paint;
    /**
     * Item Normal background color
     */
//    private int itemNormalBackgroundColor = Color.parseColor("#ffaf4c");
    private int itemNormalBackgroundColor = Color.parseColor("#5468ff");
    /**
     * Item lock background color
     */
    private int itemLockBackgroundColor = Color.parseColor("#C1C9FF");

    /**
     * Item triangle background color
     */
//    private int triangleColor = Color.parseColor("#ffaf4c");
    private int triangleColor = Color.parseColor("#5468ff");

    /**
     * View the left margin
     */
    private int paddingLeft = (int) dpToPx(15);
    /**
     * View the top padding
     */
    private int paddingTop = (int) dpToPx(60);

    /**
     * View the top padding right
     */
    private int paddingRight = (int) dpToPx(15);

    /**
     * Height of the item
     */
    private int itemHeight = (int) dpToPx(30);
    ;
    /**
     * Width of the item
     */
    private int itemWidth = (int) dpToPx(90);
    /**
     * The font size
     */
    private int textSize = (int) dpToPx(12);
    /**
     * Normal font color
     */
    private int textNormalColor = Color.parseColor("#FFFFFF");
    /**
     * Lock font color
     */
    private int textLockColor = Color.parseColor("#FFFFFF");

    /**
     * Dividing line width.
     */
    private int dividerHorizontalWidth = (int) dpToPx(10);
    /**
     * Dividing line height.
     */
    private int dividerHorizontalHeight = (int) dpToPx(5);

    /**
     * Dividing line width.
     */
    private int dividerVerticalWidth = (int) dpToPx(5);
    /**
     * Dividing line height.
     */
    private int dividerVerticalHeight = (int) dpToPx(50);

    /**
     * The total number of item records
     */
    private int itemCountRecord = 1;
    /**
     * Item to move the distance.
     */
    private int itemHorizontalMove = 0;
    /**
     * Item to move the distance.
     */
    private int itemVerticalMove = 0;
    /**
     * The number of items per line.
     */
    private int lineColumns = 5;

    /**
     * The total number of items
     */
    private int totalColumns = 40;

    /**
     * he background color of the original head.
     */
    private int headCircleColor = Color.parseColor("#FFFFFF");

    /**
     * Head size
     */
    private int headSize = 80;
    /**
     * Head down arrow top padding
     */
    private int headArrowPaddingTop = 2;
    /**
     * Now you need to show it.
     */
    private int nowPosition = 1;

    /**
     * Border color
     */
    private int headStrokeColor = Color.parseColor("#DDDDDD");

    /**
     * Head of stroke width
     */
    private int headStrokeWidth = 4;

    /**
     * The row is total of items mod linePosition equal 0
     */
    private int fullRow;

    /**
     * The remainder of a dissatisfied row.
     */
    private int modNum;

    /**
     * Now row
     */
    private int nowRow;

    /**
     * The total row
     */
    private int totalRow;

    /**
     * Number of rows
     */
    private int linePosition = 0;

    /**
     * Items canvas
     */
    private Canvas canvas;

    /**
     * Item coordinates records
     */
    private Map<String, String> itemCoordinates;

    /**
     * Head coordinates records
     */
    private Map<String, String> headCoordinates;

    /**
     * The detail of the every level.
     */
    private List<PassDetail> mPassDetailList;

    private int itemHeadSrc = R.drawable.ic_w_head;

    private Bitmap headBitmap;

    private String mItemTitle = "";
    private int mItemAddNum;//青少版B课 需要增加的课程数量


    public CircleGameStageView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public CircleGameStageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public CircleGameStageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_UP:
                float x = event.getX();
                float y = event.getY();
                //item 点击事件
                for (Map.Entry<String, String> entry : itemCoordinates.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    String coordinateStart[] = value.split("&")[0].split(",");
                    String coordinateEnd[] = value.split("&")[1].split(",");
                    if ((Integer.parseInt(key)) <= totalColumns) {
                        if (Float.parseFloat(coordinateStart[0]) <= x && x <= Float.parseFloat(coordinateEnd[0]) && Float.parseFloat(coordinateStart[1]) <= y && y <= Float.parseFloat(coordinateEnd[1])) {
                            if (onGameItemClickListener != null) {
                                onGameItemClickListener.onGameItemClick((Integer.parseInt(key)), nowPosition);
                            }
                        }
                    }
                }
                //头像点击事件
                for (Map.Entry<String, String> entry : headCoordinates.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    String[] coordinateStart = value.split("&")[0].split(",");
                    String[] coordinateEnd = value.split("&")[1].split(",");if (Float.parseFloat(coordinateStart[0]) <= x && x <= Float.parseFloat(coordinateEnd[0]) && Float.parseFloat(coordinateStart[1]) <= y && y <= Float.parseFloat(coordinateEnd[1])) {
                        if (onGameHeadClickListener != null) {
                            onGameHeadClickListener.onGameHeadClick((Integer.parseInt(key)), (Integer.parseInt(key)) > nowPosition);
                        }
                    }
                }
                break;
        }
        return true;
    }

    /**
     * Initializes the coordinate record and control properties.
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        itemCoordinates = new HashMap<>();
        headCoordinates = new HashMap<>();
        if (attrs != null) {
            TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.GameStageView, defStyleAttr, 0);
            itemWidth = (int) array.getDimension(R.styleable.GameStageView_itemWidth, itemWidth);
            itemHeight = (int) array.getDimension(R.styleable.GameStageView_itemHeight, itemHeight);

            dividerHorizontalWidth = (int) array.getDimension(R.styleable.GameStageView_itemDividerHorizontalWidth, dividerHorizontalWidth);
            dividerHorizontalHeight = (int) array.getDimension(R.styleable.GameStageView_itemDividerHorizontalHeight, dividerHorizontalHeight);
            dividerVerticalWidth = (int) array.getDimension(R.styleable.GameStageView_itemDividerVerticalWidth, dividerVerticalWidth);
            dividerVerticalHeight = (int) array.getDimension(R.styleable.GameStageView_itemDividerVerticalHeight, dividerVerticalHeight);

            totalColumns = array.getInt(R.styleable.GameStageView_totalColumns, totalColumns);
            lineColumns = array.getInt(R.styleable.GameStageView_lineColumns, lineColumns);
            nowPosition = array.getInt(R.styleable.GameStageView_nowPosition, nowPosition);

            textSize = (int) array.getDimension(R.styleable.GameStageView_itemTextSize, textSize);
            textNormalColor = array.getColor(R.styleable.GameStageView_itemNormalTextColor, textNormalColor);
            textLockColor = array.getColor(R.styleable.GameStageView_itemLockTextColor, textLockColor);
            itemNormalBackgroundColor = array.getColor(R.styleable.GameStageView_itemNormalBgColor, itemNormalBackgroundColor);
            itemLockBackgroundColor = array.getColor(R.styleable.GameStageView_itemLockBgColor, itemLockBackgroundColor);
            paddingLeft = (int) array.getDimension(R.styleable.GameStageView_InnerPaddingLeft, paddingLeft);
            paddingTop = (int) array.getDimension(R.styleable.GameStageView_InnerPaddingTop, paddingTop);
            paddingRight = (int) array.getDimension(R.styleable.GameStageView_InnerPaddingRight, paddingRight);
            headSize = (int) array.getDimension(R.styleable.GameStageView_itemHeadSize, headSize);
            headStrokeColor = array.getColor(R.styleable.GameStageView_itemHeadStrokeColor, headStrokeColor);
            headStrokeWidth = (int) array.getDimension(R.styleable.GameStageView_itemHeadStrokeWidth, headStrokeWidth);
            triangleColor = array.getColor(R.styleable.GameStageView_itemHeadArrowColor, triangleColor);
            headArrowPaddingTop = (int) array.getDimension(R.styleable.GameStageView_itemHeadArrowTopPadding, headArrowPaddingTop);
            itemHeadSrc = array.getResourceId(R.styleable.GameStageView_itemHeadSrc, itemHeadSrc);
            array.recycle();
        }
    }

    public int getCurrHeight(int currPosition) {
        return (itemHeight + dividerVerticalHeight) * ((currPosition - 1) / 3 + 1) - dividerVerticalHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;
        fullRow = totalColumns / lineColumns;
        modNum = totalColumns % lineColumns;
        //总的行数
        totalRow = fullRow + (modNum == 0 ? 0 : 1);
        //中间空余部分的宽度
        dividerHorizontalWidth = (getScreenWidth() - paddingLeft - paddingRight - itemWidth * lineColumns) / (lineColumns - 1);
        for (int row = 1; row <= totalRow; row++) {
            //逐行绘制
            nowRow = row;
            //纵向间距
            itemVerticalMove = (row - 1) * (dividerVerticalHeight + itemHeight);
            //计算出这一行绘制几列
            int nowLineColumns = lineColumns;
            if (row <= fullRow) {
                nowLineColumns = lineColumns;
            } else {
                if (modNum != 0) {
                    nowLineColumns = modNum;
                }
            }

            if (row % 2 != 0) {
                //奇数行 绘制方法
                for (int line = 1; line <= nowLineColumns; line++) {
                    linePosition = line;
                    itemCountRecord = (row - 1) * lineColumns + line;
                    itemHorizontalMove = (line - 1) * (itemWidth + dividerHorizontalWidth);
                    int lessonCurrentCount = itemCountRecord + mItemAddNum;
                    drawItems(lessonCurrentCount);
                }
            } else {
                //偶数行 绘制方法
                nowLineColumns = nowLineColumns == lineColumns ? 1 : nowLineColumns;
                for (int line = lineColumns; line >= nowLineColumns; line--) {
                    linePosition = line;
                    itemCountRecord = (nowRow * lineColumns - line + 1);
                    itemHorizontalMove = (lineColumns - (lineColumns - line) - 1) * (itemWidth + dividerHorizontalWidth);
                    int lessonCurrentCount = nowRow * lineColumns - line + 1 + mItemAddNum;
                    drawItems(lessonCurrentCount);
                }
            }
        }
    }

    public void setPassDetailList(List<PassDetail> list) {
        mPassDetailList = list;
    }

    public void setTitle(String title) {
        mItemTitle = title;
    }

    public void setAddLessonNum(int num) {
        mItemAddNum = num;
    }

    /**
     * Let me draw all the items
     *
     *
     */
    private void drawItems(int lessonCurrentCount) {
        String text;
        if ((lessonCurrentCount- mItemAddNum) <= mPassDetailList.size()) {
            int right=mPassDetailList.get(lessonCurrentCount-mItemAddNum - 1).rightCount;
            int all=mPassDetailList.get(lessonCurrentCount -mItemAddNum- 1).allCount;
            if (right>all){
                right=all;
            }
//            text = mItemTitle + "第" + lessonCurrentCount + "关:" + right + "/" + all;
            text = "lesson\t"+lessonCurrentCount+"\n"+right+"/"+all;
        } else {
//            text = mItemTitle + "第" + lessonCurrentCount + "关";
            text = "lesson\t"+lessonCurrentCount;
        }
        drawHorizontalDivider(canvas);
        drawVerticalDivider(canvas);
        drawRoundRect(canvas);
        drawRoundRectText(canvas, text);
        drawHeads(canvas);
    }

    /**
     * Draw the rounded rectangle item.
     *
     * @param canvas
     */
    private void drawRoundRect(Canvas canvas) {
        paint = new Paint();
        paint.setColor(itemCountRecord <= nowPosition ? itemNormalBackgroundColor : itemLockBackgroundColor);
        RectF rectF = new RectF();
        rectF.left = paddingLeft + itemHorizontalMove;
        rectF.right = rectF.left + itemWidth;
        rectF.top = paddingTop + itemVerticalMove;
        rectF.bottom = (float) (rectF.top + itemHeight*1.5);
//        canvas.drawRoundRect(rectF, 16, 16, paint);

        //item坐标记录
//        itemCoordinates.put(itemCountRecord + "", rectF.left + "," + rectF.top + "&" + rectF.right + "," + rectF.bottom);

        //圆形
        float cx = (rectF.left+rectF.right)/2;
        float cy = (rectF.top+rectF.bottom)/2;
        float radius = (rectF.right-rectF.left)/3;
        canvas.drawCircle(cx,cy,radius,paint);

        itemCoordinates.put(itemCountRecord + "", (cx-radius) + "," + (cy-radius) + "&" + (cx+radius) + "," + (cy+radius));
    }

    /**
     * Draw the round corner font item.
     *
     * @param canvas
     * @param text
     */
    private void drawRoundRectText(Canvas canvas, String text) {
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(itemCountRecord <= nowPosition ? textNormalColor : textLockColor);
        textPaint.setTextSize(textSize);

        Rect rect = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), rect);
        int textWidth = rect.width();
        int textHeight = rect.height();

        if (text.contains("\n")){
            String[] arrayText = text.split("\n");
            String text1 = arrayText[0];
            String text2 = arrayText[1];

            Rect rect1 = new Rect();
            textPaint.getTextBounds(text1, 0, text1.length(), rect1);
            int text1Width = rect1.width();
            int text1Height = rect1.height();

            Rect rect2 = new Rect();
            textPaint.getTextBounds(text2, 0, text2.length(), rect2);
            int text2Width = rect2.width();
            int text2Height = rect2.height();

            canvas.drawText(text1, paddingLeft + itemHorizontalMove + itemWidth / 2 - text1Width / 2, paddingTop + itemVerticalMove + ((itemHeight >> 1) + (text1Height >> 1)), textPaint);
            canvas.drawText(text2, paddingLeft + itemHorizontalMove + itemWidth / 2 - text2Width / 2, paddingTop + itemVerticalMove + ((itemHeight >> 1) + (text2Height >> 1))+50, textPaint);
        }else {
            canvas.drawText(text, paddingLeft + itemHorizontalMove + itemWidth / 2 - textWidth / 2, paddingTop + itemVerticalMove + ((itemHeight >> 1) + (textHeight >> 1))+25, textPaint);
        }

//        textPaint.setTextAlign(Paint.Align.LEFT);
//        StaticLayout staticLayout = new StaticLayout("因为它并不是通过选用更", textPaint, canvas.getWidth(), Layout.Alignment.ALIGN_LEFT, 1, 0, false);
//
//
//        canvas.save();
//        canvas.translate(0,0);
//        staticLayout.draw(canvas);
//        canvas.restore();

    }


    /**
     * Draw the horizontal divider
     *
     * @param canvas
     */
    private void drawHorizontalDivider(Canvas canvas) {
        //每行最后一个不再需要在右边的分割线
        if (linePosition % lineColumns == 0 || (nowRow == totalRow && linePosition == modNum && nowRow % 2 != 0)) {
            return;
        }
        paint = new Paint();
        paint.setColor(itemCountRecord <= nowPosition ? itemNormalBackgroundColor : itemLockBackgroundColor);
        Rect rect = new Rect();
        rect.left = paddingLeft + itemWidth + itemHorizontalMove - 100;
        rect.right = rect.left + dividerHorizontalWidth+250;
        rect.top = paddingTop + itemHeight / 2 - dividerHorizontalHeight / 2 + itemVerticalMove;
        rect.bottom = rect.top + dividerHorizontalHeight;
        canvas.drawRect(rect, paint);
    }

    /**
     * Draw the vertical divider
     *
     * @param canvas
     */
    private void drawVerticalDivider(Canvas canvas) {
        //基数行最后一个Item显示,偶数行第一个Item显示,数据的最后一个不显示
        if ((nowRow % 2 == 0 && linePosition == 1) || (nowRow % 2 != 0 && linePosition == lineColumns)) {
            if (itemCountRecord >= totalColumns) {
                return;
            }
            paint = new Paint();
            paint.setColor(itemCountRecord <= nowPosition ? itemNormalBackgroundColor : itemLockBackgroundColor);
            Rect rect = new Rect();
            rect.left = paddingLeft + itemWidth / 2 - dividerHorizontalHeight / 2 + itemHorizontalMove;
            rect.right = rect.left + dividerVerticalWidth;
            rect.top = paddingTop + itemHeight + itemVerticalMove;
            rect.bottom = rect.top + dividerVerticalHeight;
            canvas.drawRect(rect, paint);
        }
    }

    /**
     * Draw the round picture in the head view,
     * the round border of the picture and the inverted triangle.
     *
     * @param canvas
     */
    private void drawHeads(Canvas canvas) {
        if (itemCountRecord != nowPosition) {
            return;
        }
        int headXmove = paddingLeft + headSize / 2 + itemHorizontalMove;
        int headYmove = paddingTop - headSize - headSize / 5 + itemVerticalMove;
        //记录头像坐标范围
        headCoordinates.put(itemCountRecord + "", (headXmove - headSize / 2) + "," + (headYmove - headSize / 2) + "&" + (headXmove + headSize) + "," + (headYmove + headSize));
        paint = new Paint();
        //圆形头像
        Bitmap output = drawCircleHead();
        //画生成的图片
        canvas.drawBitmap(output, headXmove, headYmove, paint);
        //三角形
        drawTriangle(canvas, headXmove, headYmove);
        //边框
        drawCircleStroke(canvas, headXmove, headYmove);
    }

    /**
     * Generate round images
     *
     * @return round image bitmap
     */
    private Bitmap drawCircleHead() {
        paint = new Paint();
        Bitmap output = Bitmap.createBitmap(headSize, headSize, Bitmap.Config.ARGB_8888);
        Canvas headCanvas = new Canvas(output);
        paint.setAntiAlias(true);
        headCanvas.drawARGB(0, 0, 0, 0);
        paint.setColor(headCircleColor);
        //圆圈
        headCanvas.drawCircle(headSize / 2, headSize / 2, headSize / 2, paint);
        //设置重叠模式
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //矩形范围
        Bitmap bitmap = headBitmap != null ? headBitmap : BitmapFactory.decodeResource(getResources(), itemHeadSrc);
        Rect rectDes = new Rect(0, 0, headSize, headSize);
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        headCanvas.drawBitmap(bitmap, rect, rectDes, paint);
        paint.reset();
        //重置画笔
        return output;
    }

    /**
     * Draw the triangle
     *
     * @param canvas
     * @param headXmove head of item x move
     * @param headYmove item of item y move
     */
    private void drawTriangle(Canvas canvas, int headXmove, int headYmove) {
        Paint paint = new Paint();
        paint.setColor(triangleColor);
        paint.setAntiAlias(true);
        Path path = new Path();
        path.moveTo(headSize / 5 * 2 + headXmove, headSize + headArrowPaddingTop + headYmove);// 此点为多边形的起点
        path.lineTo(headSize / 5 * 3 + headXmove, headSize + headArrowPaddingTop + headYmove);// 此点为多边形的起点
        path.lineTo(headSize / 10 * 5 + headXmove, headSize + headSize / 5 + headArrowPaddingTop + headYmove);
        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, paint);
    }

    /**
     * Round head edge.
     *
     * @param canvas
     * @param headXmove head of item x move
     * @param headYmove item of item y move
     */
    private void drawCircleStroke(Canvas canvas, int headXmove, int headYmove) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(headStrokeWidth);
        paint.setColor(headStrokeColor);
        canvas.drawCircle(headSize / 2 + headXmove, headSize / 2 + headYmove, headSize / 2, paint);
        paint.reset();
    }

    /**
     * Get the picture width and height.
     *
     * @param resId
     * @return
     */
    private float[] bitmapWidthHeight(int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), resId, options);
        float width = options.outWidth;
        float height = options.outHeight;
        //设置为false,解析Bitmap对象加入到内存中
        options.inJustDecodeBounds = false;
        return new float[]{width, height};
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), (itemHeight + dividerVerticalHeight) * (totalColumns / 3 + 2));

    }

    /**
     * The screen of width.
     *
     * @return
     */
    public static int getScreenWidth() {
        /**
         * 适配某些奇葩手机 屏幕旋转之后的抽风情况
         */
        if(Resources.getSystem().getDisplayMetrics().widthPixels <
                Resources.getSystem().getDisplayMetrics().heightPixels){
            return Resources.getSystem().getDisplayMetrics().widthPixels;
        }else{
            return Resources.getSystem().getDisplayMetrics().heightPixels;
        }
    }

    /**
     * The screen of height.
     *
     * @return
     */
    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public void setItemNormalBackgroundColor(int itemNormalBackgroundColor) {
        this.itemNormalBackgroundColor = itemNormalBackgroundColor;
    }

    public void setItemLockBackgroundColor(int itemLockBackgroundColor) {
        this.itemLockBackgroundColor = itemLockBackgroundColor;
    }

    public void setTriangleColor(int triangleColor) {
        this.triangleColor = triangleColor;
    }

    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public void setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
    }

    public void setPaddingRight(int paddingRight) {
        this.paddingRight = paddingRight;
    }

    public void setItemHeight(int itemHeight) {
        this.itemHeight = itemHeight;
    }

    public void setItemWidth(int itemWidth) {
        this.itemWidth = itemWidth;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public void setTextNormalColor(int textNormalColor) {
        this.textNormalColor = textNormalColor;
    }

    public void setTextLockColor(int textLockColor) {
        this.textLockColor = textLockColor;
    }

    public void setDividerHorizontalWidth(int dividerHorizontalWidth) {
        this.dividerHorizontalWidth = dividerHorizontalWidth;
    }

    public void setDividerHorizontalHeight(int dividerHorizontalHeight) {
        this.dividerHorizontalHeight = dividerHorizontalHeight;
    }

    public void setDividerVerticalWidth(int dividerVerticalWidth) {
        this.dividerVerticalWidth = dividerVerticalWidth;
    }

    public void setDividerVerticalHeight(int dividerVerticalHeight) {
        this.dividerVerticalHeight = dividerVerticalHeight;
    }

    public void setLineColumns(int lineColumns) {
        this.lineColumns = lineColumns;
    }

    public void setTotalColumns(int totalColumns) {
        this.totalColumns = totalColumns;
    }

    public void setHeadCircleColor(int headCircleColor) {
        this.headCircleColor = headCircleColor;
    }

    public void setHeadSize(int headSize) {
        this.headSize = headSize;
    }

    public void setHeadArrowPaddingTop(int headArrowPaddingTop) {
        this.headArrowPaddingTop = headArrowPaddingTop;
    }

    public void setNowPosition(int nowPosition) {
        this.nowPosition = nowPosition;
    }

    public void setHeadStrokeColor(int headStrokeColor) {
        this.headStrokeColor = headStrokeColor;
    }

    public void setHeadStrokeWidth(int headStrokeWidth) {
        this.headStrokeWidth = headStrokeWidth;
    }

    public void setItemHeadSrc(int itemHeadSrc) {
        this.itemHeadSrc = itemHeadSrc;
    }

    public void setHeadBitmap(Bitmap headBitmap) {
        this.headBitmap = headBitmap;
    }

    /**
     * Px to dp
     *
     * @param px
     * @return
     */
    public static float pxToDp(float px) {
        return px / getScreenDensity();
    }

    /**
     * dp to px
     *
     * @param dp
     * @return
     */
    public static float dpToPx(float dp) {
        return dp * getScreenDensity();
    }


    /**
     * Get the screen of density
     *
     * @return
     */
    public static float getScreenDensity() {
        return Resources.getSystem().getDisplayMetrics().density;
    }

    public OnGameHeadClickListener onGameHeadClickListener;


    /**
     * Head click on the monitor event.
     */
    public interface OnGameHeadClickListener {
        void onGameHeadClick(int position, boolean isLock);
    }

    public OnGameItemClickListener onGameItemClickListener;

    /**
     * Set item to listen for events.
     *
     * @param onGameItemClickListener
     */
    public void setOnGameItemClickListener(OnGameItemClickListener onGameItemClickListener) {
        this.onGameItemClickListener = onGameItemClickListener;
    }

    /**
     * Item click on the listener event.
     */
    public interface OnGameItemClickListener {
        void onGameItemClick(int position, int max);
    }


}

