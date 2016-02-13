# Record Buffers

Record buffers are a flexible, efficient, automated mechanism for serializing structure data, especially for nested structure data. It uses an interface description language (IDL) to describe the schema of data and allows the user easily to read and write the data with special generated source code. Importantly record buffers support schema evolution, so it enables the deployed code for old format to work with the updated data structure.

## Why?

Record buffers is not the only solution while dealing with the big data problem. For example, [Thrift](https://thrift.apache.org/), [Avro](http://avro.apache.org/) and [Protocol Buffers](https://code.google.com/p/protobuf/) provide the very similar ideas. However, record buffers have some advantages considering the special application at Turn. For instance, the nested structure is normal, table should be writable since the updating is a require, reading often occurs to a subset of fields of a table. Record buffers optimize the data accessing performance in these scenarios. 


## Table Schema

Record buffers define the table schema based on an IDL Specification. Please check the details at [DataMine IDL](../doc/DataMine_IDL.md).

## Code Generation
<a name="code_generation"></a>

Record buffers follow a code generation approach while building the representation layer for table access. Particularly a set of Java classes or interfaces are created automatically, describing table schema, table access interface, table access implementation, record builder and unit test respectively. 

<img float="center" src="./doc/res/code_gen.png" width="" height="" border="0" alt="">

To enable such a code generation, the project POM should include the plugin below. 

	<build>
		<plugins>
			<plugin>
				<groupId>com.turn.datamine.mojo.storage</groupId>
				<artifactId>recordbuffers-maven-plugin</artifactId>
				<version>${datamine.version}</version>
				<executions>
					<execution>
						<id>ti</id>
						<goals>
							<goal>table_interfaces</goal>
						</goals>
						<phase>${codeGenerationPhase}</phase>
						<configuration>
							<schemaPath>path/to/schema.json</schemaPath>				
							<packageName>application.package.name.interfaces</packageName>
						</configuration>
					</execution>
					<execution>
						<id>tic</id>
						<goals>
							<goal>table_interface_convertors</goal>
						</goals>
						<phase>${codeGenerationPhase}</phase>
						<configuration>
							<schemaPath>path/to/schema.json</schemaPath>
							<packageName>application.package.name.conversion</packageName>
							<interfacePackageName>application.package.name.interfaces</interfacePackageName>
						</configuration>
					</execution>
					<execution>
						<id>sti</id>
						<goals>
							<goal>table_schema_interfaces</goal>
						</goals>
						<phase>${codeGenerationPhase}</phase>
						<configuration>
							<schemaPath>path/to/schema.json</schemaPath>
							<packageName>application.package.name.metadata</packageName>
						</configuration>
					</execution>
					<execution>
						<id>rb</id>
						<goals>
							<goal>record_buffer_tables</goal>
						</goals>
						<phase>${codeGenerationPhase}</phase>
						<configuration>
							<schemaPath>path/to/schema.json</schemaPath>
							<packageName>application.package.name.recordbuffers.wrapper</packageName>
							<interfacePackageName>application.package.name.interfaces</interfacePackageName>
							<metadataPackageName>application.package.name.metadata</metadataPackageName>
						</configuration>
					</execution>
					<execution>
						<id>rb-test</id>
						<goals>
							<goal>record_buffer_table_tests</goal>
						</goals>
						<phase>${codeGenerationPhase}</phase>
						<configuration>
							<schemaPath>path/to/schema.json</schemaPath>
							<packageName>application.package.name.recordbuffers.data</packageName>
							<interfacePackageName>application.package.name.interfaces</interfacePackageName>
							<metadataPackageName>application.package.name.metadata</metadataPackageName>
						</configuration>
					</execution>
				</executions>
			</plugin>
		</plugins>
	</build>
		

Note that there are parameters that can be configured:

- *schemaPath* : the path where the schema JSON file is stored, for example, *src/main/resources/AUPConf.json*; it may be the same for all goals.
- *packageName* : the package name of the output Java code, for example, *application.package.interfaces*; each goal may have its own unique package name. 
- *interfacePackageName* : the package name of the output Java code for the table access interfaces.
- *metadataPackageName* : the package name of the outoput Java code for the table metadata.

## Encoding

Record buffers use an array of bytes to represent the content of a table record. It has 10 sections showing the figure below. 

<img float="center" src="./doc/res/record_buf.png" width="" height="" border="0" alt="">

They work together to ensure the correct serialization and de-serialization. One big difference from other encoding schemes (like Protocol Buffers), there is a reference section defined to speed up the data access. 

- *Version No.* specifies what version of schema this record uses; it is required and takes 2 bytes.
- *The number of attributes* in the table schema is required and takes 2 bytes.
- *Reference section length* is the number of bytes used for the reference section; it is required and takes 2 bytes. 
- *Sort-key reference* stores the offset of the sort key column if exists; it is optional and takes 4 bytes if exists. 
- *The number of collection-type attributes* uses 1 byte for the number of collections in the table, and it is required.
- *Collection-type field references* stores the offsets of the collections in the table sequentially; note that the offset of an empty collection is -1.
- *The number of non-collection-type field reference* uses 1 byte for the number of non-collection-type columns which have *hasRef* annotation.
- *Non-collection-type field references* sequentially store the ID and offset pair of columns with *hasRef* annotation if exist. 
- *Bit mask of attributes* is a series of bytes to indicate the availability of any attributes in the table. 
- *Attribute values* store the values of available attributes in sequence; note that the sequence should be the same as that defined in the schema. 

### Reference Section

One of the advantages by the record buffers is that it can minimize the data de-serialization. In other words, it eliminates the record deserialization while the access occurs to the attributes having the offset references in the reference section.

Currently, there are three types of attributes having their offsets in the reference section. 

1. The attribute as the table sort-key: it speeds up the record sorting;
2. The attribute having the annotation of "hasRef": the attribute can be specified by the user if it is frequently accessed.
3. The attribute of collection type (i.e., nested table): by default, any nested table has its offset stored in the reference section; according to our experience the nested table is often the hot spot. Having its offset in the reference section, it could significantly speed up some basic operations, like getting the size of the nested table. 
