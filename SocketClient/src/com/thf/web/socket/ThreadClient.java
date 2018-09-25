package com.thf.web.socket;


public class ThreadClient extends Thread{
	
	static boolean flag = false;
	SocketClient sc = null;
	
	public void run(){
		synchronized (this) {
			if(flag){
				sc = new SocketClient();
				flag = false;
			}else{
				sc = new SocketClient("±à¼­1");
				flag = true;
			}
		}
		sc.toWork();
	}
	
	public static void main(String[] args){
		
		int i;
		ThreadClient tc = null;
		for(i=0;i<10;i++){
			tc = new ThreadClient();
			tc.start();
			try {
				tc.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
