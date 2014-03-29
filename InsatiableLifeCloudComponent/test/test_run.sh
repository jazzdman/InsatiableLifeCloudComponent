#!/bin/sh

project_dir=`find ~ -name InsatiableLifeCloudComponent -print`

for file in `find /Applications -name ant |grep bin`
do
    ant_dir=`dirname $file`
done

# Remove any previous test.xml files
if [ -f test.xml ]; then

    rm -f test.xml

fi

# fill in the name of project directory 
cat test.xml.template | sed -e s/DIR_NAME/$project_dir/g >> test.xml


# Execute the test
$ant_dir/ant -f test.xml