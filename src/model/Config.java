package model;

public enum Config {
    PATH("C:/Baptiste/UTC/GI04/NF28/TD/TD4/Rendu_Baptiste_Viera/MyJavaFXProject/resources/files");

    private Config(String config){
        this.config = config;
    }

    public String config;

    public String getPath(){
        return this.config;
    }
}
