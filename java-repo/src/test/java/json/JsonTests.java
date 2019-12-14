package json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import junitTests.simple.SimpleObject;
import org.junit.Test;
import xxx.joker.libs.core.test.JkDataTest;

import java.io.IOException;
import java.io.StringWriter;

import static xxx.joker.libs.core.util.JkConsole.display;

public class JsonTests {

    JkDataTest dataTest = new JkDataTest(666);

    @Test
    public void testSimpleObject() throws IOException {
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        StringWriter sw = new StringWriter();
        SimpleObject sobj = new SimpleObject(dataTest);
        jsonMapper.writeValue(sw, sobj);
        display(sw.toString());

    }


}
