package model;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;


// Il s'agit du modele principal de l'application
// Celui-ci permet d'ajouter des groupes, de supprimer des groupes, d'ajouter des contacts et de retirer des contacts
// et ainsi de mettre à jour des contacts déjà existants
// Permet également de charger et de sauvegarder un contact

public class ContactListManagerModel {
	

	// il s'agit d'une liste observable de groupes
	private final ObservableList<Group> groups;
	private JSONWorkSpace JSONworkspace = new JSONWorkSpace();
	private File file;

	ObservableList<PieChart.Data> groupPieChartData;

	ObjectProperty<XYChart.Series<String, Number>> contactBarChartData;

	public GraphicTool graphicsTool = new GraphicTool();

	public ContactListManagerModel() {

		// intialisation de la liste observable qui contient des groupes
		groups = FXCollections.observableArrayList();

		groupPieChartData = FXCollections.observableArrayList();
		contactBarChartData = new SimpleObjectProperty<>();

	}

	// accesseur
	public ObservableList<PieChart.Data> groupPieChartDataProperty() {
		return groupPieChartData;
	}

	public ObjectProperty<XYChart.Series<String, Number>> contactBarChartDataProperty() {
		return contactBarChartData;
	}
	

	public void addXYChart() {
		List<PieChart.Data> ldata = graphicsTool.contactPieChartData(); 
		groupPieChartData.clear();
		groupPieChartData.addAll(ldata);
	}

	public void addPieChartData() {

		XYChart.Series<String, Number> citySerie = graphicsTool.citiesSeriesData();
		contactBarChartData.setValue(citySerie);

	}

	// permet de créer un group vide sans contact
	// REFERENCES: Cette fonction est appelée dans la fonction addElement de mon Controller principal
	public void addBarData() {
		// création d'un nouveau groupe avec le constructeur de la classe Group à 1 argument
		Group group = new Group("Nouveau Groupe");
		// on ajoute le groupe en question dans la liste observable contenant les groupes
		groups.add(group);
	}


	// on retourne la liste observable qui contient des groupes
	// REFERENCES: cette fonction est appelée dans la fonction makeBinding de mon Controller principal
	// Il sera ensuite associé un listener à cette property dans le Controller
	public ObservableList<Group> groupsProperty() {
		return groups;
	}

	// permet de créer un group vide sans contact
	// REFERENCES: Cette fonction est appelée dans la fonction addElement de mon Controller principal
	public void addGroup() {
		// création d'un nouveau groupe avec le constructeur de la classe Group à 1 argument
		Group group = new Group("Nouveau Groupe");
		// on ajoute le groupe en question dans la liste observable contenant les groupes
		groups.add(group);
	}

	// mise à jour d'un contact déjà existant
	// REFERENCES: Cette fonction est appelée dans la fonction validate de mon Controller principal
	public void updateExistingContact(Contact original, Contact edited) {
		// permet de copié le contact en mode édition dans le contact original
		edited.copyTo(original);
	}

	// à partir d'un groupe donné on ajoute un contact dans ce groupe
	// REFERENCES: Cette fonction est appelée dans la fonction addElement de mon Controller principal
	public void addNewContact(Group group) {
		Contact myContact = new Contact();
		group.addContact(myContact);
	}

	
	// on supprime le groupe spécifié en paramètre dans la liste de groupes
	// REFERENCES : cette fonction est appelée dans la fonction deleteElement de mon Controller principal
	public void removeGroup(Group group) {
		groups.remove(group);
	}

	// à partir d'un contact donnée et d'un groupe donné, on supprime le contact de ce group
	// REFERENCES : cette fonction est appelée dans la fonction deleteElement de mon Controller principal
	public void removeContact(Contact contact, Group group) {
		group.remove(contact);
	}


public void setFile(File file) {
	this.file = file;
}

public void save() throws IOException {

	// assignation des groupes (important)
	// la variable grouList du fichier JSONWorkSpace prend alors la valeur de la liste Observable groups
	JSONworkspace.setGroups(groups); 
	// modification d'une observable list cela doit déclencher un listener dans le controller
	// le listener va changer la vue ?
	// le controller joue ici le rôle d'application !

	// invoque la méthode save du workspace
	JSONworkspace.save(file);

}

// récupération des groupes
public void loadFile(File f) throws Exception {

	file = f;
	Group[] groupsFromJSON = JSONworkspace.fromFile(file);

	// Au moment de la lecture d’un fichier il faut vider la liste des groupes, 
	// ce qui doit provoquer l’effacement des tree items de l’arbre
	groups.clear();

	// ajoute l'ensemble des groupes dans la liste de groupe observable permettant l'affichage
	groups.addAll(groupsFromJSON);
	
}

    
}
