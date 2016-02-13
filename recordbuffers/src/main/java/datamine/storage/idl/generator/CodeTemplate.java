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
package datamine.storage.idl.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A template is defined for code generation.
 * 
 * @author yqi
 * @author tliu
 */
public class CodeTemplate {
	
	private List<String> codes = new ArrayList<String>();
	private Map<String, String> fieldMapping = new HashMap<String, String>();
	private Map<String, List<CodeTemplate>> TempleteMapping = new HashMap<String, List<CodeTemplate>>();
	
	public CodeTemplate(){
		
	}
	
	public CodeTemplate(List<String> codes){
		this.codes.addAll(codes);
	}
	
	public CodeTemplate(String[] codes){
		for(String str : codes){
			this.codes.add(str);
		}
	}
	
	public CodeTemplate addLine(String str){
		codes.add(str);
		return this;
	}
	
	public void setFieldMapping(Map<String, String> fieldMapping) {
		for(Entry<String, String> entry : fieldMapping.entrySet()){
			this.fieldMapping.put(entry.getKey(), entry.getValue());
		}
	}

	public void fillFields(String fieldname, String field){
		fieldMapping.put(fieldname, field);
	}
	
	public void fillFields(String fieldname, CodeTemplate template){
		
		if (TempleteMapping.containsKey(fieldname)) {
			TempleteMapping.get(fieldname).add(template);
		} else{
			List<CodeTemplate> list = new ArrayList<CodeTemplate>();
			list.add(template);
			TempleteMapping.put(fieldname, list);
		}
	}
	
	public String getCode(){
		StringBuilder sb = new StringBuilder();
		for(Entry<String, String> entry: fieldMapping.entrySet()){
			String rep = "{" + entry.getKey() + "}";
			for(int i = 0; i < codes.size(); i++){
				codes.set(i, codes.get(i).replace(rep, entry.getValue()));
			}
		}
		
		for(Entry<String, List<CodeTemplate>> entry: TempleteMapping.entrySet()){
			String rep = "{" + entry.getKey() + "}";
			for(int i = 0; i < codes.size(); i++){
				StringBuilder codeBuilder = new StringBuilder();
				for (CodeTemplate template: entry.getValue()){
					codeBuilder.append(template.getCode());
				}
				codes.set(i, codes.get(i).replace(rep, codeBuilder.toString()));
			}
		}
		
		for(String str : codes){
			str = str.replaceAll("\\{\\w*\\}", "");
			sb.append(str + "\n");
		}
		return sb.toString();
	}
}
