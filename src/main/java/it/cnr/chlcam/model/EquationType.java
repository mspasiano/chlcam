package it.cnr.chlcam.model;

public enum EquationType {
	multiple_regression_model("Multiple regression model"),
	single_regression_model("Single regression model");
	
	String label;
	
	EquationType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
	
	public static EquationType valueFromLabel(String label){
		for (EquationType equationType : EquationType.values()) {
			if (equationType.label.equals(label))
				return equationType;
		}
		return null;
	}
}
