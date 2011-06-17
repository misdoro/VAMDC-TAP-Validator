#!/bin/sh
MYCWD=`pwd`
xjc -extension -verbose -d $MYCWD/../java -b report.xjb report.xsd
