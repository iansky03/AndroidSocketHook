package com.codelogic.sockethook.network.monitor;

import android.content.Context;
import java.io.IOException;
import java.io.InputStream;

public class MonitorSocketInputStream extends InputStream {
	private InputStream mInputStream;
	private MonitorSocketImpl monitorSocketImpl;
	private Context context;

	public MonitorSocketInputStream(InputStream mInputStream, MonitorSocketImpl monitorSocketImpl, Context context) {
		this.mInputStream = mInputStream;
		this.monitorSocketImpl = monitorSocketImpl;
		this.context = context;
	}

	public int read() throws IOException {
		int readByte = this.mInputStream.read();
		printLog("read byte.");
		return readByte;
	}

	public int read(byte[] buffer) throws IOException {
		return read(buffer, 0, buffer.length);
	}

	public int read(byte[] buffer, int offset, int length) throws IOException {
		int readLen = this.mInputStream.read(buffer, offset, length);
		printLog("read " + readLen);
		return readLen;
	}

	public void close() throws IOException {
		printLog("close()");
		this.mInputStream.close();
	}

	public int hashCode() {
		return this.mInputStream.hashCode();
	}
	
	public void printLog(String msg) {
		monitorSocketImpl.printLog("InputStream[" + hashCode() + "] " + msg, null);
	}
}