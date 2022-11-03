package model;

import java.util.List;
import java.util.Set;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

//il s'agit d'un groupe de contacts
public class Group {

	// nom du groupe
	private final StringProperty name;

	// liste observable de contacts.
	// On utilise le Set car il s'agit d'une collection qui ne contient aucun élément en double
	private final ObservableSet<Contact> contacts;

	// constructeurs avec 1 argument
	// on fixe le nom de la property associée
	// on initialise la liste de contact observable
	public Group(String name) {
		this.name = new SimpleStringProperty(name);
		this.contacts = FXCollections.observableSet();
	}

	// constructeurs par défaut
	public Group() {
		this.name = new SimpleStringProperty("Nouveau groupe");
		this.contacts = FXCollections.observableSet();
	}

	// property associé au nom du groupe
	public StringProperty nameProperty() {
		return name;
	}

	public String getName() {
		return name.get();
	}
	
	public void setName(String name) {
		this.name.setValue(name);
	}
	
	// retourne la liste de contacts (non observable)
	public Set<Contact> getContacts(){
		return contacts;
	}

	public int contactSize(){
		return contacts.size();
	}
	
	public void setContacts(List<Contact> listOfContact) {
		// on vide la liste observable de contacts
		contacts.clear();
		// on parcourt l'ensemble des éléments de "listOfContact" et on ajoute chaque élément dans 
		// la liste observanle "contacts"
		listOfContact.forEach(contact -> addContact(contact));
		// listOfContact.forEach(contact -> contacts.add(contact));
	}

	// retourne la liste observable de contacts
	public ObservableSet<Contact> contactsProperty() {
		return contacts;
	}
	
	// permet d'ajouter un contact dans la liste observable contacts
	public void addContact(Contact c) {
		contacts.add(c);
	}

	// permet de supprimer un contact de la liste observale contatcs
	public void remove(Contact c) {
		contacts.remove(c);
	}

	// JavaFX utilise la méthode toString d’un contact pour savoir ce qu’il doit afficher dans 
	// l’arbre (dans ce cas là il s'agit du nom du groupe). 
	// Au cas où le nom est modifié l’arbre 
	// doit être raffraîchi.
	@Override
	public String toString() {
		// il s'agit du nom string associé à la sprop name
		return getName();
	}

}
