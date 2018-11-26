import java.util.concurrent.TimeUnit;

// The purpose of this class is to combine a User's ID with their status for easy manipulation.
public class Status {
	private int availability; //Availability - 1:Online, 2:Idle, 3:Unavailable, 4:Offline
	private String uID;
	private long awayTime;
	
	public Status(String uID, int avail){
		this.uID = uID;
		this.availability = avail;
		this.awayTime = System.nanoTime();
	}
	
	public int getAvailability(){
		return availability;
	}
	
	public String getUID(){
		return uID;
	}
	
	public void setUID(String uID){
		this.uID = uID;
	}
	
	public void setAvilability(int avail){
		this.availability = avail;
	}
	
	public void resetAway(){
		this.awayTime = System.nanoTime();
	}
	
	public boolean isAway(){
		long difference = System.nanoTime() - this.awayTime;
		if( TimeUnit.NANOSECONDS.toSeconds(difference) >= 300){
			return true;
		}else return false;
	}
}
