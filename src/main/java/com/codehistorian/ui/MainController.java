package com.codehistorian.ui;

import com.codehistorian.ai.AnswerGenerator;
import com.codehistorian.ai.AnthropicLlmClient;
import com.codehistorian.ai.LlmClient;
import com.codehistorian.model.AiAnswer;
import com.codehistorian.model.CommitInfo;
import com.codehistorian.model.Evidence;
import com.codehistorian.model.RepositoryInfo;
import com.codehistorian.model.SearchResult;
import com.codehistorian.service.EvidenceService;
import com.codehistorian.service.GitHistoryService;
import com.codehistorian.service.QuestionService;
import com.codehistorian.service.RepositoryService;
import com.codehistorian.service.SearchService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MainController {

    @FXML private TreeView<Path> repoTreeView;
    @FXML private ListView<CommitInfo> commitListView;
    @FXML private TextArea codeTextArea;
    @FXML private TextArea diffTextArea;
    @FXML private TabPane mainTabPane;
    @FXML private TextField searchField;
    @FXML private ListView<SearchResult> searchResultsListView;
    @FXML private TextField questionField;
    @FXML private TextArea answerTextArea;
    @FXML private ListView<Evidence> evidenceListView;
    @FXML private Label statusLabel;

    private final RepositoryService repositoryService = new RepositoryService();
    private final GitHistoryService gitHistoryService = new GitHistoryService();
    private final SearchService searchService = new SearchService();
    private final EvidenceService evidenceService = new EvidenceService();
    private QuestionService questionService;

    @FXML
    public void initialize() {
        repoTreeView.setCellFactory(tv -> new TreeCell<>() {
            @Override
            protected void updateItem(Path item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    Path name = item.getFileName();
                    setText(name != null ? name.toString() : item.toString());
                }
            }
        });
        repoTreeView.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null && selected.getValue() != null && Files.isRegularFile(selected.getValue())) {
                showFileContent(selected.getValue());
            }
        });

        commitListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(CommitInfo commit, boolean empty) {
                super.updateItem(commit, empty);
                setText(empty || commit == null ? null
                        : commit.getShortId() + "  " + commit.getShortMessage() + "  (" + commit.getAuthorName() + ")");
            }
        });
        commitListView.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                showCommitDiff(selected);
            }
        });

        searchResultsListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(SearchResult result, boolean empty) {
                super.updateItem(result, empty);
                setText(empty || result == null ? null : "[" + result.getType() + "] " + result.getTitle());
            }
        });

        evidenceListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Evidence evidence, boolean empty) {
                super.updateItem(evidence, empty);
                setText(empty || evidence == null ? null : "[" + evidence.getType() + "] " + evidence.getLabel());
            }
        });
        evidenceListView.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                openEvidence(selected);
            }
        });

        LlmClient llmClient = new AnthropicLlmClient(System.getenv("ANTHROPIC_API_KEY"));
        questionService = new QuestionService(searchService, evidenceService, new AnswerGenerator(llmClient));
    }

    @FXML
    private void onOpenRepository() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Open Git Repository");
        File dir = chooser.showDialog(getStage());
        if (dir == null) {
            return;
        }
        openRepositoryAt(dir.toPath());
    }

    @FXML
    private void onCloneRepository() {
        TextInputDialog urlDialog = new TextInputDialog();
        urlDialog.setTitle("Clone Repository");
        urlDialog.setHeaderText("Clone a GitHub repository");
        urlDialog.setContentText("Repository URL:");
        urlDialog.showAndWait().ifPresent(url -> {
            if (url.isBlank()) {
                return;
            }
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Select destination folder");
            File targetParent = chooser.showDialog(getStage());
            if (targetParent == null) {
                return;
            }
            String repoName = url.substring(url.lastIndexOf('/') + 1).replace(".git", "");
            Path target = targetParent.toPath().resolve(repoName);
            cloneRepositoryTo(url, target);
        });
    }

    @FXML
    private void onExit() {
        Platform.exit();
    }

    @FXML
    private void onRefreshRepository() {
        RepositoryInfo info = repositoryService.getCurrentRepositoryInfo();
        if (info != null) {
            openRepositoryAt(info.getLocalPath());
        }
    }

    @FXML
    private void onBuildIndex() {
        buildSearchIndexAsync();
    }

    @FXML
    private void onAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("GitLens");
        alert.setContentText("Reconstructs the reasoning behind code changes using Git, GitHub, and AI.");
        alert.showAndWait();
    }

    @FXML
    private void onSearch() {
        String query = searchField.getText();
        if (query == null || query.isBlank()) {
            return;
        }
        Task<List<SearchResult>> task = new Task<>() {
            @Override
            protected List<SearchResult> call() throws Exception {
                return searchService.search(query, 25);
            }
        };
        task.setOnSucceeded(e -> searchResultsListView.getItems().setAll(task.getValue()));
        task.setOnFailed(e -> showError("Search failed", task.getException()));
        new Thread(task, "search").start();
    }

    @FXML
    private void onAsk() {
        String question = questionField.getText();
        if (question == null || question.isBlank()) {
            return;
        }
        answerTextArea.setText("Thinking...");
        Task<AiAnswer> task = new Task<>() {
            @Override
            protected AiAnswer call() throws Exception {
                return questionService.ask(question);
            }
        };
        task.setOnSucceeded(e -> {
            AiAnswer answer = task.getValue();
            answerTextArea.setText(answer.getAnswerText());
            evidenceListView.getItems().setAll(answer.getEvidence());
        });
        task.setOnFailed(e -> showError("Failed to get an answer", task.getException()));
        new Thread(task, "ask-question").start();
    }

    private void openRepositoryAt(Path path) {
        statusLabel.setText("Opening repository...");
        Task<RepositoryInfo> task = new Task<>() {
            @Override
            protected RepositoryInfo call() throws Exception {
                return repositoryService.openRepository(path);
            }
        };
        task.setOnSucceeded(e -> onRepositoryLoaded(task.getValue()));
        task.setOnFailed(e -> showError("Failed to open repository", task.getException()));
        new Thread(task, "open-repository").start();
    }

    private void cloneRepositoryTo(String url, Path target) {
        statusLabel.setText("Cloning repository...");
        Task<RepositoryInfo> task = new Task<>() {
            @Override
            protected RepositoryInfo call() throws Exception {
                return repositoryService.cloneRepository(url, target);
            }
        };
        task.setOnSucceeded(e -> onRepositoryLoaded(task.getValue()));
        task.setOnFailed(e -> showError("Failed to clone repository", task.getException()));
        new Thread(task, "clone-repository").start();
    }

    private void onRepositoryLoaded(RepositoryInfo info) {
        statusLabel.setText(info.getName() + "  [" + info.getCurrentBranch() + "]  "
                + info.getCommitCount() + " commits");
        populateTree(info.getLocalPath());
        loadCommits();
        buildSearchIndexAsync();
    }

    private void populateTree(Path root) {
        TreeItem<Path> rootItem = createTreeItem(root);
        rootItem.setExpanded(true);
        repoTreeView.setRoot(rootItem);
    }

    private TreeItem<Path> createTreeItem(Path path) {
        return new TreeItem<>(path) {
            private boolean loaded = false;

            @Override
            public boolean isLeaf() {
                return !Files.isDirectory(path);
            }

            @Override
            public javafx.collections.ObservableList<TreeItem<Path>> getChildren() {
                if (!loaded) {
                    loaded = true;
                    super.getChildren().setAll(buildChildren(path));
                }
                return super.getChildren();
            }
        };
    }

    private List<TreeItem<Path>> buildChildren(Path dir) {
        List<TreeItem<Path>> children = new ArrayList<>();
        try (Stream<Path> stream = Files.list(dir)) {
            stream.filter(p -> !p.getFileName().toString().equals(".git")
                            && !p.getFileName().toString().equals(".code-history"))
                    .sorted((a, b) -> {
                        boolean aDir = Files.isDirectory(a);
                        boolean bDir = Files.isDirectory(b);
                        if (aDir != bDir) {
                            return aDir ? -1 : 1;
                        }
                        return a.getFileName().toString().compareToIgnoreCase(b.getFileName().toString());
                    })
                    .forEach(p -> children.add(createTreeItem(p)));
        } catch (IOException e) {
            // unreadable directory, leave empty
        }
        return children;
    }

    private void loadCommits() {
        Task<List<CommitInfo>> task = new Task<>() {
            @Override
            protected List<CommitInfo> call() throws Exception {
                return gitHistoryService.getRecentCommits(repositoryService.getCurrentGit(), 200);
            }
        };
        task.setOnSucceeded(e -> commitListView.getItems().setAll(task.getValue()));
        task.setOnFailed(e -> showError("Failed to load commits", task.getException()));
        new Thread(task, "load-commits").start();
    }

    private void showCommitDiff(CommitInfo commit) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                return gitHistoryService.getDiff(repositoryService.getCurrentGit(), commit.getId());
            }
        };
        task.setOnSucceeded(e -> {
            diffTextArea.setText(task.getValue());
            mainTabPane.getSelectionModel().select(1);
        });
        task.setOnFailed(e -> showError("Failed to load diff", task.getException()));
        new Thread(task, "load-diff").start();
    }

    private void showFileContent(Path file) {
        try {
            codeTextArea.setText(Files.readString(file));
            mainTabPane.getSelectionModel().select(0);
        } catch (IOException e) {
            codeTextArea.setText("Unable to read file: " + e.getMessage());
        }
    }

    private void buildSearchIndexAsync() {
        RepositoryInfo info = repositoryService.getCurrentRepositoryInfo();
        if (info == null) {
            return;
        }
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                searchService.open(repositoryService.getIndexManager().getLuceneIndexPath());
                List<CommitInfo> commits = gitHistoryService.getRecentCommits(repositoryService.getCurrentGit(), 500);
                searchService.indexCommits(commits);
                try (Stream<Path> stream = Files.walk(info.getLocalPath())) {
                    List<String> paths = stream.filter(Files::isRegularFile)
                            .filter(p -> !p.toString().contains(".git"))
                            .map(p -> info.getLocalPath().relativize(p).toString())
                            .toList();
                    searchService.indexFilePaths(paths);
                }
                return null;
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() ->
                statusLabel.setText(statusLabel.getText() + "  |  index ready")));
        task.setOnFailed(e -> showError("Failed to build search index", task.getException()));
        new Thread(task, "build-index").start();
    }

    private void openEvidence(Evidence evidence) {
        switch (evidence.getType()) {
            case COMMIT -> commitListView.getItems().stream()
                    .filter(c -> c.getId().equals(evidence.getReferenceId()))
                    .findFirst()
                    .ifPresent(c -> {
                        commitListView.getSelectionModel().select(c);
                        commitListView.scrollTo(c);
                    });
            case FILE -> {
                RepositoryInfo info = repositoryService.getCurrentRepositoryInfo();
                if (info != null) {
                    showFileContent(info.getLocalPath().resolve(evidence.getReferenceId()));
                }
            }
            default -> { }
        }
    }

    private void showError(String header, Throwable ex) {
        Platform.runLater(() -> {
            statusLabel.setText(header);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(header);
            alert.setContentText(ex != null ? ex.getMessage() : "Unknown error");
            alert.showAndWait();
        });
    }

    private Stage getStage() {
        return (Stage) statusLabel.getScene().getWindow();
    }
}
