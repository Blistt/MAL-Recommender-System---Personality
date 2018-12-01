

public class anime {
	//Atributes
	String name;
	double rating;
	double position;
	
	
	
	
	//Constructors
	public anime(){
	}
	
	public anime(String str){
		name = str;
	}
	
	public anime(double num){
		rating = num;
	}
	
	public anime(String str, double num) {
		name = str;
		rating = num;
	}
	
	
	//Setters
	public void setName(String str){
		name = str;
	}	
		
	//Getters
	public String getName(){
		return name;
	}
	
	public double getRating(){
		return rating;
	}

}
