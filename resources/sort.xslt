<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    <xsl:template match="/Response/result">
        <Response>
            <result>
                <titre>
                    <xsl:value-of select="titre"/>
                </titre>
                <xsl:for-each select="auteur">
                <auteur>
                    <prenom>
                        <xsl:value-of select="prenom"/>
                    </prenom>
                    <nom>
                        <xsl:value-of select="nom"/>
                    </nom>
                </auteur>
                </xsl:for-each>
                <xsl:for-each select="bibliotheque">
                    <xsl:sort select="distance" data-type="number" order="ascending"/>
                    <bibliotheque>
                        <nom>
                            <xsl:value-of select="nom"/>
                        </nom>
                        <distance>
                            <xsl:value-of select="distance"/>
                        </distance>
                    </bibliotheque>
                </xsl:for-each>
            </result>
        </Response>
    </xsl:template>
    <xsl:template match="Response/error">
        <Response>
            <error>
                <api>
                    <xsl:value-of select="api"/>
                </api>
                <status>
                    <xsl:value-of select="status"/>
                </status>
            </error>
        </Response>
    </xsl:template>
</xsl:stylesheet>
