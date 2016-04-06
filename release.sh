if [ -z "$1" ]
  then
    echo "No release version supplied"
    exit -1
fi

cd jmxhealth-tray
./release.sh $1

cd ..
mvn package
mvn release:prepare
