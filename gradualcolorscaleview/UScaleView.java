package com.cuiweiyou.view;

/**
 * <b>类名</b>: UScaleView，刻度尺控件<br/>
 * <b>说明</b>: 首先实现垂直标尺，动态控制色柱 <br/>
 * <b>创建</b>: 2016-1-14_上午9:59:27 <br/>
 * 
 * @version 1 <br/>
 */
public class UScaleView extends View {
	
	/** 使用模式，水平刻度尺/垂直刻度尺。1:垂直，0:水平 */
	private int typeHorizontalOrVertical = 1;
	/** 展示模式，表盘/居中分割线/柱形。2:表盘(扇形)，1:线条，0:圆柱 */
	private int typeFanOrLineOrCylinder = 0;
	
	/** 刻度最大值 */
	private int valueMaxScale;
	/** 刻度最小值 */
	private int valueMinScale;
	
	/** 刻度线宽度 */
	private int sizeWidthOfLine;
	/** 默认刻度线长度 */
	private int sizeLongOfLineNormal;
	/** 节点刻度线长度 */
	private int sizeLongOfLineNode;
	/** 刻度间隔 */
	private int spaceBetweenLine;
	
	/** 默认刻度线颜色 */
	private int colorLineNormal;
	/** 节点刻度线颜色 */
	private int colorLineNode;

	/** 本控件宽 */
	private int widthOfUSV;
	/** 本控件高 */
	private int heightOfUSV;
	/** 圆柱展示模式，左中右3部分平均宽度 */
	private int widthOfModule;

	/** 节点文本字号 */
	private float sizeOfText;
	
	/** 是否接收滑动事件（仅居中分割线展示模式有效） */
	private boolean touchEnable = false;
	
	/** 是否 */
	
	/**  */
	
	/**  */
	
	/** 绘制基数 */
	private float decibel = 0;
	
	/** 画笔 */
	private Paint paint;
	
	/** 文本宽度 */
	private int textWidth;
	
	/** 内边距 */
	private int paddingLeft, paddingRight, paddingTop, paddingBottom;
	
	
	public UScaleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.e("ard", "构造方法");
		init(context, attrs);
	}
	
	/**
	 * <b>功能</b>: init，读取layout.xml中的引用配置，初始化本view <br/>
	 * <b>说明</b>:  <br/>
	 * <b>创建</b>: 2016-1-13_下午12:49:16 <br/>
	 * @version 1 <br/>
	 */
	private void init(Context context, AttributeSet attrs) {
		TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.UScaleViewAttrs);
		typeHorizontalOrVertical = attributes.getInteger(R.styleable.UScaleViewAttrs_typeHorizontalOrVertical, 0);
		typeFanOrLineOrCylinder = attributes.getInteger(R.styleable.UScaleViewAttrs_typeFanOrLineOrCylinder, 0);
		sizeWidthOfLine = attributes.getInteger(R.styleable.UScaleViewAttrs_sizeWidthOfLine, 1);
		sizeLongOfLineNormal = attributes.getInteger(R.styleable.UScaleViewAttrs_sizeLongOfLineNormal, 30);
		sizeLongOfLineNode = attributes.getInteger(R.styleable.UScaleViewAttrs_sizeLongOfLineNode, 50);
		sizeOfText = attributes.getDimension(R.styleable.UScaleViewAttrs_sizeOfText, 12);
		colorLineNormal = attributes.getColor(R.styleable.UScaleViewAttrs_colorLineNormal, Color.parseColor("#000000"));
		colorLineNode = attributes.getColor(R.styleable.UScaleViewAttrs_colorLineNode, Color.parseColor("#000000"));
		valueMaxScale = attributes.getInteger(R.styleable.UScaleViewAttrs_valueMaxScale, 100);
		valueMinScale = attributes.getInteger(R.styleable.UScaleViewAttrs_valueMinScale, 0);
		spaceBetweenLine = attributes.getInteger(R.styleable.UScaleViewAttrs_spaceBetweenLine, 30);
		touchEnable = attributes.getBoolean(R.styleable.UScaleViewAttrs_touchEnable, false);
        attributes.recycle();
        
        paint = new Paint();
		
		paint.setStrokeWidth(sizeWidthOfLine);
		paint.setColor(colorLineNormal); 
		paint.setTextSize(sizeOfText);

		TextPaint tp = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		tp.setTextSize(sizeOfText);
		textWidth = (int) Layout.getDesiredWidth("10", tp);
	}

	/** 测量本控件宽高
	 * TODO wrap_content 尚未处理（累加刻度线、文字的宽高） */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		Log.e("ard", "onMeasure");
		
	    int widthMode = MeasureSpec.getMode(widthMeasureSpec); 
	    int widthSize = MeasureSpec.getSize(widthMeasureSpec); 
	    int heightMode = MeasureSpec.getMode(heightMeasureSpec); 
	    int heightSize = MeasureSpec.getSize(heightMeasureSpec); 
	    int width;
	    int height;
	    
	    if (widthMode == MeasureSpec.EXACTLY){ 
	        width = widthSize;
	    } else { 
	        width =  (int) (getPaddingLeft() + getPaddingRight()); 
	    }
	    if (heightMode == MeasureSpec.EXACTLY) {
	        height = heightSize;
	    } else {
	        height = (int) (getPaddingTop() + getPaddingBottom());
	    }
	    
	    setMeasuredDimension(width, height);
	}
	
	/** 为控件定位
	 * 获取本view的宽高，准备一些必要数据 
	 * */
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		Log.e("ard", "onLayout");
		
		widthOfUSV = getWidth();        // 本view的宽
		heightOfUSV = getHeight();      // 本viwe的高
		widthOfModule = widthOfUSV / 3; // 

		// 刻度线的宽度调整
//		if(sizeLongOfLineNormal > widthOfModule / 4)
			sizeLongOfLineNormal = widthOfModule / 4;  // xml里配置的刻度线长度没用了
//		if(sizeLongOfLineNode > widthOfModule / 2)
			sizeLongOfLineNode = widthOfModule / 2;
		
		paddingLeft = getPaddingLeft();
		paddingRight = getPaddingRight();
		paddingTop = getPaddingTop();
		paddingBottom = getPaddingBottom();
		
		if(paddingLeft < 1)
			paddingLeft = 1;
		if(paddingRight < 1)
			paddingRight = 1;
		if(paddingTop < 1)
			paddingTop = 1;
		if(paddingBottom < 1)
			paddingBottom = 1;
	}
	
	/** 绘制界面。精髓所在
	 * TODO 目前仅实现 圆柱色柱刻度 形式
	 * postInvalidate() 或 invalidate() 方法调用后即执行此 onDraw()  */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Log.e("ard", "onDraw");

		int valueRange = valueMaxScale - valueMinScale;
		int spaceRange = heightOfUSV / valueRange;
		
		// 刻度线间隔调整
//		if(spaceBetweenLine > spaceRange)
			spaceBetweenLine = spaceRange;
		
		switch(typeFanOrLineOrCylinder){
		case 0: // 圆柱
			drawCylinderScale(canvas);
			break;
		case 1: // 线条
			drawLineScale();
			break;
		case 2: // 表盘
			drawFanScale();
			break;
		}
		
	}

	/** 0 绘制圆柱形刻度尺
	 * TODO 仅实现圆柱的垂直形式 */
	private void drawCylinderScale(Canvas canvas) {
		switch(typeHorizontalOrVertical){
		case 0:
			drawCylinderHorizontal();
			break;
		case 1:
			drawCylinderVertical(canvas);
			break;
		}
	}

	/** 线条形式 */
	private void drawLineScale() {
	}

	/** 扇形仪表盘形式 */
	private void drawFanScale() {
	}

	/** 0.1 水平圆柱刻度尺 */
	private void drawCylinderHorizontal() {
	}

	/** 0.2 垂直圆柱刻度尺 */
	private void drawCylinderVertical(Canvas canvas) {
		drawCylinderVerticalScale(canvas);
		drawCylinderVerticalRect(canvas);
	}

	/**
	 * 0.2.1
	 * <b>功能</b>: drawCylinderVerticalScale， 绘制刻度<br/>
	 * <b>说明</b>:  <br/>
	 * <b>创建</b>: 2016-1-13_下午5:14:35 <br/>
	 * @version 1 <br/>
	 */
	private void drawCylinderVerticalScale(Canvas canvas) {
		int height = heightOfUSV;
		int drawHeight = height - paddingBottom;  // 刻度线绘制位置y。x，y起点在屏幕左上角
		
		// 最大高度即本view的高度
		for (int i = valueMinScale; i <= valueMaxScale; i++) {
			
			int startX = paddingLeft;                       // 起始点x
			int startY = drawHeight;                        // 起始点y
			int stopX = paddingLeft + sizeLongOfLineNormal; // 结束点x
			int stopY = drawHeight;                         // 结束点y
			
			int startXR = widthOfUSV - widthOfModule / 4 - paddingRight; // 右侧刻度表线的x，始
			int stopXR = widthOfUSV - paddingRight;                      // 右侧刻度表线的x，末
			
			// 节点刻度处理
			if(i % 5 == 0){
				stopX = paddingLeft + sizeLongOfLineNode;       // 节点刻度线更宽
				paint.setColor(colorLineNode);                  // 节点刻度线颜色有异
				
				canvas.drawText("" + i, stopX, stopY, paint);   // 绘制左边的文本

				startXR = widthOfUSV - widthOfModule / 2 - paddingRight;
				canvas.drawText("" + i, startXR - textWidth, stopY, paint); // 绘制右边的文本
			}
		
			// 画线（startX，startY，stopX，stopY，画笔）
			canvas.drawLine(startX, startY, stopX, stopY, paint);
			canvas.drawLine(startXR, startY, stopXR, stopY, paint);
		
			drawHeight -= spaceBetweenLine;  // 刻度线间隔累计

			paint.setColor(colorLineNormal);  // 对应绘制节点刻度线的画笔色复原 
			
//			if(drawHeight < paddingTop)
//				break;
		}
	}
	
	/** 0.2.2
	 * <b>功能</b>: drawCylinderVerticalRect，绘制渐变色矩形<br/>
	 * <b>说明</b>:  <br/>
	 * <b>创建</b>: 2016-1-13_下午6:17:52 <br/>
	 * @param canvas : 本view的画布
	 * @version 1 <br/>
	 */
	private void drawCylinderVerticalRect(Canvas canvas) {
		int dec = (int)(decibel + 0.5); // 4舍5入
		// 矩形的startY
		int valueOfDB = heightOfUSV - (dec * spaceBetweenLine - valueMinScale);
		
		int height = heightOfUSV;
		
		int starX = widthOfModule;                        // 矩形左边x。忽略paddingLeft
		int widthOfCylinder = widthOfModule;
		
		// 线性渐变
LinearGradient shader = new LinearGradient(
		starX,                     // 起点x
		0,                         // 起点y
		starX,                     // 终点x
		height - paddingBottom,    // 终点y
        new int[] {                // 颜色数组，从屏幕顶部到底部
				Color.RED, 
				Color.YELLOW, 
				Color.GREEN}, 
        null,                      // 过渡位置 new float[]{0 , 0.5f, 1.0f}，长度和颜色数组相同
        Shader.TileMode.REPEAT);   // CLAMP:边界像素拉伸。REPEAT:平铺拉伸位图。MIRROR:镜像渲染。
		
		Paint paint = new Paint();
		paint.setShader(shader); // 指定渐变色填充
		
		// 定义矩形（左 上 右 下）
		RectF cylinder = new RectF(starX, paddingTop + valueOfDB, starX + widthOfCylinder, height - paddingBottom);
		canvas.drawRect(cylinder, paint);
	}

	/** 获取最大刻度 */
	public int getValueMaxScale() {
		return valueMaxScale;
	}

	/** 设置最大刻度 */
	public void setValueMaxScale(int valueMaxScale) {
		this.valueMaxScale = valueMaxScale;
		postInvalidate();
	}

	/** 获取最小刻度 */
	public int getValueMinScale() {
		return valueMinScale;
	}

	/** 设置最小刻度 */
	public void setValueMinScale(int valueMinScale) {
		this.valueMinScale = valueMinScale;
		postInvalidate();
	}

	/**  */
	public float getDecibel() {
		return decibel;
	}

	/**  */
	public void setDecibel(float decibel) {
		this.decibel = decibel;
		postInvalidate();
	}
}
