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
		int x = this.getLocation().x;								//��ȡx������
		int y = this.getLocation().y;								//��ȡy������
		
		for(int i = 0; i < 20; i++) {
			this.setLocation(x + 20, y + 20);						//�ı������
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
		bw.flush();													//ˢ�»�����
		FileInputStream fis = new FileInputStream("config.txt");	//�������ļ��ж�ȡ�����¼
		ByteArrayOutputStream baos = new ByteArrayOutputStream();	//�����ڴ������
		byte[] arr = new byte[8192];
		int len;
		
		while((len = fis.read(arr)) != -1) {						//���ļ��ϵ����ݶ���������
			baos.write(arr, 0, len);								//�������е��ֽ�����д��������
		}
		fis.close();												//����
		String message = baos.toString();							//���ڴ������������ȫ����ȡ����
		viewText.setText(message);									//��ʾ����ʾ����
	}
	
	private void send(byte[] arr, String ip) throws IOException {
		DatagramPacket packet = 
				new DatagramPacket(arr, arr.length, InetAddress.getByName(ip), 8888);
		socket.send(packet);	
	}
	
	private void send() throws IOException {
		String ip = tf.getText();
		ip = ip.trim().length() == 0 ? "255.255.255.255" : ip;
		String message = sendText.getText();						//��ȡ��Ϣ
		
		send(message.getBytes(),ip);								//����
		String time = getCurrentTime();
		String str = time + " �Ҷ�"+ (ip.equals("255.255.255.255")? "������" : ip) +"˵:\r\n"+ message + "\r\n\r\n";	//alt + shift + l ��ȡ�ֲ�����
		viewText.append(str);
		bw.write(str);
		sendText.setText("");										//��շ�������
		
	}

	private String getCurrentTime() {
		Date d = new Date();									//����ʱ�����
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy��MM��dd�� HH:mm:ss");//������ʽ������
		return sdf.format(d);									//�����ڸ�ʽ��
	}

	private void centerPanel() {
		Panel center = new Panel();	
		viewText = new TextArea();
		sendText = new TextArea(5,1);
		center.setLayout(new BorderLayout());					//�ı��˲��ֹ�����
		
		viewText.setEditable(false);							//����ʾ����������Ϊ�����Ա༭
		viewText.setBackground(new Color(255,255,255));			//���ñ�����ɫ
		
		viewText.setFont(new Font("xxx", Font.PLAIN, 15));		//��������
		sendText.setFont(new Font("yyy", Font.PLAIN, 15));
		center.add(sendText,BorderLayout.SOUTH);				//�ѷ��͵��ı��������Panel���ϱ�
		center.add(viewText,BorderLayout.CENTER);				//����ʾ���ı��������Panel���м�
		
		this.add(center,BorderLayout.CENTER);
	}

	private void southPanel() {
		Panel south = new Panel();								//�����ϱߵ����
		tf = new TextField(15);
		tf.setText("127.0.0.1");
		send = new Button("�� ��");
		clear = new Button("�� ��");
		log = new Button("�� ¼");
		shake = new Button("�� ��");
		
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
		this.setVisible(true);					//��ʾ����
	}
	
	private class Receive extends Thread {
		public void run() {
			try {
				DatagramSocket socket = new DatagramSocket(8888);					//����Socket
				DatagramPacket packet = new DatagramPacket(new byte[8192], 8192);	//����Packet
				while(true) {
					socket.receive(packet);											//����
					
					byte[] arr = packet.getData();									//��packet�л�ȡ����
					int len = packet.getLength();									//��ȡ��Ч���ֽڸ���
					if(arr[0] == -1 && len == 1) {
						shake();
						continue;
					}
					String message = new String(arr,0,len);							//����ת�����ַ���
					String time = getCurrentTime();									//��ȡ��ǰʱ��
					String ip = packet.getAddress().getHostAddress();				//��ȡip��ַ
					
					String str = time + " " + ip + " ����˵:\r\n" + message + "\r\n\r\n";
					viewText.append(str);//��ӵ���ʾ����
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


