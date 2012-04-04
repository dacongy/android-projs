package presto.android.bt;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

public class BluetoothExploreActivity extends Activity {
	public static final String TAG = "BT_EXP";
	public BluetoothAdapter btAdapter;
	public BroadcastReceiver receiver;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				
				if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent)) {
					Log.i(TAG, "ACTION_STATE_CHANGED");
					return;
				}

	            // When discovery finds a device
	            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	            	// Get the BluetoothDevice object from the Intent
	                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	                // If it's already paired, skip it, because it's been listed already
	                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
	                    Log.i(TAG, "FOUND: " + device.getName() + "@" + device.getAddress());
	                }
	                Bundle b = intent.getExtras();
	                Object[] lstName = b.keySet().toArray();
	                for (Object o : lstName) {
	                	String key = o.toString();
	                	Log.i(TAG, key + " -> " + String.valueOf(b.get(key)));
	                }
	            }
				
			}
        	
        };
        
        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(receiver, filter);
        
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!btAdapter.isEnabled()) {
        	btAdapter.enable();
        }
        Log.i(TAG, "SELF: " + btAdapter.getName() + "@" + btAdapter.getAddress());
        
        if (btAdapter.isDiscovering()) {
        	btAdapter.cancelDiscovery();
        }
        boolean ret = btAdapter.startDiscovery();
        if (!ret) {
        	Log.e(TAG, "discovery failed!");        	
        	return;
        }
        
        
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (btAdapter != null) {
            btAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(receiver);
    }
}