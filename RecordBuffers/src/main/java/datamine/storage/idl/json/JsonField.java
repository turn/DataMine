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
	@Expose @SerializedName("isAscSortKey")
	private boolean isAscSorted = false;
	@Expose @SerializedName("isDesSortKey")
	private boolean isDesSorted = false; 
	@Expose @SerializedName("isFrequentlyUsed")
	private boolean isFrequentlyUsed = false; // effective only if isSorted=true
	@Expose @SerializedName("isDerived")
	private boolean isDerived = false; // effective only if isDerived=true
	
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

	public boolean isDesSorted() {
		return isDesSorted;
	}
	
	public boolean isSorted() {
		return isDesSorted || isAscSorted;
	}

	public boolean isAscSorted() {
		return isAscSorted;
	}
	
	public boolean isFrequentlyUsed() {
		return isFrequentlyUsed;
	}

	public boolean isDerived() {
		return isDerived;
	}
	
	public String toString() {
		return new GsonBuilder().setPrettyPrinting().create().toJson(this);
	}
	
	@Override
	public void accept(JsonElementVisitor visitor) {
		visitor.visit(this);
	}

	
}
