/**
 * Copyright (C) 2015 Turn Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    	http://www.apache.org/licenses/LICENSE-2.0
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

/**
 * Definition of one field in Datamine
 * 
 * <p>
 * TODO constraints, like must have default value if optional
 * </p>
 * 
 * @author tliu
 * @author yqi
 */
public class JsonField implements JsonElement {
	
	public static final String INIT_DEFAULT = "not set";
	
	@Expose @SerializedName("id")
	private int id;
	@Expose @SerializedName("name")
	private String name;
	@Expose @SerializedName("type")
	private String type;
	@Expose @SerializedName("default")
	private String defaultValueString = INIT_DEFAULT;
	@Expose @SerializedName("isRequired")
	private boolean isRequired = false;
	@Expose @SerializedName("isSortKey")
	private boolean isSortKey = false;
	@Expose @SerializedName("isAsc")
	private boolean isAscSorted = false; // effective only if isSorted=true
	@Expose @SerializedName("hasRef")
	private boolean hasRef = false; // effective only if isSorted=true
	
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getDefaultValueString() {
		return defaultValueString;
	}

	public boolean isRequired() {
		return isRequired;
	}

	public boolean isSorted() {
		return isSortKey;
	}

	public boolean isAscSorted() {
		return isAscSorted;
	}
	
	public boolean hasRef() {
		return hasRef;
	}

	public String toString() {
		return new GsonBuilder().setPrettyPrinting().create().toJson(this);
	}
	
	@Override
	public void accept(JsonElementVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((defaultValueString == null) ? 0 : defaultValueString
						.hashCode());
		result = prime * result + id;
		result = prime * result + (isAscSorted ? 1231 : 1237);
		result = prime * result + (isRequired ? 1231 : 1237);
		result = prime * result + (isSortKey ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		JsonField other = (JsonField) obj;
		if (defaultValueString == null) {
			if (other.defaultValueString != null)
				return false;
		} else if (!defaultValueString.equals(other.defaultValueString))
			return false;
		if (id != other.id)
			return false;
		if (isAscSorted != other.isAscSorted)
			return false;
		if (isRequired != other.isRequired)
			return false;
		if (isSortKey != other.isSortKey)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
}
