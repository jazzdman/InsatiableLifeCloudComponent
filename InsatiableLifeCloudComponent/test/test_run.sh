#!/bin/bash

project_dir=`find ~ -name InsatiableLifeCloudComponent -print`

IFS=$'\n'

set `find /Applications -name ant | grep bin`

while [ "$#" -gt "1" ]
do
	shift
done

ant_executable="$1"

# Remove any previous test.xml files
if [ -f test.xml ]; then

    rm -f test.xml

fi

# fill in the name of project directory 
cat test.xml.template | sed -e "s|DIR_NAME|$project_dir|g" >> test.xml


# Execute the test
"$ant_executable" -v -f test.xml output
