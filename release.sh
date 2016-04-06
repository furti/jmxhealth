if [ -z "$1" ]
  then
    echo "No release version supplied"
    exit -1
fi

cd jmxhealth-tray
./release.sh $1

cd ..
git add .
git commit -m "Released Version $1"

mvn package
mvn release:prepare
