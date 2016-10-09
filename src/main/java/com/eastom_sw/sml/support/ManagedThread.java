package com.eastom_sw.sml.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class ManagedThread extends Thread {

	protected  Logger logger = LoggerFactory.getLogger(getClass());

	protected boolean stopFlag = false;

	private Object waitLock = new Object();

	abstract protected boolean prepare();

	/**
	 * 主体运行逻辑
	 */
	abstract protected void doWorkProcess();

	abstract protected void cleanup();

	/**
	 * 是否满足退出条件
	 * 
	 * @return
	 */
	abstract protected boolean extraExitCondition();

	public void run() {
		logger.info(this.getName() + " started ...");

		if (!prepare()) {
			logger.error(this.getName() + " prepare failure , thread exit ...");
			return;
		}

		while (!(stopFlag && extraExitCondition())) {
			doWorkProcess();
		}
		cleanup();
		logger.info(this.getName() + " stopped ...");
	}

	/**
	 * 线程进入等待阻塞模式，timeout = 0 永久等待，直至被唤醒，否则等 timeout 毫秒后自动唤醒
	 * 
	 * @param timeout
	 */
	public void relax(long timeout) {
		synchronized (waitLock) {
			try {
				waitLock.wait(timeout);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void shutdown() {
		stopFlag = true;
		synchronized (waitLock) {
			waitLock.notifyAll();
		}
		this.interrupt();

	}

	public void wakeup() {
		synchronized (waitLock) {
			waitLock.notifyAll();
		}
	}
}
