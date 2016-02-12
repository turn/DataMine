package datamine.storage.idl.generator.metadata;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import datamine.storage.idl.Schema;
import datamine.storage.idl.json.JsonSchemaConvertor;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.*;

public class SchemaStringConverterTest {

    @BeforeMethod
    public void setUp() throws Exception {

    }

    @AfterMethod
    public void tearDown() throws Exception {

    }

    @Test
    public void testApply() throws Exception {
        String filePath = "src/test/resources/RBSchema.json";
        JsonSchemaConvertor convertor = new JsonSchemaConvertor();
        Schema dmSchema = convertor.apply(
                Files.toString(new File(filePath), Charsets.UTF_8));

        SchemaStringConverter converter = new SchemaStringConverter();
        Schema rqSchema = converter.apply(dmSchema.toString());
        Assert.assertEquals(dmSchema.getTableList().size(), rqSchema.getTableList().size());
    }
}