rm -rf build
mkdir build

javac -source 11 -target 11 -sourcepath src -cp ".:lib/*" -d build src/com/jkzhou/cpubenchmark/CPUBenchmark.java

rm -f cpu_benchmark.jar
jar cvfm cpu_benchmark.jar src/Manifest.txt -C build/ .

rm -f cpu_benchmark.zip
jar cvfM cpu_benchmark.zip `find . -not -path "*/.idea/*" -not -path "*/.git/*" -not -path "*/build/*" -not -path "*/out/*" -not -path "*/*.iml" -not -type d`

