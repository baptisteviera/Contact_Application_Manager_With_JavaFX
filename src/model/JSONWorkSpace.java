
package model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

// @SuppressWarnings("unchecked")

// resources utiles

// https://www.baeldung.com/jackson-object-mapper-tutorial


public class JSONWorkSpace {

	// encapsulation de la liste de groupes
	// cette variable prendra la valeur de la liste observable de notre modèle
    private ObservableList<Group> groupList;

	// constructeur par défaut
    public JSONWorkSpace() {}

	// retourner les groupes lus à partir d'un fichier
    public Group[] fromFile(File file) throws Exception{

		Group[] groups;

		// object mapper donne des fonctionnalité pour lire et écrire des fichiers JSON
        ObjectMapper mapper = new ObjectMapper();
		
		// désérialisation
        groups = mapper.readValue(file,Group[].class);
		
        return groups;

    }

    public void save(File f) throws IOException {
        // on écrit le fichier JSON à partir des données saisies
        FileWriter fileWriter = new FileWriter(f);
        // la focntion write attend un string
        fileWriter.write(forFormatJSON());
        fileWriter.close();

    }

    // grouListProperty
    public ObservableList<Group> getGroups() { 
		return groupList; 
	
	}

    public void setGroups(ObservableList<Group> grps){
        groupList = grps;
    }

    public String forFormatJSON() throws JsonProcessingException{
		
        ArrayList<Group> groups = new ArrayList<Group>();
        // groupList contient les éléments de l'observable list groups du model
        // groupList a été initialisé dans le model
        groups.addAll(this.groupList); 
        
        // pour la serialisation
        ObjectMapper mapper = new ObjectMapper();
        String info = "";
      
        // on transforme le contenu en string
        info = mapper.writeValueAsString(groups);
        
        return info;
    }

}

/**
 * Pour lire correctement des objets Java à partir de JSON avec Jackson, 
 * il est important de savoir comment Jackson fait correspondre les champs d'un objet JSON 
 * aux champs d'un objet Java, je vais donc expliquer comment Jackson procède.
 * Par défaut, Jackson fait correspondre les champs d'un objet JSON aux champs d'un objet Java 
 * en faisant correspondre les noms des champs JSON aux méthodes getter et setter de l'objet Java. 
 * Jackson supprime la partie "get" et "set" des noms des méthodes getter et setter, et 
 * convertit le premier caractère du nom restant en minuscule.
 * Par exemple, le champ JSON nommé brand correspond aux méthodes getter et setter Java 
 * appelées getBrand() et setBrand(). Le champ JSON nommé engineNumber correspondrait aux 
 * méthodes getter et setter appelées getEngineNumber() et setEngineNumber().

 */