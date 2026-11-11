# AkttAPI-Springed

REST API на **Spring Boot** для получения, хранения и обновления расписания занятий (дневное отделение АКТТ).  
Сервис умеет автоматически забирать изменения расписания с сайта колледжа, принимать расписание в виде PDF, сохранять его в базу данных и отдавать через HTTP API. Также поддерживаются JWT-аутентификация и webhook-подписки на обновления расписания.

## Возможности

- **Автоматический парсинг HTML** — каждые 30 минут загружает страницу изменений расписания
- **Загрузка расписания из PDF** — ручная загрузка файла расписания через API (для ролей `ADMIN` / `SCHEDULER`)
- **Разбор и нормализация расписания** — извлечение групп, преподавателей, времени/номера пар, предметов, аудиторий, подгрупп, типа занятия; обработка особых случаев (дистант, практика, «Разговоры о важном», самостоятельная работа и т.д.)
- **API для клиентов** — получение расписания по группе или преподавателю, списки групп/преподавателей
- **JWT-аутентификация** — регистрация и вход пользователей
- **Уведомления об обновлениях** — подписка webhook-URL на события обновления расписания
- **OpenAPI/Swagger UI** — документация API через `springdoc-openapi`

## Технологический стек

- Java 25
- Spring Boot 4.1.1 (Web MVC, Data JPA, Security, Validation, Scheduling)
- PostgreSQL (основная БД), H2
- JWT (`jjwt`)
- Jsoup (парсинг HTML)
- Apache PDFBox (парсинг PDF)
- Lombok
- Maven

## Требования

- **JDK 25** (см. `java.version` в `pom.xml`)
- **PostgreSQL** (или совместимая БД)
- **Maven** (или Maven Wrapper `mvnw` / `mvnw.cmd`)

## Конфигурация

Основные настройки находятся в `src/main/resources/application.properties`:

| Параметр                                 | Описание                                                |
|------------------------------------------|---------------------------------------------------------|
| `spring.datasource.url`                  | JDBC URL, формируется как `jdbc:postgresql://${DB_URL}` |
| `spring.datasource.username`             | Пользователь БД (`${DB_USER}`)                          |
| `spring.datasource.password`             | Пароль БД (`${DB_PASSWORD}`)                            |
| `jwt.secret`                             | Секрет для подписи JWT (`${JWT_SECRET}`)                |
| `server.port`                            | Порт приложения (по умолчанию `16311`)                  |
| `spring.jpa.hibernate.ddl-auto`          | `update`                                                |
| `spring.servlet.multipart.max-file-size` | Максимальный размер PDF (`20MB`)                        |

### Переменные окружения

Перед запуском необходимо задать:

```bash
DB_URL=<host:port/database>
DB_USER=<username>
DB_PASSWORD=<password>
JWT_SECRET=<secret>
```

> Примечание: `DB_URL` подставляется в строку `jdbc:postgresql://${DB_URL}` — передавайте значение в формате, ожидаемом после префикса `jdbc:postgresql://` (например, `localhost:5432/aktt`).

## Сборка и запуск

### Сборка

```bash
# Windows
mvnw.cmd clean package

# Linux / macOS
./mvnw clean package
```

### Запуск

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

Или запуск собранного JAR:

```bash
java -jar target/AkttAPI-Springed-3.0.0.jar
```

По умолчанию приложение будет доступно на `http://localhost:16311`.

## API

Базовый префикс: `/api/v1`

### Расписание (`/api/v1/schedule`)

#### `GET /api/v1/schedule`
Получить расписание по группе или преподавателю.

**Query-параметры (`ScheduleRequest`):**
- `value` *(string, required)* — название группы или имя преподавателя
- `target` *(enum, required)* — `GROUP` или `TEACHER`
- `scheduleDate` *(date, optional, ISO `YYYY-MM-DD`)* — дата расписания; если не указана, возвращается последнее доступное расписание
- `subgroup` *(enum, optional)* — `FIRST`, `SECOND`, `BOTH` (по умолчанию `BOTH`; учитывается при запросе по группе)

**Пример ответа (`ScheduleResponse`):**
```json
{
  "scheduleDate": "2026-11-11",
  "scheduleDateStatus": "REAL",
  "lessons": [
    {
      "groupName": "25-41авт",
      "teacherName": "Иванов И.И.",
      "lessonTime": "8:30 - 10:00",
      "lessonTimeType": "FIRST",
      "subjectName": "Математика",
      "classroom": "12",
      "subgroup": "BOTH",
      "lessonType": "NORMAL"
    }
  ]
}
```

> `scheduleDateStatus`: `REAL` — дата распознана из документа, `ASSUMED` — дата определена эвристически.

#### `GET /api/v1/schedule/groups`
Возвращает список названий всех известных групп.

#### `GET /api/v1/schedule/teachers`
Возвращает список имён всех известных преподавателей.

#### `POST /api/v1/schedule/pdf`
Загрузить PDF-файл с расписанием для парсинга и сохранения.

- **Доступ:** требуется JWT и роль `ADMIN` или `SCHEDULER`
- **Content-Type:** `multipart/form-data`
- **Параметры:**
  - `file` *(file, required)* — PDF-документ
  - `manualDate` *(date, optional, `YYYY-MM-DD`)* — дата расписания вручную (используется, если дату не удалось определить из PDF)

### Аутентификация (`/api/v1/auth`)

#### `POST /api/v1/auth/register`
Регистрация пользователя.

```json
{
  "login": "user",
  "password": "password123"
}
```

- `login`: 1–255 символов, не пустой
- `password`: минимум 8 символов

#### `POST /api/v1/auth/login`
Аутентификация и получение JWT.

```json
{
  "login": "user",
  "password": "password123"
}
```

**Ответ:**
```json
{
  "token": "<jwt>",
  "expiresIn": 86400
}
```

### Уведомления (`/api/v1/notification`)

#### `PUT /api/v1/notification`
Создать или обновить webhook-подписку на обновления расписания.

```json
{
  "callbackUrl": "https://example.com/webhook",
  "mode": "ONLY_NEW"
}
```

- `callbackUrl` — URL, начинающийся с `http://` или `https://`
- `mode` — режим подписки (`ALL` или `ONLY_NEW`)

## Безопасность

- Аутентификация: **JWT (Bearer token)**, stateless-сессии
- `POST /api/v1/schedule/pdf` — доступен только пользователям с ролью `ADMIN` или `SCHEDULER`
- Остальные эндпоинты по умолчанию доступны без аутентификации (см. `SecurityConfig`)
- CSRF отключён (типично для stateless REST API)

## Как работает парсинг

- **HTML-парсер (`HTMLScheduleParser`)** по расписанию опрашивает сайт АКТТ, извлекает метаданные и табличные данные расписания и передаёт их в `ScheduleParserService`.
- **PDF-парсер (`PDFScheduleParser`)** извлекает текст/области из PDF (Apache PDFBox), определяет колонки расписания, группы, время занятий и содержимое пар.
- **`ScheduleParserServiceImpl`** преобразует сырые данные в сущности БД (`Schedule`, `Lesson`, `Group`, `Teacher`), вычисляет время пар (с учётом дня недели/корпуса/сокращённых пар), определяет подгруппы, аудитории и типы занятий, предотвращает дублирование по времени изменения и инициирует рассылку уведомлений подписчикам.

## Структура проекта

```text
src/main/java/me/kveex/akttapispringed/
├── config/         # Конфигурация Spring Security
├── controller/     # REST-контроллеры
├── domain/
│   ├── dto/        # DTO запросов/ответов
│   └── entity/     # JPA-сущности (schedule, user, notification)
├── parser/         # Парсеры расписания (HTML, PDF)
├── repository/     # Spring Data JPA репозитории
├── security/       # JWT-фильтр, UserDetails
└── service/        # Интерфейсы и реализации бизнес-логики
```

## Примечания

- Для работы автоматического HTML-парсинга приложению требуется доступ к сайту `aktt.org`.
- При невозможности определить дату расписания из PDF можно передать `manualDate`; иначе запрос может завершиться ошибкой валидации даты расписания.
- В проекте используется OpenAPI-зависимость (`springdoc-openapi-starter-webmvc-ui`) — после запуска Swagger UI обычно доступен по стандартному пути Springdoc (например, `/swagger-ui/index.html`), если он не переопределён конфигурацией.
- Отдельного `README`/документации по развёртыванию в репозитории ранее не было; при необходимости дополните разделы под вашу инфраструктуру (Docker, CI/CD, production-настройки БД и секретов).
