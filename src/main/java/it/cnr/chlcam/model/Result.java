package it.cnr.chlcam.model;

import it.cnr.chlcam.R;

import java.io.Serializable;
import java.util.Date;

import android.content.Context;

public class Result implements Serializable{
	private static final long serialVersionUID = 1L;
	private long id;
	private Date date;
	private String address;
	private String specie;
	private String equation;
	private Integer leafCount;
	private Integer leafR;
	private Integer leafG;
	private Integer leafB;
	private Integer averageR;
	private Integer averageG;
	private Integer averageB;
	private Double leafChlorophyll;
	private Double averageChlorophyll;
	
	public Result() {
		super();
	}

	public Result(Date date, String address, String specie,
			String equation, Integer leafR, Integer leafG,
			Integer leafB, Double leafChlorophyll) {
		super();
		this.date = date;
		this.address = address;
		this.specie = specie;
		this.equation = equation;
		this.leafR = leafR;
		this.leafG = leafG;
		this.leafB = leafB;
		this.leafChlorophyll = leafChlorophyll;
	}
	
	public Result(long id, Date date, String address, String specie,
			String equation, Integer leafCount, Integer leafR, Integer leafG,
			Integer leafB, Integer averageR, Integer averageG, Integer averageB,
			Double leafChlorophyll, Double averageChlorophyll) {
		super();
		this.id = id;
		this.date = date;
		this.address = address;
		this.specie = specie;
		this.equation = equation;
		this.leafCount = leafCount;
		this.leafR = leafR;
		this.leafG = leafG;
		this.leafB = leafB;
		this.averageR = averageR;
		this.averageG = averageG;
		this.averageB = averageB;
		this.leafChlorophyll = leafChlorophyll;
		this.averageChlorophyll = averageChlorophyll;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getSpecie() {
		return specie;
	}
	public void setSpecie(String specie) {
		this.specie = specie;
	}
	public String getEquation() {
		return equation;
	}
	public void setEquation(String equation) {
		this.equation = equation;
	}

	public Integer getLeafCount() {
		return leafCount;
	}

	public void setLeafCount(Integer leafCount) {
		this.leafCount = leafCount;
	}

	public Integer getLeafR() {
		return leafR;
	}

	public void setLeafR(Integer leafR) {
		this.leafR = leafR;
	}

	public Integer getLeafG() {
		return leafG;
	}

	public void setLeafG(Integer leafG) {
		this.leafG = leafG;
	}

	public Integer getLeafB() {
		return leafB;
	}

	public void setLeafB(Integer leafB) {
		this.leafB = leafB;
	}

	public Integer getAverageR() {
		return averageR;
	}

	public void setAverageR(Integer averageR) {
		this.averageR = averageR;
	}

	public Integer getAverageG() {
		return averageG;
	}

	public void setAverageG(Integer averageG) {
		this.averageG = averageG;
	}

	public Integer getAverageB() {
		return averageB;
	}

	public void setAverageB(Integer averageB) {
		this.averageB = averageB;
	}

	public Double getLeafChlorophyll() {
		return leafChlorophyll;
	}

	public void setLeafChlorophyll(Double leafChlorophyll) {
		this.leafChlorophyll = leafChlorophyll;
	}

	public Double getAverageChlorophyll() {
		return averageChlorophyll;
	}

	public void setAverageChlorophyll(Double averageChlorophyll) {
		this.averageChlorophyll = averageChlorophyll;
	}

	public String toShortView(Context context) {
		java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
		java.text.DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);		
		return "Date & Time: " + 
				dateFormat.format(getDate()) + " " + 
				timeFormat.format(getDate()) + 
				"\nSpecie: " + getSpecie();
	}
	
	public String toLongView(Context context) {
		String result = toShortView(context);
		result += "\nLocation: " + getAddress();
		if (getLeafCount() != 0)
			result += "\nLeaf Count: " + getLeafCount();
		result += "\nLeaf RGB: " + "R " + getLeafR() + " G " + getLeafG() + " B " + getLeafB();
		if (getLeafCount() != 0)
			result += "\nAverage RGB: " + "R " + getAverageR() + " G " + getAverageG() + " B " + getAverageB();
		result += "\nLeaf Chlorophyll: " + context.getString(R.string.result_misura, String.valueOf(getLeafChlorophyll()));
		if (getLeafCount() != 0)
			result += "\nAverage Chlorophyll: " + context.getString(R.string.result_misura, String.valueOf(getAverageChlorophyll()));		
		return result;
	}
	
}
