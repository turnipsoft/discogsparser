find . -size 0 -delete

for f in *.jp*
do
	if [ ! -f "progressive/$f" ]
	then
		echo "processing file $f"
		/Users/shartvig/jpeg-6b/jpegtran -progressive $f > progressive/$f
	fi
done
