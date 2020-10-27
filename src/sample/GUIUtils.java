package sample;

import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

public class GUIUtils {
    public static void defineTableCell(TableColumn tableColumn, String properyName) {
        tableColumn.setCellValueFactory(
                new PropertyValueFactory<Package, String>(properyName)
        );

        tableColumn.setCellFactory(column ->
                new TableCell<Package, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            Text text = new Text(item);
                            double calcwidth = text.getLayoutBounds().getWidth();
                            double cellWidth = getTableColumn().getWidth();

                            double cellWidthRatio = Math.ceil(calcwidth / cellWidth);
                            if (cellWidthRatio > 1.0) {
                                setPrefHeight(30 * cellWidthRatio);
                            }

                            setWrapText(true);
                            setText(item);
                        }
                    }
                }
        );
    }

    public static void defineTableCell(TableColumn tableColumn, String properyName, double maxWidth) {
        tableColumn.setMaxWidth(maxWidth);
        defineTableCell(tableColumn, properyName);
    }
}
