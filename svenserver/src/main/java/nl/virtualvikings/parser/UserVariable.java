package nl.virtualvikings.parser;

public class UserVariable extends Variable {
	
	public UserVariable(String name, Expression value) {
		super(name, value);
	}
	
	public String getName() {
		return name;
	}
		
	public void setValue(Expression value) {
		this.value = value;
	}
}
