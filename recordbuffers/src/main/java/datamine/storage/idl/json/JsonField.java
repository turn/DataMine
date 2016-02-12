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
	private boolean isFrequentlyUsed = false; 
	@Expose @SerializedName("isDerived")
	private boolean isDerived = false; 
	@Expose @SerializedName("hasLargeList")
	private boolean hasLargeList = false;
	
	/**
	 * @return the ID of the field
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the name of the field
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the type of the field
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the default value in the format of text
	 */
	public String getDefaultValueString() {
		return defaultValueString;
	}

	/**
	 * @return true if it is a required field
	 */
	public boolean isRequired() {
		return isRequired;
	}

	/**
	 * @return true if the values of the field are sorted in the descending order
	 */
	public boolean isDesSorted() {
		return isDesSorted;
	}
	
	/**
	 * @return true if the values of the field are sorted
	 */
	public boolean isSorted() {
		return isDesSorted || isAscSorted;
	}

	/**
	 * @return true if the values of the field are sorted in the ascending order
	 */
	public boolean isAscSorted() {
		return isAscSorted;
	}
	
	/**
	 * Note that it is a hint which can be used to improve the reading performance
	 * 
	 * @return true if the field is frequently used
	 */
	public boolean isFrequentlyUsed() {
		return isFrequentlyUsed;
	}

	/**
	 * Note that a derived field does not have its value stored physically
	 * 
	 * @return true if the field is derived
	 */
	public boolean isDerived() {
		return isDerived;
	}
	
	/**
	 * Note that it is a hint which can be used to improve the reading performance
	 * 
	 * @return true if the field is type of collection and could have a long list as value
	 */
	public boolean hasLargeList() {
		return hasLargeList;
	}
	
	public String toString() {
		return new GsonBuilder().setPrettyPrinting().create().toJson(this);
	}
	
	@Override
	public void accept(JsonElementVisitor visitor) {
		visitor.visit(this);
	}
}
