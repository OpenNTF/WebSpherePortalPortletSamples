<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:portlet="http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd">
	<xsl:output method="xml" version="1.0" encoding="UTF-8"
		indent="yes" />

	<!-- base directory for all portlets in our build -->
	<xsl:param name="BASE_DIR" />

	<!-- Include all portlet by reference. This allows us to keep the portlet 
		xml for the individual portlet next to the code for the portlet -->
	<xsl:template match="portlet:portlet">
		<xsl:copy-of select="document(concat($BASE_DIR, @href))" />
	</xsl:template>

	<!-- identity transform -->
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@* | node()" />
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>
