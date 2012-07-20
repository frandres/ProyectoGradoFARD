package mdfi.query;


public class Attribute {

	private String name;
	private String concept;
	
	public Attribute(String name, String concept) {
		super();
		this.name = name;
		this.concept = concept; 
	}

	@Override
	 public boolean equals(Object o) {
		  
	    if (o instanceof Attribute) {
	      Attribute at = (Attribute) o;
	      if (getIdentifier().compareTo(at.getIdentifier())==0){ 
	    	  return true;
	      }
	    }
	    return false;
	    
	}

	public String getName() {
		return name;
	}
	
	public String getConcept() {
		return concept;
	}

	public String toString(){
		return getIdentifier();
	}
	public String getIdentifier() {
		return getName() + "."  + getConcept();
	}
 
	public Attribute clone(){
		return new Attribute(getName(), getConcept());
	}
}
