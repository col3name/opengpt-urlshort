buildDocker:
	docker build -t url-shortener .
runBack:
	docker run -p 8080:8080 url-shortener

buildImage:
	docker build -t url-shortener:latest path/to/jar
run:
	docker-compose up

build:
	./gradlew build

deploy: build
	git push heroku master

open:
	heroku open
