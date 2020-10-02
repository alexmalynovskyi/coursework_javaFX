


package sample;
import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import sample.HttpController;


import java.io.*;

public class Controller {
    private final ObservableList<Package> packages = FXCollections.observableArrayList();
    //    private final ObservableList<ExtendedPackage> extendedPackages = FXCollections.observableArrayList();
    @FXML
    private TextField codeField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TableView<Package> table;
    @FXML
    private TableView<Package> extendedTable;

    private void saveData() {
        // @TODO refactore all controller including this logic with saing DATA!!!!
//        Gson gson = new Gson();
//        String applicationDataJson = gson.toJson(table.getItems());

        try{
            FileWriter file = new FileWriter("applicationData.json");
//            file.write(applicationDataJson);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        System.out.println(applicationDataJson);
    }

    private void loadData() {
        File file = new File("applicationData.json");

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String savedDataJson ="", line;
            while ((line = br.readLine()) != null){
                savedDataJson += line;
            }
//            Gson gson = new Gson();

            System.out.println(savedDataJson);
//            packages.addAll(gson.fromJson(savedDataJson, Package[].class));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        initializeTableValues();
        loadData();
        table.setItems(packages);
        extendedTable.setItems(packages);
    }
    public void handleAddItem() {
        getHttpResponse();
        Package pack = new Package(codeField.getText(), descriptionField.getText());
        packages.add(pack);

        loadContent(pack);
        saveData();
    }

    private void initializeTableValues() {
        // initialize preview table
        TableColumn codeField = new TableColumn("Code");
        TableColumn descriptionField = new TableColumn("Description");

        GUIUtils.defineTableCell(codeField, "code");
        GUIUtils.defineTableCell(descriptionField, "description");

        table.getColumns().addAll(codeField, descriptionField);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        // initialize extended table
        TableColumn dateField = new TableColumn("Date");
        TableColumn statusField = new TableColumn("Status");
        TableColumn textField = new TableColumn("Text");
        TableColumn codeExtendedField = new TableColumn("Code");
        TableColumn descriptionExtendedField = new TableColumn("description");

        GUIUtils.defineTableCell(dateField, "date");
        GUIUtils.defineTableCell(statusField, "status");
        GUIUtils.defineTableCell(codeExtendedField, "code");
        GUIUtils.defineTableCell(descriptionExtendedField, "description");

        extendedTable.getColumns().addAll(codeExtendedField, descriptionExtendedField, dateField, statusField, textField);
        extendedTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public void handleDeleteSelectedItem() {
        Package selectedItem = table.getSelectionModel().getSelectedItem();
        table.getItems().remove(selectedItem);
    }

    public void handleShowAll() {
        loadAllContent();
    }

    public void handleShowSelected() {
        Package selectedItem = table.getSelectionModel().getSelectedItem();

        extendedTable.getItems().removeAll();
        extendedTable.getItems().add(selectedItem);

        loadContent(selectedItem);
    }

    public void handleDeleteAll() {
        clearAllTables();
    }

    private void clearAllTables() {
        clearTable(table);
        clearTable(extendedTable);
    }

    private void clearTable(TableView table) {
        table.getItems().clear();
    }

    private void loadContent(Package pack, Boolean shouldCleareTable) {
        try {
//            WebParser.loadContent(pack);
//            WebParser.get(pack);
//        } catch (IOException e) {
//            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace( );
        }
    }

    private void loadContent(Package pack) {
        loadContent(pack, false);
    }

    private void loadAllContent() {
        for(Package pack : table.getItems())
            loadContent(pack);
    }

    public void getHttpResponse() {
        try {
            HttpController controller = new HttpController("http://localhost:3030/api/v1/order?trackingNumber=409877385", "get");
            String str = controller.send("123123");
            JsonObject jsonObject = (JsonObject) JsonParser.parseString(str);
            String date = jsonObject.get("date").getAsString();
            String text = jsonObject.get("text").getAsString();
            System.out.println(date);
            System.out.println(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


