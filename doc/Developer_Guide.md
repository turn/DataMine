# Developer Guide

We will use an example to explain how to create an application using DataMine. In the application, a profile is defined to describe the user information. A user profile has not only basic attributes, such as ID, version, but also the nested tables for events, such as impressions, id mapping events, and temporal information.  DataMine allows to use JSON to define the data structure below. 

Note that the example can be found at [..](../RecordBuffers/src/test/java/datamine/storage/recordbuffers/example)

## Table Schema

The table schema of the profile is defined in [JSON](../RecordBuffers/src/test/resources/SimpleSchema.json)

	```json
	{
	  "schema": "simple_schema",
	  "table_list": [
	    {
	      "table": "attribution_result_rule",
	      "fields": [
	        {"id": 1,"name": "run_num",    "type": "Byte",   "isRequired": true},
	        {"id": 2,"name": "value",      "type": "String", "default": "Unknown"}
	      ]
	    },
	    {
	      "table": "attribution_result",
	      "fields": [
	        {"id": 1,"name": "id","type": "Integer","isRequired": true},
	        {"id": 2,"name": "rules",      "type": "List:attribution_result_rule", "isRequired": true}
	      ]
	    },
	    {
	      "table": "impression",
	      "fields": [
	        {"id": 1, "name": "media_provider_id", "type": "Integer","isRequired": true},
	        {"id": 2,"name": "mp_tpt_category_id","type": "Short",  "default": "-1"},
	        {"id": 3,"name": "truncated_url",     "type": "String", "default": "Unknown"},
	        {"id": 4,"name": "bid",         "type": "Boolean",  "default": "true"},
	        {"id": 5,"name": "bid_type",          "type": "Byte",   "default": "-1"},
	        {"id": 6,"name": "attribution_results","type": "List:attribution_result"},
	        {"id": 7,"name": "allowed_ad_formats","type": "Long",   "default": "-1"},
			{"id": 8,"name": "cost","type": "Double",   "default": "0"}
			]
	    },
	    {
	      "table": "provider_user_id",
	      "fields": [
	        {"id": 1,"name": "provider_type",          "type": "Byte",   "isRequired": true},
	        {"id": 2,"name": "provider_id",            "type": "Integer","isRequired": true}
	      ]
	    },
	    {
	      "table": "id_map",
	      "fields": [
	        {"id": 1,"name": "media_provider_ids","type": "List:provider_user_id"}
	      ]
	    },
	    {
	      "table": "analytical_user_profile",
	      "fields": [
	        {"id": 1,"name": "user_id",                 "type": "Long",   "isRequired": true, "isFrequentlyUsed": true},
	        {"id": 2,"name": "version",         "type": "Byte", "isRequired": true, "isDesSortKey": true},
	        {"id": 3,"name": "resolution",      "type": "Short",  "default": "-1"},
	        {"id": 4,"name": "os_version",      "type": "String", "default": "Unknown"},
	        {"id": 5,"name": "impressions",            "type": "List:impression"},
	        {"id": 6,"name": "id_maps",                "type": "id_map"},
			{"id": 7, "name": "time_list",     "type": "List:Integer"},
			{"id": 0, "name": "day",     "type": "String", "isDerived": true, "default": "Unknown"}
	      ]
	    }
	  ]
	}
	```


## Code Generation

DataMine implements a set of code generator to help the user create Java classes through which the user can write its program to read, write and process the profile data. To trigger the code generation when building the application, POM needs some [changes](../RecordBuffers#code_generation). 

To simplify the example, a class [GenerateTestData.java](../RecordBuffers/src/test/java/datamine/storage/recordbuffers/example/GenerateTestData.java) is created to generate all codes. 

## Derived Field Support

DataMine allows derived attribute in the table. The value of a derived field is calculated on the fly instead of stored physically. Importantly the user or table owner should define the way how this calculation is done. For example, a class should be defined to implement the interface, [AnalyticalUserProfileDerivedValueInterface.java](../RecordBuffers/src/test/java/datamine/storage/recordbuffers/example/interfaces/AnalyticalUserProfileDerivedValueInterface.java), as a derived attribute "day" is introduced in the table of *analytical_user_profile*.  The implementation can be as follows:

	```java
	public class AnalyticalUserProfileDerived implements
			AnalyticalUserProfileDerivedValueInterface {
	
		private final AnalyticalUserProfileInterface aup;
		
		public AnalyticalUserProfileDerived(AnalyticalUserProfileInterface aup) {
			this.aup = aup;
		}
		
		@Override
		public String getDay() {
			return "Monday";
		}
	
	}
	```

A setter function is defined to connect the implementation with the table access class. For instance, 

	```java
	aup.setDerivedValueImplementation(new AnalyticalUserProfileDerived(aup));
	String theDay = aup.getDay();
	```


## Read/Write RecordBuffers

To create an empty record of *analytical_user_profile*, the user needs to create an instance of [RecordBuffersBuilder](../RecordBuffers/src/test/java/datamine/storage/recordbuffers/example/wrapper/builder/RecordBuffersBuilder.java). 

	```java
	AnalyticalUserProfileInterface aup = new RecordBuffersBuilder().build(AnalyticalUserProfileInterface.class);
	aup.setUserId(10000000);
	aup.setOsVersion("ios4");
	...
	```

DataMine supports reading records from the Hadoop File System. A writable object of [*RecordBuffer*](../RecordBuffers/src/main/java/datamine/storage/recordbuffers/RecordBuffer.java) can be used for serialization and de-serialization in the HDFS.  

To write a record of *analytical_user_profile* in the HDFS,

	```java
	SequenceFile.Writer writer = SequenceFile.createWriter(
					FileSystem.get(conf), conf,  // Hadoop configuration
					new Path("/file/to/write"),  // Output file path in the HDFS
					LongWritable.class,          // User ID as the key
					RecordBuffer.class,          // Record as the value
					SequenceFile.CompressionType.BLOCK);
	LongWritable id = new LongWritable(aup.getUserId());
	RecordBuffer rb = ((Record<AnalyticalUserProfileMetadata>) aup.getBaseObject()).getRecordBuffer();
	writer.append(id, rb);
	```

To read a record from the HDFS,

	```java
	SequenceFile.Reader reader = new SequenceFile.Reader(
					dfs,                           // Hadoop file system handler 
					new Path("/file/to/read"),     // Input file path in the HDFS
					conf);                         // Hadoop configuration
	LongWritable userId = new LongWritable();
	RecordBuffer rbuf = new RecordBuffer();
	curAUPReader.next(userId, rbuf);
	AnalyticalUserProfileInterface aup = new AnalyticalUserProfileRecord(
					new Record<AnalyticalUserProfileMetadata>(
							AnalyticalUserProfileMetadata.class, rBuf));
	```


## Schema Evolution

It is possible to change the table schema according to the [rules](../README.md#schema-evolution-data-backward-compatibility). After changing the schema, the code generation above must be re-run. The implementation for derived fields may need to update if necessary. 

## More than RecordBuffers: Columnar Storage

