package com.aspire.demo;


import mm.purchasesdk.OnPurchaseListener;
import mm.purchasesdk.Purchase;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Demo extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
	public static final int ITEM0 = Menu.FIRST;// 系统值
	public static final int ITEM1 = 2;
	public static final int ITEM2 = 3;
	private final String TAG = "Demo";

	public static Purchase purchase;
	private Context context;

	private Button billNextButton;
	private Button billButton;
	private Button queryButton;
	private Button cleanButton;
	private Button unsubButton;
	private ProgressDialog mProgressDialog;

	private EditText mPaycodeView;
	private EditText mProductNumView;
	private IAPListener mListener;

	
//	// 计费信息
	// 计费信息 (现网环境)
	private static final String APPID = "300002734147";
	private static final String APPKEY = "ECFF004CCB8F3DE4";
	// 计费点信息
	private static final String LEASE_PAYCODE = "30000273414702";
	
	private static final int PRODUCT_NUM = 1;
	
	private boolean isNextTrue = false ;
//	30000494445901  单次计费点永久有效
//	30000494445902  多次计费点(期限为0)
//	30000494445903  多次计费点(一定期限)
//	30000494445904  包月计费点
//	30000494445905  包时长计费点 30天
	// 计费信息
//		// 计费点信息
//		private static final String LEASE_PAYCODE = "30000823539702";

		
//	String monthlyPayCode = "30000494445905" ;
	
// 计费信息
	// 计费信息 (现网环境)
//	private static final String APPID = "300002675623";
//	private static final String APPKEY = "661C7C1CCFE032D9";
////	// 计费点信息
//	private static final String LEASE_PAYCODE = "30000267562301";
//	private static final String APPID = "300001489298";
//	private static final String APPKEY = "52D38DC489D75D8A";
////	 计费点信息
//	private static final String LEASE_PAYCODE = "30000148929801";
//	private static final String APPID = "300001504949";
//	private static final String APPKEY = "E80D63DD4A182C1A";
////	 计费点信息
//	private static final String LEASE_PAYCODE = "30000150494901";
	//测试环境
//	private static final String APPID = "300001517143";
//	private static final String APPKEY = "033B4102FD9AAE3A";
//	// 计费点信息
//	private static final String LEASE_PAYCODE = "30000151714302";
//	
//	private static final String APPID = "300001489327";
//	private static final String APPKEY = "CBE3C3C0454416C1";
////	 计费点信息
//	private static final String LEASE_PAYCODE = "30000148932702";
	

	private String mPaycode;
	private int mProductNum = PRODUCT_NUM;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		setTitle(getResources().getString(R.string.app_name) + "(APPID:"
				+ APPID + ")");
		mProgressDialog = new ProgressDialog(Demo.this);
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setMessage("请稍候...");
		context = Demo.this;
		
		IAPHandler iapHandler = new IAPHandler(this);

		mPaycode = readPaycode();
		mProductNum = readProductNUM();
		/**
		 * IAP组件初始化.包括下面3步。
		 */
		/**
		 * step1.实例化PurchaseListener。实例化传入的参数与您实现PurchaseListener接口的对象有关。
		 * 例如，此Demo代码中使用IAPListener继承PurchaseListener，其构造函数需要Context实例。
		 */
		mListener = new IAPListener(this, iapHandler);
		/**
		 * step2.获取Purchase实例。
		 */
		purchase = Purchase.getInstance();
		/**
		 * step3.向Purhase传入应用信息。APPID，APPKEY。 需要传入参数APPID，APPKEY。 APPID，见开发者文档
		 * APPKEY，见开发者文档
		 */
		try {
			purchase.setAppInfo(APPID, APPKEY);

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		/**
		 * step4. IAP组件初始化开始， 参数PurchaseListener，初始化函数需传入step1时实例化的
		 * PurchaseListener。
		 */
		try {
			purchase.init(context, mListener);

		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		showProgressDialog();
		billButton = (Button) findViewById(R.id.billing);
		queryButton = (Button) findViewById(R.id.query);
		cleanButton = (Button) findViewById(R.id.clean);
		unsubButton = (Button) findViewById(R.id.unsub);
		billNextButton = (Button) findViewById(R.id.billingNext);
		billButton.setOnClickListener(this);
		queryButton.setOnClickListener(this);
		cleanButton.setOnClickListener(this);
		billNextButton.setOnClickListener(this);
		unsubButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.billingNext://续订
			try {
				purchase.order(context, mPaycode, 1,"test monthly", isNextTrue, mListener);
			} catch (Exception e1) {
				//
				e1.printStackTrace();
				return;
			}
			break;
		case R.id.billing:
			/**
			 * 商品购买接口。
			 */
			order(this, mListener);
			break;
		case R.id.query:
			/**
			 * 商品查询接口
			 */
			try {
//				12-05 16:31:56.471: I/System.out(27287): 订购结果：订购成功,OrderID ： 11131205163146414758,Paycode:30000267562301,tradeID:F77C430748F93FBB3F088C5E76E5DB6F,ORDERTYPE:0
				purchase.query(context, mPaycode,null, mListener);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			showProgressDialog();
			break;
		case R.id.clean://清除证书缓存
			try {
				purchase.clearCache(this);
			} catch (Exception e1) {
				e1.printStackTrace();
				return;
			}
			break;
		case R.id.unsub://退订
			try {
				purchase.unsubscribe(context, mPaycode, mListener);
				
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			showProgressDialog();
			break;
		default:
			break;
		}

	}

	public void order(Context context, OnPurchaseListener listener) {
		try {
//			purchase.order(context, mPaycode, mProductNum, listener);
			purchase.order(context,mPaycode , readProductNUM(),"helloworl",false,listener);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showProgressDialog() {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(Demo.this);
			mProgressDialog.setIndeterminate(true);
			mProgressDialog.setMessage("请稍候.....");
		}
		if (!mProgressDialog.isShowing()) {
			mProgressDialog.show();
		}
	}

	public void dismissProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, ITEM0, 0, "修改paycode");
		menu.add(0, ITEM1, 1, "订购数量");
		menu.add(0, ITEM2, 2, "isNextCycle");
		return true;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		
		case ITEM0:
			LayoutInflater inflater = getLayoutInflater();
			View layout = inflater.inflate(R.layout.dialog,
					(ViewGroup) findViewById(R.id.dialog));
			mPaycodeView = (EditText) layout.findViewById(R.id.paycode);
			mPaycodeView.setText(readPaycode());
			new AlertDialog.Builder(this).setTitle("商品ID").setView(layout)
					.setPositiveButton("确定", mOkListener)
					.setNegativeButton("取消", null).show();
			break;
		case ITEM1:
			LayoutInflater inflater2 = getLayoutInflater();
			View layout2 = inflater2.inflate(R.layout.dialognum,
					(ViewGroup) findViewById(R.id.dialognum));
			mProductNumView = (EditText) layout2.findViewById(R.id.num);
			mProductNumView.setText("" + readProductNUM());
			new AlertDialog.Builder(this).setTitle("订购数量").setView(layout2)
					.setPositiveButton("确定", mNumClickListener)
					.setNegativeButton("取消", null).show();
			break;
		case ITEM2:
			
			isNextTrue = ! isNextTrue ;
			Toast.makeText(context, "isNextCycle : " + isNextTrue , Toast.LENGTH_SHORT).show(); ;
			
			break;
		default:

			break;
		}
		return super.onOptionsItemSelected(item);
	}

	

	private final static String PAYCODE = "Paycode";
	private final static String PRODUCTNUM = "ProductNUM";

	private void savePaycode(String paycode) {
		Editor sharedata = getSharedPreferences("data", 0).edit();
		sharedata.putString(PAYCODE, paycode);
		sharedata.commit();
	}

	private String readPaycode() {
		SharedPreferences sharedPreferences = getSharedPreferences("data", 0);
		String paycode = sharedPreferences.getString(PAYCODE, LEASE_PAYCODE);
		return paycode;
	}

	private void saveAppid(String paycode) {
		Editor sharedata = getSharedPreferences("data", 0).edit();
		sharedata.putString("app_id", paycode);
		sharedata.commit();
	}
	
	private String readPayid() 
	{
		SharedPreferences sharedPreferences = getSharedPreferences("data", 0);
		String paycode = sharedPreferences.getString("app_id", LEASE_PAYCODE);
		return paycode;
	}
	
	private void saveProductNUM(int num) {
		Editor sharedata = getSharedPreferences("data", 0).edit();
		sharedata.putInt(PRODUCTNUM, num);
		sharedata.commit();
	}

	private int readProductNUM() {
		SharedPreferences sharedPreferences = getSharedPreferences("data", 0);
		int num = sharedPreferences.getInt(PRODUCTNUM, PRODUCT_NUM);
		return num;
	}

	private DialogInterface.OnClickListener mOkListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {

			if (mPaycodeView != null) {
				String paycode = mPaycodeView.getText().toString().trim();
				savePaycode(paycode);
				mPaycode = paycode;
			}
		}
	};

	private DialogInterface.OnClickListener mNumClickListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {

			if (mProductNumView != null) {
				String num = mProductNumView.getText().toString().trim();
				Integer integer = new Integer(num);
				System.out.println("num=" + num);
				saveProductNUM(integer.intValue());
				mProductNum = integer.intValue();
				System.out.println("productNum=" + mProductNum);
			}
		}
	};

	private void initShow(String msg) {
		Toast.makeText(context, "初始化：" + msg, Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.out.println("Demo1 resume");
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		System.out.println("Demo1 onRestart");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		System.out.println("Demo1 onPause");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		System.out.println("Demo1 onStop");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	public void showDialog(String title, String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setIcon(context.getResources().getDrawable(R.drawable.icon));
		builder.setMessage((msg == null) ? "Undefined error" : msg);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
}