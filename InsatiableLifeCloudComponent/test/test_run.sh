#!/bin/bash

# Update the contents of InsatiableLifeCloudComponent directory
svn update ..

# Find where our project resides
project_dir=`find ~ -name InsatiableLifeCloudComponent -print`

IFS=$'\n'

# Find the ant executable
set `find /Applications -name ant | grep bin`

while [ "$#" -gt "1" ]
do
	shift
done

ant_executable="$1"
shift


# Remove any previous test.xml files
if [ -f test.xml ]; then

    rm -f test.xml

fi

# Remove the class files
rm -rf web_utils/*.class

# fill in the name of project directory 
cat test.xml.template | sed -e "s|DIR_NAME|$project_dir|g"  >> test.xml


# Execute the test
"$ant_executable" -lib ./libs -v -f test.xml output
