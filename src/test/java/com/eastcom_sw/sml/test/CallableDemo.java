package com.eastcom_sw.sml.test;
import java.util.List;
import java.util.concurrent.Callable;

import org.junit.Test;

import com.eastom_sw.sml.support.CallableHelper;
import com.eastom_sw.sml.tools.MapUtils;


public class CallableDemo {
	//一台打印机打印一份文件需要0.2s
	class Print{
		public  void print(int i){
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	//现在1000份文件需要多久打印完
	@SuppressWarnings("unchecked")
	@Test
	public  void test() {
		List<Callable<String>> e=MapUtils.newArrayList();
		for(int i=1;i<=10;i++){
			final int temp=i;
			Callable<String> callable=new Callable<String>() {
				public String call() throws Exception {
					new Print().print(temp);
					return temp+"";
				}
			};
			e.add(callable);
		}
		//一台打印机花费
		long start=System.currentTimeMillis();
		CallableHelper.callresults(10,1,e.toArray(new Callable[e.size()]));
		System.out.println("一台打印机花费-->"+(System.currentTimeMillis()-start));
		//五台打印机花费
		start=System.currentTimeMillis();
		CallableHelper.callresults(10,5,e.toArray(new Callable[e.size()]));
		System.out.println("五台打印机花费-->"+(System.currentTimeMillis()-start));
	}
}
