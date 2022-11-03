package model;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

import java.util.List;
import java.util.stream.Collectors;

public class GraphicTool {

    List<PieChart.Data> contactPieChartData;
    XYChart.Series<String, Number> citiesSeriesData;
    List<Group> groups;



    GraphicTool(){ }

    public List<PieChart.Data> contactPieChartData()
    {
        
        return contactPieChartData;

    }

    public void setcontactPieChartData(){

        contactPieChartData = groups.stream() .map(group -> new PieChart.Data(group.getName(), group.contactSize()))
        .collect(Collectors.toList());

    }

    public void setXYSeriesData(){
        citiesSeriesData = new XYChart.Series<String, Number>();

        groups.stream().flatMap(group -> group.getContacts().stream())
        .collect(Collectors.groupingBy(contact -> contact.getCity(), Collectors.counting())).
        forEach((city, nb) -> citiesSeriesData.getData().
        add(new XYChart.Data<String, Number>(city, nb)));

    }



    public void setGroups(List<Group> grps)
    {
        
        this.groups = grps;
       
    }

    public XYChart.Series<String, Number> citiesSeriesData()
    {

        return citiesSeriesData;

    }
    
}
