import java.io.*;

public class Tuple implements Serializable{ 
  public int x; 
  	public int y; 
  	public Tuple(int x, int y) { 
    	this.x = x; 
    	this.y = y; 
  	} 
  	public Tuple compare(Tuple t2){
  		if(x == t2.x){
  			if(y < t2.y)
  				return t2;
  			else
  				return this;
  		}
  		else{
  			if(x < t2.x)
  				return t2;
  			else
  				return this;
  		}
  	}
    public String toString(){
      return "<" + x + "," + y + ">";
    }
} 
