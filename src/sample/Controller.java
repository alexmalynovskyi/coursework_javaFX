


package sample;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;


import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Controller {
    private final ObservableList<Package> packages = FXCollections.observableArrayList();
    //private final ObservableList<ExtendedPackage> extendedPackages = FXCollections.observableArrayList();
    @FXML
    private TextField codeField;
    @FXML
    private TextField descriptionField;
    @FXML
    public TableView<Package> table;
    @FXML
    public TableView<Package> extendedTable;

    private void saveData() {
        // @TODO refactore all controller including this logic with saing DATA!!!!
        Gson gson = new Gson();
        String applicationDataJson = gson.toJson(table.getItems());

        try {
            FileWriter file = new FileWriter("applicationData.json");
            file.write(applicationDataJson);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(applicationDataJson);
    }

    private void loadData() {
        File file = new File("applicationData.json");

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String savedDataJson = "", line;
            while ((line = br.readLine()) != null) {
                savedDataJson += line;
            }
            Gson gson = new Gson();

            System.out.println(savedDataJson);
            packages.addAll(gson.fromJson(savedDataJson, Package[].class));
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
        table.setEditable(true);
        table.setItems(packages);
        extendedTable.setEditable(true);
        extendedTable.setItems(packages);
    }

    public void handleAddItem() {
        try {
            DateTimeFormatter dateFormater = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String jsonResponse = getHttpResponse(codeField.getText());
            JsonObject jsonObject = (JsonObject) JsonParser.parseString(jsonResponse);
            String htmlBody = jsonObject.get("html").getAsString();

            HtmlParser responseParser = new HtmlParser(htmlBody);
            String title = responseParser.getсClassContent(".title");
            String date = responseParser.getсClassContent(".date");
            String textStatus = responseParser.getсClassContent(".text");
            Package pack = new Package(
                    codeField.getText(),
                    descriptionField.getText(),
                    date,
                    textStatus,
                    title,
                    dateFormater.format(LocalDateTime.now())
            );
            packages.add(pack);

            loadContent(pack);
            saveData();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        TableColumn codeExtendedField = new TableColumn("Code");
        TableColumn descriptionExtendedField = new TableColumn("description");
        TableColumn updatedAtExtendedField = new TableColumn("last update");
        descriptionExtendedField.setEditable(true);
        descriptionExtendedField.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Package, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Package, String> t) {
                        ((Package) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setDescription(t.getNewValue());
                    }
                }
        );

        GUIUtils.defineTableCell(dateField, "date");
        GUIUtils.defineTableCell(statusField, "status");
        GUIUtils.defineTableCell(codeExtendedField, "code");
        GUIUtils.defineTableCell(descriptionExtendedField, "description");
        GUIUtils.defineTableCell(updatedAtExtendedField, "updatedAt");

        extendedTable.getColumns().addAll(codeExtendedField, descriptionExtendedField, dateField, statusField, updatedAtExtendedField);
        extendedTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public void uploadUpdatedData(ArrayList<Package> updatedPackages) {
        packages.clear();
        packages.addAll(updatedPackages);
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
            e.printStackTrace();
        }
    }

    private void loadContent(Package pack) {
        loadContent(pack, false);
    }

    private void loadAllContent() {
        for (Package pack : table.getItems())
            loadContent(pack);
    }

    public String getHttpResponse(String ttnNumber) throws IOException {
        HttpController controller = new HttpController("https://justin.ua/tracking", "post");
        Map<String, String> responseHash = controller.sendGet("https://justin.ua/tracking");
        System.out.println("==================================3 " + responseHash.get("response"));
        HtmlParser htmlParser = new HtmlParser(responseHash.get("response"));
        String token = htmlParser.getTagContent("meta[name=\"csrf-token\"]");
        Map<String, String> postParams = new LinkedHashMap<>();
        System.out.println("TOKEN : " + token);
        postParams.put("_token", token);
        postParams.put("number", ttnNumber);
        String jsonPayload = (new Gson()).toJson(postParams);

        String str = controller.sendPost(jsonPayload, responseHash);
        System.out.println(str +  "    output Post string!!!!");
        return str;
    }
}


