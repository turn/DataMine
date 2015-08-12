# Developer Guide

We will use an example to explain how to create an application using DataMine. In the application, a profile is defined to describe the user information. A user profile has not only basic attributes, such as ID, version, but also the nested tables for events, such as impressions, id mapping events, and temporal information.  DataMine allows to use JSON to define the data structure below. 

Note that the example can be found at [..](../RecordBuffers/src/test/java/datamine/storage/recordbuffers/example)

## Table Schema

The table schema of the profile is defined in [JSON](../RecordBuffers/src/test/resources/RBSchema.json)

```JSON
{
  "schema": "simple_schema",
  "table_list": [
    {
      "table": "second_level_nested_table",
      "fields": [
        {"id": 1,"name": "byte_required_column",			"type": "Byte",		"isRequired": true},
        {"id": 2,"name": "boolean_list_column",      		"type": "List:Boolean"}
      ]
    },
    {
      "table": "first_level_nested_table",
      "fields": [
        {"id": 1, 	"name": "int_required_column", 			"type": "Integer",		"isRequired": true},
        {"id": 2,	"name": "nested_table_column",			"type": "List:second_level_nested_table"},
		{"id": 0, 	"name": "string_derived_column",     	"type": "String", "default": "Unknown", 	"isDerived": true}
		]
    },
   {
      "table": "struct_table",
      "fields": [
        {"id": 1,	"name": "nested_table_column",			"type": "List:second_level_nested_table"}
      ]
    },
    {
      "table": "main_table",
      "fields": [
        {"id": 1,	"name": "long_required_column",			"type": "Long",   	"isRequired": true, "isFrequentlyUsed": true},
        {"id": 2,	"name": "int_sorted_column",         	"type": "Integer", 	"isRequired": true, "isDesSortKey": true},
        {"id": 3,	"name": "byte_column",      			"type": "Byte",  	"default": "-1"},
        {"id": 4,	"name": "boolean_column",      			"type": "Boolean", 	"default": "false"},
		{"id": 5,	"name": "short_column",      			"type": "Short", 	"default": "0"},
		{"id": 6,	"name": "float_column",      			"type": "Float", 	"default": "0.0"},
		{"id": 7,	"name": "double_column",      			"type": "Double", 	"default": "0.001"},
		{"id": 8,	"name": "string_column",      			"type": "String", 	"default": "Unknown"},
		{"id": 9,	"name": "binary_column",     			"type": "Binary"},
		{"id": 10,	"name": "nested_table_column",          "type": "List:first_level_nested_table", "hasLargeList": true},
		{"id": 11,	"name": "struct_column",                "type": "struct_table"},
		{"id": 12, 	"name": "int_list_column",     			"type": "List:Integer"},
		{"id": 0, 	"name": "string_derived_column",     	"type": "String", 	"default": "Unknown", 	"isDerived": true},
		{"id": 0, 	"name": "int_derived_column",     		"type": "Integer", 	"default": "0", 		"isDerived": true}
      ]
    }
  ]
}
```


## Code Generation

DataMine implements a set of code generator to help the user create Java classes through which the user can write its program to read, write and process the profile data. To trigger the code generation when building the application, POM needs some [changes](../RecordBuffers#code_generation). 

To simplify the example, a class [GenerateTestData.java](../RecordBuffers/src/test/java/datamine/storage/recordbuffers/example/GenerateTestData.java) is created to generate all codes. 

## Derived Field Support

DataMine allows derived attribute in the table. The value of a derived field is calculated on the fly instead of stored physically. Importantly the user or table owner should define the way how this calculation is done. For example, a class should be defined to implement the interface, [MainTableDerivedValueInterface.java](../RecordBuffers/src/test/java/datamine/storage/recordbuffers/example/interfaces/MainTableDerivedValueInterface.java), as a derived attribute "string_derived_column" is introduced in the table of *main_table*.  The implementation can be as follows:

```Java
public class MainTableDerived implements MainTableDerivedValueInterface {

	private final MainTableInterface mti;
	public MainTableDerived(MainTableInterface mti) {
		this.mti = mti;

	}

	@Override
	public String getStringDerivedColumn() {
		return "StringDerivedColumn@MainTable";
	}

	@Override
	public int getIntDerivedColumn() {
		return 101;
	}

}
```

A setter function is defined to connect the implementation with the table access class. For instance, 

```Java
aup.setDerivedValueImplementation(new MainTableDerived(aup));
String theValue = aup.getStringDerivedColumn();
```


## Read/Write RecordBuffers

To create an empty record of *main_table*, the user needs to create an instance of [RecordBuffersBuilder](../RecordBuffers/src/test/java/datamine/storage/recordbuffers/example/wrapper/builder/RecordBuffersBuilder.java). 

```Java
MainTableInterface aup = new RecordBuffersBuilder().build(MainTableInterface.class);
aup.setLongRequiredColumn(10000000);
aup.setStringColumn("ios4");
...
```

DataMine supports reading records from the Hadoop File System. A writable object of [*RecordBuffer*](../RecordBuffers/src/main/java/datamine/storage/recordbuffers/RecordBuffer.java) can be used for serialization and de-serialization in the HDFS.  

To write a record of *main_table* in the HDFS,

```Java
SequenceFile.Writer writer = SequenceFile.createWriter(
				FileSystem.get(conf), conf,  // Hadoop configuration
				new Path("/file/to/write"),  // Output file path in the HDFS
				LongWritable.class,          // User ID as the key
				RecordBuffer.class,          // Record as the value
				SequenceFile.CompressionType.BLOCK);
LongWritable id = new LongWritable(aup.setLongRequiredColumn());
RecordBuffer rb = ((MainTableMetadata>) aup.getBaseObject()).getRecordBuffer();
writer.append(id, rb);
```

To read a record from the HDFS,

```Java
SequenceFile.Reader reader = new SequenceFile.Reader(
				dfs,                           // Hadoop file system handler 
				new Path("/file/to/read"),     // Input file path in the HDFS
				conf);                         // Hadoop configuration
LongWritable userId = new LongWritable();
RecordBuffer rbuf = new RecordBuffer();
curAUPReader.next(userId, rbuf);
MainTableInterface aup = new MainTableInterface(
				new ReadOnlyRecord<MainTableMetadata>(
						MainTableMetadata.class, rBuf));
```


## Schema Evolution

It is possible to change the table schema according to the [rules](../README.md#schema-evolution-data-backward-compatibility). After changing the schema, the code generation above must be re-run. The implementation for derived fields may need to update if necessary. 

## More than RecordBuffers: Columnar Storage

