package it.cnr.chlcam.model;

import java.util.ArrayList;
import java.util.List;

public class Equations {
	 	private String title;
	    private List<Equation> equation = new ArrayList<Equation>();

	    public Equations(String title) {
	        this.title = title;
	    }

	    public void addEquation(Equation equation) {
	        this.equation.add(equation);
	    }
	    
	    public String getTitle() {
	        return title;
	    }
	    
	    public void setTitle(String title) {
	        this.title = title;
	    }
	    
	    public List<Equation> getEquations() {
	        return equation;
	    }
	    
	    public void setEquations(List<Equation> equations) {
	        this.equation = equations;
	    }
}
