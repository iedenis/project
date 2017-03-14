#!/bin/bash
num=$(mysql -u root -p$"ievlev85" --database="stolencars" -s -N -e "SELECT EXISTS(SELECT *  FROM cars WHERE pNumber=$1)")

#echo "LOG: number is $num"

if [ $num -eq 0 ]; 
	then  echo "LOG: number $1 not in database of stolen cars"
exit 0
		else echo "***** Car with plate number $1 appears in the police DB as stolen!!!*****" 
exit 1

fi
