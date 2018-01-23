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
 * Definition of a table of Datamine
 * @author tliu
 * @author yqi
 */
public class Table implements Element {

	private final String name;
	private final short version;
	private final List<Field> fieldList;

	public Table(String name, List<Field> fields, short version) {
		this.name = name;
		this.fieldList = fields;
		this.version = version;
	}

	/**
	 * get table name
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * get all the fields from table
	 * @return
	 */
	public List<Field> getFields() {
		return fieldList;
	}

	public short getVersion() {
		return version;
	}

	@Override
	public String toString() {
		return new GsonBuilder().setPrettyPrinting().create().toJson(this);
	}

	@Override
	public void accept(ElementVisitor visitor) {
		visitor.visit(this);
		for (Field field : fieldList) {
			field.accept(visitor);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fieldList == null) ? 0 : fieldList.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode()); 
		result = prime * result + version;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		Table other = (Table) obj;
		if (fieldList == null) {
			if (other.fieldList != null) {
				return false;
			}

		} else if (!fieldList.equals(other.fieldList)) {
			return false;
		}

		if (name == null) {
			if (other.name != null) {
				return false;
			}

		} else if (!name.equals(other.name)) {
			return false;
		}

		return true;
	}
}
