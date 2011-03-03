package com.roo.addon.portlet.jsp;

import org.springframework.roo.classpath.PhysicalTypeIdentifierNamingUtils;
import org.springframework.roo.metadata.AbstractMetadataItem;
import org.springframework.roo.metadata.MetadataIdentificationUtils;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.project.Path;
import org.springframework.roo.support.style.ToStringCreator;
import org.springframework.roo.support.util.Assert;

import com.roo.addon.portlet.PortletScaffoldAnnotationValues;
import com.roo.addon.portlet.PortletScaffoldMetadata;

public class JspMetadata extends AbstractMetadataItem {
	private static final String PROVIDES_TYPE_STRING = JspMetadata.class.getName();
	private static final String PROVIDES_TYPE = MetadataIdentificationUtils.create(PROVIDES_TYPE_STRING);
	private PortletScaffoldMetadata portletScaffoldMetadata;
	private PortletScaffoldAnnotationValues annotationValues;
	
	public JspMetadata(String identifier, PortletScaffoldMetadata portletScaffoldMetadata) {
		super(identifier);
		Assert.isTrue(isValid(identifier), "Metadata identification string '" + identifier + "' does not appear to be a valid");
		Assert.notNull(portletScaffoldMetadata, "Portlet scaffold metadata required");
		
		this.portletScaffoldMetadata = portletScaffoldMetadata;		
		this.annotationValues = portletScaffoldMetadata.getAnnotationValues();
	}
	
	public PortletScaffoldAnnotationValues getAnnotationValues() {
		return annotationValues;
	}

	public String toString() {
		ToStringCreator tsc = new ToStringCreator(this);
		tsc.append("identifier", getId());
		tsc.append("valid", valid);
		tsc.append("portlet scaffold metadata id", portletScaffoldMetadata.getId());
		return tsc.toString();
	}

	public static final String getMetadataIdentiferType() {
		return PROVIDES_TYPE;
	}
	
	public static final String createIdentifier(JavaType javaType, Path path) {
		return PhysicalTypeIdentifierNamingUtils.createIdentifier(PROVIDES_TYPE_STRING, javaType, path);
	}

	public static final JavaType getJavaType(String metadataIdentificationString) {
		return PhysicalTypeIdentifierNamingUtils.getJavaType(PROVIDES_TYPE_STRING, metadataIdentificationString);
	}

	public static final Path getPath(String metadataIdentificationString) {
		return PhysicalTypeIdentifierNamingUtils.getPath(PROVIDES_TYPE_STRING, metadataIdentificationString);
	}

	public static boolean isValid(String metadataIdentificationString) {
		return PhysicalTypeIdentifierNamingUtils.isValid(PROVIDES_TYPE_STRING, metadataIdentificationString);
	}
}
