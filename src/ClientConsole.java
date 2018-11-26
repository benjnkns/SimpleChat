// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import client.*;
import common.*;

/**
 * This class constructs the UI for a chat client.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ServerConsole 
 *
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge  
 * @author Dr Robert Lagani&egrave;re
 * @version July 2000
 */
public class ClientConsole implements ChatIF{
	//Class variables *************************************************
	final public static int DEFAULT_PORT = 5555;
	//Instance variables **********************************************

	/**
	 * The instance of the client that created this ConsoleChat.
	 */
	ChatClient client;
	boolean isLog = false;
	ArrayList<String> blockedList = new ArrayList<String>();
	ArrayList<String> whoBlocksMe = new ArrayList<String>();
	ArrayList<String> whoIForward = new ArrayList<String>();
	ArrayList<String> channels = new ArrayList<String>();
 	int userStatus;
 	static String passw;
 	GUI mainGUI;

	//Constructors ****************************************************

	/**
	 * Constructs an instance of the ClientConsole UI.
	 *
	 * @param host The host to connect to.
	 * @param port The port to connect on.
	 */
	public ClientConsole(String uID, String host, String password, int port, GUI gui){
		try{
			client= new ChatClient(uID, host, password, port, this);
			mainGUI = gui;
		} 
		catch(IOException exception){
			System.out.println("Error: Can't setup connection!" + " Terminating client.");	
			System.exit(1);
		}
	}


	//Instance methods ************************************************

	/**
	 * This method waits for input from the console.  Once it is 
	 * received, it sends it to the client's message handler.
	 */
	public void accept(){
		try{
			BufferedReader fromConsole = new BufferedReader(new InputStreamReader(System.in));
			String message;
			while (true){
				message = fromConsole.readLine();
				handleMessage(message);
			}
		} 
		catch (Exception exception){
			System.out.println
			("Unexpected error while reading from console!");
		}
	}
	
	void handleMessage(String message){
//		boolean isLog = true;
//		System.out.println("message is: " + message);
		String[] parts = message.split(" ");
		boolean prevFor = false;
//		System.out.println("parts0 = " + parts[0]);
		if(userStatus == 2){
			userStatus = 1;
			mainGUI.updateChatBox( "You are no longer away.");
		}
		switch(parts[0]){
			case "#quit":
				client.handleMessageFromClientUI("#offline" + "---" + client.getUID() + "---" + " " + "---" + " ");
				System.out.println("You have ended your session.");
				client.quit();
				mainGUI.updateChatBox( null);
				break;
			case "#logoff":
				client.handleMessageFromClientUI("#offline" + "---" + client.getUID() + "---" + " " + "---" + " ");
				try {
					client.closeConnection();
				} catch (IOException e1) {
					mainGUI.updateChatBox("Unable to close connection to server" );
				}
				isLog = false;
				mainGUI.updateChatBox ("You have been logged off");
				break;
			case "#sethost":
				if(!isLog) {
					try {
						client.setHost(parts[1]);
						mainGUI.updateChatBox ("Host has been changed to: "+parts[1]);
					}catch(ArrayIndexOutOfBoundsException e) {
						mainGUI.updateChatBox ("No host was provided. Please list a host");
					}
				} 
				else mainGUI.updateChatBox ("You are logged on and cannot change your host.");
				break;
			case "#gethost":
				mainGUI.updateChatBox ("Your current host is: "+client.getHost());
				break;
			case "#setport":
				if(!isLog) {
					try {
						client.setPort(Integer.parseInt(parts[1]));
						mainGUI.updateChatBox( "Port has been changed to: "+client.getPort());
					}catch (ArrayIndexOutOfBoundsException e) {
						mainGUI.updateChatBox ("No port was provided. Please list a port");
					}
				}
				else mainGUI.updateChatBox ("You are logged on and cannot change your port.");
				break;
			case "#getport":
				mainGUI.updateChatBox ("Your current port is: "+client.getPort());
				break;
			case "#login":
				try {
					client.openConnection();
					client.handleMessageFromClientUI("#login"+ "---" + client.getUID() + "---"+ passw + "---" + " ");
				} catch (IOException e) {
					mainGUI.updateChatBox ("Y'all done fucked up");
				}
				isLog = true;
				userStatus = 1;
				mainGUI.updateChatBox ("You have been logged on");
				break;
			case "#block":
				if(parts.length>1){
					client.handleMessageFromClientUI("#block" + "---" + client.getUID() + "---" + parts[1] + "---" + " ");	//this sends the info to the server to check if it's a valid user_id
					mainGUI.updateChatBox( "");
				}
				else mainGUI.updateChatBox("You haven't specified a user to block!");
				break;
			case "#unblock": //Unblock does not currently work
				if(parts.length>0){
					client.handleMessageFromClientUI("#unblock" + "---" + client.getUID() + "---" + parts[1] + "---" + " ");	//this sends the info to the server to check if it's a valid user_id
							//this also needs to be sent to server to send to all consoles to update block lists
					
				}
				mainGUI.updateChatBox( "");
				break;
			case "#whoiblock":		//simply prints out the arraylist of users that i block
				if(blockedList.size()>0){
					String temp = "The list of people you block is: ";
					for(int i=0;i<blockedList.size();i++){
						temp+=blockedList.get(i)+", ";
					}
					mainGUI.updateChatBox( temp);
				}
				else mainGUI.updateChatBox ("You are not blocking any users");
				break;
			case "#whoblocksme":	//simply prints out the arraylist of users that block me
				if(whoBlocksMe.size()>0){
					String temp = "The list of people who block me is: ";
					for(int i=0;i<whoBlocksMe.size();i++){
						temp +=whoBlocksMe.get(i)+", ";
					}
					mainGUI.updateChatBox( temp);
				}
				else mainGUI.updateChatBox ("There are no users that are blocking you");
				break;
			case "#available":
				userStatus = 1;
				client.handleMessageFromClientUI("#available" + "---" + client.getUID() + "---" + " " + "---" + " ");
				mainGUI.updateChatBox( "You are now appearing as available");
				break;
			case "#unavailable":
				userStatus = 3;
				client.handleMessageFromClientUI("#unavailable" + "---" + client.getUID() + "---" + " " + "---" + " ");
				mainGUI.updateChatBox( "You are now appearing as unavailable");
				break;
			case "#status":
				client.handleMessageFromClientUI("#status" + "---" + client.getUID() + "---" + parts[1] + "---" + " ");
//				mainGUI.updateChatBox( "");
				break;
			case "#startforwarding":
				prevFor = false;
				try {
					if(whoIForward.size()>0) {
						for(int i=0; i<whoIForward.size(); i++){
							if(parts[1].equals(whoIForward.get(i))) prevFor = true;
						}
						if(!prevFor) {
							whoIForward.add(parts[1]);
							mainGUI.updateChatBox("You are now forwarding to user: "+parts[1]);
						}
						else mainGUI.updateChatBox ("You are already forwarding to that user!");
					}
					else {
						whoIForward.add(parts[1]);
						mainGUI.updateChatBox ("You are now forwarding all messages to user: "+parts[1]);
					}
				}
				catch(ArrayIndexOutOfBoundsException e){
					mainGUI.updateChatBox ("You need to specify a user to forward to!");
				}
				break;
			case "#cancelforwarding":
				prevFor = false;
				try {
					if(whoIForward.size()>0){
						for(int i=0; i<whoIForward.size(); i++){
							if(parts[1].equals(whoIForward.get(i))){
								prevFor = true;
								whoIForward.remove(i);
								mainGUI.updateChatBox ("You are no longer forwarding to user: "+parts[1]);
							}
						}
						if(!prevFor){
							mainGUI.updateChatBox ("You were not forwarding to that user!");
						}
					}
					else mainGUI.updateChatBox ("You are not currently forwarding to anyone and connot stop forwarding to this user");
				}
				catch(ArrayIndexOutOfBoundsException e){
					mainGUI.updateChatBox ("You need to specify a user to stop forwarding to!");
				}
				break;
			case "#createchannel":
				try{
					client.handleMessageFromClientUI("#createchannel" + "---" + client.getUID() + "--- ---"+parts[1]);
				}
				catch (ArrayIndexOutOfBoundsException e){
					mainGUI.updateChatBox ("You need to specify a channel to create");
				}
				break;
			case "#channel":
				if(parts.length > 2){
					try{
						if(channels.size()>0){
							boolean isInChan = false;
							for (int i=0; i<channels.size();i++){
								if(channels.get(i).equals(parts[1])) isInChan=true;
							}
							if(isInChan){
								String temp="";
								for (int i=2;i<parts.length;i++)
								{
									temp+=parts[i]+" ";
								}
								client.handleMessageFromClientUI("channel---" + client.getUID()+ "---"+parts[1]+"---"+temp);
								mainGUI.updateChatBox( "");
							}
							else mainGUI.updateChatBox ("You are not in that channel!");
						}
						else mainGUI.updateChatBox ("You are not in that channel!");
					}
					catch (ArrayIndexOutOfBoundsException e){
						mainGUI.updateChatBox ("You need to specify a channel to send to");
					}
				}
				mainGUI.updateChatBox( "");
				break;
			case "#join":
				if(userStatus == 3){
					mainGUI.updateChatBox ("You cannot join a channel while unavailable.");
				}
				try{
					client.handleMessageFromClientUI("#join---"+client.getUID()+"--- ---"+parts[1]);
				}
				catch (ArrayIndexOutOfBoundsException e){
					mainGUI.updateChatBox ("You need to specify a channel to join");
				}
				mainGUI.updateChatBox( "");
				break;
			case "#leave":
				try{
					if(channels.size()>0){
						boolean removed = false;
						for(int i=0; i<channels.size(); i++){
							if(channels.get(i).equals(parts[1])){
								channels.remove(i);
								client.handleMessageFromClientUI("#leave---"+client.getUID()+"--- ---"+parts[1]);
								removed = true;
								mainGUI.updateChatBox ("You have left the channel: "+parts[1]);
							}
						}
						if(!removed){
							mainGUI.updateChatBox ("You were not in that channel and cannot leave a channel that you weren't in");
						}
						
					}
					else mainGUI.updateChatBox ("You are not in any channels and cannot leave this channel");
				}
				catch (ArrayIndexOutOfBoundsException e){
					mainGUI.updateChatBox ("You need to specify a channel that you want to leave");
				}
				mainGUI.updateChatBox( "");
				break;
			case "#private":
				if(parts.length > 2){
					String[] messageArray = Arrays.copyOfRange(parts, 2, parts.length);
					StringBuilder builder = new StringBuilder();
					for (String value : messageArray) {
					    builder.append(value + " ");
					}
					String messageToSend = builder.toString();
					client.handleMessageFromClientUI("#private" + "---" + client.getUID() + "---" + parts[1] + "---" + messageToSend);
				}
				mainGUI.updateChatBox( "");
				break;
			default: 
//				mainGUI.updateChatBox(" " + "---" + client.getUID() + "---" + "*" + "---" + message + '\n');
				client.handleMessageFromClientUI(" " + "---" + client.getUID() + "---" + "*" + "---" + message);
		}
	}
	
	
	

	/**
	 * This method overrides the method in the ChatIF interface.  It
	 * displays a message onto the screen.
	 *
	 * @param message The string to be displayed.
	 */
	public void display(String message){
		String temp = message+"";
//		mainGUI.updateChatBox(message + '\n');
		String[] parts = temp.split("---");
		if (parts[0].equals("#close") || parts[0].equals("#quit")){
			try{
				client.closeConnection();
				mainGUI.updateChatBox("> " + message+"\nYou have been logged off by the server!");
			} catch (Exception e) {
				mainGUI.updateChatBox ("Enexpected error while reading from console!");
			}
		}
		if (!(parts[2].equals(client.getUID()) || parts[2].equals("*") || isActiveChannel(parts[2]))){ // if this message was not meant for me
		//	mainGUI.updateChatBox("");
		}
		else{ // message was meant for me or everybody
			boolean isServerBlocked = false;
			for (int i =0; i < blockedList.size(); i++){
				if(blockedList.get(i).equals("Server")){isServerBlocked = true;}
				else if (parts[1].equals(blockedList.get(i))){
					mainGUI.updateChatBox(""); // the message is from somebody i've blocked
					return;
				}
			}
			
			switch (parts[0]){
				case "#block":
					if (parts[3].equals("blockInvalid")){
						mainGUI.updateChatBox ("The user you wish to block does not exist");
					}
					else if (parts[3].equals("blockSameUser")){
						mainGUI.updateChatBox ("You cannot block yourself!!");
					}
					else{
						boolean exists = false;
						for(int i=0; i<blockedList.size();i++){
							if(parts[3].equals(blockedList.get(i))){
								exists = true;
								mainGUI.updateChatBox ("You have already blocked the user: "+parts[3]);
							}
						}
						if (!exists){
							blockedList.add(parts[3]);
							mainGUI.updateChatBox ("You have blocked user: "+parts[3]);
						}
					}
					mainGUI.updateChatBox( "");
					break;
				case "#unblock":
					if (parts[3].equals("unblockInvalid")){
						mainGUI.updateChatBox ("The user you wish to unblock does not exist");
					}
					else if (parts[3].equals("unblockSameUser")){
						mainGUI.updateChatBox ("You cannot unblock yourself!!");
					}
					else{
						boolean exists = false;
						for(int i=0; i<blockedList.size();i++){
							if(parts[3].equals(blockedList.get(i))){
								exists = true;
								blockedList.remove(i);
								mainGUI.updateChatBox ("You have unblocked user: "+ parts[3]);
							}
						}
						if (!exists){
							mainGUI.updateChatBox ("The user: "+parts[3]+" was not previously blocked!");
						}
					}
					mainGUI.updateChatBox( "");
					break;
				case "#blocked":
					boolean exists = false;
					for(int i=0; i<whoBlocksMe.size();i++){
						if(parts[3].equals(whoBlocksMe.get(i)))
							exists = true;
					}
					if(!exists) whoBlocksMe.add(parts[3]);
					break;
				case "#unblocked":
					for(int i=0;i<whoBlocksMe.size();i++){
						if(parts[3].equals(whoBlocksMe.get(i))){
							whoBlocksMe.remove(i);
							break;
						}
					}
					break;
				case "#available":
					mainGUI.updateChatBox ("You have set your status to available.");
					break;
				case "#unavailable":
					mainGUI.updateChatBox ("You have set your status to unavailable.");
					break;
				case "#status":
					mainGUI.updateChatBox (parts[1] + "'s current status is " + parts[3]);
					break;
				case "#away":
					if(userStatus != 2 && userStatus != 3){
						userStatus = 2;
						mainGUI.updateChatBox ("You have been marked as Away by the server.");
					}
					mainGUI.updateChatBox( "");
					break;
				case "#join":
					String[] tem = parts[3].split(" ");
					if(tem[0].equals("success")){
						channels.add(tem[1]);
						mainGUI.updateChatBox ("Joined channel");
					}
					else if(parts[3].equals("!exist")){
						mainGUI.updateChatBox("That channel does not exist!");
					}
					else mainGUI.updateChatBox ("Something bad has happened when joining this channel");
					break;
				case "#channelstatus":
					if(parts[3].equals(" ")){
						mainGUI.updateChatBox ("There are no users in channel " + parts[1]);
					}else mainGUI.updateChatBox ("The users in channel " + parts[1] + " are" + parts[3]);
					break;
				case "#badlogin":
					System.out.println("Incorrect password. Terminating client.");
					System.exit(0);
					break;
				default:
					if(((!(userStatus == 3)) || parts[1].equals("Server")) && !(isServerBlocked)){
						mainGUI.updateChatBox("> " + parts[1] + ": " + parts[3]);
						if(whoIForward.size()>0){
							for(int i=0; i<whoIForward.size(); i++){
								client.handleMessageFromClientUI(" " + "---" + parts[1] + "---" + whoIForward.get(i) + "---Forwarded from " + client.getUID() + ": " + parts[3]);
							}
						}
					}
				}
		}
//		mainGUI.updateChatBox( "");
		
	}
	
	private boolean isActiveChannel(String chan){
		if(channels.size()>0){
			for(int i=0; i<channels.size();i++){
				if(chan.equals(channels.get(i))) return true;
			}
		}
		return false;
	}


	//Class methods ***************************************************

	/**
	 * This method is responsible for the creation of the Client UI.
	 *
	 * @param args[0] The host to connect to.
	 */
//	public static void main(String[] args){
//		String user_id="";
//		String host = "", password = "";
//		int port = 0;  //The port number
//		try{
//			user_id=args[0];
//			password=args[1];
//			passw = password;
//		}catch(ArrayIndexOutOfBoundsException e){
//			System.out.println("ERROR - No login ID specified. Connection aborted.");
//		}
//		try{
//			host = args[2];
//		}
//		catch(ArrayIndexOutOfBoundsException e){
//			host = "localhost";
//		}
//		try{
//			port = Integer.parseInt(args[3]);
//		}catch (ArrayIndexOutOfBoundsException e){
//			port = DEFAULT_PORT;
//		}
//		ClientConsole chat= new ClientConsole(user_id, host, password, port);
//		chat.accept();  //Wait for console data
//	}
}
//End of ConsoleChat class
