package com.aplos.cms.backingpage;

import java.util.ArrayList;
import java.util.Arrays;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.aplos.cms.beans.customforms.Form;
import com.aplos.cms.beans.customforms.FormElement;
import com.aplos.cms.beans.customforms.FormElement.ElementType;
import com.aplos.cms.beans.customforms.MultipleChoiceElement;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.enums.CommonBundleKey;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@SessionScoped
@AssociatedBean(beanClass=Form.class)
public class FormEditPage extends EditPage {
	private static final long serialVersionUID = 4368930405967512152L;
	private HtmlSelectOneMenu elementTypeComponent;
	private FormElement currentFormElement;

	private String newEmail;

	private String newListOrder;

	/*
	 * Multiple Choice Components
	 */

	public void addOption() {
		if( !( currentFormElement instanceof MultipleChoiceElement ) ) {
			return;
		}
		( (MultipleChoiceElement)currentFormElement ).getOptions().add( "New Option" );
	}

	public void removeOption( int index ) {
		if( !( currentFormElement instanceof MultipleChoiceElement ) ) {
			return;
		}
		( (MultipleChoiceElement)currentFormElement ).getOptions().remove( index );
	}

	public void addEmail() {
		( (Form)resolveAssociatedBean() ).getEmails().add( newEmail );
		newEmail = "";
	}

	public void removeEmail( String email ) {
		( (Form)resolveAssociatedBean() ).getEmails().remove( email );
	}

	public void changeElementType( ValueChangeEvent event ) {
		if (event.getNewValue() == null) {
			System.err.println("null value...");
			return;
		}

		if( event.getNewValue() != event.getOldValue() ) {
			Form form = (Form)resolveAssociatedBean();

			FormElement element = null;
			try {
				element = ( (ElementType)event.getNewValue() ).getNew();
			} catch( Exception e ) {
				
				e.printStackTrace();
			}

			if( element != null ) {
				int index = form.getElements().indexOf( currentFormElement );
				if( index == -1 ) {
					throw new RuntimeException( "Could not find currentelement" );
				}

				form.getElements().get( index ).setForm( null );
				form.getElements().remove( index );
				currentFormElement.hardDelete();

				element.setForm( form );
				element.setName( currentFormElement.getName() );
				form.getElements().add( index, element );
				currentFormElement = element;
			}
		}
	}

	public void changeElementType( ActionEvent event ) {
		Form form = (Form)resolveAssociatedBean();
		Object value = null;//( (HtmlSelectOneMenu)( (HtmlAjaxSupport)event.getSource() ).getParent() ).getSubmittedValue();
		FormElement e = null;
		try {
			e = ElementType.valueOf( value.toString() ).getNew();
		} catch( Exception e1 ) {
			
			e1.printStackTrace();
		}

		if( e != null ) {
			int index = form.getElements().indexOf( currentFormElement );
			if( index == -1 ) {
				throw new RuntimeException( "Could not find currentelement" );
			}

			deleteCurrentElement();

			e.setForm( form );
			e.setName( currentFormElement.getName() );
			form.getElements().add( index, e );
			form.saveDetails();
			currentFormElement = form.getElements().get( index );
		}
	}

	public void deleteCurrentElement() {
		currentFormElement.getForm().getElements().remove( currentFormElement );
		currentFormElement.setForm( null );
		currentFormElement.hardDelete();
	}

	public void addComponent() {
		Form form = (Form)resolveAssociatedBean();
		form.addElement();
	}

	@Override
	public void okBtnAction() {
//		try {
			AplosBean associatedBean = resolveAssociatedBean();

			associatedBean.saveDetails();

			/* Force flush so we can catch unique constraint error */
//			HibernateUtil.getCurrentSession().flush();
			JSFUtil.addMessage(ApplicationUtil.getAplosContextListener().translateByKey( CommonBundleKey.SAVED_SUCCESSFULLY ));
			//TODO Make this pick up correctly
			JSFUtil.addMessage( "Each form item must have a different name", FacesMessage.SEVERITY_ERROR );
			super.cancelBtnAction();

//		} catch (ConstraintViolationException e) {
//			/* TODO Extract JDBC exception, match field name with Regex and
//			 * display useful error message. Then move this into superclass.
//			 */

//		}
	}

	@Override
	public void applyBtnAction() {
//		try {
			AplosBean associatedBean = resolveAssociatedBean();

			boolean redirect = false;
			if (associatedBean.isNew()) { redirect = true; }

			associatedBean.saveDetails();

			/* Force flush so we can catch unique constraint error */
//			HibernateUtil.getCurrentSession().flush();

			JSFUtil.addMessage(ApplicationUtil.getAplosContextListener().translateByKey( CommonBundleKey.SAVED_SUCCESSFULLY ));
			//TODO Make this pick up correctly
			JSFUtil.addMessage( "Each form item must have a different name", FacesMessage.SEVERITY_ERROR );
//		} catch (ConstraintViolationException e) {
//			/* TODO Extract JDBC exception, match field name with Regex and
//			 * display useful error message. Then move this into superclass.
//			 */
//		}

	}

	public void updateList() {
		Form form = (Form)resolveAssociatedBean();
		String orderParameter = newListOrder;

		String[] order = orderParameter.split( "&" );
		FormElement[] elements = new FormElement[ order.length ];

		for( int dest = 0; dest < order.length; dest++ ) {
			int src = Integer.parseInt( order[ dest ].split( "=" )[ 1 ] );
			elements[ dest ] = form.getElements().get( src );
		}

		form.setElements( new ArrayList<FormElement>(Arrays.asList(elements)) );
		form.saveDetails();
	}

	public FormElement getCurrentFormElement() {
		return currentFormElement;
	}

	public void setCurrentFormElement( FormElement currentFormElement ) {
		this.currentFormElement = currentFormElement;
	}

	public HtmlSelectOneMenu getElementTypeComponent() {
		return elementTypeComponent;
	}

	public void setElementTypeComponent( HtmlSelectOneMenu elementTypeComponent ) {
		this.elementTypeComponent = elementTypeComponent;
	}

	public String getNewEmail() {
		return newEmail;
	}

	public void setNewEmail( String newEmail ) {
		this.newEmail = newEmail;
	}

	public void setNewListOrder( String newListOrder ) {
		this.newListOrder = newListOrder;
	}

	public String getNewListOrder() {
		return newListOrder;
	}

}
