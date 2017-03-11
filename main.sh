#!/bin/bash
MONITORDIR="$(pwd)/images/"
SCRIPTDIRECTORY=$(pwd)
echo $0
inotifywait -m -r -e create --format '%w%f' "${MONITORDIR}" | while read NEWFILE
do
        echo  "LOG: Image ${NEWFILE} has been created"
line=$(alpr -c eu ${NEWFILE} -n 1)
stringArray=($line)

#echo "LOG: The first line is $line"
echo "LOG: The plate number is ${stringArray[4]}"

re='^[0-9]+$'
if ! [[ ${stringArray[4]} =~ $re ]]; then echo "Couldn't detect the number from the photo"

else
echo
	/home/fox/Project/./checkPNumber.sh ${stringArray[4]}
if ! [ $? -eq 0 ]; then 
echo "LOG: returned value $?"
cd $(pwd)/jar_files && java -jar report.jar ${NEWFILE}
fi
fi
done
