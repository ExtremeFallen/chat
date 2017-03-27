package chat;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Demo4_GUI_Chat extends Frame {

	private TextField tf;
	private Button send;
	private Button clear;
	private Button log;
	private Button shake;
	private TextArea viewText;
	private TextArea sendText;
	private DatagramSocket socket;
	private BufferedWriter bw;
	/**
	 * @param args
	 * @throws SocketException 
	 */
	public Demo4_GUI_Chat() throws IOException {
		init();
		southPanel();
		centerPanel();
		event();
	}

	private void event() {
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					socket.close();
					bw.close();
				} catch (IOException e1) {
					
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});
		
		send.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					send();
				} catch (IOException e1) {
					
					e1.printStackTrace();
				}
			}

		});
		
		clear.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				viewText.setText("");
			}
		});
		
		log.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					logFile();
				} catch (IOException e1) {
					
					e1.printStackTrace();
				}
			}

		});
		
		shake.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					send(new byte[]{-1},tf.getText());
				} catch (Exception e1) {
					
					e1.printStackTrace();
				}
			}

		});
		
		sendText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
					try {
						send();
					} catch (IOException e1) {
						
						e1.printStackTrace();
					}
			}
		});
	}

	private void shake() throws InterruptedException {
		int x = this.getLocation().x;								//获取x的坐标
		int y = this.getLocation().y;								//获取y的坐标
		
		for(int i = 0; i < 20; i++) {
			this.setLocation(x + 20, y + 20);						//改变坐标点
			Thread.sleep(20);
			this.setLocation(x + 20, y - 20);
			Thread.sleep(20);
			this.setLocation(x - 20, y + 20);
			Thread.sleep(20);
			this.setLocation(x - 20, y - 20);
			Thread.sleep(20);
			this.setLocation(x, y);
		}
	}
	
	private void logFile() throws IOException {
		bw.flush();													//刷新缓冲区
		FileInputStream fis = new FileInputStream("config.txt");	//从配置文件中读取聊天记录
		ByteArrayOutputStream baos = new ByteArrayOutputStream();	//创建内存输出流
		byte[] arr = new byte[8192];
		int len;
		
		while((len = fis.read(arr)) != -1) {						//将文件上的数据读到数组中
			baos.write(arr, 0, len);								//将数组中的字节数据写到缓存中
		}
		fis.close();												//关流
		String message = baos.toString();							//把内存输出流的数据全部获取出来
		viewText.setText(message);									//显示在显示区域
	}
	
	private void send(byte[] arr, String ip) throws IOException {
		DatagramPacket packet = 
				new DatagramPacket(arr, arr.length, InetAddress.getByName(ip), 8888);
		socket.send(packet);	
	}
	
	private void send() throws IOException {
		String ip = tf.getText();
		ip = ip.trim().length() == 0 ? "255.255.255.255" : ip;
		String message = sendText.getText();						//获取信息
		
		send(message.getBytes(),ip);								//发送
		String time = getCurrentTime();
		String str = time + " 我对"+ (ip.equals("255.255.255.255")? "所有人" : ip) +"说:\r\n"+ message + "\r\n\r\n";	//alt + shift + l 抽取局部变量
		viewText.append(str);
		bw.write(str);
		sendText.setText("");										//清空发送区域
		
	}

	private String getCurrentTime() {
		Date d = new Date();									//创建时间对象
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");//创建格式化对象
		return sdf.format(d);									//对日期格式化
	}

	private void centerPanel() {
		Panel center = new Panel();	
		viewText = new TextArea();
		sendText = new TextArea(5,1);
		center.setLayout(new BorderLayout());					//改变了布局管理器
		
		viewText.setEditable(false);							//把显示的区域设置为不可以编辑
		viewText.setBackground(new Color(255,255,255));			//设置背景颜色
		
		viewText.setFont(new Font("xxx", Font.PLAIN, 15));		//设置字体
		sendText.setFont(new Font("yyy", Font.PLAIN, 15));
		center.add(sendText,BorderLayout.SOUTH);				//把发送的文本区域放在Panel的南边
		center.add(viewText,BorderLayout.CENTER);				//把显示的文本区域放在Panel的中间
		
		this.add(center,BorderLayout.CENTER);
	}

	private void southPanel() {
		Panel south = new Panel();								//创建南边的面板
		tf = new TextField(15);
		tf.setText("127.0.0.1");
		send = new Button("发 送");
		clear = new Button("清 屏");
		log = new Button("记 录");
		shake = new Button("震 动");
		
		south.add(tf);
		south.add(send);
		south.add(clear);
		south.add(log);
		south.add(shake);
		
		this.add(south,BorderLayout.SOUTH);
	}

	public void init() throws IOException {
		this.setSize(400, 600);
		this.setLocation(500, 50);
		new Receive().start();
		socket = new DatagramSocket();
		bw = new BufferedWriter(new FileWriter("config.txt",true));
		this.setVisible(true);					//显示窗体
	}
	
	private class Receive extends Thread {
		public void run() {
			try {
				DatagramSocket socket = new DatagramSocket(8888);					//创建Socket
				DatagramPacket packet = new DatagramPacket(new byte[8192], 8192);	//创建Packet
				while(true) {
					socket.receive(packet);											//接受
					
					byte[] arr = packet.getData();									//从packet中获取数据
					int len = packet.getLength();									//获取有效的字节个数
					if(arr[0] == -1 && len == 1) {
						shake();
						continue;
					}
					String message = new String(arr,0,len);							//将其转化成字符串
					String time = getCurrentTime();									//获取当前时间
					String ip = packet.getAddress().getHostAddress();				//获取ip地址
					
					String str = time + " " + ip + " 对我说:\r\n" + message + "\r\n\r\n";
					viewText.append(str);//添加到显示区域
					bw.write(str);
				}
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		new Demo4_GUI_Chat();
	}

}


