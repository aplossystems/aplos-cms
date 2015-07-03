package com.aplos.cms.beans.customforms;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.Inheritance;
import com.aplos.common.annotations.persistence.InheritanceType;
import com.aplos.common.annotations.persistence.JoinColumn;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.beans.AplosBean;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class FormElement extends AplosBean {

	private static final long serialVersionUID = 1292894579477241L;

	private String name = "New Field";

	private boolean required = false;

	@ManyToOne
	@JoinColumn(name="form_id", insertable=false, updatable=false)
	private Form form;

	/* ************************************************ Generic Stuff */

	public ElementType getElementType() {
		return ElementType.valueOf( getClass().getSimpleName() );
	}

	public void setElementType(ElementType type) {
		/* This is handled by a ValueChangeListener in backing page */
	}

	public enum ElementType {
		/* Enum names must match class names */
		InputTextElement("Single Line Text", InputTextElement.class),
		InputTextareaElement("Paragraph Text", InputTextareaElement.class),
		DropDownElement("Drop Down Box", DropDownElement.class),
		RadioElement("Multiple Choice", RadioElement.class),
		CheckboxElement("Checkboxes", CheckboxElement.class),
		ListBoxElement("Select from a list", ListBoxElement.class),
		FileUploadElement("Upload a file", FileUploadElement.class);

		@SuppressWarnings("rawtypes")
		ElementType(String label, Class clazz) { this.label = label; this.clazz = clazz; }

		private String label;
		@SuppressWarnings("rawtypes")
		private Class clazz;

		public FormElement getNew() throws InstantiationException, IllegalAccessException
			{ return (FormElement) clazz.newInstance(); }

		public String getLabel() { return label; }
	}

	public List<SelectItem> getElementTypeSelectItems() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (ElementType e : ElementType.values()) {
			items.add( new SelectItem(e, e.getLabel()) );
		}
		return items;
	}

	/* End */

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public Form getForm() {
		return form;
	}

	public void setForm( Form form ) {
		this.form = form;
	}

	public abstract String getHtml();

	public void setRequired( boolean required ) {
		this.required = required;
	}

	public boolean isRequired() {
		return required;
	}
}
