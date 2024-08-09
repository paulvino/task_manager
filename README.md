# Task Manager

### Tests and linter status
[![Actions Status](https://github.com/paulvino/task_manager/actions/workflows/main.yml/badge.svg)](https://github.com/paulvino/task_manager/actions/workflows/main.yml)
[![Maintainability](https://api.codeclimate.com/v1/badges/dd14e0bef08887ea295e/maintainability)](https://codeclimate.com/github/paulvino/task_manager/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/dd14e0bef08887ea295e/test_coverage)](https://codeclimate.com/github/paulvino/task_manager/test_coverage)

### Description
This little application is an API of Task management system. 
It allows you to create, edit, delete and view tasks. 

Every task have a title, description, status, priority, author and assignee.

### How to install:
```bash
git clone git@github.com:paulvino/task_manager.git
cd task_manager
```
### How to run:
```bash
make run
```

#### You can see API documentation on http://localhost:8080/swagger-ui/index.html after run the project

### Another how to:
```bash
make build # building project
make clean # cleaning project
make report # tests reporting
make lint # linter checks
```
