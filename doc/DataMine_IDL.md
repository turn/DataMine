# DataMine IDL (Interface Description Language)

DataMine schema defines an interface description language (IDL) to describe the data structure in the DataMine storage. 


A schema is usually stored in the individual file. For instance, the file, [SimpleSchema.json](../RecordBuffers/src/test/resources/SimpleSchema.json) depicts the schema of *simple_schema*.


## Elements

Basically a DataMine schema is composed of tables. Furthermore a table has a list of fields, where a field is described as a set of attributes. Below we will iterate these elements of a DataMine schema.  

### schema

*Schema* is an element in the DataMine schema to depict the structure of a set of tables. It should have at least two attributes:

- *name* is the name of the schema, which should be a type of STRING [^1].
- *table\_list* describes a list of *table*. 


### table

A *table* is a concept to describe the data structure. It has at least two attributes:

- *table* is the name of the table. 
- *fields* has a list of *fields* in the table. 

Optionally a table can have an attribute as *version no.*, which is introduced for the table schema evolution.

### field

The element, *field* describes the attributes of a column in the table. Particularly it has the following attributes:

- *id* is for the ID of the field. 
- *name* is the name of the field.
- *type* defines the type information.
- *default value* specifies the default value of the field if it is not a required field. 
- *constraints* depicts the constraints enforced upon the field. It could be one or multiple of the following:
	- isRequired: it is true if the field is required.
	- isAscSortKey or isDesSortKey: it is true if the field is used for sorting records. Note that a table has at most one field to be sort key and it must define its sort key while initialization. A table cannot get rid of its sort key once defined. 
	- isFrequentlyUsed: it is introduced to improve the reading performance of the field.
	- isDerived: it is true when the field has its value calculated on the fly according to the application; now a derived field must be optional and primitive and all derived fields have the same *id* (e.g., 0).

### type

DataMine schema supports primitive type, group type and collection type.  

- The primitive type is a set, including:
	- BYTE: a byte type, whose value is contained in a byte
	- BOOL: a boolean type, whose values include TRUE and FALSE, contained in a byte
	- INT16: an integer type, whose value is represented by two bytes
	- INT32: an integer type, whose value is represented by four bytes
	- INT64: an integer type, whose value is represented by eight bytes
	- FLOAT: a float numeric type, whose value is represented by four bytes
	- DOUBLE: a float numeric type, whose value is represented by eight bytes
	- STRING: a string type, which is UTF8???
	- BINARY: an array of bytes
	
- The group type describes the structure of a record, and it has attributes:
	- *name*: the name of the structure it describes
	- *type*: the group type, e.g., "STRUCT"
	
- The collection type is a type for a collection of values
	- *type*: the collection type, e.g., a nested table can be a type of "LIST" 
	- *element type*: the type of value in the collection


## Support for Schema Evolution

It is quite common to change the table schema, e.g. by introducing new columns. This requires that the data access support backward compatibility, in other words, the change must allow reading data in the old format. Therefore the schema evolution should follow some restrictions. 

* No new *REQUIRED* field can be added to any existing table; adding *OPTIONAL* field is OK.
* An *OPTIONAL* field cannot be changed to *REQUIRED*; it is OK to change a *REQUIRED* field to be *OPTIONAL*.
* The type of existing field cannot be changed.
* No existing field can be removed.

## An Implementation in JSON 

JSON is a language .... (Check the reference). Considering its flexibility and representative, JSON is a good candidate to describe the DataMine schema. 

Specifically a DataMine schema is represented as a set of key-value pairs. At the top level, there are two attributes: 

- *schema* specifies the name of the schema. 
- *table_list* is an array of tables in the schema. 

A set of tables are defined in each *table_list*. Furthermore, a table has two attributes:

- *table* specifies the name of the table.
- *fields* is an array of fields or attributes in the table.

To explain every field, a set of attributes are defined, including

- *id* is an integer, which increases continuously. 
- *name* is a string [^1] for the field name. 
- *type* specifies the field type, which can be 
	- primitive type, such as Byte (BYTE), Boolean (BOOL), Integer (INT32), Float (FLOAT), Double (DOUBLE), Long (INT64), Short (INT16), String (STRING) etc
	- group type, e.g., provider_user_id (STRUCT)
	- collection type, e.g., List:Short, List:provider_user_id (LIST)
- *default* indicates the default value for an optional field
- *isRequired* identifies the necessity of a field
- *isAscSortKey* indicates a sort key of the table (in ascending order)
- *isDesSortKey* indicates a sort key of the table (in descending order)
- *isFrequentlyUsed* implicates an data access optimization on the field 
- *isDerived* indicates a field not stored physically (i.e., its value is calculated on the fly)

### Example

An example below illustrates the schema of "attribution" in the JSON format. 

	{
	  "schema": "attribution",
	  "table_list": [
	    {
	      "table": "attribution_result_rule",
	      "fields": [
	        {"id": 1,"name": "run_num",    "type": "Byte",   "isRequired": true, "isAscSortKey": true},
	        {"id": 2,"name": "category_id","type": "String", "default": "\"Unknown\""},
	        {"id": 3,"name": "keyword",    "type": "String", "default": "\"Unknown\""},
	        {"id": 4,"name": "key",        "type": "String", "default": "\"Unknown\""},
	        {"id": 5,"name": "value",      "type": "String", "default": "\"Unknown\""},
	        {"id": 0,"name": "note",      "type": "String", "default": "\"Unknown\"", "isDerived": true}
	      ]
	    },
	    {
	      "table": "attribution_result",
	      "fields": [
	        {"id": 1,"name": "contract_id","type": "Integer","isRequired": true},
	        {"id": 2,"name": "data_cost",  "type": "Float",  "isRequired": true},
	        {"id": 3,"name": "rules",      "type": "List:attribution_result_rule", "isRequired": true}
	      ]
	    }
	   ]
	}


[^1]: A recommended naming convention can be lower case with infix underscores, following the pattern:  '^\[a\-zA\-Z\]\[a\-zA\-Z0\-9\_\]\*$'.
