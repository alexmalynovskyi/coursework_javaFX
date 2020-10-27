package sample;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private UpdateCheckService service;

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = (Parent) loader.load();
//        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Graduate work 2020 ");
        primaryStage.setScene(new Scene(root, 1000, 800));
        primaryStage.show();
        Controller controller = (Controller) loader.getController();

        service = new UpdateCheckService(controller);
        service.setPeriod(Duration.seconds(5));

        Label resultLabel = new Label();
        service.setOnRunning(e -> resultLabel.setText(null));
        service.setOnSucceeded(
                e -> {
                    if (service.getValue() != null) {
                        resultLabel.setText("UPDATES AVAILABLE");
                        controller.uploadUpdatedData(service.getValue());
                    } else {
                        resultLabel.setText("UP-TO-DATE");
                        //System.out.println(controller.extendedTable.getItems().toString());
                    }
                });

        service.start();
    }

    private static class UpdateCheckService extends ScheduledService<ArrayList<Package>> {
        private Controller controller;

        UpdateCheckService(Controller controller) {
            this.controller = controller;
        }

        @Override
        protected Task<ArrayList<Package>> createTask() {
            return new Task() {

                @Override
                protected ArrayList<Package> call() throws Exception {
                    DateTimeFormatter dateFormater = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                    ObservableList<Package> packages = controller.extendedTable.getItems();
                    ArrayList<Package> updatedPackages = new ArrayList<Package>();

                    for (Package pck : packages) {
                        String ttn = pck.getCode();

                        String jsonResponse = controller.getHttpResponse(ttn);
                        JsonObject jsonObject = (JsonObject) JsonParser.parseString(jsonResponse);
                        String htmlBody = jsonObject.get("html").getAsString();

                        HtmlParser responseParser = new HtmlParser(htmlBody);
                        String title = responseParser.getсClassContent(".title");
                        String date = responseParser.getсClassContent(".date");
                        String textStatus = responseParser.getсClassContent(".text");
//            String reponseHtml = jsonObject.get("html").getAsString();
                        Package pack = new Package(
                                pck.getCode(),
                                pck.getDescription(),
                                date,
                                textStatus,
                                title,
                                dateFormater.format(LocalDateTime.now())
                        );
                        updatedPackages.add(pack);

                    }
                    return updatedPackages;
                }
            };
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
