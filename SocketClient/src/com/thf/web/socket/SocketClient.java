package com.thf.web.socket;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;

public class SocketClient{
	
	private static int seq = 0;
	private String sendMsgAddress = "C:\\Users\\tlcb\\Desktop";
	private String sendMsgName = "编辑2.xml";
	private static String logFilePath = "D:\\SocketText\\Logs";
	private String sendMsg = "";
	private Socket socket = null;
	private String reciveMsg = "";
	
	public SocketClient(){
		
	}
	
	public SocketClient(String sendMsgName){
		this.sendMsgName = sendMsgName;
	}
	
	static{
		seq++;
		File logp = new File(logFilePath);
		if(!logp.exists())
			logp.mkdirs();		
		File[] uFiles=logp.listFiles();
		for (File ufile : uFiles) {
			if(ufile.isFile() || ufile.isDirectory())
				ufile.delete();				
		}
	}
	
	public void toWork(){
		
		try {
			Long startTime = System.currentTimeMillis();
			//1.业务处理，拼接报文，此处从文件中获取发送报文。
			getSendMsg();
			
			//2.获取socket连接配置，建立连接
			getSocketConn();
			
			//3.发送报文
			toSendMsg();
			Long endTime = System.currentTimeMillis();
			//4.写入日志文件，一笔报文分一个文件
			writeLog(startTime,endTime);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getSendMsg(){
		//1.创建文件输入流，读取文件
		try {
			InputStream fis = new FileInputStream(new File(sendMsgAddress+File.separator+sendMsgName));
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			int msgLine;
			StringBuffer msg = new StringBuffer();
			while((msgLine = br.read()) != -1){
				msg.append((char)msgLine);
			}
			System.out.println(sendMsg = msg.toString());	
			fis.close();

		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public void getSocketConn() throws Exception{
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("socket.properties");
		Properties p = new Properties();
		p.load(in);
		String host = p.getProperty("host");
		int port = Integer.valueOf(p.getProperty("port"));
		
		socket = new Socket(Proxy.NO_PROXY);
		socket.connect(new InetSocketAddress(host,port), 60000);
	}
	
	public void toSendMsg() throws Exception{

		OutputStream out = socket.getOutputStream();
		InputStream in = socket.getInputStream();
		out.write(sendMsg.getBytes("UTF-8"));	
		System.out.println(reciveMsg = getBackMsg(in));
		
		out.close();
		in.close();
		socket.close();
	}
	
	public void writeLog(Long ...args) throws Exception{
		
		String currentDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
		String currentTime = new SimpleDateFormat("HHmmssSSS").format(new Date());
		
		String logFileName = currentDate+"_"+currentTime+"_"+seq+++".xml";	
		FileOutputStream fos = new FileOutputStream(logFilePath+File.separator+logFileName);
		fos.write(Thread.currentThread().getName().getBytes("UTF-8"));
		fos.write(Long.toString(args[0]).getBytes());
		fos.write("\n\r".getBytes());
		fos.write(sendMsg.getBytes("UTF-8"));
		fos.write("\n\r".getBytes());
		fos.write(reciveMsg.getBytes("UTF-8"));
		fos.write("\n\r".getBytes());
		fos.write(Thread.currentThread().getName().getBytes("UTF-8"));
		fos.write(Long.toString(args[1]).getBytes());
		fos.write("\n\r".getBytes());
		fos.write(Long.toString(args[1]-args[0]).getBytes());
		fos.close();
		
	}
	
	public String getBackMsg(InputStream in) throws Exception{
		byte[] recBuffer = new byte[102400];
		byte[] part = new byte[10240];

		int j = 0, k = 0, rcount = 0;
		while ((rcount = in.read(part)) > -1) {
			for (int i = 0; i < rcount; i++) {
				recBuffer[j++] = part[i];
			}
			k++;
		}

		byte[] rtn = new byte[j];
		for (int i = 0; i < j; i++) {
			rtn[i] = recBuffer[i];
		}

		return new String(rtn, "UTF-8");
	}
	
	/**
	 * Socket客户端
	 * @param args
	 */
	public static void main(String[] args) {
		
		ResourceBundle rb = ResourceBundle.getBundle("socket");
		System.out.println(rb.getString("host"));
		
		SocketClient sc = new SocketClient();
		//sc.toWork();	 

	}

}
