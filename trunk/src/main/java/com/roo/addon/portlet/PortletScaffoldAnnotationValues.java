package com.roo.addon.portlet;

import org.springframework.roo.classpath.PhysicalTypeMetadata;
import org.springframework.roo.classpath.details.annotations.populator.AbstractAnnotationValues;
import org.springframework.roo.classpath.details.annotations.populator.AutoPopulate;
import org.springframework.roo.classpath.details.annotations.populator.AutoPopulationUtils;
import org.springframework.roo.model.JavaType;

public class PortletScaffoldAnnotationValues extends AbstractAnnotationValues {
	@AutoPopulate JavaType formBackingObject = null;
	
	public PortletScaffoldAnnotationValues(PhysicalTypeMetadata governorPhysicalTypeMetadata) {
		super(governorPhysicalTypeMetadata, new JavaType(RooPortletScaffold.class.getName()));
		AutoPopulationUtils.populate(this, annotationMetadata);
	}
	
	public JavaType getFormBackingObject() {
		return formBackingObject;
	}
}
