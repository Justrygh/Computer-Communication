package Codes;

import javax.swing.*;

import Codes.ChatMessage;



import java.awt.*;
import java.awt.event.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.io.*;




/*
 * This class represents a server
 * 
 * We found the following web site pretty useful so we used it in this program
 * https://www.dreamincode.net/forums/topic/259777-a-simple-chat-program-with-clientserver-gui-optional/
 *  
 *   @author yoni and Eli
 */





public class Server extends JFrame implements ActionListener, WindowListener {


	// a unique ID for each connection
	private static int uniqueId;
	// an ArrayList to keep the list of the Client
	private ArrayList<ClientThread> al;
	// if I am in a GUI
	private Server sg;
	// to display time
	private SimpleDateFormat sdf;
	// the port number to listen for connection
	private int port;
	// the boolean that will be turned of to stop the server
	private boolean keepGoing;

	private static final long serialVersionUID = 1L;
	// the stop and start buttons
	private JButton stopStart; private JButton stop;
	// JTextArea for the chat room and the events
	private JTextArea chat, event;
	// The port number
	private JTextField tPortNumber;
	// my server
	private Server server;

	/*
	 *  server constructor that receive the port to listen to for connection as parameter
	 *  
	 */


	public Server(int port, Server sg) {
		this.sg = sg;
		this.port = port;
		sdf = new SimpleDateFormat("HH:mm:ss");
		al = new ArrayList<ClientThread>();
	}


	/*
	 * server constructor
	 * */ 
	Server(int port) {
		this(port, null);
		server = null;
		JPanel north = new JPanel();

		stopStart = new JButton("Start");
		stopStart.addActionListener(this);
		north.add(stopStart);
		add(north, BorderLayout.NORTH);

		north.add(new JLabel("                                                    Server                                                                               "));
		tPortNumber = new JTextField("  " + port);
		north.add(tPortNumber);
		tPortNumber.setVisible(false);

		stop = new JButton("stop");
		stop.setEnabled(false);
		stop.addActionListener(this);
		north.add(stop);
		add(north, BorderLayout.NORTH);


		JPanel center = new JPanel(new GridLayout(2,1));
		chat = new JTextArea(80,80);
		chat.setEditable(false);
		//chat.setVisible(false);
		appendRoom("Chat room.\n");
		center.add(new JScrollPane(chat));
		event = new JTextArea(80,80);
		event.setEditable(false);
		appendEvent("Events log.\n");
		center.add(new JScrollPane(event));	
		add(center);
		addWindowListener(this);
		setSize(600, 600);
		setVisible(true);
	}		

/*	append message to the two JTextArea position at the end
*/	public void appendRoom(String str) {
		chat.append(str);
		chat.setCaretPosition(chat.getText().length() - 1);
	}
	public void appendEvent(String str) {
		event.append(str);
	}

/*	 start or stop where clicked
*/	
	public void actionPerformed(ActionEvent e) {
		if(server != null) {

			server.stop();
			server = null;
			tPortNumber.setEditable(true);
			stopStart.setText("Start");
			stop.setEnabled(false);
			stopStart.setEnabled(true);
			return;
		}
	
		int port;
		try {
			port = Integer.parseInt(tPortNumber.getText().trim());
		}
		catch(Exception er) {
			appendEvent("Invalid port number");
			return;
		}

		if (stopStart.isEnabled() == true) {

			server = new Server(port, this);
			new ServerRunning().start();
			stopStart.setEnabled(false);

			stop.setEnabled(true);
			tPortNumber.setEditable(false);
		}


	}
	
	/*start the server*/
	public void start() {
		keepGoing = true;
		try 
		{
			ServerSocket serverSocket = new ServerSocket(port);
			while(keepGoing) 
			{
				Socket socket = serverSocket.accept();  
				if(!keepGoing)	break;
				ClientThread t = new ClientThread(socket); 
				al.add(t);									
				t.start();
			}
			try {
				serverSocket.close();
				for(int i = 0; i < al.size(); ++i) {
					ClientThread tc = al.get(i);
					try {
						tc.sInput.close();	tc.sOutput.close();	tc.socket.close();
					}
					catch(IOException ioE) {
					}
				}
			}
			catch(Exception e) {
			}
		}
	
		catch (IOException e) {
			String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
			display(msg);
		}
	}		
	/*
	 *  stop the server
	 */

	@SuppressWarnings("resource")
	protected void stop() {
		keepGoing = false;

		try {
			new Socket("localhost", port);
		}
		catch(Exception e) {
		}
	}
/*	entry point to start the Server
*/	public static void main(String[] arg) {
		new Server(1500);
	}

	/*
	 * by closing the window disconnect from the server
	 */
	public void windowClosing(WindowEvent e) {
		if(server != null) {
			try {
				server.stop();			
			}
			catch(Exception eClose) {
			}
			server = null;
		}
		dispose();
		System.exit(0);
	}
	public void windowClosed(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}


	/*
	 * A thread to run the Server
	 */
	class ServerRunning extends Thread {
		public void run() {
			server.start();
			stopStart.setText("Start");
			tPortNumber.setEditable(true);
			appendEvent("Server is not connected\n");
			server = null;
		}
	}

	/*adds a msg which only the user sees */
	private void display(String msg) {
		String time = sdf.format(new Date()) + " " + msg;
			sg.appendEvent(time + "\n");
	}

	class ClientThread extends Thread {
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		int id;
		String username;
		ChatMessage cm;
		String date;

		ClientThread(Socket socket) {
			// a unique id
			id = ++uniqueId;
			this.socket = socket;
			try
			{
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());
				username = (String) sInput.readObject();
				broadcast("system:" +username + " just connected.");
			}
			catch (IOException e) {
				return;
			}
			catch (ClassNotFoundException e) {
			}
			date = new Date().toString() + "\n";
		}

		/*remove a user*/
		synchronized void remove(int id) {
			for(int i = 0; i < al.size(); ++i) {
				ClientThread ct = al.get(i);
				if(ct.id == id) {
					al.remove(i);
					return;
				}
			}
		}
		/*
		 *  to broadcast a message to all Clients 
		 */
		private synchronized void broadcast(String message) {
			String time = sdf.format(new Date());
			String messageLf = time + " " + message + "\n";
			String user = username;

				sg.appendRoom(messageLf);   
			
			if(messageLf.contains("`") == true) {
				
				String _msg_Tosend[] = messageLf.split("`");
				messageLf = _msg_Tosend[0];
							
			
				_msg_Tosend[1] = _msg_Tosend[1].replaceAll("\n","");
				messageLf = messageLf.replaceAll("\n","");
			
				
				for(int i = al.size(); --i >= 0;) {
					
					ClientThread ct = al.get(i);
					
				
					if(ct.username.equals(_msg_Tosend[1].substring(0, _msg_Tosend[1].length())) ) {
						ct.writeMsg("<PM> " +  messageLf +"\n" );
						ct.writeMsg("<msg_lst><num_of_msgs>  "+ user + ": " +  messageLf + "<end>\n");
					}
					
					if (ct.username.equals(user)) {
						ct.writeMsg("System: PM To  "+ user + ":\n>" +  _msg_Tosend[0] + "\n");
					}


	
				}
				
				return;
			}
			
			
			else {
				for(int i = al.size(); --i >= 0;) {
					ClientThread ct = al.get(i);
					// try to write to the Client if it fails remove it from the list
					if(!ct.writeMsg(messageLf)) {
						al.remove(i);
						display("Systme: Disconnected Client " + ct.username + " removed from list.");
					}
				}
			}
		}
		
		/*this function runs throughout the entire time*/
		public void run() {
			boolean keepGoing = true;
			while(keepGoing) {
				try {
					cm = (ChatMessage) sInput.readObject();
				}
				catch (IOException e) {
					display(username + " Exception reading Streams: " + e);
					break;				
				}
				catch(ClassNotFoundException e2) {
					break;
				}
				String message = cm.getMessage();

				switch(cm.getType()) {

				case ChatMessage.MESSAGE:
				
						broadcast(username + ": " + message);			

					break;
				case ChatMessage.LOGOUT:
					broadcast("system: " +username + " disconnected.");
					keepGoing = false;
					break;
				case ChatMessage.WHOISIN:
					writeMsg("System: <users_lst><num_of_users>");
				
					for(int i = 0; i < al.size(); ++i) {
						ClientThread ct = al.get(i);
						writeMsg((i+1) + ") " + ct.username + " since " + ct.date);
					}
					writeMsg("<end>\n");
					break;
				}
			}

			remove(id);
			close();
		}

/*		  close the connections
*/		private void close() {
			try {
				if(sOutput != null) sOutput.close();
			}
			catch(Exception e) {}
			try {
				if(sInput != null) sInput.close();
			}
			catch(Exception e) {};
			try {
				if(socket != null) socket.close();
			}
			catch (Exception e) {}
		}

		/*
		 * send a msg to the client
		 */
		public boolean writeMsg(String msg) {
			if(!socket.isConnected()) {
				close();
				return false;
			}
			try {
				sOutput.writeObject(msg);
			}

	        catch(IOException e) {

			}
			return true;
		}
	}

	

}

