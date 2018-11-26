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
public class ServerConsole implements ChatIF{
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
	

	//Constructors ****************************************************

	/**
	 * Constructs an instance of the ClientConsole UI.
	 *
	 * @param host The host to connect to.
	 * @param port The port to connect on.
	 */
	public ServerConsole(String uID, String host, String password, int port){
		try{
			client= new ChatClient(uID, host, password, port, this);
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
				this.handleMessage(message);
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
		switch(parts[0]){
			case "#quit":
				client.handleMessageFromClientUI("#quit" + "---" + client.getUID() + "---" + " " + "---" + " ");
				break;
			case "#stop":
				client.handleMessageFromClientUI("#stop" + "---" + client.getUID() + "---" + " " + "---" + " ");
				break;
			case "#close":
				client.handleMessageFromClientUI("#close" + "---" + client.getUID() + "---" + " " + "---" + " ");
				break;
			case "#setport":
				client.handleMessageFromClientUI("#setport" + "---" + client.getUID() + "---" + " " + "---" + parts[1]);
				break;
			case "#getport":
				client.handleMessageFromClientUI("#getport" + "---" + client.getUID() + "---" + " " + "---" + " ");
				break;
			case "#start":
				client.handleMessageFromClientUI("#start" + "---" + client.getUID() + "---" + " " + "---" + " ");
				break;
			case "#block":
				if(parts.length>1){
					client.handleMessageFromClientUI("#block" + "---" + client.getUID() + "---" + parts[1] + "---" + " ");	//this sends the info to the server to check if it's a valid user_id
				}
				else System.out.println("You haven't specified a user to block!");
				break;
			case "#unblock":
				if(parts.length>0){
					client.handleMessageFromClientUI("#unblock" + "---" + client.getUID() + "---" + parts[1] + "---" + " ");	//this sends the info to the server to check if it's a valid user_id
							//this also needs to be sent to server to send to all consoles to update block lists
				}
				break;
				
			case "#whoiblock":		//simply prints out the arraylist of users that i block
				if(blockedList.size()>0){
					System.out.print("The list of people you block is: ");
					for(int i=0;i<blockedList.size();i++){
						System.out.print(blockedList.get(i)+", ");
					}
					System.out.println("");
				}
				else System.out.println("You are not blocking any users");
				break;
			case "#whoblocksme":	//simply prints out the arraylist of users that block me
				if(whoBlocksMe.size()>0){
					System.out.print("The list of people who block me is: ");
					for(int i=0;i<whoBlocksMe.size();i++){
						System.out.print(whoBlocksMe.get(i)+", ");
					}
					System.out.println("");
				}
				else System.out.println("There are no users that are blocking you");
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
							System.out.println("You are now forwarding to user: "+parts[1]);
							break;
						}
						else System.out.println("You are already forwarding to that user!");
					}
					else {
						whoIForward.add(parts[1]);
						System.out.println("You are now forwarding all messages to user: "+parts[1]);
					}
				}
				catch(ArrayIndexOutOfBoundsException e){
					System.out.println("You need to specify a user to forward to!");
				}
				break;
			case "cancelforwarding":
				prevFor = false;
				try {
					if(whoIForward.size()>0){
						for(int i=0; i<whoIForward.size(); i++){
							if(parts[1].equals(whoIForward.get(i))){
								prevFor = true;
								whoIForward.remove(i);
								System.out.println("You are no longer forwarding to user: "+parts[1]);
								break;
							}
						}
						if(!prevFor){
							System.out.println("You were not forwarding to that user!");
							break;
						}
					}
					else System.out.println("You are not currently forwarding to anyone and connot stop forwarding to this user");
				}
				catch(ArrayIndexOutOfBoundsException e){
					System.out.println("You need to specify a user to stop forwarding to!");
				}
				break;
				
			case "#private":
				String[] messageArray = Arrays.copyOfRange(parts, 2, parts.length);
				StringBuilder builder = new StringBuilder();
				for (String value : messageArray) {
				    builder.append(value + " ");
				}
				String messageToSend = builder.toString();
				client.handleMessageFromClientUI("#private" + "---" + client.getUID() + "---" + parts[1] + "---" + messageToSend);
				break;
				
			default: client.handleMessageFromClientUI(" " + "---" + client.getUID() + "---" + "*" + "---" + message);
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
		String[] parts = temp.split("---");		
		if (parts[0].equals("#quit")){
			try{
				client.closeConnection();
				System.out.println("> " + "Server: Server has shut down!");
			} catch (Exception e) {
				System.out.println("Enexpected error while reading from console!");
			}
			return;
		}
		if (!(parts[2].equals(client.getUID()) || parts[2].equals("*"))){ // if this message was not meant for me
			return;
		}
		else{ // message was meant for me or everybody
			for (int i =0; i < blockedList.size(); i++){
				if (parts[1].equals(blockedList.get(i))){
					return; // the message is from somebody i've blocked
				}
			}
			
			switch (parts[0]){
				case "#block":
					if (parts[3].equals("blockInvalid")){
						System.out.println("The user you wish to block does not exist");
					}
					else if (parts[3].equals("blockSameUser")){
						System.out.println("You cannot block yourself!!");
					}
					else{
						boolean exists = false;
						for(int i=0; i<blockedList.size();i++){
							if(parts[3].equals(blockedList.get(i))){
								exists = true;
								System.out.println("You have already blocked the user: "+parts[3]);
							}
						}
						if (!exists){
							blockedList.add(parts[3]);
							System.out.println("You have blocked user: "+parts[3]);
						}
					}
					break;
				case "#unblock":
					if (parts[3].equals("unblockInvalid")){
						System.out.println("The user you wish to unblock does not exist");
					}
					else if (parts[3].equals("unblockSameUser")){
						System.out.println("You cannot unblock yourself!!");
					}
					else{
						boolean exists = false;
						for(int i=0; i<blockedList.size();i++){
							if(parts[3].equals(blockedList.get(i))){
								exists = true;
								blockedList.remove(i);
								System.out.println("You have unblocked user: "+ parts[3]);
							}
						}
						if (!exists){
							System.out.println("The user: "+parts[3]+" was not previously blocked!");
						}
					}
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
				default:
					System.out.println("> " + parts[1] + ": " + parts[3]);
				}
		}
		return;
	}


	//Class methods ***************************************************

	/**
	 * This method is responsible for the creation of the Client UI.
	 *
	 * @param args[0] The host to connect to.
	 */
	public static void main(String[] args){
		String user_id="Server";
		String host = "", password="";
		int port = 0;  //The port number
		try{
			password = args[0];
		}
		catch(ArrayIndexOutOfBoundsException e) {
			System.out.println("ERROR: No password provided for logging onto server console, connection aborted");
			System.exit(0);
		}
		try{
			host = args[1];
		}
		catch(ArrayIndexOutOfBoundsException e){
			host = "localhost";
		}
		try{
			port = Integer.parseInt(args[2]);
		}catch (ArrayIndexOutOfBoundsException e){
			port = DEFAULT_PORT;
		}
		ServerConsole serv= new ServerConsole(user_id, host, password, port);
		serv.accept();  //Wait for console data
	}
}
//End of ConsoleChat class