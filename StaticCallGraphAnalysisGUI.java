package testers;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.commons.io.IOUtils;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;



import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class StaticCallGraphAnalysisGUI extends Application {

	
	 private Git git;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, null)));

        Label headingLabel = new Label("Static Call Graph Analysis");
        headingLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: green");

        Button chooseDirectoryButton = new Button("Choose Project Directory");
        chooseDirectoryButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px;");

        Button retrieveMetricsButton = new Button("Retrieve Metrics from GitHub");
        retrieveMetricsButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px;");

        Button generateCallGraphButton = new Button("Generate Call Graph DOT");
        generateCallGraphButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px;");

        Label resultLabel = new Label();
        resultLabel.setStyle("-fx-font-size: 16px;");

        chooseDirectoryButton.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(primaryStage);

            if (selectedDirectory != null && selectedDirectory.isDirectory()) {
                Results results = calculateMetrics(selectedDirectory);
                String analysis = generateMetricsAnalysis(results);
                resultLabel.setText(analysis);
            } else {
                resultLabel.setText("Invalid project directory.");
            }
        });

        retrieveMetricsButton.setOnAction(event -> {
            TextInputDialog tokenDialog = new TextInputDialog();
            tokenDialog.setTitle("GitHub Token");
            tokenDialog.setHeaderText("Enter your GitHub token:");
            tokenDialog.initOwner(primaryStage);

            tokenDialog.showAndWait().ifPresent(githubToken -> {
                if (!githubToken.isEmpty()) {
                    retrieveMetricsFromGitHub(githubToken, resultLabel, primaryStage);
                } else {
                    resultLabel.setText("GitHub token cannot be empty.");
                }
            });
        });

        generateCallGraphButton.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(primaryStage);

            if (selectedDirectory != null && selectedDirectory.isDirectory()) {
                generateCallGraphDot(selectedDirectory);
                resultLabel.setText("Call graph DOT files generated successfully.");
            } else {
                resultLabel.setText("Invalid project directory.");
            }
        });

        root.getChildren().addAll(headingLabel, chooseDirectoryButton, retrieveMetricsButton, generateCallGraphButton, resultLabel);

        Scene scene = new Scene(root, 400, 450);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    private void retrieveMetricsFromGitHub(String githubToken, Label resultLabel, Stage primaryStage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(primaryStage);

        if (selectedDirectory != null && selectedDirectory.isDirectory()) {
            Results accumulatedResults = new Results(0, 0, 0, 0, 0, 0);

            try {
                GitHub github = GitHub.connectUsingOAuth(githubToken);
                GHRepository repository = github.getRepository("murtaza215/SOR");

                // Append a unique timestamp to the directory name
                long timestamp = System.currentTimeMillis();
                File tempDirectory = Files.createTempDirectory("temp-repo-" + timestamp).toFile();

                retrieveFilesFromRepository(repository, "", tempDirectory, accumulatedResults);

                String analysis = generateMetricsAnalysis(accumulatedResults);
                resultLabel.setText(analysis);
            } catch (IOException e) {
                e.printStackTrace();
                resultLabel.setText("Error retrieving repository or files.");
            }
        } else {
            resultLabel.setText("Invalid project directory.");
        }
    }

    private void retrieveFilesFromRepository(GHRepository repository, String path, File destinationDir, Results accumulatedResults) throws IOException {
        for (GHContent content : repository.getDirectoryContent(path)) {
            String contentPath = content.getPath();
            if (content.isFile() && contentPath.endsWith(".java")) {
                System.out.println("Fetching file: " + contentPath);
                try {
                    GHContent fileContent = repository.getFileContent(contentPath);
                    byte[] fileBytes = IOUtils.toByteArray(fileContent.read());

                    String relativePath = contentPath.substring(path.length());
                    File tempFile = new File(destinationDir, relativePath);

                    // Ensure parent directories exist
                    File parentDir = tempFile.getParentFile();
                    if (!parentDir.exists()) {
                        parentDir.mkdirs();
                    }

                    Files.write(tempFile.toPath(), fileBytes);

                    // Calculate metrics for the fetched file
                    Results results = calculateMetrics(tempFile);
                    accumulatedResults.add(results);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (content.isDirectory()) {
                // If the content is a directory, recursively retrieve its contents
                String subDirectoryPath = path.isEmpty() ? content.getName() : path + "/" + content.getName();
                retrieveFilesFromRepository(repository, subDirectoryPath, destinationDir, accumulatedResults);
            }
        }
    }


    // ... (other methods)


    private void generateCallGraphDot(File projectDir) {
        CallGraph callGraph = new CallGraph();

        for (File file : projectDir.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".java")) {
                try {
                    ParseResult<CompilationUnit> parseResult = new JavaParser().parse(file);
                    if (parseResult.isSuccessful()) {
                        CompilationUnit cu = parseResult.getResult().orElseThrow(() -> new IOException("Parsing failed"));

                        CallGraphVisitor visitor = new CallGraphVisitor();
                        visitor.visit(cu, null); 

                        CallGraph fileCallGraph = visitor.getCallGraph();
                        callGraph.add(fileCallGraph); 
                    } else {
                        throw new IOException("Parsing failed");
                    }
                } catch (IOException e) {
                    System.err.println("Error processing file: " + e.getMessage());
                }
            }
        }

        try {
            callGraph.generateDotFile("callgraph.dot"); 
        } catch (IOException e) {
            System.err.println("Error generating DOT file: " + e.getMessage());
        }
    }





    private void analyzeCallGraph(CallGraph callGraph) {
        try {
            callGraph.generateDotFile("call_graph.dot");
            System.out.println("DOT file generated successfully.");
        } catch (IOException e) {
            System.err.println("Error generating DOT file: " + e.getMessage());
        }
    }




    private String generateMetricsAnalysis(Results results) {
        String analysis = "Metrics Analysis:\n\n";

        analysis += "Total Coupling Score: " + results.couplingScore + "\n";
        if (results.couplingScore < 10) {
            analysis += "Low coupling indicates good modularity.\n";
        } else if (results.couplingScore < 20) {
            analysis += "Moderate coupling may be acceptable depending on the context.\n";
        } else {
            analysis += "High coupling could lead to maintenance issues.\n";
        }

        analysis += "\nTotal Cyclomatic Complexity: " + results.cyclomaticComplexity + "\n";
        if (results.cyclomaticComplexity < 10) {
            analysis += "Low complexity indicates simpler code.\n";
        } else if (results.cyclomaticComplexity < 20) {
            analysis += "Moderate complexity could be manageable.\n";
        } else {
            analysis += "High complexity might lead to hard-to-maintain code.\n";
        }

        analysis += "\nTotal Lines of Code: " + results.linesOfCode + "\n";
        if (results.linesOfCode < 1000) {
            analysis += "Relatively small codebase.\n";
        } else if (results.linesOfCode < 5000) {
            analysis += "Moderate-sized codebase.\n";
        } else {
            analysis += "Large codebase; consider modularization.\n";
        }

        analysis += "\nTotal Number of Classes: " + results.totalClasses + "\n";
        if (results.totalClasses < 10) {
            analysis += "Low number of classes; codebase might lack organization.\n";
        } else if (results.totalClasses < 20) {
            analysis += "Moderate number of classes; codebase is reasonably organized.\n";
        } else {
            analysis += "High number of classes; ensure proper modularization and organization.\n";
        }

        analysis += "\nTotal Number of Methods: " + results.totalMethods + "\n";
        if (results.totalMethods < 50) {
            analysis += "Low number of methods; codebase might be too simple or lacks functionality.\n";
        } else if (results.totalMethods < 100) {
            analysis += "Moderate number of methods; codebase has moderate complexity.\n";
        } else {
            analysis += "High number of methods; consider refactoring for better maintainability.\n";
        }

        return analysis;
    }




    private Results calculateMetrics(File projectDir) {
        int totalCouplingScore = 0;
        int totalCyclomaticComplexity = 0;
        int totalLinesOfCode = 0;
        int totalFiles = 0;
        int totalClasses = 0;
        int totalMethods = 0;

        for (File file : projectDir.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".java")) {
                try {
                    StaticCallGraphAnalysis calculator = new StaticCallGraphAnalysis();

                    CompilationUnit cu = StaticJavaParser.parse(file);

                    if (cu != null) { // Check if parsing was successful
                        totalCouplingScore += calculator.calculateCouplingScore(cu);
                        totalCyclomaticComplexity += calculator.calculateCyclomaticComplexity(file);

                        try {
                            List<String> lines = Files.readAllLines(file.toPath());
                            if (lines != null) {
                                totalLinesOfCode += lines.size();
                            } else {
                                System.err.println("Error reading file: " + file.getName());
                            }
                        } catch (IOException e) {
                            System.err.println("Error reading file: " + e.getMessage());
                        }

                        totalMethods += calculator.countMethods(cu);

                        totalFiles++;

                        if (file.getName().endsWith(".java")) {
                            totalClasses++;
                        }
                    } else {
                        System.err.println("Error parsing file: " + file.getName());
                    }
                } catch (IOException e) {
                    System.err.println("Error processing file: " + e.getMessage());
                }
            }
        }

        return new Results(totalCouplingScore, totalCyclomaticComplexity, totalLinesOfCode, totalFiles, totalClasses, totalMethods);
    }




    private static class Results {
        int couplingScore;
        int cyclomaticComplexity;
        int linesOfCode;
        int totalFiles;
        int totalClasses;
        int totalMethods;

        Results(int couplingScore, int cyclomaticComplexity, int linesOfCode, int totalFiles, int totalClasses, int totalMethods) {
            this.couplingScore = couplingScore;
            this.cyclomaticComplexity = cyclomaticComplexity;
            this.linesOfCode = linesOfCode;
            this.totalFiles = totalFiles;
            this.totalClasses = totalClasses;
            this.totalMethods = totalMethods;
        }
        
        public void add(Results other) {
            couplingScore += other.couplingScore;
            cyclomaticComplexity += other.cyclomaticComplexity;
            linesOfCode += other.linesOfCode;
            totalFiles += other.totalFiles;
            totalClasses += other.totalClasses;
            totalMethods += other.totalMethods;
        }
    }
}
