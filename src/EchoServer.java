// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import java.util.ArrayList;

import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer {
	//Class variables *************************************************
	final public static int DEFAULT_PORT = 5555;
	public static ArrayList <String> user_list;
	public static ArrayList <String> logged_in = new ArrayList <String> ();
	public static ArrayList <Channel> all_channels = new ArrayList <Channel>();
	public ArrayList<Status> userStatus = new ArrayList<Status>();
	//Constructors ****************************************************

	/**
	 * Constructs an instance of the echo server.
	 *
	 * @param port The port number to connect on.
	 */
	public EchoServer(int port) {
		super(port);
	}


	//Instance methods ************************************************

	/**
	 * This method handles any messages received from the client.
	 *
	 * @param msg The message received from the client.
	 * @param client The connection from which the message originated.
	 */
	public void handleMessageFromClient(Object msg, ConnectionToClient client){
		//System.out.println("Command received: " + msg + " from " + client);
		String temp = msg+"";
		System.out.println(msg);
		String[] parts = temp.split("---");
		
		for(int i = 0; i < userStatus.size(); i++){
			if(parts[1].equals(userStatus.get(i).getUID())){
				userStatus.get(i).resetAway();
				if(userStatus.get(i).getAvailability() == 2){
					userStatus.get(i).setAvilability(1);
				}
			}
			if(userStatus.get(i).isAway()){
				userStatus.get(i).setAvilability(2);
				this.sendToAllClients("#away" + "---" + userStatus.get(i).getUID() + "---" + userStatus.get(i).getUID() + "---" + " ");
			}
		}
		
		switch (parts[0]){
			case "#login":
				boolean isUser = false;
				for(int i=0; i<user_list.size(); i++){
					System.out.println(user_list.size());
					String[] userpass = user_list.get(i).split(" ");
					if(userpass[0].equals(parts[1])&&userpass[1].equals(parts[2])){
						isUser = true;
					}
				}
				if (!isUser){
					this.sendToAllClients("#badlogin" + "---" + "Server" + "---" + parts[1] + "---" + "Bad password entered.");
					break;
				}
				if (logged_in.contains(parts[1])) {
					this.sendToAllClients("#error" + "---" + "Server" + "---" + parts[1] + "---" + "You are already logged in");
				}
				else{
					logged_in.add(parts[1]);
				}
				boolean added = false;
				for(int i=0; i < userStatus.size(); i++){
					if(parts[1].equals(userStatus.get(i).getUID())){
						userStatus.get(i).setAvilability(1);
						added = true;
					}
				}
				if(!added) userStatus.add(new Status(parts[1], 1));
				break;
			case "#close":
				if (parts[1].equals("Server")){
					this.sendToAllClients("#close" + "---" + "Server" + "---" + "*" + "---" + "Server is closing");
					this.stopListening();
				}
				break;
			case "#stop":
				if (parts[1].equals("Server")){
					this.stopListening();
				}
				break;
			case "#quit":
				if (parts[1].equals("Server")){
					this.sendToAllClients("#quit" + "---" + "Server" + "---" + "*" + "---" + "Server is closing");
					System.exit(0);
				}
				break;
			case "#setport":
				if (parts[1].equals("Server")){
					if(this.isListening() || this.getNumberOfClients() > 1){
						this.sendToAllClients(" " + "---" + "Server" + "---" + parts[1] + "---" + "Server is not closed. Cannot set port.");
					}else{
						String portValue = parts[3];
						this.setPort(Integer.parseInt(portValue));
						System.out.println("Set port to " + portValue);
					}
				}
				break;
			case "#getport":
				//sends port number from server to the requester of getport
				this.sendToAllClients(" " + "---" + "Server" + "---" + parts[1] + "---" + this.getPort());
				break;
			case "#start":
				if (parts[1].equals("Server")){
					if (this.isListening()){
						this.sendToAllClients(" " + "---" + "Server" + "---" + "Server" + "---" + "Server is already Listening on port " + this.getPort());
					}
					else{
						try {
							this.listen(); //Start listening for connections
						} 
						catch (Exception ex) {
							this.sendToAllClients(" " + "---" + "Server" + "---" + "Server" + "---" +"ERROR - Could not listen for clients!" );
						}
					}
				}
				break;
			case "#block":
				if (parts[1].equals (parts[2])){
					this.sendToAllClients("#block" + "---" + "Server" + "---" + parts[1] + "---" +  "blockSameUser");
				}
				else{
					boolean isValid = false;
					for(int i=0;i<user_list.size();i++){
						String[] userpass = user_list.get(i).split(" "); 
						if(parts[2].equals(userpass[0])){
							isValid=true;
							this.sendToAllClients("#blocked" + "---" + "Server" +  "---" + parts[2] + "---" + parts[1]); // tells the user they've been blocked
							this.sendToAllClients("#block" + "---" + "Server" +  "---" + parts[1] + "---" + parts[2]);
						}
					}
					if(!isValid)
						this.sendToAllClients("#block" + "---" + "Server" + "---" + parts[1] + "blockInvalid");
				}
				break;
			case "#unblock":
				if (parts[1].equals (parts[2])){
					this.sendToAllClients("#unblock" + "---" + "Server" + "---" + parts[1] + "---" +  "unblockSameUser");
				}
				else if (parts[2].equals("*")){
					for(int i=0;i<user_list.size();i++){
						String[] userpass = user_list.get(i).split(" ");
						if(!userpass[0].equals(parts[1])){
							this.sendToAllClients("#unblock" + "---" + "Server" +  "---" + parts[1] + "---" + userpass[0]);	//this sends user1 isunblocking user2 for all user2 in the list except for itself (user1)
							this.sendToAllClients("#unblocked" + "---" + "Server" +  "---" + userpass[0] + "---" + parts[1]);	//tells all users they've been unblocked
						}
					}
				}
				else{
					boolean isValid=false;
					for(int i=0;i<user_list.size();i++){
						String[] userpass = user_list.get(i).split(" ");
						if(parts[2].equals(userpass[0])){
							isValid = true;
							this.sendToAllClients("#unblocked" + "---" + "Server" +  "---" + parts[2] + "---" + parts[1]); //tells the user they've been unblocked
							this.sendToAllClients("#unblock" + "---" + "Server" +  "---" + parts[1] + "---" + parts[2]);
						}
					}
					if(!isValid){
						this.sendToAllClients("#unblock" + "---" + "Server" + "---" + parts[1] + "unblockInvalid");
					}
				}
				break;
			case "#available":
				for(int i=0; i < userStatus.size(); i++){
					if(parts[1].equals(userStatus.get(i).getUID())){
						userStatus.get(i).setAvilability(1);
						this.sendToAllClients("#available" + "---" + parts[1] + "---" + parts[1] + "---" + " ");
					}
				}
				break;
			case "#unavailable":
				for(int i=0; i < userStatus.size(); i++){
					if(parts[1].equals(userStatus.get(i).getUID())){
						userStatus.get(i).setAvilability(3);
						this.sendToAllClients("#unavailable" + "---" + parts[1] + "---" + parts[1] + "---" + " ");
					}
				}
				break;
			case "#offline":
				for(int i=0; i < userStatus.size(); i++){
					if(parts[1].equals(userStatus.get(i).getUID())){
						userStatus.get(i).setAvilability(4);
					}
				}
				break;
			case "#status":
				boolean noPerson = true;
				for(int i=0; i < userStatus.size(); i++){
					if(parts[2].equals(userStatus.get(i).getUID())){
						String userAvailab = "---. ERROR: No availability found.";
						if(userStatus.get(i).getAvailability() == 1){
							userAvailab = "Online.";
						}else if(userStatus.get(i).getAvailability() == 2){
							userAvailab = "Away.";
						}else if(userStatus.get(i).getAvailability() == 3){
							userAvailab = "Unavailable.";
						}else if(userStatus.get(i).getAvailability() == 4){
							userAvailab = "Offline.";
						}
						this.sendToAllClients("#status" + "---" + parts[2] + "---" + parts[1] + "---" + userAvailab);
						noPerson = false;
					}
				}
				if(noPerson){
					boolean noChannel = true;
					for(int i=0;i<all_channels.size();i++){
						if(all_channels.get(i).getChName().equals(parts[2])){
							String users = all_channels.get(i).getUsers();
							
							//Split users string into separate users
							String[] tempUsers = users.split(" ");
							
							//Find each user in userStatus arrayList
							String chanStatus = " ";
							for(int j=0; j < userStatus.size(); j++){
								for(int k=0; k < tempUsers.length; k++){
									if(tempUsers[k].equals(userStatus.get(j).getUID())){
										String userAvailability = " ---. ERROR 243. ";
										if(userStatus.get(j).getAvailability() == 1){
											userAvailability = " Online";
										}else if(userStatus.get(j).getAvailability() == 2){
											userAvailability = " Away";
										}else if(userStatus.get(j).getAvailability() == 3){
											userAvailability = " Unavailable";
										}else if(userStatus.get(j).getAvailability() == 4){
											userAvailability = " Offline";
										}
										chanStatus += tempUsers[k] + " -" + userAvailability + ", ";
									}
								}
							}
							
							this.sendToAllClients("#channelstatus" + "---" + all_channels.get(i).getChName() + "---" + parts[1] + "---" + chanStatus);
							noChannel = false;
						}
					}
					if(noChannel){
						this.sendToAllClients("#status" + "---" + parts[2] + "---" + parts[1] + "---" + "Offline.");
					}
				}
				break;
			case "#createchannel":
				if(all_channels.size()>0){
					boolean isChan = false;
					for(int i=0;i<all_channels.size();i++){
						if(all_channels.get(i).getChName().equals(parts[3])){
							this.sendToAllClients("#createdchannel"+"---"+"Server"+"---"+parts[1]+"---"+"Error: That channel already exists!");
							isChan=true;
						}
					}
					if(!isChan){
						all_channels.add(new Channel(parts[3]));
						this.sendToAllClients("#createchannel"+"---"+"Server"+"---"+parts[1]+"---"+"Created a new channel");
					}
				}
				else{
					all_channels.add(new Channel(parts[3]));
					this.sendToAllClients("#createchannel" + "---" + "Server" + "---" + parts[1] + "---" + "Created new channel");
				}
				break;
			case "#join":
				if(all_channels.size()>0){
					boolean isChan=false;
					for(int i=0;i<all_channels.size();i++){
						if(all_channels.get(i).getChName().equals(parts[3])){
							this.sendToAllClients("#join---Server---"+parts[1]+"---success "+parts[3]);
							all_channels.get(i).addUser(parts[1]);
							isChan=true;
						}
					}
					if(!isChan) this.sendToAllClients("#join"+"---"+"Server"+"---"+parts[1]+"---!exist");
				}
				else{
					this.sendToAllClients("#join"+"---"+"Server"+"---"+parts[1]+"---!exist");
				}
				break;
			case "#leave":
				for(int i=0;i<all_channels.size();i++){
					if(all_channels.get(i).getChName().equals(parts[3])){
						if(!(all_channels.get(i).removeUser(parts[1]))){
							System.out.println("Error removing user " + parts[1] + " from channel " + parts[3] + ".");
						}
					}
				}
				break;
			case "#channel":
				this.sendToAllClients("#channel"+"---"+parts[1]+"---"+parts[2]+"---"+parts[3]);
				break;
			default: this.sendToAllClients(" " + "---" + parts[1] + "---" + parts[2] + "---" + parts[3]);
		}
	}

	/**
	 * This method overrides the one in the superclass.  Called
	 * when the server starts listening for connections.
	 */
	protected void serverStarted(){
		System.out.println
		("Server listening for connections on port " + getPort());
	}

	/**
	 * This method overrides the one in the superclass.  Called
	 * when the server stops listening for connections.
	 */
	protected void serverStopped(){
		System.out.println
		("Server has stopped listening for connections.");
	}

	//Class methods ***************************************************

	/**
	 * This method is responsible for the creation of 
	 * the server instance (there is no UI in this phase).
	 *
	 * @param args[0] The port number to listen on.  Defaults to 5555 
	 *          if no argument is entered.
	 */
	public static void main(String[] args) {
		int port = 0; //Port to listen on

		try{
			BufferedReader reader = new BufferedReader(new FileReader(args[0]));
			int numEntries = Integer.parseInt(reader.readLine());
			user_list = new ArrayList <String> (numEntries);
			for (int i=0; i<numEntries; i++) {
				user_list.add(reader.readLine());
			}
			reader.close();
		} catch(IOException e) {
			
		}
		try{
			port = Integer.parseInt(args[1]); //Get port from command line
		}
		catch(Throwable t){
			port = DEFAULT_PORT; //Set port to 5555
		}

		EchoServer echoServer = new EchoServer(port);

		try {
			echoServer.listen(); //Start listening for connections
		} 
		catch (Exception ex) {
			System.out.println("ERROR - Could not listen for clients!");
		}
	}
}
//End of EchoServer class
