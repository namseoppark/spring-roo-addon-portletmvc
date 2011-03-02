package com.roo.portlet;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.classpath.PhysicalTypeCategory;
import org.springframework.roo.classpath.PhysicalTypeIdentifier;
import org.springframework.roo.classpath.TypeLocationService;
import org.springframework.roo.classpath.TypeManagementService;
import org.springframework.roo.classpath.details.ClassOrInterfaceTypeDetailsBuilder;
import org.springframework.roo.classpath.details.annotations.AnnotationAttributeValue;
import org.springframework.roo.classpath.details.annotations.AnnotationMetadataBuilder;
import org.springframework.roo.classpath.details.annotations.ClassAttributeValue;
import org.springframework.roo.classpath.details.annotations.StringAttributeValue;
import org.springframework.roo.model.JavaSymbolName;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.process.manager.FileManager;
import org.springframework.roo.project.Dependency;
import org.springframework.roo.project.DependencyScope;
import org.springframework.roo.project.DependencyType;
import org.springframework.roo.project.Path;
import org.springframework.roo.project.PathResolver;
import org.springframework.roo.project.ProjectOperations;
import org.springframework.roo.project.ProjectType;
import org.springframework.roo.project.Repository;
import org.springframework.roo.support.util.Assert;
import org.springframework.roo.support.util.XmlUtils;
import org.w3c.dom.Element;

/**
 * Implementation of operations this add-on offers.
 *
 * @since 1.1
 */
@Component
@Service
public class PortletOperationsImpl implements PortletOperations {
	
	@Reference private FileManager fileManager;
	@Reference private PathResolver pathResolver;
	@Reference private ProjectOperations projectOperations;
	@Reference private TypeLocationService typeLocationService;
	@Reference private FileWriterOperations fileWriterOperations;
	@Reference private TypeManagementService typeManagementService;
	
	public boolean isCommandAvailable() {
		return fileManager.exists(pathResolver.getIdentifier(Path.SRC_MAIN_RESOURCES, "META-INF/persistence.xml"));
	}

	public void createController(JavaType entity, JavaType controller) {
		Assert.notNull(entity, "Entity type required");
		Assert.notNull(controller, "Controller type required");
		
		String resourceIdentifier = typeLocationService.getPhysicalLocationCanonicalPath(controller, Path.SRC_MAIN_JAVA);
		
		//-- return if the controller already exists
		if (fileManager.exists(resourceIdentifier)) {
			return;
		}
		
		List<AnnotationMetadataBuilder> metadataBuilder = new ArrayList<AnnotationMetadataBuilder>();
		
		//-- build @RooPortletScaffold annotation
		List<AnnotationAttributeValue<?>> rooPortletScaffoldAttributes = new ArrayList<AnnotationAttributeValue<?>>();
		rooPortletScaffoldAttributes.add(new ClassAttributeValue(new JavaSymbolName("formBackingObject"), entity));
		metadataBuilder.add(new AnnotationMetadataBuilder(new JavaType(RooPortletScaffold.class.getName()), rooPortletScaffoldAttributes));
		
		//-- build @RequestMapping annotation
		List<AnnotationAttributeValue<?>> requestMappingAttributes = new ArrayList<AnnotationAttributeValue<?>>();
		requestMappingAttributes.add(new StringAttributeValue(new JavaSymbolName("value"), "VIEW"));	
		metadataBuilder.add(new AnnotationMetadataBuilder(new JavaType("org.springframework.web.bind.annotation.RequestMapping"), requestMappingAttributes));
		
		//--build @Controller annotation
		metadataBuilder.add(new AnnotationMetadataBuilder(new JavaType("org.springframework.stereotype.Controller")));
		
		String controllerMetadataString = PhysicalTypeIdentifier.createIdentifier(controller, pathResolver.getPath(resourceIdentifier));
		ClassOrInterfaceTypeDetailsBuilder typeDetailsBuilder = new ClassOrInterfaceTypeDetailsBuilder(controllerMetadataString, Modifier.PUBLIC, controller, PhysicalTypeCategory.CLASS);
		typeDetailsBuilder.setAnnotations(metadataBuilder);
		
		typeManagementService.generateClassFile(typeDetailsBuilder.build());
	}

	/** {@inheritDoc} */
	public void createControllerAll() {
		for (JavaType type: typeLocationService.findTypesWithAnnotation(new JavaType(RooEntity.class.getName()))) {
			createController(type, null);
		}
	}
	
	/** {@inheritDoc} */
	public void setup(PortalServer portalServer) {
		// Install the add-on Google code repository needed to get the annotation 
		projectOperations.addRepository(new Repository("Portlet Roo add-on repository", "Portlet Roo add-on repository", "https://portlet-addon.googlecode.com/svn/repo"));
		
		List<Dependency> dependencies = new ArrayList<Dependency>();
		
		// Install the dependency on the add-on jar (
		dependencies.add(new Dependency("roo.addon.portlet", "roo.addon.portlet", "0.1.0.BUILD-SNAPSHOT", DependencyType.JAR, DependencyScope.PROVIDED));
		
		// Install dependencies defined in external XML file
		for (Element dependencyElement : XmlUtils.findElements("/configuration/spring-portlet-mvc/dependencies/dependency", XmlUtils.getConfiguration(getClass()))) {
			dependencies.add(new Dependency(dependencyElement));
		}

		// Add all new dependencies to pom.xml
		projectOperations.addDependencies(dependencies);
		
		fileWriterOperations.createPortletContextXml();
		fileWriterOperations.createWebXml();
		fileWriterOperations.createPortletXml();
		fileWriterOperations.createPortalServerSpecificConfigs(portalServer);
		projectOperations.updateProjectType(ProjectType.WAR);
	}
}