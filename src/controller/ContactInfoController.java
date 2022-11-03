package controller;

import java.util.LinkedHashMap;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import model.Contact;
import model.Country;

public class ContactInfoController {

	// Déclaration de tous les éléments de la vue qui doivent être référencés 
	// en utilisant l’annotation @FXML et en 
	// conformité avec les identificateurs utilisés dans le fichier FXML

	@FXML
	private TextField lastName, firstName, city;

	@FXML
	private ComboBox<String> country;

	@FXML
	private ToggleGroup sexeGroup;

	@FXML
	private RadioButton genderm;

	@FXML
	private RadioButton genderf;


	// regroupe les contrôles dans un dictionnaire qui associe le nom de la proriété à un contrôle
	// un contrôle est un élément du FXML (textfield,...)
	private Map<String, Control> controls;

	// il s'agit du contact en cours, qui est édité (création du model contact)
	public Contact editingContact;

	// il s'agit du listener qui sera associé
	MapChangeListener<String, String> validateFunction;


	// constructeur
	public ContactInfoController() {

		editingContact = new Contact();

		controls = new LinkedHashMap<>();
	}

	public void initialize() {

		makeBindings();

		// on ajoute les contrôles dans le dictionnaire associé au contact editable
		addControls(editingContact);
	}

	private void makeBindings() {

		// Dans ValidateFunction l'intruction "change" fait référence à un changement élémentaire effectué sur une ObservableMap. 
		// Le changement contient des informations sur une opération put ou remove. 
		// L'opération put peut supprimer un élément s'il y avait déjà une valeur associée à la même clé. 
		// Dans ce cas, wasAdded() et wasRemoved() renverront tous deux true.

		validateFunction = change -> {
			Control control = controls.get(change.getKey());
			if (change.wasRemoved()) {
				control.setStyle("-fx-border-color:  blue ;");
				control.setTooltip(null);
			} else if (change.wasAdded()) {
				control.setStyle("-fx-border-color: red ;");
				control.setTooltip(new Tooltip(change.getValueAdded()));
			}
		};

		// make bindings
		lastName.textProperty().bindBidirectional(editingContact.lastNameProperty());
		firstName.textProperty().bindBidirectional(editingContact.firstNameProperty());
		city.textProperty().bindBidirectional(editingContact.cityProperty());

		// binding pour country

		// sens 1
		// View -> Model grâce au controller
		// country combox de ma liste...
		country.getSelectionModel().selectedItemProperty().addListener((observable, oldv, newv) -> {
			editingContact.countryProperty().setValue(newv);
		});

		// sens 2
		// Model -> View grâce au controller
		editingContact.countryProperty().addListener((obj, o, n) -> {
			country.getSelectionModel().select(n);
		});

		// binding pour les items de country
		country.setItems(FXCollections.observableList(Country.getCountryNames()));

		// binding pour sexe

		// sens 1
		sexeGroup.selectedToggleProperty().addListener((observable, oldv, newv) -> {
			if (newv != null) {
				RadioButton bt = (RadioButton) newv;
				editingContact.genderProperty().set(bt.getText());
			}

		});

		// sens 2
		editingContact.genderProperty().addListener(
			
			(obj, o, n) -> 
			
			{
				if (n == null) {
					return;
				}
				if (n.equals("M")) {
					sexeGroup.selectToggle(genderm);
				} else {
					sexeGroup.selectToggle(genderf);
				}
			}
		
		);

		// binding 
		// ajout du listener afin de vérifier les champs de l'inferface
		// validateFunction est définie plus haut
		editingContact.validationMessagesProperty().addListener(validateFunction);

	}

	// on ajoute les contrôles dans le dictionnaire associé au contact
	// un contrôle est un élément du FXML (textfield,...)
	private void addControls(Contact contact) {
		controls.put(contact.lastNameProperty().getName(), lastName);
		controls.put(contact.firstNameProperty().getName(), firstName);
		controls.put(contact.cityProperty().getName(), city);
		controls.put(contact.countryProperty().getName(), country);
		controls.put(contact.genderProperty().getName(), genderf);
	}

	// cette fonction est associée au bouton valider dans l'interface graphique
	@FXML
	public void validate() {
		editingContact.validateAllStringProperty();
	}

}
