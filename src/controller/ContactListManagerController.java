package controller;

import java.io.File;
import java.io.IOException;

import javafx.collections.ListChangeListener;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import model.Contact;
import model.ContactListManagerModel;
import model.Group;
import model.Config;

import view.ContactInfo;

public class ContactListManagerController {

	// le controller se charge de créer le model principal
    private final ContactListManagerModel contactListManagerModel;

    // création du model Contact (éditable)
	private Contact editingContact;

	// création du model contact (original)s
	private Contact originalContact;


    public ContactListManagerController(){
		// initialisation du model principal
        contactListManagerModel = new ContactListManagerModel();
    }


    @FXML
    private BorderPane rootPane;

    @FXML
    private TreeView<Object> tree;

	@FXML
	MenuItem saveButton; 

	@FXML
	private PieChart groupPieChart;

	@FXML 
	private BarChart<String, Number> contactBarChart;


	// zone de l'interface qui va contenir la zone de contact
    Parent contactPane;

	// variable qui contiendra le controller associé au contact info
    ContactInfoController contactInformationController;
    
	// icone pour les groups et les contacts
    private final Image groupIcon = new Image(getClass().getResourceAsStream("Group.png"));
	private final Image contactIcon = new Image(getClass().getResourceAsStream("Contact.png"));

    ListChangeListener<Group> groupListener;


	// Dans la class Group on a définit une collection observable de type set qui contient plusieurs contacts
	// contactListener est le listener qui sera associé à cette property de type ObservableSet
	SetChangeListener<Contact> contactListener;

	// Il s'agit d'un objet de l'arborescence (root ou group ou contact )
    private TreeItem<Object> selectedItem;

    
    public void initialize() throws IOException{

        initializeContactPane();

        makeBindings();

    }

    public void initializeContactPane() throws IOException {

		// initialisation de la vue associé à la création d'un contact
        ContactInfo contactInfo = new ContactInfo();

        // contrôleur du sous modèle MVC 
		// on récupére le controller associé à cette vue
        contactInformationController = contactInfo.contactInfoController; 

        // modèle du sous modèle MVC
		// on récupère le modèle de type contact associé au controller précédent
        editingContact = contactInformationController.editingContact;

        // vue du sous modèle MVC 
        contactPane = contactInfo.root;

        // ajout dans l’interface (rootPane est par exemple un BorderPane) 
        rootPane.setCenter(contactPane); 
    
    } 


    private void makeBindings(){


		// permet d'ajouter ou supprimer un group;
        groupListener = setGroupListener();
		

		// permet d'ajouter ou supprimer un contact
		contactListener = setContactsListener();

		// PieChartDataListener = setPieChartDataListener();


		contactListManagerModel.groupsProperty().addListener(groupListener);

		
		groupPieChart.setData(contactListManagerModel.groupPieChartDataProperty());

		contactListManagerModel.contactBarChartDataProperty().addListener( (obsv, oldv, newv) -> {
			
			contactBarChart.getData().clear();
			contactBarChart.getData().add(newv);

		});
		/*
		contactBarChart.dataProperty().addListener((obsv, oldv, newv) ->
		{
			contactListManagerModel.contactBarChartDataProperty().get().add(newv);
		}
		
		);
		*/
		
		// après modification et seulement lorsque editingContact est validé (grâce à la BooleanProperty ajoutée dans un contact), 
		// le contrôleur demande au modèle de mettre à jour originalContact à partir de editingContact. 
		editingContact.validateStateProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue)
				validateContactModifications();
		});

		// permet de rendre invisible la fenêtre des contacts à l'ouverture de l'application car aucun élément n'est sélectionné au début
		
		contactPane.visibleProperty().set(false);

		// création de l'arbre
		TreeConfiguration();
    }

	

	// on définit le listener associé à la liste observable de groupes
	public ListChangeListener<Group> setGroupListener(){

        return change -> {

			change.next();

			if (change.wasAdded()) {

				// getAddedSubList permet d'obtenir la vue d'une sous liste d'une liste de groupes qui contient seulement les groupes ajoutés
				// on parcourt ensuite cette liste
				// contactListManagerModel.groupsProperty().forEach(...) n'aurait pas fonctionné car ajoute aussi les anciens groupes
				// cela est aussi utilisé pour le chargement de fichier

				change.getAddedSubList().forEach(group -> {
					
					// System.out.println("testGroup");

					// ajout d'un groupe dans l'arborescence

					// création du groupItem à partir de l'objet group dans la sous list
					TreeItem<Object> groupItem = new TreeItem<Object>(group, new ImageView(groupIcon));
					tree.getRoot().getChildren().add(groupItem);

					setSelectedElements(groupItem, null);


					// SetChangeListener<Contact> contactListener;
					// LISTENER
					// RAPPEL : contactsProperty est une ObservableList de contact
					group.contactsProperty().addListener(contactListener); 

					
					// cela est est utile lorsque l'on charge des fichiers 
					// pour l'ajout à la main de groupe et de contact, le programme ne fait pas appel à cette instruction car la liste est vide.
					group.contactsProperty().forEach(contact -> {
						
						// System.out.println("testContact");

						// ajout de contact dans un groupe
						TreeItem<Object> contactItem = new TreeItem<>(contact,new ImageView(contactIcon));
						groupItem.getChildren().add(contactItem);
					
					});
					

				});

			} else if (change.wasRemoved()) {

				// le parent des groupes sera la racine
				TreeItem<Object> parentItem = selectedItem.getParent(); 
				// supression du groupe
				parentItem.getChildren().remove(selectedItem);
				// la racine est sélectionnée
				setSelectedElements(parentItem, null);
			}
		};

	}

	// on définit le listener associé à la liste observable de contacts
	public SetChangeListener<Contact> setContactsListener(){
		
		// System.out.println("je suis dans le listener contact");

		// permet d'ajouter ou supprimer un contact
		return change -> {

			TreeItem<Object> parent;
			// si l'élément sélectionné est un groupe alors il est stocké dans la variable "parent"
			if (selectedItem.getValue() instanceof Group)
				parent = selectedItem;
			// si l'élément sélectionné est un contact alors on stocke son parent dans la variable du même nom
			else
				parent = selectedItem.getParent();

			if (change.wasAdded()) {

				// on récupère le contact créé dans une variable
				Contact contact = change.getElementAdded();
				
				// ajout d'un contact dans le groupe sélectionné
				TreeItem<Object> contactItem = new TreeItem<>(contact,new ImageView(contactIcon));
				parent.getChildren().add(contactItem);

				// permet de fixer l'élément sélectionné et de fixer la variable originalContact avec le contact ajouté 
				// la référence du nouveau contact est enregistré dans la variable originalcontact
				setSelectedElements(contactItem, contact);

				// les nouveaux contacts ajoutés sont des contacts par défaut c'est-à-dire que les champs n'ont pas encore été renseignés par l'utilisateur

				// Est aussi déclenché indirectement le binding entre l'originalContact et le editingContact.
				// Les champs de editingContact sont réinitialisés avec ceux de originalContact

			} 
			else if (change.wasRemoved()) {
				
				// supression d'un contact dans le groupe sélectionné
				parent.getChildren().remove(selectedItem);

				// permet de fixer l'élément sélectionné (qui sera le groupe du contact supprimé)
				setSelectedElements(parent, null);

			}
			tree.refresh();
		};
	}

	// le contrôleur demande au modèle de mettre à jour originalContact à partir de
	// editingContact. 
	public void validateContactModifications() {
		// permet de valider les modification d'un contact
		// REFERENCES: UpdateExistingContact fait appel à la méthode copyTo du model contact
		contactListManagerModel.updateExistingContact(originalContact, editingContact);
		tree.refresh();
		
	}

	private void TreeConfiguration() {

		// Création de l'arbre avec la racine
		TreeItem<Object> rootNode;
		rootNode = new TreeItem<>("Annuaire");
		rootNode.setExpanded(true);
		tree.setRoot(rootNode);
		tree.setEditable(true);

		
		tree.setCellFactory(param -> new TextFieldTreeCellImpl());

		// Afin de mémoriser l’item sélectionné dans l’arbre (indispensable), 
		// il est utile d’installer une fonction de réponse sur la property correspondante
		tree.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {

			if (newValue == null)
				return;

			this.selectedItem = (TreeItem<Object>) newValue;

			// par défaut la zone de contact n'est pas affichée
			contactPane.visibleProperty().set(false);
			
			// au début original est null car aucun contact dans l'arbre
			originalContact = null;
			
			if (this.selectedItem.getValue() instanceof Contact) {

				// si l'item sélectionné est un contact on affiche le contact info
				contactPane.visibleProperty().set(true);
				// on initialise le contact orginal avec l'item contact créé
				originalContact = (Contact) this.selectedItem.getValue();
				
				// les champs de editingContact sont réinitialisés avec ceux de originalContact.
				// rien de nouveau n’apparaît dans l’arbre
				reInitializeEditContatcWithOriginal();

			}

		});

	}

	// les champs de editingContact sont réinitialisés avec ceux de originalContact.
	public void reInitializeEditContatcWithOriginal() {
		originalContact.copyTo(editingContact);
	}

	// permet de fixer l'item sélectionné dans une variable
	// permet de sléctionner l'item dans l'arbre
	// permet d'associer la variable originalContact au contact courant (il s'agit de la référence)
	private void setSelectedElements(TreeItem<Object> item, Contact c) {
		selectedItem = item;
		tree.getSelectionModel().select(selectedItem);
		originalContact = c;
	}

	// code fourni 

    private static class TextFieldTreeCellImpl extends TreeCell<Object> {

		private TextField textField;

		public TextFieldTreeCellImpl() {
		}

		@Override
		public void startEdit() {
			super.startEdit();
			if (!(getTreeItem().getValue() instanceof Group)) {
				return;
			}

			if (textField == null) {
				createTextField();
			}
			setText(null);
			setGraphic(textField);
			textField.selectAll();
		}

		@Override
		public void cancelEdit() {
			super.cancelEdit();
			setText(getItem() != null ? getItem().toString() : null);
			setGraphic(getTreeItem().getGraphic());
		}

		@Override
		public void updateItem(Object item, boolean empty) {
			super.updateItem(item, empty);

			if (empty) {
				setText(null);
				setGraphic(null);
			} else {
				if (isEditing()) {
					if (textField != null) {
						textField.setText(getString());
					}
					setText(null);
					setGraphic(textField);
				} else {
					setText(getString());
					setGraphic(getTreeItem().getGraphic());
				}
			}
		}

		private void createTextField() {
			textField = new TextField(getString());
			textField.setOnKeyReleased(t -> {
				if (t.getCode() == KeyCode.ENTER) {
					((Group) getTreeItem().getValue()).nameProperty().set(textField.getText());
					commitEdit(getTreeItem().getValue());
				} else if (t.getCode() == KeyCode.ESCAPE) {
					cancelEdit();
				}
			});
		}

		private String getString() {
			return getItem() == null ? "" : getItem().toString();
		}
	}
	  
	
    @FXML
    void addElement(ActionEvent event) {

        // rien n'est sélectionné
        if (selectedItem == null)
            return;

        // ajout un group : cela ajoute indirectement un groupe dans la liste observable
		// cas particulier : on est au niveau de la racine (élément sélectionné) -> on peut ajouter uniquement un groupe et non un contact
        if (selectedItem.getParent() == null){
            contactListManagerModel.addGroup();
			return; // important de mettre les return pour empêcher que les autres if s'activent
		}
		// l'élément sélectionné est un groupe alors on peut ajouter seulement un contact
        // ajout d'un contact au groupe associé -> dans l'observableSet
        if (selectedItem.getValue() instanceof Group) {

            Group group = (Group) selectedItem.getValue();
            contactListManagerModel.addNewContact(group);
			return; // important de mettre les return pour empêcher que les autres if s'activent
        }
        // l'élément sélectionné est un contact alors on peut ajouter seulement un contact dans le groupe du contact sélectionné
		// ajout d'un contact au groupe associé -> dans l'observableSet
        if (selectedItem.getValue() instanceof Contact) {
			
			//System.out.println("j'ajoute un contact avec le bouton ajouter");

            Group group = (Group) selectedItem.getParent().getValue();
            contactListManagerModel.addNewContact(group);
        }
    }

    @FXML
    void deleteElement(ActionEvent event) {

        if (selectedItem == null)
			return;

		// on supprime le groupe -> suppression d'un élément de l'ObservableList de groups
		if (selectedItem.getValue() instanceof Group) {
			contactListManagerModel.removeGroup((Group) selectedItem.getValue());

		} 


		// on supprime le contact -> suppression d'un élément de l'ObservableSet de contacts
		else if (selectedItem.getValue() instanceof Contact) {

			Group group = (Group) selectedItem.getParent().getValue();
			contactListManagerModel.removeContact((Contact) selectedItem.getValue(), group);

		}

    }


	@FXML
	public void save() throws Exception {
		try{
			contactListManagerModel.save();
		}
		catch (Exception e){
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Erreur sauvegarde");
			alert.setContentText("Veuillez sauvegarder sous avant ! ");
			alert.show();
			e.printStackTrace();
		}
	}


	@FXML
	public void saveAs() throws IOException {

		try {

			// ouverture de l'explorateur de fichiers

			FileChooser fileChooser = new FileChooser();
			// fixer le répertoire par défaut
			fileChooser.setInitialDirectory(new File(Config.PATH.config));
			File file = fileChooser.showSaveDialog(null);

			if (file != null) {
				
				contactListManagerModel.setFile(file);
				contactListManagerModel.save();
				saveButton.setDisable(false);
			}
				
		}
		catch (IOException e){
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Erreur sauvegarde sous");
			alert.setContentText(e.getMessage());
			alert.show();
			e.printStackTrace();
		}
		
	}


	@FXML
	public void load() throws Exception {

		try {

			FileChooser fileChooser = new FileChooser();
			fileChooser.setInitialDirectory(new File(Config.PATH.config));
			File file = fileChooser.showOpenDialog(null);
			if (file != null) {
				selectedItem = null;
				tree.getRoot().getChildren().clear();
				contactListManagerModel.loadFile(file);
				saveButton.setDisable(false);
			}
		}
	catch (IOException e){
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Erreur chargement");
			alert.setContentText(e.getMessage());
			alert.show();
			e.printStackTrace();
		}


	}


	@FXML
	public void refresh() throws Exception {

		contactListManagerModel.graphicsTool.setGroups(contactListManagerModel.groupsProperty());

		contactListManagerModel.graphicsTool.setcontactPieChartData();

		contactListManagerModel.addPieChartData();

		contactListManagerModel.graphicsTool.setXYSeriesData();

		contactListManagerModel.addXYChart();



	}



}
