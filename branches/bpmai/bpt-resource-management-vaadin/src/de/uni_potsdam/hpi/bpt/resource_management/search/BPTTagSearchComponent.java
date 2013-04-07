package de.uni_potsdam.hpi.bpt.resource_management.search;

import java.util.ArrayList;

import de.uni_potsdam.hpi.bpt.resource_management.vaadin.BPTApplication;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.BPTTagComponent;

@SuppressWarnings("serial")
public class BPTTagSearchComponent extends BPTTagComponent{
	
	public BPTTagSearchComponent(BPTApplication application, String tagColumns, boolean newTagsAllowed) {
		super(application, tagColumns, newTagsAllowed);
		searchInput.setInputPrompt("Tag search");
//		searchInput.setWidth("70%");
	}
	
	@Override
	public void refresh() {
		super.refresh();
		application.refresh();
	}
	
	public ArrayList<String> getSelectedTags() {
		return tagBox.getTagValues();
	}
	
	@Override
	public void addChosenTag(String value){
		tagBox.addTag(value);
	}
	
	@Override
	protected void addTagBox(){
		tagBox = new BPTSearchTagBoxes();
		layout.addComponent(tagBox);
	}

}
