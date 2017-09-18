package com.mazaiting;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import java.util.Random;

/**
 * Verify View(自定义验证码视图)
 * Created by mazaiting on 2017/9/18.
 */

public class VerifyView extends View implements View.OnClickListener {
  private static final String TAG = "VerifyView";
  /**文本内容*/
  private String mCodeText = "";
  /**文本大小*/
  private int mCodeTextSize = 0;
  /**文本长度*/
  private int mCodeLength = 4;
  /**背景色*/
  private int mCodeBackground = Color.WHITE;
  /**是否包含字母*/
  private boolean isContainChar = false;
  /**干扰点数*/
  private int mPointNum = 100;
  /**干扰线数*/
  private int mLineNum = 3;
  /**画笔*/
  private Paint mPaint = null;
  /**验证码矩形大小*/
  private Rect mBound = null;
  /**图片*/
  private Bitmap mBitmap = null;
  /**随机数工具*/
  private Random mRandom = new Random();
  /**控件的宽度*/
  private int mWidth = 400;
  /**控件的高度*/
  private int mHeight = 200;

  public VerifyView(Context context) {
    this(context, null);
  }

  public VerifyView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public VerifyView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initAttrValues(context, attrs);
    initData();
  }

  /**
   * 初始化属性集合
   * @param context 上下文
   * @param attrs 属性
   */
  private void initAttrValues(Context context, AttributeSet attrs) {
    // 获取在AttributeSet中定义的VerifyCode中声明的属性的集合
    TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VerifyView);
    // 获取TypeArray的长度
    int count = typedArray.getIndexCount();
    for (int i = 0;i < count; i++){
      // 获取此项属性的ID
      int index = typedArray.getIndex(i);
      if (index == R.styleable.VerifyView_codeTextSize) {
        // 默认设置为16sp，TypeValue类 px转sp 一个转换类
        mCodeTextSize = typedArray.getDimensionPixelSize(index, (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()
        ));
      } else if (index == R.styleable.VerifyView_codeBackground){
        // 获取背景颜色
        mCodeBackground = typedArray.getColor(index, Color.WHITE);
      } else if (index == R.styleable.VerifyView_codeLength){
        // 获取验证码长度
        mCodeLength = typedArray.getInteger(index, 4);
      } else if (index == R.styleable.VerifyView_isContainChar){
        // 获取是否包含字符
        isContainChar = typedArray.getBoolean(index, false);
      } else if (index == R.styleable.VerifyView_pointNum){
        // 获取干扰点数目
        mPointNum = typedArray.getInteger(index, 100);
      } else if (index == R.styleable.VerifyView_lineNum){
        // 获取干扰线数目
        mLineNum = typedArray.getInteger(index, 3);
      }
    }
    // 官方解释：回收TypedArray 以便后面的使用者重用
    typedArray.recycle();
  }

  /**
   * 初始化数据
   */
  private void initData() {
    mCodeText = getValidationCode(mCodeLength, isContainChar);
    mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mPaint.setAntiAlias(true); // 设置抗锯齿
    mBound = new Rect();
    // 计算文字所在矩形， 可以得到宽高
    mPaint.getTextBounds(mCodeText, 0, mCodeText.length(), mBound);
    setOnClickListener(this);
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    // 设置宽高，默认为建议的最小宽高
    int width;
    int height;
    // 获取控件宽高的显示模式
    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    // 获取宽高的尺寸值，固定值的宽度
    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    int heightSize = MeasureSpec.getSize(heightMeasureSpec);
    /**
     * MeasureSpec父布局传递给后代的布局要求 包含 确定大小和三种模式
     * EXACTLY：一般是设置了明确的值或者是MATCH_PARENT
     * AT_MOST：表示子布局限制在一个最大值内，一般为WARP_CONTENT
     * UNSPECIFIED：表示子布局想要多大就多大，很少使用
     */
    if (widthMode == MeasureSpec.EXACTLY){
      width = widthSize;
    } else {
      mPaint.setTextSize(mCodeTextSize);
      mPaint.getTextBounds(mCodeText, 0, mCodeText.length(), mBound);
      int textWidth = mBound.width();
      width = getPaddingLeft() + textWidth + getPaddingRight();
    }

    if (heightMode == MeasureSpec.EXACTLY){
      height = heightSize;
    } else {
      mPaint.setTextSize(mCodeTextSize);
      mPaint.getTextBounds(mCodeText, 0, mCodeText.length(), mBound);
      int textHeight = mBound.height();
      height = getPaddingTop() + textHeight + getPaddingBottom();
    }
    // 设置测量的宽高
    setMeasuredDimension(width, height);
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    mWidth = getWidth();
    mHeight = getHeight();

    if (null == mBitmap){
      mBitmap = createBitmapValidate();
    }
    canvas.drawBitmap(mBitmap, 0, 0, mPaint);
  }

  /**
   * 创建验证码图片
   * @return 图片
   */
  private Bitmap createBitmapValidate() {
    if (null != mBitmap && !mBitmap.isRecycled()){
      // 回收并且置为null
      mBitmap.recycle();
      mBitmap = null;
    }
    // 创建图片
    Bitmap sourceBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
    // 创建画布
    Canvas canvas = new Canvas(sourceBitmap);
    // 画上背景颜色
    canvas.drawColor(mCodeBackground);
    // 初始化文字画笔
    mPaint.setStrokeWidth(3f);
    // 设置文字大小
    mPaint.setTextSize(mCodeTextSize);
    // 测量验证码字符串显示的宽度值
    float textWidth = mPaint.measureText(mCodeText);
    // 获取验证码长度
    int length = mCodeText.length();
    // 计算一个字符所占位置
    float charLength = textWidth / length;
    // 画出验证码
    for (int i = 1; i <= length; i++){
      int offsetDegree = mRandom.nextInt(15);
      // 这里只会产生0和1， 如果是1那么正旋转， 否则旋转负角度
      offsetDegree = mRandom.nextInt(2) == 1 ? offsetDegree : -offsetDegree;
      // 用来保存Canvas的状态。save之后， 可以调用Canvas的平移、放缩、旋转、错切、裁剪等操作
      canvas.save();
      // 设置旋转
      canvas.rotate(offsetDegree, mWidth / 2, mHeight / 2);
      // 给画笔设置随机颜色
      mPaint.setARGB(255, mRandom.nextInt(200) + 20, mRandom.nextInt(200) + 20,
          mRandom.nextInt(200) + 20);
      // 设置字体的绘制位置
      canvas.drawText(String.valueOf(mCodeText.charAt(i - 1)), (i - 1) * charLength + 5,
          mHeight * 4 / 5f, mPaint);
      // 用来恢复Canvas之前保存的状态。防止save后对Canvas执行的操作对后续的绘制有影响
      canvas.restore();
    }
    // 重新设置画笔
    mPaint.setARGB(255, mRandom.nextInt(200) + 20, mRandom.nextInt(200) + 20,
        mRandom.nextInt(200) + 20);
    mPaint.setStrokeWidth(1f);
    // 产生干扰效果1 -- 干扰点
    for (int i = 0; i < mPointNum; i++){
      drawPoint(canvas, mPaint);
    }
    // 产生干扰效果2 -- 干扰线
    for (int i = 0; i < mLineNum; i++){
      drawLine(canvas, mPaint);
    }
    return sourceBitmap;
  }

  /**
   * 生成干扰线
   * @param canvas 画布
   * @param paint 画笔
   */
  private void drawLine(Canvas canvas, Paint paint) {
    int startX = mRandom.nextInt(mWidth);
    int startY = mRandom.nextInt(mHeight);
    int endX = mRandom.nextInt(mWidth);
    int endY = mRandom.nextInt(mHeight);
    canvas.drawLine(startX, startY, endX, endY, paint);
  }

  /**
   * 生成干扰点
   * @param canvas 画布
   * @param paint 画笔
   */
  private void drawPoint(Canvas canvas, Paint paint) {
    PointF pointF = new PointF(mRandom.nextInt(mWidth) + 10, mRandom.nextInt(mHeight) + 10);
    canvas.drawPoint(pointF.x, pointF.y, paint);
  }

  /**
   * 获取验证码
   * @param length 生成随机数的长度
   * @param contains 是否包含字符
   * @return 验证码
   */
  private String getValidationCode(int length, boolean contains) {
    String val = "";
    Random random = new Random();
    for (int i = 0; i < length; i++){
      if (contains){
        // 0 代表字符, 1 代表数字
        int code = random.nextInt(2);
        // 字符串
        if (0 == code){
          int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
          val += (char)(choice + random.nextInt(26));
        } else if (1 == code){
          val += String.valueOf(random.nextInt(10));
        }
      } else {
        val += String.valueOf(random.nextInt(10));
      }
    }
    return val;
  }

  /**
   * 判断验证码是否一致，忽略大小写
   * @param codeString 验证码
   * @return true 一致
   *          false 不一致
   */
  public boolean isEqualsIgnoreCase(String codeString){
    return mCodeText.equalsIgnoreCase(codeString);
  }

  /**
   * 判断验证码是否一致，不忽略大小写
   * @param codeString 验证码
   * @return true 一致
   *          false 不一致
   */
  public boolean isEquals(String codeString){
    return mCodeText.equals(codeString);
  }

  /**
   * 刷新验证码
   */
  private void refresh(){
    mCodeText = getValidationCode(mCodeLength, isContainChar);
    mBitmap = createBitmapValidate();
    invalidate();
  }

  @Override public void onClick(View view) {
    refresh();
  }
}
