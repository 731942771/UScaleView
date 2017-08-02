package com.cuiweiyou.activity;

public class MainActivity extends Activity implements View.OnClickListener {

	/** 监测界面刻度控件 */
	private UPureColorScaleView mUCSV;

	/** 消息处理器  */
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			String s = msg.obj.toString();

			// 绘制监测界面刻度 每次消息传递一个dB值
			if("status".equals(s)){
				String volume = msg.getData().getString("volume"); 
				float db = 0f;
				
				try {
					db = Float.valueOf(volume);
				} catch (Exception e) {
					Toast.makeText(MainActivity.this, "error:" + e, 0).show();
				}
	
				mUCSV.setMultiColorHeitht(db, highRate, centerRate);
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initView();
	}

	/** 1.初始化 */
	private void initView() {

		vRec = View.inflate(this, R.layout.page_record, null);
		mUCSV = (UPureColorScaleView) vRec.findViewById(R.id.record_ucsv);

	}
}
