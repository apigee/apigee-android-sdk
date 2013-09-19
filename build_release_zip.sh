#!/bin/sh
#******************************************************************************
# This shell script pulls together the pieces that make up the Android SDK
# distribution and builds a single zip file containing those pieces.
#
# The version of the SDK should be passed in as an argument.
# Example: build_sdk_zip.sh 1.4.3
#******************************************************************************

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

# make a clean build
cd source
"${MVN_COMMAND}" clean
"${MVN_COMMAND}" install -Dmaven.test.skip=true

cd ..

# new jar file found?
if [ ! -f "${BUILT_SDK_JAR_FILE}" ] ; then
	echo "Error: unable to find jar file '${BUILT_SDK_JAR_FILE}'"
	exit 1
fi

# zip directory exists?
if [ -d "${DEST_ZIP_DIR}" ]; then
	# erase all existing files there
	find "${DEST_ZIP_DIR}" -type f -exec rm {} \;
else
	mkdir -p "${DEST_ZIP_DIR}"
fi

# copy everything from repository
for entry in *
do
	if [ -f "$entry" ]; then
		cp "$entry" "${DEST_ZIP_DIR}"
	elif [ -d "$entry" ]; then
		if [ "$entry" != "${TOPLEVEL_ZIP_DIR}" ]; then
			cp -r "$entry" "${DEST_ZIP_DIR}"
		fi
	fi
done


# if we have source/target in zip directory, delete everything under source/target
if [ -d "${DEST_ZIP_DIR}/source/target" ]; then
	rm -rf "${DEST_ZIP_DIR}/source/target"
	rmdir "${DEST_ZIP_DIR}/source/target"
fi

# create directory for jar file
mkdir -p "${ZIP_JAR_DIR}"

# copy jar file to destination directory
cp "${BUILT_SDK_JAR_FILE}" "${ZIP_JAR_DIR}"


# copy new jar file to new-project-template
# missing libs directory?
PROJECT_TEMPLATE_LIBS_DIR="${DEST_ZIP_DIR}/${NEW_PROJECT_TEMPLATE_DIR}/libs"
if [ ! -d "${PROJECT_TEMPLATE_LIBS_DIR}" ]; then
	mkdir -p "${PROJECT_TEMPLATE_LIBS_DIR}"
fi

cp "${BUILT_SDK_JAR_FILE}" "${PROJECT_TEMPLATE_LIBS_DIR}"


# copy new jar file to each sample app lib directory
for sample_entry in ${DEST_SAMPLES_DIR}/*
do
	if [ -d "${sample_entry}" ]; then
		# is it missing a libs subdirectory?
		SAMPLE_LIBS_DIR="${sample_entry}/libs"
		if [ ! -d "${SAMPLE_LIBS_DIR}" ]; then
			mkdir "${SAMPLE_LIBS_DIR}"
		fi
		
		# copy jar file to libs dir for sample app
		cp "${BUILT_SDK_JAR_FILE}" "${SAMPLE_LIBS_DIR}"
	fi
done

# create the zip file
cd ${TOPLEVEL_ZIP_DIR} && zip -r -y ${ZIP_FILE_NAME} .

