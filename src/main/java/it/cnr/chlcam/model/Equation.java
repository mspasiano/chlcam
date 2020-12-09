package it.cnr.chlcam.model;

import java.io.Serializable;

public class Equation implements Serializable{
	private static final long serialVersionUID = 1L;
	private long id;
    private String name;
    private String expression;
    private EquationType type;
    private boolean protetto;
	public Equation(long id, String name, String expression, EquationType type, boolean protetto) {
		super();
		this.id = id;
		this.name = name;
		this.expression = expression;
		this.type = type;
		this.protetto = protetto;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	public EquationType getType() {
		return type;
	}
	public void setType(EquationType type) {
		this.type = type;
	}
	public boolean isProtetto() {
		return protetto;
	}
	public void setProtetto(boolean protetto) {
		this.protetto = protetto;
	}      	
}
