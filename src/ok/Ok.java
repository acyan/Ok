/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ok;


import com.sun.scenario.Settings;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import jfx.messagebox.MessageBox;


/**
 *
 * @author dasha
 */
public class Ok extends Application {
    
    @Override
    public void start(Stage primaryStage) throws IOException, SQLException {
      //  Integer i =0;
    //    IntegerProperty timer = new SimpleIntegerProperty(i);
        
        primaryStage.setTitle("Sites");
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 700, 400, Color.WHITE);
        

    //    TimerService time = new TimerService(0);
  //      timer.bind(time.lastValueProperty());
  //      time.setDelay(Duration.ZERO);
  //      time.setPeriod(Duration.seconds(1));
  //      time.start();
        
        GridPane gridpane = new GridPane();
        gridpane.setPadding(new Insets(5));
        gridpane.setHgap(10);
        gridpane.setVgap(10);
        root.setCenter(gridpane);
        
        Label siteLbl = new Label("Сайты");
        GridPane.setHalignment(siteLbl, HPos.CENTER);
        gridpane.add(siteLbl, 0, 0);  
        Label setLbl = new Label("Настройки");
        GridPane.setHalignment(setLbl, HPos.CENTER);
        gridpane.add(setLbl, 2, 0);
        
        
        ObservableList<Site> sites = getSites();
     
        MyService2 service = new MyService2(getUrl(sites));
        service.setDelay(new Duration(300));
        service.setPeriod(new Duration(60000));      
        
        service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            @Override
            public void handle(WorkerStateEvent event) {
                BlockingQueue<String> results = (BlockingQueue<String>) event.getSource().getValue();
                for(Site site:sites){
                    site.setStatus(results.poll());
                }
             //   time.setI(0);
            }
        });
        service.start();
        
        final TableView<Site> siteTableView = new TableView<>();
        siteTableView.setItems(sites);
        siteTableView.setPrefWidth(300);
        
        TableColumn<Site, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setEditable(true);
        nameColumn.setCellValueFactory(new PropertyValueFactory("name"));
        nameColumn.setPrefWidth(siteTableView.getPrefWidth()/3*2);
        
        TableColumn<Site, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setEditable(true);
        statusColumn.setCellValueFactory(new PropertyValueFactory("status"));     
        statusColumn.setPrefWidth(siteTableView.getPrefWidth()/3);
        
        siteTableView.getColumns().setAll(nameColumn, statusColumn);
        

        GridPane settings = new GridPane();
        CheckBox checkBox = new CheckBox("Проверять изменения");
        settings.add(checkBox, 3, 0);
        settings.setVisible(false);
        siteTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Site>() {

            @Override
            public void changed(ObservableValue<? extends Site> observable, Site oldValue, Site newValue) {
                if (observable != null && observable.getValue() != null) {
                    settings.setVisible(true);
                    checkBox.setSelected(newValue.getChange());
//                    for(Site site:sites){
//                        System.out.println(site.getName()+" "+site.getChange());
//                    }
                }
            }
        });        
        checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                siteTableView.getSelectionModel().getSelectedItem().setChange(newValue);
                Connection conn=null;
                try {
                    Class.forName("org.h2.Driver").newInstance();
                    conn = DriverManager.getConnection("jdbc:h2:./test","sa", "");
                    Statement st = null;
                    st = conn.createStatement();
                    st.execute("update sites set change_freq='"+newValue+"' where name='"+siteTableView.getSelectionModel().getSelectedItem().getName()+"'");
            ResultSet result;
            result = st.executeQuery("SELECT * FROM sites");
            while (result.next()) {
                System.out.println("DB "+result.getString("NAME")+" "+ result.getBoolean("change_freq"));
            }                    
            System.out.println();        
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        conn.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(Ok.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
//        Button btn = new Button();
//        btn.setText("Say 'Hello World'");
//        btn.setOnAction(new EventHandler<ActionEvent>() {
//            
//            @Override
//            public void handle(ActionEvent event) {
//                System.out.println("Hello World!");
//            }
//        });
        
 //       Label label = new Label();
 //       label.textProperty().bind(Bindings.convert(timer));
        
        
//        for(Site site:sites){
//            site.statusProperty().addListener(new ChangeListener<String>() {
//
//                @Override
//                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
//                    if(oldValue!=null){
//                        
//                        MessageBox.show(primaryStage,
//                            site.getName()+" "+newValue,
//                            "Information dialog",
//                            MessageBox.ICON_INFORMATION | MessageBox.OK | MessageBox.CANCEL);
//                    }
//                      //  System.out.println("lol");
//                        
//                }
//            });
//        }        
        gridpane.add(siteTableView,0,1);
        gridpane.add(settings, 1,1);
     //   gridpane.add(btn,1,1);
     //   gridpane.add(label,1,0);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    private ObservableList<Site> getSites() throws FileNotFoundException, IOException, SQLException{
        
        ObservableList<Site> sites = FXCollections.<Site>observableArrayList();
        Connection conn=null;
        try {
            Class.forName("org.h2.Driver").newInstance();
            conn = DriverManager.getConnection("jdbc:h2:./test","sa", "");
            Statement st = null;
            st = conn.createStatement();
            st.execute("create table IF NOT EXISTS sites(id int NOT NULL auto_increment, name varchar(64), change_freq boolean, change_number int, unique(name), primary key(id))");
            st.execute("INSERT INTO sites(name,change_freq) SELECT * FROM(SELECT 'http://google.com','true') AS tmp WHERE NOT EXISTS(SELECT name FROM sites WHERE name = 'http://google.com') LIMIT 1");
            st.execute("INSERT INTO sites(name,change_freq) SELECT * FROM(SELECT 'http://yandex.ru','true') AS tmp WHERE NOT EXISTS(SELECT name FROM sites WHERE name = 'http://yandex.ru') LIMIT 1");
            
            ResultSet result;
            result = st.executeQuery("SELECT * FROM sites");
            while (result.next()) {
                String name = result.getString("NAME");
                Boolean change = result.getBoolean("change_freq");
                sites.add(new Site(name, change));
                System.out.println(result.getString("NAME")+" "+ result.getBoolean("change_freq"));
            }
        //    st.execute("drop table sites");
//        try{
//             new FileReader(new File("sites.txt")).close();           
//        } catch(FileNotFoundException e){
//            PrintWriter writer = new PrintWriter("sites.txt", "UTF-8");
//        }
//
//
//        BufferedReader reader = new BufferedReader(new FileReader(new File("sites.txt")));
//        String line;
//        while ((line = reader.readLine()) != null){
//            sites.add(new Site(line));
//        }
//        reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
        return sites;
    } 
    
    private List<String> getUrl(ObservableList<Site> sites){
        List<String> result = new ArrayList<String>();
        for(Site site:sites){
            result.add(site.getName());
        }
        return result;
    }
}
