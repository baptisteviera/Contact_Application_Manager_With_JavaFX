package model;

import java.util.function.Predicate;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;


public class Contact {

	private final StringProperty lastName;
	private final StringProperty firstName;
	private final StringProperty city;
	private final StringProperty country;
	private final StringProperty gender;

	// permet d'indiquer le nom de chaque proriété mal renseignée ainsi que le 
	// message adpaté lors de la création du tooltip
	private final ObservableMap<String, String> validationMessages;
	
	// permet de faire la validation
	private final BooleanProperty validateState;


	public Contact() {
		this.lastName = new SimpleStringProperty(this, Property.NAME.name(), "Nom");
		this.firstName = new SimpleStringProperty(this, Property.GIVEN_NAME.name(), "Prenom");
		this.gender = new SimpleStringProperty(this, Constants.SEX, "M");
		this.city = new SimpleStringProperty(this, Property.CITY.name(), "Ville");
		this.country = new SimpleStringProperty(this, Property.COUNTRY.name(), "");
		validationMessages = FXCollections.observableHashMap();

		validateState = new SimpleBooleanProperty();
	}

	// Property non renseignée
	// trim permet de retourner une chaîne dont la valeur est cette chaîne, 
	// avec tous les espaces de début et de fin supprimés, 
	// ou cette chaîne si elle n’a pas d’espaces de début ou de fin
	// Si testPropresty est true alors proprerty non renseignée
	// sinon property renseignée
	// "Predicate" est une interface fonctionnelle dont la méthode fonctionnelle est test(Object).

	Predicate<StringProperty> testProperty = prop -> prop.get() == null || prop.get().trim().equals("");

	public StringProperty lastNameProperty() {
		return lastName;
	}

	public StringProperty firstNameProperty() {
		return firstName;
	}

	public StringProperty genderProperty() {
		return gender;
	}

	public StringProperty countryProperty() {
		return country;
	}

	public StringProperty cityProperty() {
		return city;
	}

	public BooleanProperty validateStateProperty() {
		return validateState;
	}

	
	// il s'agit des getter et setter nécessaires pour JSON

	/**
	 * Jackson supprime la partie "get" et "set" des noms des méthodes getter et setter, et 
 	 * convertit le premier caractère du nom restant en minuscule.
	 * 
	 */

	public String getLastName() {
		return lastName.getValue();
	}

	public void setLastName(String lastName) {
		this.lastName.setValue(lastName);
	}

	public String getFirstName() {
		return firstName.getValue();
	}

	public void setFirstName(String firstName) {
		this.firstName.setValue(firstName);
	}

	public String getCountry() {
		return this.country.getValue();
	}

	public void setCountry(String country) {
		this.country.setValue(country);
	}

	public String getCity() {
		return this.city.getValue();
	}

	public void setCity(String city) {
		this.city.setValue(city);
	}

	public String getGender() {
		return this.gender.getValue();
	}

	public void setGender(String gender) {
		this.gender.setValue(gender);
	}

	public boolean getValidate() { 
		return validateState.getValue(); 
	}

	public void setValidate(Boolean v) { 
		validateState.setValue(v); 
	}


	// Validité d'une StringProperty (sprop)
	// REFERENCES: cette fonction est appelé dans la fonction validate de cette même classe Contact
	private void validateOneStringProperty(StringProperty sprop) {
		if (testProperty.test(sprop)) // si property non renseigné, on ajoute dans le doctionnaire le nom de la property et le message de warning
			// ajout d'un élément (clé,valeur) dans le HaspMap
			// (clé, valeur) => (nom de la sprop, message warning)
			validationMessages.put(sprop.getName(), Property.valueOf(sprop.getName()).tooltip);
	}

	// Etudie la validité de chaque Property
	// REFERENCES: cette fonction est appelée dans la fonction validate de mon controller principal
	public void validateAllStringProperty() {

		// on vide le dictionnaire qui contient les messages de warning associés au sprop
		// à chaque fois que l'on clique sur le bouton "valider", on appelle cette méthode et on réinialise à chaque fois le dictionnaire pour analyser de nouveaux les property
		validationMessages.clear();

		
		validateOneStringProperty(lastName);

		validateOneStringProperty(firstName);

		validateOneStringProperty(city);

		validateOneStringProperty(country);

		// si les validate ont ajouté un element dans le dictionnaire validateMessages alors
		// affichage du warning grâce au controller, property mal renseignées

		// on réinitialise à chaque appel
		validateState.setValue(false); 


		// si validateMessages est vide alors validate = True, les property sont bien renseignées
		// si validateMessages n'est pas vide alors property mal renseignées et donc validate = False
		validateState.setValue(validationMessages.isEmpty());
		
	}

	// REFERENCES: cette fonction est appelée dans la fonction makeBinding de mon Controller ContactInfo
	// Il sera ensuite associé un listener à cette property dans le Controller
	public ObservableMap<String, String> validationMessagesProperty() {
		return validationMessages;
	}

	// JavaFX utilise la méthode toString d’un contact pour savoir ce qu’il doit afficher dans 
	// l’arbre (choisir par exemple le nom ou nom + prénom). Au cas où le nom est modifié, l’arbre 
    // doit être raffraîchi.
	// on override la fonction déjà existante pour qu'elle retourne le nom + prenom
	// TextFieldTreeCellImpl
	// https://www.delftstack.com/fr/howto/java/override-tostring-java/
	@Override
	public String toString() {
		return lastNameProperty().get() + " " + firstNameProperty().get();
	}

	// copie le contact courant dans une cible
	// cela nous sera utile pour la création d'un contact clone par le modèle prinicpal ("ContactListManagerModel")
	// REFERENCES: cette fonction est appeléé dans la fonction updateExistingContact de mon modèle principal
	public void copyTo(Contact target) {
		target.lastNameProperty().set(lastNameProperty().get());
		target.firstNameProperty().set(firstNameProperty().get());
		target.cityProperty().set(cityProperty().get());
		target.countryProperty().set(countryProperty().get());
		target.genderProperty().set(genderProperty().get());
	}

}
