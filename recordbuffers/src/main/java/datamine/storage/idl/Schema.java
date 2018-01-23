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
package datamine.storage.idl;

import com.google.gson.GsonBuilder;

import java.util.List;

/**
 * Schema definition of DataMine
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
 * @author tliu
 * @author yqi
 */
public class Schema implements Element {
	
	private final String name;
	private final List<Table> tableList;
	
	public Schema(String name, List<Table> tables) {
		this.name = name;
		this.tableList = tables;
	}
	
	public String getName() {
		return this.name;
	}
	
	/**
	 * get table list from schema
	 * @return ArrayList of tables
	 */
	public List<Table> getTableList() {
		return tableList;
	}

	@Override
	public String toString() {
		return new GsonBuilder().setPrettyPrinting().create().toJson(this);
	}
	
	@Override
	public void accept(ElementVisitor visitor) {
		visitor.visit(this);
		for (Table table : tableList) {
			table.accept(visitor);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((tableList == null) ? 0 : tableList.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
			
		if (obj == null) {
			return false;
		}
			
		if (getClass() != obj.getClass()) {
			return false;
		}
			
		Schema other = (Schema) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
				
		} else if (!name.equals(other.name)) {
			return false;
		}
			
		if (tableList == null) {
			if (other.tableList != null) {
				return false;
			}
				
		} else if (!tableList.equals(other.tableList)) {
			return false;
		}
			
		return true;
	}
}
