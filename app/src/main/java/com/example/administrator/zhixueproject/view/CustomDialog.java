package com.example.administrator.zhixueproject.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import java.util.UUID;

/**
 * 通用的自定义布局的dialog
 *
 */
public class CustomDialog extends Dialog {
	private int mLayoutId = 0;

	public CustomDialog(Context context) {
		super(context);
	}

	public CustomDialog(Context context, int theme, int layoutId) {
		super(context, theme);
		mLayoutId = layoutId;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(mLayoutId);
	}

	public static String getUniquePsuedoID() {
		String serial = null;
		String m_szDevIDShort = "35" + // 35是IMEI开头的号
		Build.BOARD.length()%10+ Build.BRAND.length()%10 +
				Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 +
				Build.DISPLAY.length()%10 + Build.HOST.length()%10 +
				Build.ID.length()%10 + Build.MANUFACTURER.length()%10 +
				Build.MODEL.length()%10 + Build.PRODUCT.length()%10 +
				Build.TAGS.length()%10 + Build.TYPE.length()%10 +
				Build.USER.length()%10 ; //13 位
		 try {       serial = Build.class.getField("SERIAL").get(null).toString();       //API>=9 使用serial号
			  return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
		 } catch (Exception exception) {       //serial需要一个初始化
			 serial = "serial"; // 随便一个初始化   }   //使用硬件信息拼凑出来的15位号码
			 return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();

		 }
	}

}
