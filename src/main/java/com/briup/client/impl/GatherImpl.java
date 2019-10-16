package com.briup.client.impl;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import com.briup.bean.Environment;
import com.briup.client.Gather;

@SuppressWarnings("all")
public class GatherImpl implements Gather {

	@Override
	public void init(Properties properties) {

	}

	@Override
	public Collection<Environment> gather() throws Exception {
		// 1、读取文件
		File file = new File("src/main/java/radwtmp2");
		BufferedReader br = new BufferedReader(new FileReader(file));

		String line = null;

		List<Environment> list = new ArrayList<>();

		// light:光强，concentration:浓度，t:温度，h:湿度
		int light = 0;
		int concentration = 0;
		double t = 0;
		double h = 0;

		File skipFile = new File("src/main/java/skip");
		if (skipFile.exists()) {// 判断文件存在
			FileInputStream fis = new FileInputStream(skipFile);
			DataInputStream dis = new DataInputStream(fis);
			// 本次需要跳过的上一次数据
			long skip = dis.readLong();
			br.skip(skip);
		}

		while ((line = br.readLine()) != null) {
			// 100|101|2|16|1|3|5d606f7802|1|1516323596029
			// 100|101|2|256|1|3|016703|1|1529372858396
			String[] split = line.split("[|]");
			String msg = split[3];
			Environment envir1 = new Environment();
			Environment envir2 = new Environment();

			if ("256".equals(msg)) {
				// spilt[6]是环境值，将16进制转换为10进制
				// subString左闭右开区间
				String sub = split[6].substring(0, 4);
				light = Integer.parseInt(sub, 16);
				java.sql.Timestamp time = new java.sql.Timestamp(Long.parseLong(split[8]));

				envir1.setName("光照强度");
				envir1.setSrcId(split[0]);
				envir1.setDesId(split[1]);
				envir1.setDevId(split[2]);
				envir1.setSersorAddress(split[3]);
				envir1.setCount(Integer.parseInt(split[4]));
				envir1.setCmd(split[5]);
				envir1.setData(light);
				envir1.setStatus(Integer.parseInt(split[7]));
				envir1.setGather_date(time);
				list.add(envir1);
			} else if ("1280".equals(msg)) {
				String sub = split[6].substring(0, 4);
				concentration = Integer.parseInt(sub, 16);
				java.sql.Timestamp time = new java.sql.Timestamp(Long.parseLong(split[8]));

				envir1.setName("二氧化碳浓度");
				envir1.setSrcId(split[0]);
				envir1.setDesId(split[1]);
				envir1.setDevId(split[2]);
				envir1.setSersorAddress(split[3]);
				envir1.setCount(Integer.parseInt(split[4]));
				envir1.setCmd(split[5]);
				envir1.setData(concentration);
				envir1.setStatus(Integer.parseInt(split[7]));
				envir1.setGather_date(time);
				list.add(envir1);
			} else {
				String sub = split[6].substring(0, 4);
				t = ((float) Integer.parseInt(sub, 16) * 0.00268127) - 46.85;
				java.sql.Timestamp time = new java.sql.Timestamp(Long.parseLong(split[8]));

				envir1.setName("温度");
				envir1.setSrcId(split[0]);
				envir1.setDesId(split[1]);
				envir1.setDevId(split[2]);
				envir1.setSersorAddress(split[3]);
				envir1.setCount(Integer.parseInt(split[4]));
				envir1.setCmd(split[5]);
				envir1.setData((float) t);
				envir1.setStatus(Integer.parseInt(split[7]));
				envir1.setGather_date(time);

				String sub2 = split[6].substring(4, 8);
				h = ((float) Integer.parseInt(sub2, 16) * 0.00190735) - 6;
				java.sql.Timestamp time2 = new java.sql.Timestamp(Long.parseLong(split[8]));

				envir2.setName("湿度");
				envir2.setSrcId(split[0]);
				envir2.setDesId(split[1]);
				envir2.setDevId(split[2]);
				envir2.setSersorAddress(split[3]);
				envir2.setCount(Integer.parseInt(split[4]));
				envir2.setCmd(split[5]);
				envir2.setData((float) h);
				envir2.setStatus(Integer.parseInt(split[7]));
				envir2.setGather_date(time2);
				list.add(envir1);
				list.add(envir2);
			}
		}

		long len = 0;

		String msg = "";
		while ((msg = br.readLine()) != null) {
			System.out.println(msg);
			len += msg.length();
			len++;
		}
		// 本次读取的数据总量
		long myskip = file.length();
		// 保存
		FileOutputStream fos = new FileOutputStream("src/main/java/skip");
		DataOutputStream dos = new DataOutputStream(fos);
		dos.writeLong(myskip);
		dos.flush();
		dos.close();
		fos.close();

		for (Object obj : list) {
			System.out.println(obj);
		}

		br.close();
		return list;

	}
}
