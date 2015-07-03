package com.aplos.cms.beans.customforms;

import java.util.List;

import javax.faces.model.SelectItem;

import com.aplos.common.LabeledEnumInter;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.utils.CommonUtil;

@Entity
public class InputTextElement extends FormElement {
	private static final long serialVersionUID = 556947818661760280L;

	public enum FieldType implements LabeledEnumInter { NORMAL("Normal"), EMAIL("E-Mail"), PASSWORD("Password");
		private final String label;
		FieldType(String label) { this.label = label; }
		@Override
		public String getLabel() { return label; }
		public static List<SelectItem> getItems() {
			return CommonUtil.getEnumSelectItems(FieldType.class, null);
		}
	};

	public List<SelectItem> getFieldTypes() {
		return FieldType.getItems();
	}

	private int maxlength = 0;
	private FieldType type;

	@Override
	public String getHtml() {
		return "<input type='" + (type==FieldType.PASSWORD ? "password" : "text") + "'"
			+ " class='" + (isRequired() ? "required " : "")
			+ (type==FieldType.EMAIL || type==FieldType.PASSWORD ? type.toString().toLowerCase() : "" ) + "'"
			+ " name='" + getName() + "' />";
	}

	public void setMaxlength( int maxlength ) {
		this.maxlength = maxlength;
	}

	public int getMaxlength() {
		return maxlength;
	}

	public void setType( FieldType type ) {
		this.type = type;
	}

	public FieldType getType() {
		return type;
	}

}
