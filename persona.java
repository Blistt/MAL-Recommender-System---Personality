


public class persona {
	//Attributes
	String name;
	double rating;
	
	
	
	//Constructors
	public persona(){
	}
	
	public persona(String str){
		name = str;
	}
	
	public persona(double num){
		rating = num;
	}
	
	public persona(String str, double num) {
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
