package assignment7;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClientMain {
	private JTextArea incoming;
	private JTextField outgoing;
	private BufferedReader reader;
	private PrintWriter writer;
	 static int clientNo;

	public void run() throws Exception {
		initView();
		setUpNetworking();
	}

	private void initView() {
		JFrame frame = new JFrame("Ludicrously Simple Chat Client");
		JPanel mainPanel = new JPanel();
		incoming = new JTextArea(15, 50);
		incoming.setLineWrap(true);
		incoming.setWrapStyleWord(true);
		incoming.setEditable(false);
		JScrollPane qScroller = new JScrollPane(incoming);
		qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		outgoing = new JTextField(20);
		//
		JTextField id = new JTextField(30);
		id.setText("Enter ID");
		
		JButton idbtn = new JButton("Set ID");
		idbtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent aae){
				clientNo = Integer.parseInt(id.getText());
			}
		});
		mainPanel.add(idbtn);
		mainPanel.add(id);
		//
		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(new SendButtonListener());
		mainPanel.add(qScroller);
		mainPanel.add(outgoing);
		mainPanel.add(sendButton);
		frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
		frame.setSize(650, 500);
		frame.setVisible(true);
	}

	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		Socket sock = new Socket("127.0.0.1", 4242);
		//Socket sock = new Socket("128.62.50.136", 4242);
		InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
		reader = new BufferedReader(streamReader);
		writer = new PrintWriter(sock.getOutputStream());
		System.out.println("networking established");
		Thread readerThread = new Thread(new IncomingReader());
		readerThread.start();
	}
	
	
	class SendButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			writer.println("Client " + clientNo + " says: " +outgoing.getText());
			writer.flush();
			outgoing.setText("");
			outgoing.requestFocus();
		}
	}

	public static void main(String[] args) {
		try {
			new ClientMain().run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class IncomingReader implements Runnable {
		public void run() {
			String message;
			try {
				while ((message = reader.readLine()) != null) {
					String[] getNo = message.split(" ");
					int cNo  = -1;
					try{
						cNo = Integer.parseInt(getNo[3]);
					}catch(Exception e){
						e.printStackTrace();
					}
					//delete directing keyword and rebuild string
					String msg = "";
					for(int i =0; i<getNo.length;i++){
						if(i!=3){
							msg += getNo[i];
							msg += " ";
						}
					}
					message = msg;
					//String message = Arrays.stream(getNo).collect(Collectors.joining(" "));
					if(cNo == clientNo){
					incoming.append(message + "\n");
					}
					else if(cNo ==0){
						incoming.append(message + "\n");
					}
					
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
