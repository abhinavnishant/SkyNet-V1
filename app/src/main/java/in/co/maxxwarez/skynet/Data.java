package in.co.maxxwarez.skynet;

import java.util.HashMap;
import java.util.Map;

public class Data {
    public String getA1() {
        return a1;
    }

    public String getB1() {
        return b1;
    }

    public String a1, b1;
    //getters and setters ...

    //toMap() is necessary for the push process
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("a1", a1);
        result.put("b1", b1);
        return result;
    }
}