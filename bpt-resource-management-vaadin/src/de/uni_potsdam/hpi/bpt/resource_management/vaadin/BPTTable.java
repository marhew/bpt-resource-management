package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDocumentStatus;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTContainerProvider;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTPropertyValueType;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTVaadinResources;

public class BPTTable extends Table {

	private IndexedContainer dataSource, visibleRows;
	private VerticalLayout layout;
	
	public BPTTable(){
		
		super();
		ArrayList<BPTDocumentStatus> statusList = new ArrayList<BPTDocumentStatus>();
		statusList.add(BPTDocumentStatus.Published);
		dataSource = BPTContainerProvider.getVisibleEntries(statusList, new ArrayList<String>());
        visibleRows = BPTContainerProvider.createContainerWithProperties(); 
		setImmediate(true);
		setSelectable(true);
		setColumnReorderingAllowed(true);
        setColumnCollapsingAllowed(true);
        setContainerDataSource(dataSource);
        setWidth("100%");
        addListenerToTable();
	}
	
	public void filterBy(ArrayList<String> tagValues) {
		visibleRows.removeAllItems();
		for (Object rowId : dataSource.getItemIds()){
			Item row = dataSource.getItem(rowId);
			if (rowShouldBeVisible(row, tagValues)){
				Item item = visibleRows.addItem(rowId);
				for (Object columnName : BPTVaadinResources.getColumnNames("BPTTool")) {
					item.getItemProperty(columnName).setValue(row.getItemProperty(columnName).getValue());
				}
			}
		}
		setContainerDataSource(visibleRows);
	}
	
	private boolean rowShouldBeVisible(Item item, ArrayList<String> tagValues) {
		
		ArrayList<String> itemAsArrayList = new ArrayList<String>();
		String[] relevantColumns = BPTVaadinResources.getRelevantColumnsForTags("BPTTool");
		for (Object propertyId : relevantColumns) {
			String property = item.getItemProperty(propertyId).getValue().toString();
			List<String> tags = Arrays.asList(property.split("\\s*,\\s*"));
			itemAsArrayList.addAll(tags);
		}
		for (int i = 0; i < tagValues.size(); i++){
			if (!itemAsArrayList.contains(tagValues.get(i))) return false;
			
		}
		return true;
	}
	
	private void addListenerToTable() {
		this.addListener(new Table.ValueChangeListener() {
			public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
				if ((getItem(getValue()) != null)){
					showSelectedEntry(getItem(getValue()));
				}		
			}

			private void showSelectedEntry(Item item) {
				final Window popupWindow = new Window(item.getItemProperty("Name").getValue().toString());
				popupWindow.setWidth("600px");
				
				final String _id = item.getItemProperty("ID").getValue().toString();
				Map<String, Object> tool = ((BPTApplication)getApplication()).getToolRepository().readDocument(_id);
				
				for (Object[] entry : BPTVaadinResources.getEntries("BPTTool")){
					popupWindow.addComponent(new Label(entry[1] + ":"));
					Object value = BPTVaadinResources.generateComponent(((BPTApplication)getApplication()).getToolRepository(), tool, (String)entry[0], (BPTPropertyValueType)entry[3], (String)entry[4]);
					if (entry[2] == Component.class) {
						popupWindow.addComponent((Component)value);
					} else if (entry[2] == Embedded.class) {
						popupWindow.addComponent((Embedded)value);
					} else {
						popupWindow.addComponent(new Label(value.toString()));
					}
				}
				
				if (((BPTApplication)getApplication()).isLoggedIn()){
					
					HorizontalLayout layout = new HorizontalLayout();
					popupWindow.addComponent(layout);
					
					Button deleteButton = new Button("delete");
					deleteButton.addListener(new Button.ClickListener(){
						public void buttonClick(ClickEvent event) {
							((BPTApplication)getApplication()).getToolRepository().deleteDocument(_id);
							getWindow().removeWindow(popupWindow);
						}
					});
					layout.addComponent(deleteButton);
					
					BPTDocumentStatus actualState = ((BPTApplication)getApplication()).getToolRepository().getDocumentStatus(_id);
					
					if (actualState == BPTDocumentStatus.Unpublished){
						
						Button publishButton = new Button("publish");
						publishButton.addListener(new Button.ClickListener(){
							public void buttonClick(ClickEvent event) {
								((BPTApplication)getApplication()).getToolRepository().publishDocument(_id);
								getWindow().removeWindow(popupWindow);
							}
						});
						layout.addComponent(publishButton);
						
						Button rejectButton = new Button("reject");
						rejectButton.addListener(new Button.ClickListener(){
							public void buttonClick(ClickEvent event) {
								((BPTApplication)getApplication()).getToolRepository().rejectDocument(_id);
								getWindow().removeWindow(popupWindow);
							}
						});
						layout.addComponent(rejectButton);						
						
					}
					else if (actualState == BPTDocumentStatus.Published) {
						Button unpublishButton = new Button("unpublish");
						unpublishButton.addListener(new Button.ClickListener(){
							public void buttonClick(ClickEvent event) {
								((BPTApplication)getApplication()).getToolRepository().unpublishDocument(_id);
								getWindow().removeWindow(popupWindow);
							}
						});
						layout.addComponent(unpublishButton);	
					}
					else {
						Button proposeButton = new Button("propose");
						proposeButton.addListener(new Button.ClickListener(){
							public void buttonClick(ClickEvent event) {
								((BPTApplication)getApplication()).getToolRepository().unpublishDocument(_id);
								getWindow().removeWindow(popupWindow);
							}
						});
						layout.addComponent(proposeButton);	
					}
					
				}
				getWindow().addWindow(popupWindow);
				
				
			}
			});
	}
	public void setContent(IndexedContainer newDataSource){
		dataSource = newDataSource;
		setContainerDataSource(dataSource);
	}	
	
	public void refreshContent(ArrayList<BPTDocumentStatus> statusList){
		dataSource = BPTContainerProvider.getVisibleEntries(statusList, new ArrayList<String>());
		setContainerDataSource(dataSource);
	}
	
	private void show(){
		 setContainerDataSource(dataSource);
	}
}
