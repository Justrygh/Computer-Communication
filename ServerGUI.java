package _Codes;

import javax.swing.*;

import Codes.ChatMessage;

//import Codes.Server.ClientThread;


import java.awt.*;
import java.awt.event.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.io.*;


/*
 * The server as a GUI
 */
public class ServerGUI extends JFrame implements ActionListener, WindowListener {
	
	
	// a unique ID for each connection
	private static int uniqueId;
	// an ArrayList to keep the list of the Client
	private ArrayList<ClientThread> al;
	// if I am in a GUI
	private ServerGUI sg;
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
	private ServerGUI server;
	
	/*
	 *  server constructor that receive the port to listen to for connection as parameter
	 *  in console
	 */
	
	/*
	 *  server constructor that receive the port to listen to for connection as parameter
	 *  in console
	 */
	
	
	
	public ServerGUI(int port, ServerGUI sg) {
		// GUI or not
		this.sg = sg;
		// the port
		this.port = port;
		// to display hh:mm:ss
		sdf = new SimpleDateFormat("HH:mm:ss");
		// ArrayList for the Client list
		al = new ArrayList<ClientThread>();
	}
	
	
	// server constructor that receive the port to listen to for connection as parameter
	ServerGUI(int port) {
		this(port, null);

		//super("Chat Server");
		server = null;
		// in the NorthPanel the PortNumber the Start and Stop buttons
		JPanel north = new JPanel();

		// to stop or start the server, we start with "Start"
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
		
		
		// the event and chat room
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
		
		// need to be informed when the user click the close button on the frame
		addWindowListener(this);
		setSize(600, 600);
		setVisible(true);
	}		

	// append message to the two JTextArea
	// position at the end
	public void appendRoom(String str) {
		chat.append(str);
		chat.setCaretPosition(chat.getText().length() - 1);
	}
	public void appendEvent(String str) {
		event.append(str);
	//	event.setCaretPosition(chat.getText().length() - 1);
		
	}
	
	// start or stop where clicked
	public void actionPerformed(ActionEvent e) {
		// if running we have to stop
		if(server != null) {
			
			server.stop();
			server = null;
			tPortNumber.setEditable(true);
			stopStart.setText("Start");
			stop.setEnabled(false);
			stopStart.setEnabled(true);
			return;
		}
      	// OK start the server	
		int port;
		try {
			port = Integer.parseInt(tPortNumber.getText().trim());
		}
		catch(Exception er) {
			appendEvent("Invalid port number");
			return;
		}
		// ceate a new Server
		
		if (stopStart.isEnabled() == true) {
		
		server = new ServerGUI(port, this);
		// and start it as a thread
		new ServerRunning().start();
		stopStart.setEnabled(false);
		
		stop.setEnabled(true);
		tPortNumber.setEditable(false);
		}
		else {
			System.out.println("?");
			
		}
	}
	public void start() {
		keepGoing = true;
		/* create socket server and wait for connection requests */
		try 
		{
			// the socket used by the server
			ServerSocket serverSocket = new ServerSocket(port);

			// infinite loop to wait for connections
			while(keepGoing) 
			{
				// format message saying we are waiting
				display("Server waiting for Clients on port " + port + ".");
				
				Socket socket = serverSocket.accept();  	// accept connection
				// if I was asked to stop
				if(!keepGoing)
					break;
				ClientThread t = new ClientThread(socket);  // make a thread of it
				al.add(t);									// save it in the ArrayList
				t.start();
			}
			// I was asked to stop
			try {
				serverSocket.close();
				for(int i = 0; i < al.size(); ++i) {
					ClientThread tc = al.get(i);
					try {
					tc.sInput.close();
					tc.sOutput.close();
					tc.socket.close();
					}
					catch(IOException ioE) {
						// not much I can do
					}
				}
			}
			catch(Exception e) {
				display("Exception closing the server and clients: " + e);
			}
		}
		// something went bad
		catch (IOException e) {
            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
			display(msg);
		}
	}		
    /*
     * For the GUI to stop the server
     */
	protected void stop() {
		keepGoing = false;
		// connect to myself as Client to exit statement 
		// Socket socket = serverSocket.accept();
		try {
			new Socket("localhost", port);
		}
		catch(Exception e) {
			// nothing I can really do
		}
	}
	// entry point to start the Server
	public static void main(String[] arg) {
		// start server default port 1500
		new ServerGUI(1500);
	}

	/*
	 * If the user click the X button to close the application
	 * I need to close the connection with the server to free the port
	 */
	public void windowClosing(WindowEvent e) {
		// if my Server exist
		if(server != null) {
			try {
				server.stop();			// ask the server to close the conection
			}
			catch(Exception eClose) {
			}
			server = null;
		}
		// dispose the frame
		dispose();
		System.exit(0);
	}
	// I can ignore the other WindowListener method
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
			server.start();         // should execute until if fails
			// the server failed
			stopStart.setText("Start");
			tPortNumber.setEditable(true);
			appendEvent("Server crashed\n");
			server = null;
		}
	}

	
	
	
	
	
	
	
	/*
	 * Display an event (not a message) to the console or the GUI
	 */
	private void display(String msg) {
		String time = sdf.format(new Date()) + " " + msg;
		if(sg == null)
			System.out.println(time);
		else
			sg.appendEvent(time + "\n");
	}

	/** One instance of this thread will run for each client */
	class ClientThread extends Thread {
		// the socket where to listen/talk
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		// my unique id (easier for deconnection)
		int id;
		// the Username of the Client
		String username;
		// the only type of message a will receive
		ChatMessage cm;
		// the date I connect
		String date;

		// Constructore
		ClientThread(Socket socket) {
			// a unique id
			id = ++uniqueId;
			this.socket = socket;
			/* Creating both Data Stream */
			System.out.println("Thread trying to create Object Input/Output Streams");
			try
			{
				// create output first
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());
				// read the username
				username = (String) sInput.readObject();
				display(username + " just connected.");
			}
			catch (IOException e) {
				display("Exception creating new Input/output Streams: " + e);
				return;
			}
			// have to catch ClassNotFoundException
			// but I read a String, I am sure it will work
			catch (ClassNotFoundException e) {
			}
            date = new Date().toString() + "\n";
		}
		
		
		// for a client who logoff using the LOGOUT message
		synchronized void remove(int id) {
			// scan the array list until we found the Id
			for(int i = 0; i < al.size(); ++i) {
				ClientThread ct = al.get(i);
				// found it
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
			// add HH:mm:ss and \n to the message
			String time = sdf.format(new Date());
			String messageLf = time + " " + message + "\n";
			// display message on console or GUI
			if(sg == null)
				System.out.print(messageLf);
			else
				sg.appendRoom(messageLf);     // append in the room window
			
			// we loop in reverse order in case we would have to remove a Client
			// because it has disconnected
			for(int i = al.size(); --i >= 0;) {
				ClientThread ct = al.get(i);
				// try to write to the Client if it fails remove it from the list
				if(!ct.writeMsg(messageLf)) {
					al.remove(i);
					display("Disconnected Client " + ct.username + " removed from list.");
				}
			}
		}
		// what will run forever
		public void run() {
			// to loop until LOGOUT
			boolean keepGoing = true;
			while(keepGoing) {
				// read a String (which is an object)
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
				// the messaage part of the ChatMessage
				String message = cm.getMessage();

				// Switch on the type of message receive
				switch(cm.getType()) {

				case ChatMessage.MESSAGE:
					broadcast(username + ": " + message);
					break;
				case ChatMessage.LOGOUT:
					display(username + " disconnected with a LOGOUT message.");
					keepGoing = false;
					break;
				case ChatMessage.WHOISIN:
					writeMsg("List of the users connected at " + sdf.format(new Date()) + "\n");
					// scan al the users connected
					for(int i = 0; i < al.size(); ++i) {
						ClientThread ct = al.get(i);
						writeMsg((i+1) + ") " + ct.username + " since " + ct.date);
					}
					break;
				}
			}
			// remove myself from the arrayList containing the list of the
			// connected Clients
			remove(id);
			close();
		}
		
		// try to close everything
		private void close() {
			// try to close the connection
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
		 * Write a String to the Client output stream
		 */
		private boolean writeMsg(String msg) {
			// if Client is still connected send the message to it
			if(!socket.isConnected()) {
				close();
				return false;
			}
			// write the message to the stream
			try {
				sOutput.writeObject(msg);
			}
			// if an error occurs, do not abort just inform the user
			catch(IOException e) {
				display("Error sending message to " + username);
				display(e.toString());
			}
			return true;
		}
	}
	
	///////////////////////////chat msg
	
	

	 

    //protected static final long serialVersionUID = 1112122200L;

 

    // The different types of message sent by the Client

    // WHOISIN to receive the list of the users connected

    // MESSAGE an ordinary message

    // LOGOUT to disconnect from the Server

    public static final int WHOISIN = 0;



	public static final int MESSAGE = 1;



	public static final int LOGOUT = 2;

    private int type;

    private String message;

     

    // constructor

    public void ChatMessage(int type, String message) {

        this.type = type;

        this.message = message;

    }

     

    // getters

  /*  public int getType() {

        return type;

    }*/

    public String getMessage() {

        return message;

    }
	
}

