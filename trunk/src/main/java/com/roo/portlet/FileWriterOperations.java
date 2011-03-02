package com.roo.portlet;

public interface FileWriterOperations {
	void createPortletContextXml();
	void createPortletXml();
	void createWebXml();
	void createPortalServerSpecificConfigs(PortalServer portalServer);
}
