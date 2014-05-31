chmod 755 /Users/shartvig/discogsparser/getImages.sh
echo "Fetching Images..."
/Users/shartvig/discogsparser/getImages.sh
echo "Converting images"
cd /Users/shartvig/discogsparser/images
./prog.sh
echo "Uploading images"
/Users/shartvig/discogsparser/ftpImages.sh
echo "Uploading Site"
/Users/shartvig/discogsparser/ftpSite.sh
echo "Magic done!"
