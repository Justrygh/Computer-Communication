package _Codes;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

import Codes.ChatMessage;

//import _Codes.ChatMessage;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

import javax.swing.GroupLayout;

/**
 * @author erlichsefi
 */
public class SClient extends javax.swing.JFrame implements Serializable  {
//////////////////////////////////// Client:
	
	
	// for I/O
	private ObjectInputStream sInput;		// to read from the socket
	private ObjectOutputStream sOutput;		// to write on the socket
	private Socket socket;

	
	// the Client object
	private SClient client;
	
	
	
	
	
	// if I use a GUI or not
	private SClient cg;
	
	// the server, the port and the username
	private String server, username;
	private int port;

	/*
	 *  Constructor called by console mode
	 *  server: the server address
	 *  port: the port number
	 *  username: the username
	 */
	SClient(String server, int port, String username) {
		// which calls the common constructor with the GUI set to null
		this(server, port, username, null);
	}

	/*
	 * Constructor call when used from a GUI
	 * in console mode the ClienGUI parameter is null
	 */
	SClient(String server, int port, String username, SClient cg) {
		this.server = server;
		this.port = port;
		this.username = username;
		// save if we are in GUI mode or not
	//	this.cg = cg;
	}
	
	/*
	 * To start the dialog
	 */
	public boolean start() {
		// try to connect to the server
		try {
			socket = new Socket(server, port);
		} 
		// if it failed not much I can so
		catch(Exception ec) {
			display("Error connectiong to server:" + ec);
			return false;
		}
		
		String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
		display(msg);
	
		/* Creating both Data Stream */
		try
		{
			sInput  = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException eIO) {
			display("Exception creating new Input/output Streams: " + eIO);
			return false;
		}

		// creates the Thread to listen from the server 
		new ListenFromServer().start();
		// Send our username to the server this is the only message that we
		// will send as a String. All other messages will be ChatMessage objects
		try
		{
			sOutput.writeObject(username);
		}
		catch (IOException eIO) {
			display("Exception doing login : " + eIO);
			disconnect();
			return false;
		}
		// success we inform the caller that it worked
		return true;
	}

	/*
	 * To send a message to the console or the GUI
	 */
	private void display(String msg) {
		if(cg == null)
			System.out.println(msg);      // println in console mode
		else
			cg.append(msg + "\n");		// append to the ClientGUI JTextArea (or whatever)
	}
	
	/*
	 * To send a message to the server
	 */
	void sendMessage(ChatMessage msg) {
		try {
			sOutput.writeObject(msg);
		}
		catch(IOException e) {
			display("Exception writing to server: " + e);
		}
	}

	/*
	 * When something goes wrong
	 * Close the Input/Output streams and disconnect not much to do in the catch clause
	 */
	private void disconnect() {
		try { 
			if(sInput != null) sInput.close();
		}
		catch(Exception e) {} // not much else I can do
		try {
			if(sOutput != null) sOutput.close();
		}
		catch(Exception e) {} // not much else I can do
        try{
			if(socket != null) socket.close();
		}
		catch(Exception e) {} // not much else I can do
		
		// inform the GUI
		if(cg != null)
			cg.connectionFailed();
			
	}
	
	// called by the GUI is the connection failed
		// we reset our buttons, label, textfield
		public void connectionFailed() {
			System.out.println("// connection failed");
//			login.setEnabled(true);
//			logout.setEnabled(false);
//			whoIsIn.setEnabled(false);
//			label.setText("Enter your username below");
//			tf.setText("Anonymous");
//			// reset port number and host name as a construction time
//			tfPort.setText("" + defaultPort);
//			tfServer.setText(defaultHost);
//			// let the user change them
//			tfServer.setEditable(false);
//			tfPort.setEditable(false);
//			// don't react to a <CR> after the username
//			tf.removeActionListener(this);
//			connected = false;
		}
		
	/*
	 * To start the Client in console mode use one of the following command
	 * > java Client
	 * > java Client username
	 * > java Client username portNumber
	 * > java Client username portNumber serverAddress
	 * at the console prompt
	 * If the portNumber is not specified 1500 is used
	 * If the serverAddress is not specified "localHost" is used
	 * If the username is not specified "Anonymous" is used
	 * > java Client 
	 * is equivalent to
	 * > java Client Anonymous 1500 localhost 
	 * are eqquivalent
	 * 
	 * In console mode, if an error occurs the program simply stops
	 * when a GUI id used, the GUI is informed of the disconnection
	 */


	/*
	 * a class that waits for the message from the server and append them to the JTextArea
	 * if we have a GUI or simply System.out.println() it in console mode
	 */
	class ListenFromServer extends Thread {

		public void run() {
			while(true) {
				try {
					String msg = (String) sInput.readObject();
					// if console mode print the message and add back the prompt
					if(cg == null) {
						System.out.println(msg);
						System.out.print("> ");
					}
					else {
						cg.append(msg);
					}
				}
				catch(IOException e) {
					display("Server has close the connection: " + e);
					if(cg != null) 
						cg.connectionFailed();
					break;
				}
				// can't happen with a String object but need the catch anyhow
				catch(ClassNotFoundException e2) {
				}
			}
		}
	}
	
	
	
	
	
	
	
	
	//////////////////////////////////////
	/**
	 * 
	 */
	private static final long serialVersionUID = 3970976847258960979L;

	/**
	 * Creates new form Client
	 */
	public SClient() {
		initComponents();


	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jToggleButton_connect = new javax.swing.JToggleButton();
		my_name = new javax.swing.JTextField();
		jLabel_name = new javax.swing.JLabel();
		jLabel_address = new javax.swing.JLabel();
		ip_ad = new javax.swing.JTextField();
		jToggleButton_showOnline = new javax.swing.JToggleButton();
		jToggleButton_clear = new javax.swing.JToggleButton();
		jToggleButton_reset = new javax.swing.JToggleButton();
		jScrollPane1 = new javax.swing.JScrollPane();
		jTextArea_Main = new javax.swing.JTextArea("");
		jLabel_To = new javax.swing.JLabel();
		dst = new javax.swing.JTextField();
		message_field = new javax.swing.JTextField("");
		jButton_send = new javax.swing.JButton();
		jButton_disconnect = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		jToggleButton_connect.setText("Connect");
		jToggleButton_connect.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				Connect(evt);
			}
		});
		jToggleButton_connect.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jToggleButton1ActionPerformed(evt);
			}
		});

		my_name.setText("name");
		my_name.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				my_nameActionPerformed(evt);
			}
		});

		jLabel_name.setText("name:");

		jLabel_address.setText("address:");

		ip_ad.setText("localhost");
		ip_ad.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				ip_adActionPerformed(evt);
			}
		});

		jToggleButton_showOnline.setText("Show online users");
		jToggleButton_showOnline.setEnabled(false);
		jToggleButton_showOnline.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				showonline(evt);
			}
		});

		jToggleButton_clear.setText("Clear");
		jToggleButton_clear.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				Clear(evt);
			}
		});
		jToggleButton_clear.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jToggleButton3ActionPerformed(evt);
			}
		});

		jToggleButton_reset.setText("Reset");
		jToggleButton_reset.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				Reset(evt);
			}
		});
		jToggleButton_reset.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jToggleButtonResetActionPerformed(evt);
			}
		});

		jTextArea_Main.setColumns(20);
		jTextArea_Main.setRows(5);
		jScrollPane1.setViewportView(jTextArea_Main);

		jLabel_To.setText("TO:");

		dst.setText("NAME");
		dst.setEditable(false);
		dst.setToolTipText("");

		message_field.setText("");
		message_field.setEditable(false);
		message_field.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				message_fieldActionPerformed(evt);
			}
		});

		jButton_send.setText("Send");
		jButton_send.setEnabled(false);
		jButton_send.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				Send(evt);
			}
		});

		jButton_disconnect.setText("Disconnect");
		jButton_disconnect.setEnabled(false);
		jButton_disconnect.setToolTipText("");
		jButton_disconnect.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				Disconnect(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		layout.setHorizontalGroup(
				layout.createParallelGroup(Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(layout.createParallelGroup(Alignment.LEADING)
								.addGroup(layout.createSequentialGroup()
										.addComponent(jLabel_To)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(dst, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(ComponentPlacement.UNRELATED)
										.addComponent(message_field, GroupLayout.PREFERRED_SIZE, 328, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(jButton_send))
								.addGroup(layout.createSequentialGroup()
										.addGap(17)
										.addGroup(layout.createParallelGroup(Alignment.TRAILING)
												.addGroup(layout.createSequentialGroup()
														.addComponent(jLabel_name)
														.addPreferredGap(ComponentPlacement.UNRELATED)
														.addComponent(my_name, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
														.addGap(18)
														.addComponent(jLabel_address)
														.addPreferredGap(ComponentPlacement.RELATED)
														.addComponent(ip_ad, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														.addComponent(jToggleButton_showOnline))
												.addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 459, GroupLayout.PREFERRED_SIZE)
												.addGroup(Alignment.LEADING, layout.createSequentialGroup()
														.addComponent(jToggleButton_connect, GroupLayout.PREFERRED_SIZE, 123, GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(ComponentPlacement.RELATED)
														.addComponent(jButton_disconnect, GroupLayout.PREFERRED_SIZE, 118, GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(ComponentPlacement.RELATED)
														.addComponent(jToggleButton_reset, GroupLayout.PREFERRED_SIZE, 119, GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(ComponentPlacement.RELATED)
														.addComponent(jToggleButton_clear, GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)))
										.addGap(97)))
						.addContainerGap())
				);
		layout.setVerticalGroup(
				layout.createParallelGroup(Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(layout.createParallelGroup(Alignment.BASELINE)
								.addComponent(jToggleButton_connect)
								.addComponent(jButton_disconnect, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
								.addComponent(jToggleButton_reset)
								.addComponent(jToggleButton_clear))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(Alignment.BASELINE)
								.addComponent(jToggleButton_showOnline)
								.addComponent(jLabel_name)
								.addComponent(my_name, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel_address)
								.addComponent(ip_ad, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 211, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addGroup(layout.createParallelGroup(Alignment.BASELINE)
								.addComponent(jLabel_To)
								.addComponent(dst, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(message_field, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(jButton_send))
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				);
		getContentPane().setLayout(layout);

		pack();
	}// </editor-fold>//GEN-END:initComponents

	private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
		// TODO add your handling code here:
	}//GEN-LAST:event_jToggleButton1ActionPerformed

	private void my_nameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_my_nameActionPerformed
		// TODO add your handling code here:
	}//GEN-LAST:event_my_nameActionPerformed

	private void ip_adActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ip_adActionPerformed
		// TODO add your handling code here:
	}//GEN-LAST:event_ip_adActionPerformed

	private void jToggleButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton3ActionPerformed
		// TODO add your handling code here:
	}//GEN-LAST:event_jToggleButton3ActionPerformed
	private void jToggleButtonResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton3ActionPerformed
		// TODO add your handling code here:
	}//GEN-LAST:event_jToggleButton3ActionPerformed

	private void Clear(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Clear

		jTextArea_Main.setText("");
	}//GEN-LAST:event_Clear


	private void Reset(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Clear

		dispose();
		SClient game = new SClient();
		game.setVisible(true);
	}//GEN-LAST:event_Reset


	private void showonline(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SHOWONLINE
		System.out.println("showonline botten clicked");

	}//GEN-LAST:event_SHOWONLINE

	private void Connect(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Connect
		System.out.println("connect button clicked");
		
	
		
		// ok it is a connection request
		String username = my_name.getText().trim();
	
		// empty username ignore it
		if(username.length() == 0) {
			System.out.println("!??");
			return;
		}
		// empty serverAddress ignore it
		String server = ip_ad.getText().trim();
		if(server.length() == 0)
			return;
		// empty or invalid port numer, ignore it
		int portNumber = 1500;
		if(portNumber == 0)
			return;
		int port = 0;
		try {
			port = portNumber;
		}
		catch(Exception en) {
			return;   // nothing I can do if port number is not valid
		}

		// try creating a new Client with GUI
		 client = new SClient(server, port, username, this);
		// test if we can start the Client
		if(!client.start()) {
			System.out.println("error");
			return;
		
		}
		my_name.setEditable(true);
		message_field.setEditable(true);
		
		
		my_name.setEnabled(false);
		jButton_disconnect.setEnabled(true);
		jToggleButton_connect.setEnabled(false);
		message_field.setEnabled(true);
		jButton_send.setEnabled(true);
		

		
		//dst.setEnabled(true);


	}//GEN-LAST:event_Connect
	
	
	// called by the Client to append text in the TextArea 
		void append(String str) {
			client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, str));
			
			jTextArea_Main.append(my_name.getText() +": " +  str + "\n");
			message_field.setText("");
		//	jTextArea_Main.setCaretPosition(message_field.getText().length() - 1);
		}
	private void Send(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Send
		append(message_field.getText());
		
	}//GEN-LAST:event_Send

	private void Disconnect(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Disconnect
		System.out.println("Disconnect botten clicked");
		client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
		
		jButton_disconnect.setEnabled(false);
		jToggleButton_connect.setEnabled(true);
		message_field.setEnabled(false);
		jButton_send.setEnabled(false);
		my_name.setEnabled(true);

	}//GEN-LAST:event_Disconnect

	private void message_fieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_message_fieldActionPerformed
		// TODO add your handling code here:
	}//GEN-LAST:event_message_fieldActionPerformed

	

	
	
	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		
		// default values
		int portNumber = 1500;
		String serverAddress = "localhost";
		String userName = "Anonymous";

		// depending of the number of arguments provided we fall through
		switch(args.length) {
			// > javac Client username portNumber serverAddr
			case 3:
				serverAddress = args[2];
			// > javac Client username portNumber
			case 2:
				try {
					portNumber = Integer.parseInt(args[1]);
				}
				catch(Exception e) {
					System.out.println("Invalid port number.");
					System.out.println("Usage is: > java Client [username] [portNumber] [serverAddress]");
					return;
				}
			// > javac Client username
			case 1: 
				userName = args[0];
			// > java Client
			case 0:
				break;
			// invalid number of arguments
			default:
				System.out.println("Usage is: > java Client [username] [portNumber] {serverAddress]");
			return;
		}
		
		
	//	new SClient("localhost", 1500);
		
		/* Set the Nimbus look and feel */
		//<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
		/* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
		 * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(SClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(SClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(SClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(SClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		//</editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new SClient().setVisible(true);
			}
		});
	}
//////////////////
	
	
	
	
	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JTextField dst;
	private javax.swing.JTextField ip_ad;
	private javax.swing.JButton jButton_send;
	private javax.swing.JButton jButton_disconnect;
	private javax.swing.JLabel jLabel_name;
	private javax.swing.JLabel jLabel_address;
	private javax.swing.JLabel jLabel_To;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTextArea jTextArea_Main;
	private javax.swing.JToggleButton jToggleButton_connect;
	private javax.swing.JToggleButton jToggleButton_showOnline;
	private javax.swing.JToggleButton jToggleButton_clear;
	private javax.swing.JToggleButton jToggleButton_reset;
	private javax.swing.JTextField message_field;
	private javax.swing.JTextField my_name;
	// End of variables declaration//GEN-END:variables
}
