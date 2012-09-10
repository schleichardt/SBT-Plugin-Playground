cd FooPlugin
sbt ~publish-local &

cd ../TestApp
sbt ~test
