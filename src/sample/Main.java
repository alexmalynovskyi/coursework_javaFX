package sample;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.*;

public class Main extends Application {

    private UpdateCheckService service;

    private static final String iconImageLoc =
            "http://icons.iconarchive.com/icons/scafer31000/bubble-circle-3/16/GameCenter-icon.png";

    private Timer notificationTimer = new Timer();

    private Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {

        stage = primaryStage;

        // instructs the javafx system not to exit implicitly when the last application window is shut.
        Platform.setImplicitExit(false);

        // sets up the tray icon (using awt code run on the swing thread).
        javax.swing.SwingUtilities.invokeLater(this::addAppToTray);


        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = (Parent) loader.load();
//        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Graduate work 2020 ");
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.setScene(new Scene(root, bounds.getWidth(), bounds.getHeight()));
        primaryStage.show();
        Controller controller = (Controller) loader.getController();

        service = new UpdateCheckService(controller);
        service.setPeriod(Duration.hours(1));

        Label resultLabel = new Label();
        service.setOnRunning(e -> resultLabel.setText(null));
        service.setOnSucceeded(
                e -> {
                    if (service.getValue() != null) {
                        resultLabel.setText("UPDATES AVAILABLE");
                        controller.uploadUpdatedData(service.getValue());
                    } else {
                        resultLabel.setText("UP-TO-DATE");
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

                        if (pck.getText() != textStatus || pck.getText() != textStatus) {
                            try {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        String notificationTitle = "justin app";
                                        String message = "Посилка ttn з номером " + pck.getCode() + " змінила статус.";

                                        TrayNotification tray = new TrayNotification(notificationTitle, message, NotificationType.SUCCESS);
                                        tray.showAndWait();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
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


    /**
     * Sets up a system tray icon for the application.
     */
    private void addAppToTray() {
        try {
            // ensure awt toolkit is initialized.
            java.awt.Toolkit.getDefaultToolkit();

            // app requires system tray support, just exit if there is no support.
            if (!java.awt.SystemTray.isSupported()) {
                System.out.println("No system tray support, application exiting.");
                Platform.exit();
            }

            // set up a system tray icon.
            java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();
            URL imageLoc = new URL(
                    iconImageLoc
            );
            java.awt.Image image = ImageIO.read(imageLoc);
            java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(image);

            // if the user double-clicks on the tray icon, show the main app stage.
            trayIcon.addActionListener(event -> Platform.runLater(this::showStage));

            // if the user selects the default menu item (which includes the app name),
            // show the main app stage.
            java.awt.MenuItem openItem = new java.awt.MenuItem("Open justin application");
            openItem.addActionListener(event -> Platform.runLater(this::showStage));

            // the convention for tray icons seems to be to set the default icon for opening
            // the application stage in a bold font.
            java.awt.Font defaultFont = java.awt.Font.decode(null);
            java.awt.Font boldFont = defaultFont.deriveFont(java.awt.Font.BOLD);
            openItem.setFont(boldFont);

            // to really exit the application, the user must go to the system tray icon
            // and select the exit option, this will shutdown JavaFX and remove the
            // tray icon (removing the tray icon will also shut down AWT).
            java.awt.MenuItem exitItem = new java.awt.MenuItem("Exit");
            exitItem.addActionListener(event -> {
                notificationTimer.cancel();
                Platform.exit();
                tray.remove(trayIcon);
            });

            // setup the popup menu for the application.
            final java.awt.PopupMenu popup = new java.awt.PopupMenu();
            popup.add(openItem);
            popup.addSeparator();
            popup.add(exitItem);
            trayIcon.setPopupMenu(popup);


            // add the application tray icon to the system tray.
            tray.add(trayIcon);
        } catch (java.awt.AWTException | IOException e) {
            System.out.println("Unable to init system tray");
            e.printStackTrace();
        }
    }

    /**
     * Shows the application stage and ensures that it is brought ot the front of all stages.
     */
    private void showStage() {
        if (stage != null) {
            stage.show();
            stage.toFront();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
