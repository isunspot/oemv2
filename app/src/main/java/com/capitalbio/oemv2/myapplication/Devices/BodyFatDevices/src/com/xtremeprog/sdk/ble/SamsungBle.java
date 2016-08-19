/**
 * This XPG software is supplied to you by Xtreme Programming Group, Inc.
 * ("XPG") in consideration of your agreement to the following terms, and your
 * use, installation, modification or redistribution of this XPG software
 * constitutes acceptance of these terms.锟�If you do not agree with these terms,
 * please do not use, install, modify or redistribute this XPG software.
 * 
 * In consideration of your agreement to abide by the following terms, and
 * subject to these terms, XPG grants you a non-exclusive license, under XPG's
 * copyrights in this original XPG software (the "XPG Software"), to use and
 * redistribute the XPG Software, in source and/or binary forms; provided that
 * if you redistribute the XPG Software, with or without modifications, you must
 * retain this notice and the following text and disclaimers in all such
 * redistributions of the XPG Software. Neither the name, trademarks, service
 * marks or logos of XPG Inc. may be used to endorse or promote products derived
 * from the XPG Software without specific prior written permission from XPG.锟� * Except as expressly stated in this notice, no other rights or licenses,
 * express or implied, are granted by XPG herein, including but not limited to
 * any patent rights that may be infringed by your derivative works or by other
 * works in which the XPG Software may be incorporated.
 * 
 * The XPG Software is provided by XPG on an "AS IS" basis.锟�XPG MAKES NO
 * WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE IMPLIED
 * WARRANTIES OF NON-INFRINGEMENT, MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, REGARDING THE XPG SOFTWARE OR ITS USE AND OPERATION ALONE OR IN
 * COMBINATION WITH YOUR PRODUCTS.
 * 
 * IN NO EVENT SHALL XPG BE LIABLE FOR ANY SPECIAL, INDIRECT, INCIDENTAL OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) ARISING IN ANY WAY OUT OF THE USE, REPRODUCTION, MODIFICATION
 * AND/OR DISTRIBUTION OF THE XPG SOFTWARE, HOWEVER CAUSED AND WHETHER UNDER
 * THEORY OF CONTRACT, TORT (INCLUDING NEGLIGENCE), STRICT LIABILITY OR
 * OTHERWISE, EVEN IF XPG HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * ABOUT XPG: Established since June 2005, Xtreme Programming Group, Inc. (XPG)
 * is a digital solutions company based in the United States and China. XPG
 * integrates cutting-edge hardware designs, mobile applications, and cloud
 * computing technologies to bring innovative products to the marketplace. XPG's
 * partners and customers include global leading corporations in semiconductor,
 * home appliances, health/wellness electronics, toys and games, and automotive
 * industries. Visit www.xtremeprog.com for more information.
 * 
 * Copyright (C) 2013 Xtreme Programming Group, Inc. All Rights Reserved.
 */

package com.capitalbio.oemv2.myapplication.Devices.BodyFatDevices.src.com.xtremeprog.sdk.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import com.samsung.android.sdk.bt.gatt.BluetoothGatt;
import com.samsung.android.sdk.bt.gatt.BluetoothGattAdapter;
import com.samsung.android.sdk.bt.gatt.BluetoothGattCallback;
import com.samsung.android.sdk.bt.gatt.BluetoothGattCharacteristic;
import com.samsung.android.sdk.bt.gatt.BluetoothGattDescriptor;
import com.samsung.android.sdk.bt.gatt.BluetoothGattService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class SamsungBle implements IBle, IBleRequestHandler,IBleExpand {

	protected static final String TAG = "blelib";

	private BluetoothAdapter mBtAdapter;
	private BodyFatDevices mDevices;
	private BluetoothGatt mBluetoothGatt;
	private boolean mScanning;
	
	//�����豸��context����
	private Context mContext;
	
	
	private final BluetoothGattCallback mGattCallbacks = new BluetoothGattCallback() {
		@Override
		public void onAppRegistered(int status) {
		}

		@Override
		public void onScanResult(BluetoothDevice device, int rssi,
				byte[] scanRecord) {
			mDevices.bleDeviceFound(device, rssi, scanRecord,
					BodyFatDevices.DEVICE_SOURCE_SCAN);
		}

		@Override
		public void onConnectionStateChange(BluetoothDevice device, int status,
				int newState) {
			if (mBluetoothGatt == null) {
				return;
			}

			if (status != BluetoothGatt.GATT_SUCCESS) {
				disconnect(device.getAddress());
				mDevices.bleGattDisConnected(device.getAddress());
				return;
			}

			if (newState == BluetoothProfile.STATE_CONNECTED) {
				mDevices.bleGattConnected(device);
				mDevices.addBleRequest(new BleRequest(
						BleRequest.RequestType.DISCOVER_SERVICE, device.getAddress()));
			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				mDevices.bleGattDisConnected(device.getAddress());
				disconnect(device.getAddress());
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothDevice device, int status) {
			String address = device.getAddress();
			if (status != BluetoothGatt.GATT_SUCCESS) {
				disconnect(address);
				mDevices.bleGattDisConnected(address);
				mDevices.requestProcessed(address,
						BleRequest.RequestType.DISCOVER_SERVICE, false);
				return;
			}
			mDevices.bleServiceDiscovered(device.getAddress());
		}

		@Override
		public void onCharacteristicRead(
				BluetoothGattCharacteristic characteristic, int status) {
			BleRequest request = mDevices.getCurrentRequest();
			String address = request.address;
			if (status != BluetoothGatt.GATT_SUCCESS) {
				mDevices.requestProcessed(address,
						BleRequest.RequestType.READ_CHARACTERISTIC, false);
				return;
			}
			mDevices.bleCharacteristicRead(address, characteristic.getUuid()
					.toString(), status, characteristic.getValue());
		}

		@Override
		public void onCharacteristicChanged(
				BluetoothGattCharacteristic characteristic) {
			Log.d(TAG, "onCharacteristicChanged");
			String address = mDevices.getNotificationAddress();
			mDevices.bleCharacteristicChanged(address, characteristic.getUuid()
					.toString(), characteristic.getValue());
		}

		@Override
		public void onCharacteristicWrite(
				BluetoothGattCharacteristic characteristic, int status) {
			BleRequest request = mDevices.getCurrentRequest();
			String address = request.address;
			if (status != BluetoothGatt.GATT_SUCCESS) {
				mDevices.requestProcessed(address,
						BleRequest.RequestType.WRITE_CHARACTERISTIC, false);
				return;
			}
			mDevices.bleCharacteristicWrite(address, characteristic.getUuid()
					.toString(), status);
		};

		@Override
		public void onDescriptorRead(BluetoothGattDescriptor descriptor,
				int status) {
			BleRequest request = mDevices.getCurrentRequest();
			String address = request.address;
			byte[] value = descriptor.getValue();
			byte[] val_set = null;
			if (request.type == BleRequest.RequestType.CHARACTERISTIC_NOTIFICATION) {
				val_set = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
			} else if (request.type == BleRequest.RequestType.CHARACTERISTIC_INDICATION) {
				val_set = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE;
			} else {
				val_set = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
			}

			if (Arrays.equals(value, val_set)) {
				if (request.type == BleRequest.RequestType.CHARACTERISTIC_NOTIFICATION) {
					mDevices.bleCharacteristicNotification(address, descriptor
							.getCharacteristic().getUuid().toString(), true,
							status);
				} else if (request.type == BleRequest.RequestType.CHARACTERISTIC_INDICATION) {
					mDevices.bleCharacteristicIndication(address, descriptor
							.getCharacteristic().getUuid().toString(), status);
				} else {
					mDevices.bleCharacteristicNotification(address, descriptor
							.getCharacteristic().getUuid().toString(), false,
							status);
				}
				return;
			}

			if (!descriptor.setValue(val_set)) {
				mDevices.requestProcessed(address, request.type, false);
			}

			mBluetoothGatt.writeDescriptor(descriptor);
		};

		@Override
		public void onDescriptorWrite(BluetoothGattDescriptor descriptor,
				int status) {
			BleRequest request = mDevices.getCurrentRequest();
			String address = request.address;
			if (request.type == BleRequest.RequestType.CHARACTERISTIC_NOTIFICATION
					|| request.type == BleRequest.RequestType.CHARACTERISTIC_INDICATION
					|| request.type == BleRequest.RequestType.CHARACTERISTIC_STOP_NOTIFICATION) {
				if (status != BluetoothGatt.GATT_SUCCESS) {
					mDevices.requestProcessed(address, request.type, false);
					return;
				}

				if (request.type == BleRequest.RequestType.CHARACTERISTIC_NOTIFICATION) {
					mDevices.bleCharacteristicNotification(address, descriptor
							.getCharacteristic().getUuid().toString(), true,
							status);
				} else if (request.type == BleRequest.RequestType.CHARACTERISTIC_INDICATION) {
					mDevices.bleCharacteristicIndication(address, descriptor
							.getCharacteristic().getUuid().toString(), status);
				} else {
					mDevices.bleCharacteristicNotification(address, descriptor
							.getCharacteristic().getUuid().toString(), false,
							status);
				}
				return;
			}

		};
	};

	private final BluetoothProfile.ServiceListener mProfileServiceListener = new BluetoothProfile.ServiceListener() {
		@Override
		public void onServiceConnected(int profile, BluetoothProfile proxy) {
			mBluetoothGatt = (BluetoothGatt) proxy;
			mBluetoothGatt.registerApp(mGattCallbacks);
		}

		@Override
		public void onServiceDisconnected(int profile) {
			mBluetoothGatt = null;
		}
	};

	public SamsungBle(BodyFatDevices devices) {
		mDevices = devices;
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBtAdapter == null) {
			mDevices.bleNoBtAdapter();
			return;
		}
		BluetoothGattAdapter.getProfileProxy(mContext, mProfileServiceListener,
				BluetoothGattAdapter.GATT);
	}

	@Override
	public void startScan() {
		if (mScanning) {
			return;
		}

		if (mBluetoothGatt == null) {
			mScanning = false;
			return;
		}

		mScanning = true;
		mBluetoothGatt.startScan();
	}
	
	@Override
	public void startScan(UUID uuid) {
		if (mScanning) {
			return;
		}

		if (mBluetoothGatt == null) {
			mScanning = false;
			return;
		}

		mScanning = true;
		mBluetoothGatt.startScan(new UUID[]{uuid});
	}

	@Override
	public void stopScan() {
		if (!mScanning || mBluetoothGatt == null) {
			return;
		}

		mScanning = false;
		mBluetoothGatt.stopScan();
	}

	@Override
	public boolean adapterEnabled() {
		if (mBtAdapter != null) {
			return mBtAdapter.isEnabled();
		}
		return false;
	}

	@Override
	public boolean connect(String address) {
		BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
		return mBluetoothGatt.connect(device, false);
	}

	@Override
	public void disconnect(String address) {
		BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
		mBluetoothGatt.cancelConnection(device);
	}

	@Override
	public ArrayList<BleGattService> getServices(String address) {
		ArrayList<BleGattService> list = new ArrayList<BleGattService>();
		BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
		List<BluetoothGattService> services = mBluetoothGatt
				.getServices(device);
		for (BluetoothGattService s : services) {
			list.add(new BleGattService(s));
		}
		return list;
	}

	@Override
	public boolean requestReadCharacteristic(String address,
			BleGattCharacteristic characteristic) {
		mDevices.addBleRequest(new BleRequest(BleRequest.RequestType.READ_CHARACTERISTIC,
				address, characteristic));
		return true;
	}

	@Override
	public boolean discoverServices(String address) {
		return mBluetoothGatt.discoverServices(mBtAdapter
				.getRemoteDevice(address));
	}

	@Override
	public boolean readCharacteristic(String address,
			BleGattCharacteristic characteristic) {
		return mBluetoothGatt.readCharacteristic(characteristic
				.getGattCharacteristicS());
	}

	@Override
	public BleGattService getService(String address, UUID uuid) {
		BluetoothGattService service = mBluetoothGatt.getService(
				mBtAdapter.getRemoteDevice(address), uuid);
		if (service == null) {
			return null;
		} else {
			return new BleGattService(service);
		}
	}

	@Override
	public boolean requestCharacteristicNotification(String address,
			BleGattCharacteristic characteristic) {
		mDevices.addBleRequest(new BleRequest(
				BleRequest.RequestType.CHARACTERISTIC_NOTIFICATION, address,
				characteristic));
		return true;
	}

	@Override
	public boolean characteristicNotification(String address,
			BleGattCharacteristic characteristic) {
		BluetoothGattCharacteristic c = characteristic.getGattCharacteristicS();

		if (!mBluetoothGatt.setCharacteristicNotification(c, true)) {
			return false;
		}

		BluetoothGattDescriptor descriptor = c
				.getDescriptor(BodyFatDevices.DESC_CCC);
		if (descriptor == null) {
			return false;
		}

		return mBluetoothGatt.readDescriptor(descriptor);
	}

	@Override
	public boolean requestWriteCharacteristic(String address,
			BleGattCharacteristic characteristic, String remark) {
		mDevices.addBleRequest(new BleRequest(BleRequest.RequestType.WRITE_CHARACTERISTIC,
				address, characteristic));
		return true;
	}

	@Override
	public boolean writeCharacteristic(String address,
			BleGattCharacteristic characteristic) {
		return mBluetoothGatt.writeCharacteristic(characteristic
				.getGattCharacteristicS());
	}

	@Override
	public boolean requestConnect(String address) {
		mDevices.addBleRequest(new BleRequest(BleRequest.RequestType.CONNECT_GATT, address));
		return true;
	}

	@Override
	public String getBTAdapterMacAddr() {
		if (mBtAdapter != null) {
			return mBtAdapter.getAddress();
		}
		return null;
	}

	@Override
	public boolean requestIndication(String address,
			BleGattCharacteristic characteristic) {
		mDevices.addBleRequest(new BleRequest(
				BleRequest.RequestType.CHARACTERISTIC_INDICATION, address, characteristic));
		return true;
	}

	@Override
	public boolean requestStopNotification(String address,
			BleGattCharacteristic characteristic) {
		mDevices.addBleRequest(new BleRequest(
				BleRequest.RequestType.CHARACTERISTIC_STOP_NOTIFICATION, address,
				characteristic));
		return true;
	}
}
