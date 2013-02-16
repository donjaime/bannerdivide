gunzip -c ./deps.tar.gz > ./deps.tar
tar -xvf ./deps.tar
mkdir -p ./target/scala-2.9.1/
gunzip -c ./prebuild.tar.gz > ./prebuild.tar
tar -xvf ./prebuild.tar ./target/scala-2.9.1/