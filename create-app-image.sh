dir=$PWD

./build.sh

cd ../app-image-builder
./build-jvm-based-app-image.sh $dir

cd $dir
