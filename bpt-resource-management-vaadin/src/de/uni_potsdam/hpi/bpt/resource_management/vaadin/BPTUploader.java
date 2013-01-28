package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.terminal.FileResource;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDocumentTypes;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolRepository;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTVaadinResources;

public class BPTUploader extends CustomComponent implements Upload.SucceededListener, Upload.FailedListener, Upload.Receiver {
	
	private VerticalLayout layout;
	private Upload upload;
	private TextField nameInput, providerInput, downloadInput, documentationInput, screencastInput;
	private TextArea descriptionInput;
	private Button finishUploadButton, removeImageButton;
	private BPTTagComponent availabilitiesTagComponent, modelTagComponent, platformTagComponent, functionalityTagComponent;
	private Panel imagePanel;
	private File logo;
	private FileOutputStream outputStream;
	private final String[] supportedImageTypes = new String[] {"image/jpeg", "image/gif", "image/png"};
	private String documentId, imageName, imageType;
	
	public BPTUploader(){
		layout = new VerticalLayout();
		setCompositionRoot(layout);
		
		layout.addComponent(new Label("Name:"));
		nameInput = new TextField();
		layout.addComponent(nameInput);
		
		layout.addComponent(new Label("Description:"));
		descriptionInput = new TextArea();
		layout.addComponent(descriptionInput);
		
		layout.addComponent(new Label("Provider:"));
		providerInput = new TextField();
		layout.addComponent(providerInput);
		
		layout.addComponent(new Label("Download:"));
		downloadInput = new TextField();
		layout.addComponent(downloadInput);
		
		layout.addComponent(new Label("Documentation:"));
		documentationInput = new TextField();
		layout.addComponent(documentationInput);
		
		layout.addComponent(new Label("Screencast:"));
		screencastInput = new TextField();
		layout.addComponent(screencastInput);
		
		layout.addComponent(new Label("Availabilities:"));
		availabilitiesTagComponent = new BPTTagComponent("availabilities", true);
		layout.addComponent(availabilitiesTagComponent);
		
		layout.addComponent(new Label("Model Type:"));
		modelTagComponent = new BPTTagComponent("modelTypes", true);
		layout.addComponent(modelTagComponent);
		
		layout.addComponent(new Label("Platform:"));
		platformTagComponent = new BPTTagComponent("platforms", true);
		layout.addComponent(platformTagComponent);
		
		layout.addComponent(new Label("Supported functionality:"));
		functionalityTagComponent = new BPTTagComponent("supportedFunctionalities", true);
		layout.addComponent(functionalityTagComponent);
		
		imagePanel = new Panel("Logo");
		
		createUploadComponent(imagePanel);
		
        imagePanel.addComponent(new Label("No image uploaded yet"));
        layout.addComponent(imagePanel);

		finishUploadButton = new Button("Submit");
		layout.addComponent(finishUploadButton);
		finishUploadButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				
				BPTToolRepository toolRepository = ((BPTApplication)getApplication()).getToolRepository();
				
				if(toolRepository.containsName((String)nameInput.getValue())) {
					addWarningWindow(getWindow());
				}
				else{
					finishUpload();
					}
				
			}

			private void finishUpload() {
				BPTToolRepository toolRepository = ((BPTApplication)getApplication()).getToolRepository();
				documentId = toolRepository.createDocument(generateDocument(new Object[] {
						(String)nameInput.getValue(),
						(String)descriptionInput.getValue(),
						(String)providerInput.getValue(),
						(String)downloadInput.getValue(),
						(String)documentationInput.getValue(),
						(String)screencastInput.getValue(),
						new ArrayList<String>(availabilitiesTagComponent.getTagValues()),
						new ArrayList<String>(modelTagComponent.getTagValues()),
						new ArrayList<String>(platformTagComponent.getTagValues()),
						new ArrayList<String>(functionalityTagComponent.getTagValues()),
						((BPTApplication)getApplication()).getUsername(), 
						((BPTApplication)getApplication()).getMailAddress(), 
						new Date(),
						new Date()
					}));
					
					if (logo != null) { // logo.exists()
						Map<String, Object> document = toolRepository.readDocument(documentId);
						String documentRevision = (String)document.get("_rev");
						
						toolRepository.createAttachment(documentId, documentRevision, "logo", logo, imageType);
						
						logo.delete();
					}
					
					getWindow().showNotification("New entry submitted: " + (String)nameInput.getValue());
					((BPTApplication)getApplication()).finder();
				
			}

			private void addWarningWindow(final Window window) {
				final Window warningWindow = new Window("Warning");
				warningWindow.setWidth("400px");
				warningWindow.setModal(true);
				warningWindow.addComponent(new Label("The name you have chosen is already taken - continue?"));
				Button yesButton = new Button("yes");
				Button cancelButton = new Button("cancel");
				warningWindow.addComponent(yesButton);
				warningWindow.addComponent(cancelButton);
				cancelButton.addListener(new Button.ClickListener(){
					public void buttonClick(ClickEvent event) {
						window.removeWindow(warningWindow);
					}
				});
				yesButton.addListener(new Button.ClickListener(){
					public void buttonClick(ClickEvent event) {
						window.removeWindow(warningWindow);
						finishUpload();
					}
				});
				window.addWindow(warningWindow);
				
			}
		});
	}

	private Map<String, Object> generateDocument(Object[] values) {
		Map<String, Object> document = new HashMap<String, Object>();
		ArrayList<String> keysList = BPTVaadinResources.getDocumentKeys(true);
		String[] keys = keysList.toArray(new String[keysList.size()]);
		for(int i = 0; i < keys.length; i++) {
			document.put(keys[i], values[i]);
		}
		return document;
	}
	
	private void createUploadComponent(Panel parent) {
		upload = new Upload("Upload a logo (*.jpg, *.gif, *.png supported):", this);
		upload.setImmediate(false);
		upload.setWidth("-1px");
		upload.setHeight("-1px");
		upload.addListener((Upload.SucceededListener)this);
        upload.addListener((Upload.FailedListener)this);
		parent.addComponent(upload);
	}
	
	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		imageName = filename;
		imageType = mimeType;
		
        if(System.getProperty("os.name").contains("Windows")) {
			logo = new File("C:\\temp\\" + filename);
		}
		else {
			logo = new File("/tmp/" + filename);
		}
        
        try {
        	if (Arrays.asList(supportedImageTypes).contains(imageType)) {
        		outputStream = new FileOutputStream(logo);
        	}
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        return outputStream;
	}
	
	@Override
	public void uploadSucceeded(final SucceededEvent event) {
		final FileResource imageResource = new FileResource(logo, getApplication());
        imagePanel.removeAllComponents();
        imagePanel.addComponent(new Embedded(event.getFilename(), imageResource));
        removeImageButton = new Button("Remove image");
		imagePanel.addComponent(removeImageButton);
		removeImageButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent clickEvent) {
				outputStream = null;
				imagePanel.removeAllComponents();
				createUploadComponent(imagePanel);
				imagePanel.addComponent(new Label("No image uploaded yet"));
				boolean deletionSuccessful = logo.delete();
				if (!deletionSuccessful) {
					throw new IllegalArgumentException("Deletion of " + event.getFilename() + " failed.");
				}
				logo = null;
			}
		});
	}
	
	@Override
	public void uploadFailed(FailedEvent event) {
		getWindow().showNotification(
                "Upload failed :(",
                "The type of the file you have submitted is not supported or the file was not found.",
                Notification.TYPE_ERROR_MESSAGE);
	}
}