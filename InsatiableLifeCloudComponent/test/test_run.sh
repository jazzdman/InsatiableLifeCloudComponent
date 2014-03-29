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

# Find the ant junit libraries
set `find /Applications -name "ant*junit*jar" -print`

while [ "$#" -gt "1" ]
do
	shift
done

junit_path_1="$1"
shift


set `find /Applications -name "junit*jar" -print`

while [ "$#" -gt "1" ]
do
	shift
done

junit_path_2="$1"
shift

# Find servlet-api.jar
set `find /Applications -name "servlet-api.jar" -print`

while [ "$#" -gt "1" ]
do
	shift
done

servlet_path="$1"
shift


classpath="$servlet_path:$junit_path_1:$junit_path_2:$project_dir/src/java"

echo "$classpath"


# Remove any previous test.xml files
if [ -f test.xml ]; then

    rm -f test.xml

fi

# Remove the class files
rm -rf web_utils/*.class

# fill in the name of project directory 
cat test.xml.template | sed -e "s|JUNITPATH|$junit_path_2|g"| sed -e "s|DIR_NAME|$project_dir|g" | sed -e "s|CLASSPATH|$classpath|g" >> test.xml


# Execute the test
"$ant_executable" -lib . -v -f test.xml output
