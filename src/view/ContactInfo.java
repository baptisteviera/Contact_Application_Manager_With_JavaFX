package view;

import java.io.IOException;

import controller.ContactInfoController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class ContactInfo {

    public Parent root = null;
    
    public ContactInfoController contactInfoController; 
    
    public ContactInfo() throws IOException { 

        super();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("contact_info.fxml"));

        root = fxmlLoader.load();

        contactInfoController = fxmlLoader.getController();

    } 

}
   