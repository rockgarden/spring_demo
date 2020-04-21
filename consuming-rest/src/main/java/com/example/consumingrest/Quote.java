package com.example.consumingrest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The domain class to contain the data that you need.
 * 
 * Spring uses the Jackson JSON library to convert response into a Quote object. 
 * 
 * JsonIgnoreProperties from the Jackson JSON processing library to indicate
 * that any properties not bound in this type should be ignored.
 * 
 * The @JsonIgnoreProperties annotation tells Spring to ignore any attributes not listed in the class. 
 * This makes it easy to make REST calls and produce domain objects.
 *
 * To directly bind your data to your custom types, you need to specify the
 * variable name to be exactly the same as the key in the JSON document returned
 * from the API.
 * 
 * In case your variable name and key in JSON doc do not match, you can
 * use @JsonProperty annotation to specify the exact key of the JSON document.
 * (This example matches each variable name to a JSON key, so you do not need
 * that annotation here.)
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true) // 忽略此类型中未绑定的任何属性
public class Quote {

	private String type;
	private Value value;

	public Quote() {
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Value getValue() {
		return value;
	}

	public void setValue(Value value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Quote{" + "type='" + type + '\'' + ", value=" + value + '}';
	}
}