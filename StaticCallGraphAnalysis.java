package testers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit; // JavaParser's CompilationUnit
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.WhileStmt;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StaticCallGraphAnalysis extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Attack Surface Meter");

        Button calculateButton = new Button("Calculate Metrics");
        Label resultLabel = new Label();

        calculateButton.setOnAction(event -> {
            String projectDirectory = "D:\\Projects\\Java Projects\\OOP Labs\\src\\Lab11";
            File projectDir = new File(projectDirectory);

            if (!projectDir.exists() || !projectDir.isDirectory()) {
                resultLabel.setText("Invalid project directory.");
                return;
            }

            Results results = calculateMetrics(projectDir);
            resultLabel.setText("Total Coupling Score: " + results.couplingScore
                    + "\nTotal Cyclomatic Complexity: " + results.cyclomaticComplexity
                    + "\nTotal Lines of Code: " + results.linesOfCode
                    + "\nTotal Number of Files: " + results.totalFiles
                    + "\nTotal Number of Classes: " + results.totalClasses
                    + "\nTotal Number of Methods: " + results.totalMethods);
            String analysis = generateMetricsAnalysis(results);
            resultLabel.setText(analysis);
        });

        VBox vbox = new VBox(10, calculateButton, resultLabel);
        Scene scene = new Scene(vbox, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private CallGraph generateCallGraph(File file) throws IOException {
        ParseResult<CompilationUnit> parseResult = new JavaParser().parse(file);
        
        if (parseResult.isSuccessful()) {
            CompilationUnit cu = parseResult.getResult().orElseThrow(() -> new IOException("Parsing failed"));
            
            CallGraphVisitor visitor = new CallGraphVisitor();
            cu.accept(visitor, null);

            return visitor.getCallGraph();
        } else {
            throw new IOException("Parsing failed");
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

        analysis += "Total Cyclomatic Complexity: " + results.cyclomaticComplexity + "\n";
        if (results.cyclomaticComplexity < 10) {
            analysis += "Low complexity indicates simpler code.\n";
        } else if (results.cyclomaticComplexity < 20) {
            analysis += "Moderate complexity could be manageable.\n";
        } else {
            analysis += "High complexity might lead to hard-to-maintain code.\n";
        }

        analysis += "Total Lines of Code: " + results.linesOfCode + "\n";
        if (results.linesOfCode < 1000) {
            analysis += "Relatively small codebase.\n";
        } else if (results.linesOfCode < 5000) {
            analysis += "Moderate-sized codebase.\n";
        } else {
            analysis += "Large codebase; consider modularization.\n";
        }
        
        analysis += "Total Number of Files: " + results.totalFiles + "\n";
        analysis += "Total Number of Classes: " + results.totalClasses+ "\n";
        analysis +=  "Total Number of Methods: " + results.totalMethods+ "\n";

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
                    CompilationUnit cu = StaticJavaParser.parse(file);
                    totalCouplingScore += calculateCouplingScore(cu);
                    totalCyclomaticComplexity += calculateCyclomaticComplexity(file);

                    List<String> lines = Files.readAllLines(file.toPath());
                    totalLinesOfCode += lines.size();

                    totalMethods += countMethods(cu);

                    totalFiles++;
                } catch (IOException e) {
                    System.err.println("Error processing file: " + e.getMessage());
                }

                if (file.getName().endsWith(".java")) {
                    totalClasses++;
                }
            }
        }

        return new Results(totalCouplingScore, totalCyclomaticComplexity, totalLinesOfCode, totalFiles, totalClasses, totalMethods);
    }

    public int countMethods(CompilationUnit cu) {
        int count = 0;
        for (ClassOrInterfaceDeclaration type : cu.findAll(ClassOrInterfaceDeclaration.class)) {
            count += type.getMethods().size();
        }
        return count;
    }

    public int calculateCyclomaticComplexity(File file) throws IOException {
        ParserConfiguration parserConfiguration = new ParserConfiguration();
        StaticJavaParser.setConfiguration(parserConfiguration);

        CompilationUnit cu = StaticJavaParser.parse(file);

        return countDecisions(cu);
    }

    public int calculateCouplingScore(CompilationUnit cu) {
        int couplingScore = 0;

        for (MethodCallExpr methodCall : cu.findAll(MethodCallExpr.class)) {
            if (!methodCall.getScope().isPresent()) {
                couplingScore++;
            }
        }

        return couplingScore;
    }

    private int countDecisions(Node node) {
        int count = 0;

        if (node instanceof IfStmt || node instanceof ForStmt ||
                node instanceof WhileStmt || node instanceof DoStmt ||
                node instanceof SwitchStmt) {
            count++;
        }

        if (node instanceof BinaryExpr) {
            count++;
        }

        for (Node child : node.getChildNodes()) {
            count += countDecisions(child);
        }

        return count;
    }
    
    private void analyzeCallGraph(CallGraph callGraph) {
        try {
            callGraph.generateDotFile("call_graph.dot");
            System.out.println("DOT file generated successfully.");
        } catch (IOException e) {
            System.err.println("Error generating DOT file: " + e.getMessage());
        }
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
    }
}
