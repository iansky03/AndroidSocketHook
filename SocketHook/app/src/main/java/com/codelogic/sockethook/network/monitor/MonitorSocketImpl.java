package com.codelogic.sockethook.network.monitor;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketImpl;
import java.net.SocketTimeoutException;

import android.content.Context;
import android.util.Log;

public class MonitorSocketImpl extends SocketImpl {

	private Context mContext;
	private SocketImpl mSocketImpl;
	private Class sIclass;

	public String host = "";
	public int port;
	
	private String mConnTag = "";

	public MonitorSocketImpl(Context context, Class clasz) {
		try {
			Constructor constructor = clasz.getDeclaredConstructor(new Class[0]);
			constructor.setAccessible(true);
			SocketImpl impl = (SocketImpl) constructor.newInstance(new Object[0]);
			this.mSocketImpl = impl;
			this.mContext = context;
			this.sIclass = clasz;
			copyFd();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			getMethod(sIclass, "wait", new Class[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Object getOption(int optID) throws SocketException {
		if (mSocketImpl != null)
			return this.mSocketImpl.getOption(optID);
		return null;
	}

	@Override
	public void setOption(int optID, Object val) throws SocketException {
		if (mSocketImpl != null) {
			mSocketImpl.setOption(optID, val);
		}
	}

	@Override
	protected void accept(SocketImpl newSocket) throws IOException {
		try {
			Method method = getMethod(this.sIclass, "accept", new Class[0]);
			method.setAccessible(true);
			method.invoke(this.mSocketImpl, new Object[] { newSocket });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected int available() throws IOException {
		int available = 0;
		try {
			Method method = getMethod(this.sIclass, "available", new Class[0]);
			method.setAccessible(true);
			available = ((Integer) method.invoke(this.mSocketImpl, new Object[0])).intValue();
			printLog("available=" + available);
			return available;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return available;
	}

	@Override
	protected void bind(InetAddress address, int port) throws IOException {
		try {
			Method method = getMethod(this.sIclass, "bind", new Class[] {
					InetAddress.class, Integer.TYPE });
			method.setAccessible(true);
			method.invoke(this.mSocketImpl, new Object[] { address, Integer.valueOf(port) });
			printLog("bind addr=" + address + " port=" + port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void close() throws IOException {
		try {
			Method method = getMethod(this.sIclass, "close", new Class[0]);
			method.setAccessible(true);
			method.invoke(this.mSocketImpl, new Object[0]);
			printLog("close");
		} catch (Exception e) {
			printLog("close MonitorSocket failed.", e);
			throw new IOException(e.toString());
		}
	}

	@Override
	protected void connect(String host, int port) throws IOException {
		this.host = host;
		this.port = port;
		try {
			Method method = getMethod(this.sIclass, "connect", new Class[] {String.class, Integer.TYPE });
			method.setAccessible(true);
			method.invoke(this.mSocketImpl, new Object[] { host, Integer.valueOf(port) });
			copyFd();
			printLog("connect to host 1 " + host + ":" + port);
		} catch (Exception e) {
			if ((e instanceof InvocationTargetException)) {
				throw new IOException(((InvocationTargetException) e)
						.getTargetException().toString());
			}
			throw new IOException(e.toString());
		}
	}

	@Override
	protected void connect(InetAddress address, int port) throws IOException {
		try {
			this.host = address.getHostName();
			this.port = port;
			Log.e("MonitorSocket", "connect to host 2 " + host + " succ.");
			Method method = getMethod(this.sIclass, "connect", new Class[] {
					InetAddress.class, Integer.TYPE });

			method.setAccessible(true);
			method.invoke(this.mSocketImpl, new Object[] { address, Integer.valueOf(port) });
			copyFd();
			printLog("connect to host 2 " + address + ":" + port);
		} catch (Exception e) {
			if ((e instanceof InvocationTargetException)) {
				throw new IOException(((InvocationTargetException) e)
						.getTargetException().toString());
			}
			throw new IOException(e.toString());
		}
	}

	@Override
	protected void create(boolean isStreaming) throws IOException {
		try {
			printLog("create isStreaming " + isStreaming);
			Method method = getMethod(this.sIclass, "create",
					new Class[] { Boolean.TYPE });
			method.setAccessible(true);
			method.invoke(this.mSocketImpl, new Object[] { Boolean.valueOf(isStreaming) });
		} catch (Exception e) {
			throw new IOException(e.toString());
		}
	}

	@Override
	protected InputStream getInputStream() throws IOException {
		try {
			printLog("getInputStream()");
			Method method = getMethod(this.sIclass, "getInputStream", new Class[0]);
			method.setAccessible(true);
			InputStream is = (InputStream) method.invoke(this.mSocketImpl, new Object[0]);
			return new MonitorSocketInputStream(is, this, mContext);
		} catch (Exception e) {
			throw new IOException(e.toString());
		}
	}

	@Override
	protected OutputStream getOutputStream() throws IOException {
		try {
			printLog("getOutputStream()");
			Method method = getMethod(this.sIclass, "getOutputStream", new Class[0]);
			method.setAccessible(true);
			OutputStream os = (OutputStream) method.invoke(this.mSocketImpl,
					new Object[0]);
			return new MonitorSocketOutputStream(os, this, mContext);
		} catch (Exception e) {
			throw new IOException(e.toString());
		}
	}

	@Override
	protected void listen(int backlog) throws IOException {
		
	}

	@Override
	protected void connect(SocketAddress remoteAddr, int timeout)
			throws IOException {
		long startTime = 0L;
		try {
			InetAddress address = ((InetSocketAddress) remoteAddr).getAddress();
			if (address == null) {
				this.host = ((InetSocketAddress) remoteAddr).getHostName();
				this.port = ((InetSocketAddress) remoteAddr).getPort();
			} else {
				this.host = address.getHostAddress();
				this.port = ((InetSocketAddress) remoteAddr).getPort();
			}
			
			Method method = getMethod(this.sIclass, "connect", new Class[] {
					SocketAddress.class, Integer.TYPE });

			method.setAccessible(true);
			startTime = System.currentTimeMillis();
			method.invoke(this.mSocketImpl,
					new Object[] { remoteAddr, Integer.valueOf(timeout) });
			copyFd();
			printLog("connect to host 3 " + remoteAddr + " timeout=" + timeout);
		} catch (Exception e) {
			long timeCost = System.currentTimeMillis() - startTime;
			if (timeCost >= timeout) {
				throw new SocketTimeoutException("timeoutexception "
						+ e.toString());
			}
			if ((e instanceof InvocationTargetException)) {
				throw new IOException(((InvocationTargetException) e)
						.getTargetException().toString());
			}
			throw new IOException(e.toString());
		}
	}

	@Override
	protected void sendUrgentData(int value) throws IOException {
		try {
			printLog("sendUrgentData " + value);
			Method method = getMethod(this.sIclass, "sendUrgentData",
					new Class[] { Integer.TYPE });
			method.setAccessible(true);
			method.invoke(this.mSocketImpl,
					new Object[] { Integer.valueOf(value) });
		} catch (Exception e) {
			throw new IOException(e.toString());
		}
	}
	
	@Override
	protected InetAddress getInetAddress() {
		try {
			Method method = getMethod(this.sIclass, "getInetAddress",
					new Class[0]);
			method.setAccessible(true);
			InetAddress inetAddress = (InetAddress) method.invoke(this.mSocketImpl, new Object[0]);
			printLog("getInetAddress() " + inetAddress);
			return inetAddress;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected int getPort() {
		try {
			Method method = getMethod(this.sIclass, "getPort", new Class[0]);
			method.setAccessible(true);
			return ((Integer) method.invoke(this.mSocketImpl, new Object[0])).intValue();
		} catch (Exception e) {
		}
		return 0;
	}

	private void copyFd() {
		try {
			Class baseClass = Socket.class.getClassLoader().loadClass( "java.net.SocketImpl");
			Method method = baseClass.getDeclaredMethod("getFileDescriptor", new Class[0]);
			method.setAccessible(true);
			this.fd = ((FileDescriptor) method.invoke(this.mSocketImpl, new Object[0]));
			mConnTag = this.hashCode() + "--" + this.fd.hashCode() + "[" + this.host + ":" + this.port + "]";
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Method getMethod(Class clazz, String methodName,
			Class... params) {
		Method implMethod = null;
		StringBuilder logBuilder = new StringBuilder();
		try {
			return clazz.getDeclaredMethod(methodName, params);
		} catch (Exception e) {
			Class superClass = clazz.getSuperclass();
			if (superClass == null) {
				return implMethod;
			}
			implMethod = getMethod(superClass, methodName, params);
		}
		return implMethod;
	}
	
	public void printLog(String msg) {
		printLog(msg, null);
	}
	
	public void printLog(String msg, Throwable t) {
		if(t == null) {
			Log.i("MonitorSocket", mConnTag + " " + msg);
		} else {
			Log.e("MonitorSocket", mConnTag + " " + msg, t);
		}
	}
}
