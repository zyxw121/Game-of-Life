ARGS=-cp .:"./BUILD":".PUBLISH//cso.jar:" -d "./BUILD"

all: display life

display: src/display.scala
	exec scalac $(ARGS) src/display.scala 

life: src/life.scala
	exec scalac $(ARGS) src/life.scala 
