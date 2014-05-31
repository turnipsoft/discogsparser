find /Users/shartvig/discogsparser/images/progressive -mtime -1 -print | while read -r FILE
do
  fname=`basename $FILE`
  ftp -u ftp://shartvig@web15.gigahost.dk/www/turnips.dk/images/jpeg-small/$fname $FILE 
done