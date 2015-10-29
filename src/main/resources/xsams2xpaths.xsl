<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output omit-xml-declaration="yes" indent="yes"/>
    <xsl:strip-space elements="*"/>
    
    <xsl:variable name="vApos">'</xsl:variable>
    
    <xsl:template match="*[@* or not(*)] ">
        <xsl:if test="not(*)">
            <xsl:apply-templates select="ancestor-or-self::*" mode="path"/> 
            <xsl:text>&#xA;</xsl:text>
        </xsl:if>
        <xsl:apply-templates select="@*|*"/>
    </xsl:template>
    
    <xsl:template match="*" mode="path">
        <xsl:value-of select="concat('/',name())"/>
    </xsl:template>
    
    <xsl:template match="@*">
        <xsl:apply-templates select="../ancestor-or-self::*" mode="path"/>
        <xsl:value-of select="concat('[@',name(),']')"/>
        <xsl:text>&#xA;</xsl:text>
    </xsl:template>
</xsl:stylesheet>
