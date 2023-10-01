package testers;

import java.util.HashMap;
import java.util.Map;

public class CallGraphNode {
    private String moduleName;
    private String name;
    private boolean isMethod;
    private CallGraphNode parent;
    private Map<String, CallGraphNode> callees;

    public CallGraphNode(String moduleName, String name, boolean isMethod) {
        this.moduleName = moduleName;
        this.name = name;
        this.isMethod = isMethod;
        this.callees = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public String getModuleName() {
        return moduleName;
    }

    public boolean isMethod() {
        char firstChar = name.charAt(0);
        return Character.isLowerCase(firstChar) || !Character.isLetter(firstChar);
    }
    public CallGraphNode getParent() {
        return parent;
    }

    public void setParent(CallGraphNode parent) {
        this.parent = parent;
    }

    public Map<String, CallGraphNode> getCallees() {
        return callees;
    }

    public void addCallee(String calleeName, CallGraphNode calleeNode) {
        callees.put(calleeName, calleeNode);
    }

  
}
