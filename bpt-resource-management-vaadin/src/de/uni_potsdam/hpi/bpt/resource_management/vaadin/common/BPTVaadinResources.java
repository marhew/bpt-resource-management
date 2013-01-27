package de.uni_potsdam.hpi.bpt.resource_management.vaadin.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ektorp.DocumentNotFoundException;

import com.vaadin.Application;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.FileResource;
import com.vaadin.terminal.StreamResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDocumentRepository;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.BPTApplication;

/**
 * Contains resources required by Vaadin to display various components.
 * 
 * @author tw
 *
 */
public class BPTVaadinResources {
	
	private static List<Object[]> bptTools = new ArrayList<Object[]>() {
	    { 
	    	add(new Object[] {"_id", "ID", Integer.class, BPTPropertyValueType.IGNORE, null, false});
	    	add(new Object[] {"name", "Name", String.class, BPTPropertyValueType.IGNORE, null, true});
	    	add(new Object[] {"description", "Description", Component.class, BPTPropertyValueType.RICH_TEXT, null, true});
	    	add(new Object[] {"provider", "Provider", String.class, BPTPropertyValueType.IGNORE, null, true});
	    	add(new Object[] {"download_url", "Download", Component.class, BPTPropertyValueType.LINK, null, true});
	    	add(new Object[] {"documentation_url", "Documentation", Component.class, BPTPropertyValueType.LINK, null, true});
	    	add(new Object[] {"screencast_url", "Screencast", Component.class, BPTPropertyValueType.LINK, null, true});
	    	add(new Object[] {"availabilities", "Availability", String.class, BPTPropertyValueType.LIST, null, true});
	    	add(new Object[] {"model_types", "Model type", String.class, BPTPropertyValueType.LIST, null, true});
	    	add(new Object[] {"platforms", "Platform", String.class, BPTPropertyValueType.LIST, null, true});
	    	add(new Object[] {"supported_functionalities", "Supported functionality", String.class, BPTPropertyValueType.LIST, null, true});
	    	add(new Object[] {"contact_name", "Contact name", String.class, BPTPropertyValueType.IGNORE, null, true});
	    	add(new Object[] {"contact_mail", "Contact mail", Component.class, BPTPropertyValueType.EMAIL, null, true});
	    	add(new Object[] {"date_created", "Date created", Date.class, BPTPropertyValueType.DATE, null, true});
	    	add(new Object[] {"last_update", "Last update", Date.class, BPTPropertyValueType.DATE, null, true});
	    	// TODO: display image --- 
	    	add(new Object[] {"_attachments", "Logo", Embedded.class, BPTPropertyValueType.IMAGE, "logo", false});
	    }
	};
	
	/**
	 * Returns resources required by Vaadin to display various components.
	 * 
	 * @param documentType the document type
	 * @return list of array containing the resource elements
	 * 
	 * array element #0: attribute name under which the value is stored in CouchDB
	 * array element #1: attribute name displayed in Vaadin
	 * array element #2: property data type for Vaadin table
	 * array element #3: BPTPropertyValueType enum type to identify how to generate the specific Vaadin components that are shown
	 * array element #4: attachment file name
	 * array element #5: true if attribute is modifiable by user (attachments to be handled separately)
	 * 
	 */
	public static List<Object[]> getEntries(String documentType) {
		if (documentType.equals("BPTTool")) {
			return bptTools;
		}
		return new ArrayList<Object[]>();
	}
	
	/**
	 * @param documentType the document type
	 * @return attribute names under which the values are stored in the database
	 * 
	 */
	public static ArrayList<String> getDocumentKeys(String documentType, boolean modifiableOnly) {
		ArrayList<String> values = new ArrayList<String>();
		if (documentType.equals("BPTTool")) {
			for (Object[] entry : bptTools) {
				if (!modifiableOnly || (Boolean)entry[5]) {
					values.add((String)entry[0]);
				}
			}	
		}
		return values;
	}
	
	/**
	 * @param documentType the document type
	 * @return attribute names displayed in Vaadin
	 * 
	 */
	public static ArrayList<String> getColumnNames(String documentType) {
		ArrayList<String> values = new ArrayList<String>();
		if (documentType.equals("BPTTool")) {
			for (Object[] entry : bptTools) {
				values.add((String)entry[1]);
			}
		}
		return values;
	}
	
	/**
	 * @param documentType the document type
	 * @return property data type for Vaadin table
	 * 
	 */
	public static ArrayList<Class<?>> getPropertyDataTypes(String documentType) {
		ArrayList<Class<?>> values = new ArrayList<Class<?>>();
		if (documentType.equals("BPTTool")) {
			for (Object[] entry : bptTools) {
				values.add((Class<?>)entry[2]);
			}
		}
		return values;
	}
	
	/**
	 * @param documentType the document type
	 * @return BPTPropertyValueType enum type to identify how to generate the specific Vaadin components that are shown
	 * 
	 */
	public static ArrayList<BPTPropertyValueType> getPropertyValueTypes(String documentType) {
		ArrayList<BPTPropertyValueType> values = new ArrayList<BPTPropertyValueType>();
		if (documentType.equals("BPTTool")) {
			for (Object[] entry : bptTools) {
				values.add((BPTPropertyValueType)entry[3]);
			}
		}
		return values;
	}
	
	/**
	 * @param documentType the document type
	 * @return columns (including lists) whose entries are relevant for searching by tags
	 * 
	 */
	public static String[] getRelevantColumnsForTags(String documentType) {
		String[] values = new String[0];
		if (documentType.equals("BPTTool")) {
			values = new String[] {"Availability", "Model type", "Platform", "Supported functionality"};
		}
		return values;
	}
	
	/**
	 * @param tool the database document as java.util.Map
	 * @param documentColumnName the name of the attribute
	 * @param valueType required to what type of Vaadin component will be generated - see return methods below
	 * @return returns the specific Vaadin component or a String if the value type is IGNORE
	 * 
	 */
	public static Object generateComponent(BPTDocumentRepository toolRepository, Map<String, Object> tool, String documentColumnName, BPTPropertyValueType valueType, String attachmentName) {
		Object value;
		if (documentColumnName.equals("_attachments")) {
			try {
				value = toolRepository.readAttachment((String)tool.get("_id"), attachmentName);
			} catch (DocumentNotFoundException e) {
				value = new Object();
			}
		} else {
			value = tool.get(documentColumnName);
		}
		System.out.println(documentColumnName + ": " + value + " trololol" + value.getClass());
		switch (valueType) {
			case LINK : return asLink((String)value);
			case EMAIL : return asEmailLink((String)value);
			case LIST : return asFormattedString((ArrayList<String>)value);
			case DATE : return asDate((String)value);
			case RICH_TEXT : return asRichText((String)value);
			case IMAGE : return asImage(value, tool, attachmentName);
			default : return value;
		}
	}

	private static String asFormattedString(ArrayList<String> stringList) {
		return stringList.toString().replace("[", "").replace("]", "");
	}
	
	private static Link asLink(String linkString) {
		return new Link(linkString, new ExternalResource(linkString));
	}
	
	private static Link asEmailLink(String emailLinkString) {
		return new Link(emailLinkString, new ExternalResource("mailto:" + emailLinkString));
	}
	
	private static Date asDate(String dateString) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static Label asRichText(String richTextString) {
		Label richText = new Label(richTextString);
	    richText.setContentMode(Label.CONTENT_XHTML);
	    return richText;
	}
	
	private static Embedded asImage(Object input, Map<String, Object> tool, String attachmentName) {
		String filename;
		InputStream inputStream;
		
		try {
			inputStream = (InputStream)input; 
		} catch (ClassCastException e) {
			return new Embedded();
		}
		
		// TODO: images only visible in eclipse browser, not in Firefox etc. - cause might be below
		if (System.getProperty("os.name").contains("Windows")) {
			filename = "C:\\temp\\" + (String)tool.get("_id") + "_logo.tmp";
		} else {
			filename = "/tmp/" + (String)tool.get("_id") + "_logo.tmp";
		}
		File logo = new File(filename);
		
		try {
			OutputStream out = new FileOutputStream(logo);
			byte buffer[] = new byte[1024];
			int length;
			while((length = inputStream.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
			out.close();
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ExternalResource imageResource = new ExternalResource(filename);
		// TODO: line below too long
		imageResource.setMIMEType((String)((Map<String, Object>)((Map<String, Object>)tool.get("_attachments")).get(attachmentName)).get("content_type"));
		Embedded image = new Embedded("", imageResource);
		image.setType(Embedded.TYPE_IMAGE);
		// TODO: image size in component that shows all entries ... putting the code here would resize it everywhere
		// image.setWidth("50px");
		// image.setHeight("50px");
	    return image;
	}
	
}
