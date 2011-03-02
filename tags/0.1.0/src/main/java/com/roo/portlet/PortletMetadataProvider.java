package com.roo.portlet;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;
import org.springframework.roo.addon.entity.EntityMetadata;
import org.springframework.roo.addon.web.mvc.controller.details.WebMetadataUtils;
import org.springframework.roo.classpath.PhysicalTypeIdentifier;
import org.springframework.roo.classpath.PhysicalTypeMetadata;
import org.springframework.roo.classpath.TypeLocationService;
import org.springframework.roo.classpath.details.ClassOrInterfaceTypeDetails;
import org.springframework.roo.classpath.details.MemberFindingUtils;
import org.springframework.roo.classpath.itd.AbstractItdMetadataProvider;
import org.springframework.roo.classpath.itd.ItdTypeDetailsProvidingMetadataItem;
import org.springframework.roo.classpath.scanner.MemberDetails;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.project.Path;

/**
 * Provides {@link PortletMetadata}. This type is called by Roo to retrieve the metadata for this add-on.
 * Use this type to reference external types and services needed by the metadata type. Register metadata triggers and
 * dependencies here. Also define the unique add-on ITD identifier.
 * 
 * @since 1.1
 */
@Component
@Service
public final class PortletMetadataProvider extends AbstractItdMetadataProvider {
	@Reference private TypeLocationService typeLocationService;
	/**
	 * The activate method for this OSGi component, this will be called by the OSGi container upon bundle activation 
	 * (result of the 'addon install' command) 
	 * 
	 * @param context the component context can be used to get access to the OSGi container (ie find out if certain bundles are active)
	 */
	protected void activate(ComponentContext context) {
		metadataDependencyRegistry.registerDependency(PhysicalTypeIdentifier.getMetadataIdentiferType(), getProvidesType());
		addMetadataTrigger(new JavaType(RooPortletScaffold.class.getName()));
	}
	
	/**
	 * The deactivate method for this OSGi component, this will be called by the OSGi container upon bundle deactivation 
	 * (result of the 'addon uninstall' command) 
	 * 
	 * @param context the component context can be used to get access to the OSGi container (ie find out if certain bundles are active)
	 */
	protected void deactivate(ComponentContext context) {
		metadataDependencyRegistry.deregisterDependency(PhysicalTypeIdentifier.getMetadataIdentiferType(), getProvidesType());
		removeMetadataTrigger(new JavaType(RooPortletScaffold.class.getName()));	
	}
	
	protected ItdTypeDetailsProvidingMetadataItem getMetadata(String metadataIdentificationString, JavaType aspectName, PhysicalTypeMetadata governorPhysicalTypeMetadata, String itdFilename) {
		//--get the @RooPortletController annotation values for the governor
		PortletScaffoldAnnotationValues annotationValues = new PortletScaffoldAnnotationValues(governorPhysicalTypeMetadata);
		if (!annotationValues.isAnnotationFound() || annotationValues.formBackingObject == null) {
			return null;
		}
		
		//-- obtain the formBackingObject, which is basically a @RooEntity class
		JavaType formBackingObject = annotationValues.formBackingObject;
		Path path = Path.SRC_MAIN_JAVA;
		
		//--Create instance-specific metadata identifier strings for the metadata
		String entityMetadataKey = EntityMetadata.createIdentifier(formBackingObject, path);

		//--Get the metadata
		EntityMetadata entityMetadata = (EntityMetadata) metadataService.get(entityMetadataKey);
		
		//-- return if metadata isn't available or invalid
		if (entityMetadata == null || !entityMetadata.isValid()) {
			return null;
		}

		//--register downstream dependencies
		metadataDependencyRegistry.registerDependency(entityMetadataKey, metadataIdentificationString);
		
		ClassOrInterfaceTypeDetails controllerClassOrInterfaceDetails = (ClassOrInterfaceTypeDetails) governorPhysicalTypeMetadata.getMemberHoldingTypeDetails();
		MemberDetails controllerMemberDetails = memberDetailsScanner.getMemberDetails(getClass().getName(), controllerClassOrInterfaceDetails);

		PhysicalTypeMetadata formBackingObjectPhysicalTypeMetadata = (PhysicalTypeMetadata) metadataService.get(PhysicalTypeIdentifier.createIdentifier(formBackingObject, path));
		ClassOrInterfaceTypeDetails formbackingClassOrInterfaceDetails = (ClassOrInterfaceTypeDetails) formBackingObjectPhysicalTypeMetadata.getMemberHoldingTypeDetails();
		MemberDetails formBackingObjectMemberDetails = memberDetailsScanner.getMemberDetails(getClass().getName(), formbackingClassOrInterfaceDetails);

		// Pass dependencies required by the metadata in through its constructor
		return new PortletMetadata(metadataIdentificationString, aspectName, governorPhysicalTypeMetadata, annotationValues,
				MemberFindingUtils.getMethods(controllerMemberDetails),
				WebMetadataUtils.getRelatedApplicationTypeMetadata(formBackingObject, formBackingObjectMemberDetails, metadataService, typeLocationService, metadataIdentificationString, metadataDependencyRegistry), 
				WebMetadataUtils.getDependentApplicationTypeMetadata(formBackingObject, formBackingObjectMemberDetails, metadataService, typeLocationService, metadataIdentificationString, metadataDependencyRegistry), 
				WebMetadataUtils.getDatePatterns(formBackingObject, formBackingObjectMemberDetails, metadataService, metadataIdentificationString, metadataDependencyRegistry), 
				WebMetadataUtils.getDynamicFinderMethodsAndFields(formBackingObject, formBackingObjectMemberDetails, metadataService, metadataIdentificationString, metadataDependencyRegistry));
	}
	
	/**
	 * Define the unique ITD file name extension, here the resulting file name will be **_ROO_Portlet.aj
	 */
	public String getItdUniquenessFilenameSuffix() {
		return "PortletController";
	}

	protected String getGovernorPhysicalTypeIdentifier(String metadataIdentificationString) {
		JavaType javaType = PortletMetadata.getJavaType(metadataIdentificationString);
		Path path = PortletMetadata.getPath(metadataIdentificationString);
		return PhysicalTypeIdentifier.createIdentifier(javaType, path);
	}
	
	protected String createLocalIdentifier(JavaType javaType, Path path) {
		return PortletMetadata.createIdentifier(javaType, path);
	}

	public String getProvidesType() {
		return PortletMetadata.getMetadataIdentiferType();
	}
}