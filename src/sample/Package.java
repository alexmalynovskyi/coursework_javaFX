package sample;

import javafx.beans.InvalidationListener;
import javafx.collections.ArrayChangeListener;
import javafx.collections.ObservableArray;

public class Package implements ObservableArray<Package> {
    public Package(String text) {
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String code;
    private String description;
    private String date;
    private String status;
    private String text;

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "Package{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", status='" + status + '\'' +
                ", text='" + text + '\'' +
                '}';
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public String getText() {
        return text;
    }

    public Package(String code, String description, String date, String status, String text) {
        this.code = code;
        this.description = description;
        this.date = date;
        this.status = status;
        this.text = text;
    }

    public String getDescription() {
        return description;
    }

    public Package(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public void addListener(ArrayChangeListener<Package> arrayChangeListener) {

    }

    @Override
    public void removeListener(ArrayChangeListener<Package> arrayChangeListener) {

    }

    @Override
    public void resize(int i) {

    }

    @Override
    public void ensureCapacity(int i) {

    }

    @Override
    public void trimToSize() {

    }

    @Override
    public void clear() {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {

    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {

    }
}