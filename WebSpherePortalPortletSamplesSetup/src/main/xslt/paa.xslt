<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:iudd="http://www.ibm.com/xmlns/prod/autonomic/solutioninstall/IUDD"
	xmlns:OSRT="http://www.ibm.com/xmlns/prod/autonomic/resourcemodel/OS/resourcetypes"
	xmlns:OSAT="http://www.ibm.com/xmlns/prod/autonomic/resourcemodel/OS/artifacttypes"
	xmlns:J2EERT="http://www.ibm.com/xmlns/prod/autonomic/resourcemodel/J2EE/resourcetypes">

	<xsl:output method="xml" version="1.0" encoding="UTF-8"
		indent="yes" />

	<!-- name of the component to include -->
	<xsl:param name="COMPONENT_NAME" />
	<xsl:param name="VERSION" />
	<xsl:param name="LISTING" />

	<xsl:template match="component">

		<xsl:variable name="NAME" select="$COMPONENT_NAME" />

		<iudd:iudd>
			<packageIdentity contentType="Assembly">
				<name>
					<xsl:value-of select="$NAME" />
				</name>
				<version>
					<xsl:value-of select="$VERSION" />
				</version>
				<displayName key="d0001" default="{$NAME}" />
				<manufacturer>
					<displayName key="AC_01" default="IBM" />
				</manufacturer>
			</packageIdentity>

			<topology>
				<resource type="OSRT:OperatingSystem" id="OS" />
			</topology>

			<content xsi:type="iudd:RootIUContent">
				<rootIU id="{$NAME}">
					<identity>
						<name>
							<xsl:value-of select="$NAME" />
						</name>
					</identity>
					<variables>
						<parameters>
							<parameter name="installLocation" defaultValue="/usr/dummy.offr.1" />
						</parameters>
					</variables>
					<xsl:apply-templates select="document($LISTING)" />
				</rootIU>
			</content>

		</iudd:iudd>
	</xsl:template>

	<xsl:template match="files">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="sdd">
		<xsl:variable name="name"
			select="document(concat('file:///', text()))/iudd:iudd/packageIdentity/name" />
		<containedPackage id="{$name}" pathname="{$name}/sdd.xml" />
	</xsl:template>

</xsl:stylesheet>
