package testers;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class CallGraphVisitor extends VoidVisitorAdapter<Void> {
    private CallGraph callGraph = new CallGraph();
    private String currentModule = null;
    private String currentClass = null;

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Void arg) {
        currentModule = determineModuleName(n);
        currentClass = n.getNameAsString();
        super.visit(n, arg);
        currentClass = null;
        currentModule = null;
    }

    @Override
    public void visit(MethodDeclaration n, Void arg) {
        if (currentModule != null && currentClass != null) {
            CallGraphNode classNode = callGraph.getClassNode(currentModule, currentClass, false);
            CallGraphNode methodNode = callGraph.getClassNode(currentModule, n.getNameAsString(), false); // Updated this line
            classNode.addCallee(n.getNameAsString(), methodNode);
        }
        super.visit(n, arg);
    }

    private String determineModuleName(ClassOrInterfaceDeclaration classNode) {
        String packageName = classNode.getFullyQualifiedName().orElse("");
        String[] packageParts = packageName.split("\\.");
        if (packageParts.length > 0) {
            return packageParts[0]; 
        }
        return null; 
    }

    public CallGraph getCallGraph() {
        return callGraph;
    }
}
