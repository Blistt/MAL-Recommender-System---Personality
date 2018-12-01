

public class similarity {
	//Attributes
	String name;
	int pos;
	double num;
	
	
	//Constructors
	similarity (){
	}
	
	similarity(String str){
		this.name = str;
	}
	
	similarity(double d){
		this.num = d;
	}
	
	similarity(String str, double d){
		this.name = str;
		this.num = d;
	}
}
