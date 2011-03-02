package com.roo.portlet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.springframework.roo.metadata.MetadataService;
import org.springframework.roo.process.manager.FileManager;
import org.springframework.roo.process.manager.MutableFile;
import org.springframework.roo.project.Path;
import org.springframework.roo.project.PathResolver;
import org.springframework.roo.project.ProjectMetadata;
import org.springframework.roo.support.util.Assert;
import org.springframework.roo.support.util.FileCopyUtils;
import org.springframework.roo.support.util.TemplateUtils;
import org.springframework.roo.support.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Component
@Service
public class FileWriterOperationsImpl implements FileWriterOperations {
	@Reference private PathResolver pathResolver;
	@Reference private MetadataService metadataService;
	@Reference protected FileManager fileManager;
	
	public void createPortletContextXml() {
		ProjectMetadata projectMetadata = (ProjectMetadata) metadataService.get(ProjectMetadata.getProjectIdentifier());
		Assert.notNull(projectMetadata, "Project metadata required");
		if(!fileManager.exists(pathResolver.getIdentifier(Path.SRC_MAIN_WEBAPP, "WEB-INF/spring" + projectMetadata.getProjectName() + "-portlet.xml"))) {
			InputStream templateInputStream = TemplateUtils.getTemplate(getClass(), "portletmvc-config.xml");
			Document portletMvcConfig;
			try {
				portletMvcConfig = XmlUtils.getDocumentBuilder().parse(templateInputStream);
			} catch (Exception ex) {
				throw new IllegalStateException(ex);
			}

			Element rootElement = (Element) portletMvcConfig.getDocumentElement();
			XmlUtils.findFirstElementByName("context:component-scan", rootElement).setAttribute("base-package", projectMetadata.getTopLevelPackage().getFullyQualifiedPackageName());
			writeToDiskIfNecessary(pathResolver.getIdentifier(Path.SRC_MAIN_WEBAPP, "WEB-INF/spring/" + projectMetadata.getProjectName() + "-portlet.xml"), portletMvcConfig);
			try {
				templateInputStream.close();
			} catch (IOException ignore) {
			}
			fileManager.scan();
		}		
	}
	
	public void createPortletXml() {
		ProjectMetadata projectMetadata = (ProjectMetadata) metadataService.get(ProjectMetadata.getProjectIdentifier());
		Assert.notNull(projectMetadata, "Project metadata required");
		
		if(!fileManager.exists(pathResolver.getIdentifier(Path.SRC_MAIN_WEBAPP, "portlet.xml"))) {
			InputStream templateInputStream = TemplateUtils.getTemplate(getClass(), "portlet-template.xml");
			Document portletXML;
			try {
				portletXML = XmlUtils.getDocumentBuilder().parse(templateInputStream);
			} catch (Exception ex) {
				throw new IllegalStateException(ex);
			}

			Element rootElement = (Element) portletXML.getDocumentElement();
			
			Element portletNameElement = XmlUtils.findFirstElement("/portlet-app/portlet/portlet-name", rootElement);
			portletNameElement.setTextContent(projectMetadata.getProjectName() + "-portlet");
			
			Element contextLocationElement = XmlUtils.findFirstElement("/portlet-app/portlet/init-param/value", rootElement);
			contextLocationElement.setTextContent("/WEB-INF/spring/" + projectMetadata.getProjectName() + "-portlet.xml");
			
			Element portletTitleElement = XmlUtils.findFirstElement("/portlet-app/portlet/portlet-info/title", rootElement);
			portletTitleElement.setTextContent(projectMetadata.getProjectName() + "portlet");
			
			writeToDiskIfNecessary(pathResolver.getIdentifier(Path.SRC_MAIN_WEBAPP, "WEB-INF/portlet.xml"), portletXML);
			try {
				templateInputStream.close();
			} catch (IOException ignore) {
			}
			fileManager.scan();
		}
	}
	
	
	public void createWebXml() {		
		if(!fileManager.exists(pathResolver.getIdentifier(Path.SRC_MAIN_WEBAPP, "WEB-INF/web.xml"))) {
			InputStream templateInputStream = TemplateUtils.getTemplate(getClass(), "web-template.xml");
			Document webXML;
			try {
				webXML = XmlUtils.getDocumentBuilder().parse(templateInputStream);
			} catch (Exception ex) {
				throw new IllegalStateException(ex);
			}

			writeToDiskIfNecessary(pathResolver.getIdentifier(Path.SRC_MAIN_WEBAPP, "WEB-INF/web.xml"), webXML);
			try {
				templateInputStream.close();
			} catch (IOException ignore) {
			}
			fileManager.scan();
		}
	}
	
	public void createPortalServerSpecificConfigs(PortalServer portalServer) {
		ProjectMetadata projectMetadata = (ProjectMetadata) metadataService.get(ProjectMetadata.getProjectIdentifier());
		Assert.notNull(projectMetadata, "Project metadata required");
		
		if(portalServer == PortalServer.LIFERAY) {
			if(!fileManager.exists(pathResolver.getIdentifier(Path.SRC_MAIN_WEBAPP, "WEB-INF/liferay-portlet.xml"))) {
				InputStream templateInputStream = TemplateUtils.getTemplate(getClass(), "liferay-portlet-template.xml");
				Document liferayPortletXML;
				try {
					liferayPortletXML = XmlUtils.getDocumentBuilder().parse(templateInputStream);
				} catch (Exception ex) {
					throw new IllegalStateException(ex);
				}
				Element rootElement = liferayPortletXML.getDocumentElement();
				Element portletNameElement = XmlUtils.findFirstElement("/liferay-portlet-app/portlet/portlet-name", rootElement);
				portletNameElement.setTextContent(projectMetadata.getProjectName() + "-portlet");
				writeToDiskIfNecessary(pathResolver.getIdentifier(Path.SRC_MAIN_WEBAPP, "WEB-INF/liferay-portlet.xml"), liferayPortletXML);
				try {
					templateInputStream.close();
				} catch (IOException ignore) {
				}
				fileManager.scan();
			}
			if(!fileManager.exists(pathResolver.getIdentifier(Path.SRC_MAIN_WEBAPP, "WEB-INF/liferay-display.xml"))) {
				InputStream templateInputStream = TemplateUtils.getTemplate(getClass(), "liferay-display-template.xml");
				Document liferayDisplayXML;
				try {
					liferayDisplayXML = XmlUtils.getDocumentBuilder().parse(templateInputStream);
				} catch (Exception ex) {
					throw new IllegalStateException(ex);
				}
				Element rootElement = liferayDisplayXML.getDocumentElement();
				Element displayCategoryElement = XmlUtils.findFirstElement("/display/category", rootElement);
				displayCategoryElement.setAttribute("name", "Roo Addon Generated");
				
				Element portletElement = XmlUtils.findFirstElement("/display/category/portlet", rootElement);
				portletElement.setAttribute("id", projectMetadata.getProjectName() + "-portlet");
				
				writeToDiskIfNecessary(pathResolver.getIdentifier(Path.SRC_MAIN_WEBAPP, "WEB-INF/liferay-display.xml"), liferayDisplayXML);
				try {
					templateInputStream.close();
				} catch (IOException ignore) {
				}
				fileManager.scan();
			}
		}
	}
	
	private boolean writeToDiskIfNecessary(String fileName, Document proposed) {
		// Build a string representation of the JSP
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		XmlUtils.writeXml(XmlUtils.createIndentingTransformer(), byteArrayOutputStream, proposed);
		String xmlContent = byteArrayOutputStream.toString();
		try {
			byteArrayOutputStream.close();
		} catch (IOException ignore) {}

		// If mutableFile becomes non-null, it means we need to use it to write out the contents of jspContent to the file
		MutableFile mutableFile = null;
		if (fileManager.exists(fileName)) {
			// First verify if the file has even changed
			File f = new File(fileName);
			String existing = null;
			try {
				existing = FileCopyUtils.copyToString(new FileReader(f));
			} catch (IOException ignoreAndJustOverwriteIt) {
			}

			if (!xmlContent.equals(existing)) {
				mutableFile = fileManager.updateFile(fileName);
			}

		} else {
			mutableFile = fileManager.createFile(fileName);
			Assert.notNull(mutableFile, "Could not create XML file '" + fileName + "'");
		}

		try {
			if (mutableFile != null) {
				// We need to write the file out (it's a new file, or the existing file has different contents)
				FileCopyUtils.copy(xmlContent, new OutputStreamWriter(mutableFile.getOutputStream()));
				// Return and indicate we wrote out the file
				return true;
			}
		} catch (IOException ioe) {
			throw new IllegalStateException("Could not output '" + mutableFile.getCanonicalPath() + "'", ioe);
		}

		// A file existed, but it contained the same content, so we return false
		return false;
	}
}
