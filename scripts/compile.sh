cd ../Cofre/
mvn clean package

cd target/
cp CofreDigital-1.0-SNAPSHOT-jar-with-dependencies.jar ../../

cd ../../LogRead/
mvn clean package

cd target/
cp LogReader-1.0-SNAPSHOT-jar-with-dependencies.jar ../../