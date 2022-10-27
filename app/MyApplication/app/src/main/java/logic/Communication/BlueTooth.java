package logic.Communication;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BlueTooth extends AppCompatActivity {
	Context context;

	boolean isConneted = false;

	//public ConnectedBluetoothThread mThreadConnectedBluetooth;
	Button mBtnConnect;
	Button mBtnSendData;

	BluetoothAdapter mBluetoothAdapter;
	Set<BluetoothDevice> mPairedDevices;
	List<String> mListPairedDevices;

	Button MainButton;

	Handler mBluetoothHandler;
	BlueTooth.ConnectedBluetoothThread mThreadConnectedBluetooth;
	BluetoothDevice mBluetoothDevice;
	BluetoothSocket mBluetoothSocket;

	final static int BT_REQUEST_ENABLE = 1;
	final static int BT_MESSAGE_READ = 2;
	final static int BT_CONNECTING_STATUS = 3;
	final static UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	public BlueTooth(Context context, BluetoothAdapter mBluetoothAdapter, Handler mBluetoothHandler) {
		this.context = context;
		this.mBluetoothAdapter = mBluetoothAdapter;
		this.mBluetoothHandler = mBluetoothHandler;
	}


	public void sendData(byte[] bytes){
		if(mThreadConnectedBluetooth != null) {
			System.out.println("블루투스 전송 시작");
			mThreadConnectedBluetooth.write(bytes);
		}
	}

	public void listPairedDevices() {
		if (mBluetoothAdapter.isEnabled()) {
			mPairedDevices = mBluetoothAdapter.getBondedDevices();

			if (mPairedDevices.size() > 0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("장치 선택");

				mListPairedDevices = new ArrayList<String>();
				for (BluetoothDevice device : mPairedDevices) {
					mListPairedDevices.add(device.getName());
					//mListPairedDevices.add(device.getName() + "\n" + device.getAddress());
				}
				final CharSequence[] items = mListPairedDevices.toArray(new CharSequence[mListPairedDevices.size()]);
				mListPairedDevices.toArray(new CharSequence[mListPairedDevices.size()]);

				builder.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int item) {
						connectSelectedDevice(items[item].toString());
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			} else {
				Toast.makeText(getApplicationContext(), "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();
			}
		}
		else {
			Toast.makeText(getApplicationContext(), "블루투스가 비활성화 되어 있습니다.", Toast.LENGTH_SHORT).show();
		}
	}
	void connectSelectedDevice(String selectedDeviceName) {
		for(BluetoothDevice tempDevice : mPairedDevices) {
			if (selectedDeviceName.equals(tempDevice.getName())) {
				mBluetoothDevice = tempDevice;
				break;
			}
		}
		try {
			mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(BT_UUID);
			mBluetoothSocket.connect();
			mThreadConnectedBluetooth = new BlueTooth.ConnectedBluetoothThread(mBluetoothSocket);
			mThreadConnectedBluetooth.start();
			mBluetoothHandler.obtainMessage(BT_CONNECTING_STATUS, 1, -1).sendToTarget();
			isConneted = true;
		} catch (IOException e) {
			Toast.makeText(context, "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
		}
	}

	public boolean isConneted(){
		return isConneted;
	}

	private class ConnectedBluetoothThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public ConnectedBluetoothThread(BluetoothSocket socket) {
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
				Toast.makeText(getApplicationContext(), "소켓 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run() {
			byte[] buffer = new byte[1024];
			int bytes;

			while (true) {
				try {
					bytes = mmInStream.available();
					if (bytes != 0) {
						SystemClock.sleep(100);
						bytes = mmInStream.available();
						bytes = mmInStream.read(buffer, 0, bytes);
						mBluetoothHandler.obtainMessage(BT_MESSAGE_READ, bytes, -1, buffer).sendToTarget();
					}
				} catch (IOException e) {
					break;
				}
			}
		}
		public void write(byte[] bytes) {
			try {
				mmOutStream.write(bytes);
			} catch (IOException e) {
				Toast.makeText(getApplicationContext(), "데이터 전송 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
			}
		}
		public void cancel() {
			try {
				mmSocket.close();
				isConneted = false;
			} catch (IOException e) {
				Toast.makeText(getApplicationContext(), "소켓 해제 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
			}
		}
	}
}