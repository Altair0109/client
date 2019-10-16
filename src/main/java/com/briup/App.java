package com.briup;

import java.util.Collection;

import com.briup.bean.Environment;
import com.briup.client.Gather;
import com.briup.client.impl.GatherImpl;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws Exception {

//		//创建gather模块的对象 
		Gather gather = new GatherImpl();
//		//调用gather模块的采集方法 得到 当前的数据
		Collection<Environment> gatherDate = gather.gather();
		System.out.println(gatherDate);
//		//创建客户端对象
//		Client client = new ClientImpl();
//		//把当前采集到的数据发送到服务器端
//		client.send(gatherDate);
//		System.out.println("客户端本次发送完成");
	}
}
