
convertdaystr() {
retdaystr=''
	case $1 in
	01)
		retdaystr="Monday"
		break
		;;
	02)
		retdaystr="Tuesday"
		break
		;;
	03)
		retdaystr="Wednesday"
		break
		;;
	04)
		retdaystr="Thursday"
		break
		;;
	05)
		retdaystr="Friday"
		break
		;;
	06)
		retdaystr="Saturday"
		break
		;;
	07)
		retdaystr="Sunday"
		break
		;;
	00)
		retdaystr="Sunday"
		break
		;;
	*)
		retdaystr=""
		break
		;;
	esac
	echo $retdaystr
}

incrementstr() {
	firsthalf_c=$1
	firsthalf=$1
	lasthalf=$2
	islastitem=$3
	type=$4
	retstr=''
	wordval=''
	
	if [[ ${#firsthalf_c} -lt 2 ]] ; then firsthalf_c="0${firsthalf_c}"; fi
	if [[ ${#firsthalf} -lt 2 ]] ; then firsthalf="0${firsthalf}"; fi
	if [[ ${#lasthalf} -lt 2 ]] ; then lasthalf="0${lasthalf}"; fi


	while [ $firsthalf -le $lasthalf ]
	do
		if [[ $count -eq '1' && $firsthalf -eq $firsthalf_c ]]; then
			if [[ $type == "days" ]]; then
				retstr=$(convertdaystr $firsthalf)
			else
				retstr=$firsthalf
			fi
		elif [[ $islastitem == 'yes' && $firsthalf -eq $lasthalf ]]; then
			if [[ $type == "days" ]]; then
				retstr=$retstr' and '$(convertdaystr $firsthalf)
			else
				retstr=$retstr' and '$firsthalf
			fi
		else
			if [[ $type == "days" ]]; then
				retstr=$retstr'; '$(convertdaystr $firsthalf)
			else
				retstr=$retstr"; "$firsthalf
			fi
		fi
		firsthalf=`expr $firsthalf + 1`
		if [[ ${#firsthalf} -lt 2 ]] ; then firsthalf="0${firsthalf}"; fi
	done
	echo $retstr
}

parseIt() {
    argval=$1
	argtype=$2
	retstr=''
	valuestr=''
	intitstr=''
    count=0
	recount=1
	if [[ ! -z $argval && ! -z $argtype ]]; then
		if [[ $argtype == "hours" ]]; then
			intitstr='  past Hours -  '
		elif [[ $argtype == "mins" ]]; then
			intitstr='At Minutes -  '
		elif [[ $argtype == "days" ]]; then
			intitstr='  On every '
		fi
        
		IFS=','; for word in $argval; do count=`expr $count + 1`; done
		for word in $argval
		do
			word="${word## }"; word="${word%% }"
			if [[ ! -z $word  && ! $word == 'all' ]]; then
				if [[ $word == *"-"* ]]; then
					startval=`echo $word | cut -d "-" -f 1`
					endval=`echo $word | cut -d "-" -f 2`
					if [[ $recount -eq $count ]]; then
						valuestr=$valuestr$(incrementstr $startval $endval 'yes' $argtype)
					else
						valuestr=$valuestr$(incrementstr $startval $endval 'no' $argtype)
					fi
				else
					if [[ ${#word} -lt 2 ]]; then
						word="0${word}"
					fi
					if [[ $recount -eq 1 ]]; then
						if [[ $argtype == "days" ]]; then
							valuestr=$(convertdaystr $word)
						else
							valuestr=$word
						fi
					elif [[ $recount -eq $count ]]; then
						if [[ $argtype == "days" ]]; then
							valuestr=$valuestr' and '$(convertdaystr $word)
						else
							valuestr=$valuestr' and '$word
						fi
					else
						if [[ $argtype == "days" ]]; then
							valuestr=$valuestr"; "$(convertdaystr $word)
						else
							valuestr=$valuestr"; "$word
						fi
					fi
				fi
			elif [[ $word == "all" ]]; then
				if [[ $argtype == "hours" ]]; then
					intitstr='  Past every Hour  '
					valuestr=''
				elif [[ $argtype == "mins" ]]; then
					intitstr='Every Minute,  '
					valuestr=''
				elif [[ $argtype == "days" ]]; then
					intitstr='  On All the Days - Mon - Sun'
					valuestr=''
				fi
			fi
			
			
		recount=`expr $recount + 1`
		done
		retstr=$intitstr$valuestr
    fi
    echo $retstr
}


if [[ -z $1 ]]; then
	echo "Enter the CRON user for which to display the tables"
	exit 1;
fi;
counter=0
echo -e "List of commands to be executed for the user $1\n"	
server=`hostname`
outfile=$1"-"$server".txt"
crontab -l $1 > /tmp/$1_cronfile
cat /tmp/$1_cronfile | grep -v ^# > /tmp/$1_cronfileWOcomment

while read line
	do 
		###if [[ ! $line =~ ^\# && ! -z $line ]]; then
		if [[ ! $line = \#* && ! -z $line ]]; then
			echo "^^^^^^^^^^^^line:"$line"^^^^^^^^^^^^"
			COMBINED_DATE=0
			TIME_str=""
			DATE_str=""
		
			#explode to values
			COMMAND=`echo "$line" | sed 's/^\(.\{1,8\} \)\{5\}//'`
			MINUTES=`echo "$line" |cut -d" " -f1`
			HOUR=`echo "$line" |cut -d" " -f2`
			MONTH_DAY=`echo "$line" |cut -d" " -f3`
			MONTH=`echo "$line" |cut -d" " -f4`
			WEEK_DAY=`echo "$line" |cut -d" " -f5`
			
			echo "command:"$COMMAND"   minutes:"$MINUTES"-   hour:"$HOUR"-   month_day:"$MONTH_DAY"   month:"$MONTH"   week_day:"$WEEK_DAY"-"
			if [[ $WEEK_DAY == "*" ]]; then
				WEEK_DAY="all"
			fi
			if [[ $HOUR == "*" ]]; then
				HOUR="all"
			fi
			if [[ $HOUR == "*" ]]; then
				MINUTES="all"
			fi
			retval_MINUTES=$(parseIt $MINUTES "mins")
			retval_MINUTES=`echo $retval_MINUTES | tr ';' ', '`
			echo "retval_MINUTES::"$retval_MINUTES
			
			retval_HOUR=$(parseIt $HOUR "hours")
			retval_HOUR=`echo $retval_HOUR | tr ';' ', '`
			echo "retval_HOUR::"$retval_HOUR
			
			retval_WEEK_DAY=$(parseIt $WEEK_DAY "days")
			retval_WEEK_DAY=`echo $retval_WEEK_DAY | tr ';' ', '`
			echo "retval_WEEK_DAY::"$retval_WEEK_DAY
	
			STATEMENT=$retval_MINUTES"  "$retval_HOUR"  "$retval_WEEK_DAY


		fi;
		
		### Write a line of values retrieved from CRON.
		
		linetofile=$COMMAND";"$STATEMENT
		if [[ $counter -eq 0 ]]; then
			echo $linetofile>/tmp/$outfile
		else
			echo $linetofile>>/tmp/$outfile
		fi
		chmod 777 /tmp/$outfile
		
		counter=$((counter+1));
		if [[ "$counter" -gt 20 ]]; then
			break;
		fi;
done < /tmp/$1_cronfileWOcomment

###### This portion establisheh the connectivity with IBM Cloud STORAGE bucket ####
###### and uploads the generated file to the bucket							   ####
##run the cURL command to generate the connection TOKEN

resp=`curl -X "POST" "https://iam.cloud.ibm.com/identity/token"  -H 'Accept: application/json'  -H 'Content-Type: application/x-www-form-urlencoded'  --data-urlencode "apikey=M1pq18Tgq95sc6
XNAKJJ8DfqzyquuisBPb20jzWqCZZU"  --data-urlencode "response_type=cloud_iam"  --data-urlencode "grant_type=urn:ibm:params:oauth:grant-type:apikey"`


##Extract the exact token value string from the response returned

echo "response-"$resp"-"
startstr='"access_token":"'
endstr='","refresh_'

resp=${resp##*$startstr}
#echo "resp1:-"$resp"-"

resp=${resp%%$endstr*}
#echo "resp2:-"$resp"-"
token=$resp

##Upload the file to Cloud Storage, sfter creating the proper command
filename=devercdd_cronfileWOcomment

cmdstr1='curl -X PUT "https://s3.jp-tok.cloud-object-storage.appdomain.cloud/9cronsbucket9/'$outfile'" -H "Authorization: bearer '
cmdstr2=$token
cmdstr3='" -H "Content-Type: text/plain" --data-binary "@'
cmdstr4=/tmp/$outfile'"'

upldcommand=$cmdstr1$cmdstr2$cmdstr3$cmdstr4
echo $upldcommand
echo ""
echo ""
eval $upldcommand
RC=$?
echo "return-"$RC

exit 0;



