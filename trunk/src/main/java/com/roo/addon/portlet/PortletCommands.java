package com.roo.addon.portlet;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.shell.CliAvailabilityIndicator;
import org.springframework.roo.shell.CliCommand;
import org.springframework.roo.shell.CliOption;
import org.springframework.roo.shell.CommandMarker;

/**
 * Sample of a command class. The command class is registered by the Roo shell following an
 * automatic classpath scan. You can provide simple user presentation-related logic in this
 * class. You can return any objects from each method, or use the logger directly if you'd
 * like to emit messages of different severity (and therefore different colours on 
 * non-Windows systems).
 * 
 * @since 1.1
 */
@Component // Use these Apache Felix annotations to register your commands class in the Roo container
@Service
public class PortletCommands implements CommandMarker { // All command types must implement the CommandMarker interface
	
	/**
	 * Get a reference to the PortletOperations from the underlying OSGi container
	 */
	@Reference private PortletOperations operations;
	
	/**
	 * This method is optional. It allows automatic command hiding in situations when the command should not be visible.
	 * For example the 'entity' command will not be made available before the user has defined his persistence settings 
	 * in the Roo shell or directly in the project.
	 * 
	 * You can define multiple methods annotated with {@link CliAvailabilityIndicator} if your commands have differing
	 * visibility requirements.
	 * 
	 * @return true (default) if the command should be visible at this stage, false otherwise
	 */
	@CliAvailabilityIndicator({ "portlet setup", "portlet add", "portlet all" })
	public boolean isCommandAvailable() {
		return operations.isCommandAvailable();
	}
	
	/**
	 * This method registers a command with the Roo shell. It also offers a mandatory command attribute.
	 * 
	 * @param type 
	 */
	@CliCommand(value = "portlet add", help = "Some helpful description")
	public void add(
			@CliOption(key = "entity", mandatory = true, help = "The JPA entity for which the CRUD operations needs to be added to the portlet") JavaType target,
			@CliOption(key = "controller", mandatory = false, help = "The name of the Spring Portlet MVC annotated controller containing CRUD operations") JavaType controller
			) {
		operations.createController(target, controller);
	}
	
	/**
	 * This method registers a command with the Roo shell. It has no command attribute.
	 * 
	 */
	@CliCommand(value = "portlet all", help = "Create Spring Portlet MVC controllers for all the JPA entities in the project")
	public void all() {
		operations.createControllerAll();
	}
	
	/**
	 * This method registers a command with the Roo shell. It has no command attribute.
	 * 
	 */
	@CliCommand(value = "portlet setup", help = "Setup Portlet addon")
	public void setup(@CliOption(key = "server", mandatory = true, help = "The portal server Liferay or GateIn for which the portlet support needs to be added") PortalServer portalServer) {
		operations.setup(portalServer);
	}
}