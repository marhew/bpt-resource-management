package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.terminal.FileResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import de.uni_potsdam.hpi.bpt.resource_management.BPTValidator;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseStatus;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTContainerProvider;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTPropertyValueType;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTVaadinResources;

@SuppressWarnings("serial")
public class BPTUploader extends TabSheet {
	
	private String set_id;
	private BPTApplication application;
	private BPTExerciseRepository exerciseRepository = BPTExerciseRepository.getInstance();
	
	public BPTUploader(Item item, final BPTApplication application) {
		super();
		this.application = application;
		
        if (item != null) {
        	set_id = item.getItemProperty("Exercise Set ID").getValue().toString();
        	 List<Map> map = exerciseRepository.getDocumentsBySetId(set_id);
        	 IndexedContainer entries = BPTContainerProvider.generateContainer(map);
     		for(Object id : entries.getItemIds()){
     				Item nextItem = entries.getItem(id);
     				addComponent(new BPTUploadPanel(nextItem, application, this));
     		}
        }
        else{
        	set_id = exerciseRepository.nextAvailableSetId();
        }
        addNewUploadPanel();
	}

	public void addNewUploadPanel() {
		addComponent(new BPTUploadPanel(null, application, this));
	}

	public String getSetId() {
		return set_id;
	}
}
