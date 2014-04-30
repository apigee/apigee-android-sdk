#!/bin/sh
#******************************************************************************
# This shell script pulls together the pieces that make up the Android SDK
# distribution and builds a single zip file containing those pieces.
#
# The version of the SDK should be passed in as an argument.
# Example: build_sdk_zip.sh 1.4.3
#******************************************************************************

# variables used for javadoc generation
MAVEN_REPO="/Users/ApigeeCorporation/.m2/repository"
# ANDROID_SDK_PATH="/Applications/adt-bundle-mac/sdk/platforms/android-4.2"
JAVADOC_EXE_PATH="/System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Home/bin"
JAVA_VERSION="1.6"


# verify that we've been given an argument (the version string)
if [ $# -eq 0 ]
  then
    echo "Error: SDK version string should be passed as argument"
	exit 1
fi

# SDK version string is passed in as an argument to this script
SDK_VERSION="$1"

# the following command extracts the version string from the sdk source
SDK_SOURCE_VERSION=`grep "String SDK_VERSION" source/src/main/java/com/apigee/sdk/ApigeeClient.java | awk '{print $7}' | cut -d'"' -f2`

if [ "${SDK_VERSION}" != "${SDK_SOURCE_VERSION}" ]; then
	echo "Error: sdk source version (${SDK_SOURCE_VERSION}) does not match specified version (${SDK_VERSION})"
	exit 1
fi


# set up our tools
MVN_COMMAND="mvn"

# set our paths and file names
LIBRARY_BASE_NAME="apigee-android"
JAR_FILE_NAME="${LIBRARY_BASE_NAME}-${SDK_VERSION}.jar"
ZIP_BASE_NAME="${LIBRARY_BASE_NAME}-sdk"
ZIP_FILE_NAME="${ZIP_BASE_NAME}.zip"
TOPLEVEL_ZIP_DIR="zip"
DEST_ZIP_DIR="${TOPLEVEL_ZIP_DIR}/${LIBRARY_BASE_NAME}-sdk-${SDK_VERSION}"
BUILT_SDK_JAR_FILE="source/target/${JAR_FILE_NAME}"
ZIP_JAR_DIR="${DEST_ZIP_DIR}/lib"
NEW_PROJECT_TEMPLATE_DIR="new-project-template"
SAMPLES_DIR="samples"
DEST_SAMPLES_DIR="${DEST_ZIP_DIR}/${SAMPLES_DIR}"
DOCS_DIR="source/docs"

# make a clean build
cd source
"${MVN_COMMAND}" clean
"${MVN_COMMAND}" install -Dmaven.test.skip=true

cd ..

# have build_release_zip.sh?
if [ -f "${DEST_ZIP_DIR}/build_release_zip.sh" ]; then
	# delete it
	rm "${DEST_ZIP_DIR}/build_release_zip.sh"
fi

# generate javadocs
JAVADOC_OUTPUT_DIR="${DEST_ZIP_DIR}/docs"
if [ -d "${JAVADOC_OUTPUT_DIR}" ]; then
  # delete everything that may be there
  rm -r "${JAVADOC_OUTPUT_DIR}/*"
else
  mdir "${JAVADOC_OUTPUT_DIR}"
fi

JAVADOC_OPTIONS="-splitindex -use -version -public -author"
CLASSPATH="./source/target/test-classes:${MAVEN_REPO}/com/fasterxml/jackson/core/jackson-core/2.2.3/jackson-core-2.2.3.jar:${MAVEN_REPO}/com/fasterxml/jackson/core/jackson-annotations/2.2.3/jackson-annotations-2.2.3.jar:${MAVEN_REPO}/com/fasterxml/jackson/core/jackson-databind/2.2.3/jackson-databind-2.2.3.jar:${MAVEN_REPO}/commons-codec/commons-codec/1.4/commons-codec-1.4.jar:${MAVEN_REPO}/commons-logging/commons-logging/1.1.1/commons-logging-1.1.1.jar:${MAVEN_REPO}/org/apache/httpcomponents/httpclient/4.1.2/httpclient-4.1.2.jar:${MAVEN_REPO}/org/apache/httpcomponents/httpcore/4.1.2/httpcore-4.1.2.jar:${ANDROID_SDK_PATH}/android.jar"
SOURCEPATH="./source/src/main/java"
PACKAGE_LIST=@source/docs/packages
YDOC_DIR="${DOCS_DIR}/yworks-uml-doclet-3.0_02-jdk1.5"
YDOC_OPTIONS="${YDOC_DIR}/lib/ydoc.jar:${YDOC_DIR}/resources"
DOCTITLE="Apigee API BaaS Android SDK Reference"
HEADER=@source/docs/header
BOTTOM="<p>&copy;&#32;2014&#32;Apigee&#32;Corp.&#32;All&#32;rights&#32;reserved</p>"
FOOTER="<p></p>"
STYLESHEET=./source/docs/stylesheet.css

JDOC_CMD="${JAVADOC_EXE_PATH}/javadoc ${JAVADOC_OPTIONS} -classpath ${CLASSPATH} -d ${JAVADOC_OUTPUT_DIR} -source ${JAVA_VERSION} -sourcepath ${SOURCEPATH} -footer ${FOOTER} -bottom ${BOTTOM} -header ${HEADER} -stylesheetfile ${STYLESHEET} -docletpath ${YDOC_OPTIONS} -doclet ydoc.doclets.YStandard -filterpath ${YDOC_DIR}/lib/ydoc.jar -filter ydoc.filters.ExcludeFilter ${PACKAGE_LIST}"
echo ${JDOC_CMD}
${JDOC_CMD} 

# create the zip file
cd ${TOPLEVEL_ZIP_DIR} && zip -r -y ${ZIP_FILE_NAME} .

