import java.util.ArrayList;

public class Channel {
	private String chName;
	private ArrayList <String> users = new ArrayList <String>();
		
	public Channel(String chName){
		this.chName = chName;
	}
		
	public String getUsers(){
		String temp = "";
		for(int i = 0; i < users.size(); i++){
			temp += users.get(i) + " ";
		}
		return temp;
	}
	
	public void addUser(String user){
		users.add(user);
	}
	
	public boolean removeUser(String user){
		return users.remove(user);
	}
	
	public String getChName(){
		return chName;
	}
}
