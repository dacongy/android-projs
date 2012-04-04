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
	public boolean stall = false;
	
	public static final int REQUEST_ENABLE_BT = 1;
	Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	
//	public boolean bluetoothOn = false;
	
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
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        this.registerReceiver(receiver, filter);
        doDiscovery();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == REQUEST_ENABLE_BT) {
    		if (resultCode != RESULT_OK) {
    			Log.e(TAG, "Cannot enable bluetooth!");
    			onDestroy();
    		}
    		while (!btAdapter.isEnabled()) ;
    		stall = false;
    	}
    }
    
    public void doDiscovery() {    	
    	btAdapter = BluetoothAdapter.getDefaultAdapter();
    	int state = btAdapter.getState();
    	if (state == BluetoothAdapter.STATE_OFF || state == BluetoothAdapter.STATE_TURNING_OFF) {        
        	stall = true;
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            while (stall) ;            
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
    public void onPause() {
    	super.onPause();
    	if (btAdapter != null && btAdapter.isDiscovering()) {
    		btAdapter.cancelDiscovery();
    	}
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	doDiscovery();
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