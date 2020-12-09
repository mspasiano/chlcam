package it.cnr.chlcam.model;

import java.util.ArrayList;
import java.util.List;

public class Results {
	 	private String title;
	    private List<Result> result = new ArrayList<Result>();

	    public Results(String title) {
	        this.title = title;
	    }

	    public void addResult(Result result) {
	        this.result.add(result);
	    }
	    
	    public String getTitle() {
	        return title;
	    }
	    
	    public void setTitle(String title) {
	        this.title = title;
	    }
	    
	    public List<Result> getResults() {
	        return result;
	    }
	    
	    public void setResults(List<Result> results) {
	        this.result = results;
	    }
}
