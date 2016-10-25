package com.codelogic.sockethook.network.monitor;

import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;

public class MonitorSocketOutputStream extends OutputStream {
	private OutputStream mOutputStream;
	private MonitorSocketImpl mMonitorSocketImpl;
	private Context mContext;

	public MonitorSocketOutputStream(OutputStream mOutputStream,
			MonitorSocketImpl monitorSocketImpl, Context context) {
		this.mOutputStream = mOutputStream;
		this.mMonitorSocketImpl = monitorSocketImpl;
		this.mContext = context;
	}

	public void write(int oneByte) throws IOException {
		printLog("write byte " + oneByte);
		this.mOutputStream.write(oneByte);
	}

	public void close() throws IOException {
		printLog("close()");
		this.mOutputStream.close();
	}

	public void flush() throws IOException {
		printLog("flush()");
		this.mOutputStream.flush();
	}

	public void write(byte[] buffer) throws IOException {
		printLog("write count " + buffer.length);
		write(buffer, 0, buffer.length);
	}

	public void write(byte[] buffer, int offset, int count) throws IOException {
		printLog("write count " + count);
		this.mOutputStream.write(buffer, offset, count);
	}

	public int hashCode() {
		return this.mOutputStream.hashCode();
	}
	
	public void printLog(String msg) {
		mMonitorSocketImpl.printLog("OutputStream[" + hashCode() + "] " + msg, null);
	}
}