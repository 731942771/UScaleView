package com.cuiweiyou.fragment;

/**
 * <b>类名</b>: RecordFragment<br/>
 * <b>说明</b>:  <br/>
 * <b>创建</b>: 2016-1-13_上午9:53:24 <br/>
 * 
 * @version 1 <br/>
 */
public class RecordFragment extends Fragment implements OnClickListener {

	/** 本fmg界面 */
	private View view;
	/** 自定义刻度控件 */
	private UScaleView mUSV;
	
	/** 消息处理器，用于本fmg上控件刷新 */
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			String s = msg.obj.toString();
			
			float db = 0f;
			try{
				db = Float.valueOf(s);
			}catch(Exception e){
				Toast.makeText(getActivity(), "error:" + e, 0).show();
			}

			mUSV.setDecibel(db);
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.fmg_record, container, false);
		
		mUSV = (UScaleView) view.findViewById(R.id.usv);

		return view;
	}

	@Override
	public void onClick(View v) {

	}
}
