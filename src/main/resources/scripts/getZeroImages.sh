for f in *.jp*
do
	echo "processing file $f"
	wget http://s.pixogs.com/image/$f -O /Users/shartvig/discogsparser/images/0size/fetched/$f
done