package model;

public enum Property {
	// warning possible associé au champs non renseigné
	NAME(" Le nom est obligatoire"), 
	GIVEN_NAME(" Le prénom est obligatoire"), 
	COUNTRY(" Le pays est obligatoire"), CITY(" La ville est obligatoire");
	
	private Property(String tooltip) {
		this.tooltip = tooltip;
	}
	
	// variable qui contiendra le type de warning qui devra être affiché sur l'interface de l'utilisateur
	public String tooltip;
}
