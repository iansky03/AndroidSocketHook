package com.codelogic.sockethook.network.monitor;

import java.lang.reflect.Field;
import java.net.Socket;
import java.net.SocketImpl;
import java.net.SocketImplFactory;

import android.content.Context;
import android.util.Log;

public class MonitorSocketFactory implements SocketImplFactory {

	private Context mContext;
	
	private SocketImpl mSocketImpl;
	private Class mSocketClass = null;

	public MonitorSocketFactory(Context context) {
		try {
			mContext = context;
			Socket socket = new Socket();

			Field implField = Socket.class.getDeclaredField("impl");
			implField.setAccessible(true);

			mSocketImpl = ((SocketImpl) implField.get(socket));
			mSocketClass = mSocketImpl.getClass();
		} catch (Exception e) {
			Log.e("MonitorSocket", "get SocketImpl failed ", e);
		}
	}

	@Override
	public SocketImpl createSocketImpl() {
		return new MonitorSocketImpl(this.mContext, this.mSocketClass);
	}

}
