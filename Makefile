run-dist:
	./build/install/task_manager/bin/task_manager

build:
	@echo "Dependencies installing..."
	./gradlew build

clean:
	@echo "Cleaning the project..."
	./gradlew clean

test:
	@echo "Starting tests..."
	./gradlew test

report:
	@echo "Reporting..."
	./gradlew jacocoTestReport

lint:
	@echo "Starting linter tests..."
	./gradlew checkstyleMain checkstyleTest

run:
	@echo "Starting the App..."
	./gradlew bootRun
	@echo "Task Manager is alive!"

.PHONY: build