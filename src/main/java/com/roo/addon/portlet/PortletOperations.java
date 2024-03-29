package com.roo.addon.portlet;

import org.springframework.roo.model.JavaType;

/**
 * Interface of operations this add-on offers. Typically used by a command type or an external add-on.
 *
 * @since 1.1
 */
public interface PortletOperations {

	/**
	 * Indicate commands should be available
	 * 
	 * @return true if it should be available, otherwise false
	 */
	boolean isCommandAvailable();

	/**
	 * Annotate the provided Java type with the trigger of this add-on
	 */
	void createController(JavaType type, JavaType controller);
	
	/**
	 * Annotate all Java types with the trigger of this add-on
	 */
	void createControllerAll();
	
	/**
	 * Setup all add-on artifacts (dependencies in this case)
	 */
	void setup(PortalServer portalServer);
}