package com.codehistorian.ui;

import com.codehistorian.model.AppSettings;
import com.codehistorian.service.SettingsService;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.io.IOException;
import java.util.Optional;

public class SettingsDialog {

    private final SettingsService settingsService;

    public SettingsDialog(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void showAndSave(Window owner) {
        AppSettings current = settingsService.load();

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Settings");
        dialog.setHeaderText("AI Assistant");
        dialog.initOwner(owner);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        PasswordField apiKeyField = new PasswordField();
        apiKeyField.setPromptText("sk-ant-...");
        apiKeyField.setPrefWidth(360);
        if (current.getAnthropicApiKey() != null) {
            apiKeyField.setText(current.getAnthropicApiKey());
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Anthropic API Key"), 0, 0);
        grid.add(apiKeyField, 1, 0);

        Label pathLabel = new Label("Stored in plain text at:\n" + settingsService.settingsFile());
        pathLabel.setWrapText(true);
        pathLabel.setMaxWidth(420);

        VBox content = new VBox(12, grid, pathLabel);
        content.setPadding(new Insets(16));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getStylesheets().add(
                SettingsDialog.class.getResource("/css/modern.css").toExternalForm());

        dialog.setResultConverter(button -> button == saveButtonType ? apiKeyField.getText() : null);

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(key -> {
            AppSettings updated = new AppSettings();
            updated.setAnthropicApiKey(key.isBlank() ? null : key);
            try {
                settingsService.save(updated);
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Failed to save settings: " + e.getMessage()).showAndWait();
            }
        });
    }
}
