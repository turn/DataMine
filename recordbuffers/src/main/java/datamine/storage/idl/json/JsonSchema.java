/**
 * Copyright (C) 2016 Turn Inc. (yan.qi@turn.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package datamine.storage.idl.json;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Schema definition
 * 
 * <p>
 * The schema is the biggest concept in the DataMine schema. It is 
 * a collection of tables. 
 * </p>
 * 
 * <p>
 * A schema must have the following attributes:
 * <i> The name 
 * <i> A list of tables
 * </p>
 * 
 * <p>
 * It is stored in a JSON file, and uses GSON as a JSON parser for reading.
 * </p>
 * 
 * TODO (TAO) add a unit test for this class
 * 
 * @author tliu
 * @author yqi
 */
public class JsonSchema implements JsonElement {
	
	static final Logger logger = LoggerFactory.getLogger(JsonSchema.class);
	
	@Expose @SerializedName("schema")
	private String schema;
	
	@Expose @SerializedName("table_list")
	private List<JsonTable> tableList = new ArrayList<JsonTable>();
	
	/**
	 * get table list from schema
	 * @return ArrayList of tables
	 */
	public List<JsonTable> getTableList() {
		return tableList;
	}
	
	public String getName() {
		return this.schema;
	}

	@Override
	public String toString(){
		return new GsonBuilder().setPrettyPrinting().create().toJson(this);
	}
	
	@Override
	public void accept(JsonElementVisitor visitor) {
		visitor.visit(this);
		for (JsonTable table : tableList){
			table.accept(visitor);
		}
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((schema == null) ? 0 : schema.hashCode());
		result = prime * result
				+ ((tableList == null) ? 0 : tableList.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JsonSchema other = (JsonSchema) obj;
		if (schema == null) {
			if (other.schema != null)
				return false;
		} else if (!schema.equals(other.schema))
			return false;
		if (tableList == null) {
			if (other.tableList != null)
				return false;
		} else if (!tableList.equals(other.tableList))
			return false;
		return true;
	}

}