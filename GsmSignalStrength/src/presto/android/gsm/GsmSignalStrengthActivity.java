package presto.android.gsm;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class GsmSignalStrengthActivity extends Activity {
	/*
	 * This variables need to be global, so we can used them onResume and
	 * onPause method to stop the listener
	 */
	TelephonyManager Tel;
	MyPhoneStateListener MyListener;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		MyListener = new MyPhoneStateListener();
		Tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		Tel.listen(MyListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Tel.listen(MyListener, PhoneStateListener.LISTEN_NONE);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Tel.listen(MyListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
	}
	
	private class MyPhoneStateListener extends PhoneStateListener {
		@Override
		public void onSignalStrengthsChanged(SignalStrength sigStrength) {
			super.onSignalStrengthsChanged(sigStrength);
			Toast.makeText(getApplicationContext(),
				"Go to Firstdroid!!! GSM Cinr = " + String.valueOf(sigStrength.getGsmSignalStrength()),
				Toast.LENGTH_SHORT).show();
		}
	}
}