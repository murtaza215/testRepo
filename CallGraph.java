package testers;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CallGraph {
    private Map<String, Map<String, CallGraphNode>> moduleClassNodes = new HashMap<>();

    public CallGraphNode getClassNode(String moduleName, String className, boolean isMethod) {
        moduleClassNodes.putIfAbsent(moduleName, new HashMap<>());
        Map<String, CallGraphNode> classNodes = moduleClassNodes.get(moduleName);

        if (!classNodes.containsKey(className)) {
            classNodes.put(className, new CallGraphNode(moduleName, className, isMethod));
        }

        return classNodes.get(className);
    }

    public void add(CallGraph otherGraph) {
        for (Map.Entry<String, Map<String, CallGraphNode>> entry : otherGraph.moduleClassNodes.entrySet()) {
            String moduleName = entry.getKey();
            for (Map.Entry<String, CallGraphNode> classEntry : entry.getValue().entrySet()) {
                String className = classEntry.getKey();
                CallGraphNode existingClassNode = getClassNode(moduleName, className, classEntry.getValue().isMethod());
                CallGraphNode newClassNode = classEntry.getValue();
                for (Map.Entry<String, CallGraphNode> calleeEntry : newClassNode.getCallees().entrySet()) {
                    String calleeName = calleeEntry.getKey();
                    CallGraphNode calleeNode = calleeEntry.getValue();
                    existingClassNode.addCallee(calleeName, calleeNode);
                }

                if (newClassNode instanceof InheritanceAwareNode) {
                    CallGraphNode parentClassNode = ((InheritanceAwareNode) newClassNode).getParent();
                    if (parentClassNode != null) {
                        existingClassNode.setParent(parentClassNode);
                    }
                }
            }
        }
    }

    public void generateDotFile(String filePath) throws IOException {
        FileWriter writer = new FileWriter(filePath);

        writer.write("digraph CallGraph {\n");
        for (String moduleName : moduleClassNodes.keySet()) {
            writer.write("  subgraph cluster_" + moduleName + " {\n");
            writer.write("    label=\"" + moduleName + "\";\n");

            for (CallGraphNode classNode : moduleClassNodes.get(moduleName).values()) {
                String nodeShape = classNode.isMethod() ? "ellipse" : "box"; // Set different shapes
                writer.write("    \"" + classNode.getName() + "\" [shape=" + nodeShape + "];\n");
                for (CallGraphNode methodNode : classNode.getCallees().values()) {
                    String methodShape = methodNode.isMethod() ? "ellipse" : "box"; // Set different shapes
                    writer.write("    \"" + classNode.getName() + "\" -> \"" + methodNode.getName() + "\" [shape=" + methodShape + "];\n");
                }

                CallGraphNode parentClassNode = classNode.getParent();
                if (parentClassNode != null) {
                    writer.write("    \"" + parentClassNode.getName() + "\" -> \"" + classNode.getName() + "\" [style=dashed];\n");
                }
            }

            writer.write("  }\n");
        }
        writer.write("}");

        writer.close();
    }
}
