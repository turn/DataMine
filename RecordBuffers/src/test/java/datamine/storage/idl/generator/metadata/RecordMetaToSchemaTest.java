package datamine.storage.idl.generator.metadata;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import datamine.storage.idl.Schema;
import datamine.storage.idl.json.JsonSchemaConvertor;
import datamine.storage.recordbuffers.example.model.MainTableMetadata;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.assertEquals;

public class RecordMetaToSchemaTest {

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
        RecordMetaToSchema converter = new RecordMetaToSchema();
        Schema rqSchema = converter.apply(MainTableMetadata.class);
        assertEquals(rqSchema.getTableList().size(), dmSchema.getTableList().size());
    }
}