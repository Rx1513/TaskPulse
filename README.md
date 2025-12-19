# TaskPulse

Веб-приложение на **Spring Boot** + **Thymeleaf**, позволяющее управлять списком задач: создавать, просматривать и удалять задачи.  

## Функционал

- Регистрация и авторизация пользователей (страницы `/auth/register` и `/auth/login`)
- Просмотр списка задач (`/tasks`)
- Создание новой задачи (`/task/new`)
- Просмотр детальной информации о задаче (`/task/show/{id}`)
- Удаление задачи (`/task/delete/{id}`)


## Структура проекта
```
src/
└─ main/
├─ java/web/
│ └─ ContentController.java
└─ resources/
   ├─ templates/
   │ ├─ tasks.html
   │ ├─ show_task.html
   │ ├─ new_task.html
   │ ├─ login.html
   │ └─ register.html
   └─ application.properties
```

## Пример использования

Запустить приложение:
mvn spring-boot:run

Перейти в браузере на:
http://localhost:8080/auth/login

Создать задачу:
http://localhost:8080/task/new

Просмотреть список задач:
http://localhost:8080/tasks

Просмотреть задачу и удалить её:
http://localhost:8080/task/show/{id}

## Безопасность
Все операции доступны только авторизованным пользователям

Используется стандартная форма логина

CSRF-защита включена по умолчанию
