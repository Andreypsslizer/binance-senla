# Binance Price Service

Сервис периодически получает среднюю цену торговой пары через `GET /api/v3/avgPrice`, сохраняет каждый успешный снимок в PostgreSQL и предоставляет REST API для чтения истории.

## Запуск

Требуется Docker и Docker Compose:

```bash
cp .env.example .env
# при необходимости измените пароль и интервал опроса
docker compose up --build -d
```

Приложение: `http://localhost:8080`. Проверка здоровья: `GET /actuator/health`. Миграции Flyway запускаются автоматически.

Локально без Docker: Java 21+, PostgreSQL, затем `mvn spring-boot:run`.

## Переменные окружения

| Переменная | По умолчанию | Назначение |
|---|---|---|
| `BINANCE_SYMBOL` | `BNBUSDT` | Торговая пара |
| `BINANCE_POLL_INTERVAL` | `PT30S` | Интервал опроса в ISO-8601 Duration |
| `BINANCE_BASE_URL` | `https://api.binance.com` | Базовый URL Binance |
| `BINANCE_CONNECT_TIMEOUT` | `PT3S` | Таймаут соединения |
| `BINANCE_READ_TIMEOUT` | `PT5S` | Таймаут чтения |
| `DB_URL` | `jdbc:postgresql://localhost:5432/binance` | JDBC URL |
| `DB_USERNAME` | `binance` | Пользователь БД |
| `DB_PASSWORD` | `binance` | Пароль БД |
| `SERVER_PORT` | `8080` | HTTP-порт |

## REST API

Все даты передаются как ISO-8601 с часовым поясом, например `2026-09-01T00:00:00Z`. Границы периода включительные: `[from, to]`.

### Последняя сохранённая цена

```http
GET /api/v1/prices/latest
```

```json
{"symbol":"BNBUSDT","price":612.34,"fetchedAt":"2026-09-30T12:00:00Z"}
```

### Средняя цена за период

```http
GET /api/v1/prices/average?from=2026-09-01T00:00:00Z&to=2026-09-30T23:59:59Z
```

```json
{"averagePrice":610.2}
```

### История с пагинацией

```http
GET /api/v1/prices?from=2026-09-01T00:00:00Z&to=2026-09-30T23:59:59Z&page=0&size=20
```

### Тренд

```http
GET /api/v1/prices/trend?from=2026-09-01T00:00:00Z&to=2026-09-30T23:59:59Z
```

Тренд определяется сравнением первой и последней цены в интервале: `UPWARD`, `DOWNWARD`, `FLAT`. Если в интервале меньше двух записей — `INSUFFICIENT_DATA`.

## Ошибки и устойчивость

- Ошибки Binance и БД логируются; исключение не выходит из задачи планировщика, следующий запуск продолжится.
- Неуспешный запрос не создаёт запись; успешные ответы сохраняются отдельными снимками.
- HTTP connect/read timeout ограничивают зависание потока.
- HTTP 429/418 не ретраятся немедленно: планировщик дождётся следующего интервала, не создавая retry storm. В этой версии `Retry-After` не используется для динамического переноса следующего запуска; для production рекомендуется добавить централизованный cooldown по `Retry-After`/`X-MBX-USED-WEIGHT-*` и метрики.
- Время сохраняется как `TIMESTAMPTZ` и сериализуется в UTC.
- `NUMERIC(30,12)` и `BigDecimal` исключают ошибки двоичной арифметики.

## Тесты

```bash
mvn test
```

Есть unit-тесты расчёта тренда/пустого среднего и WireMock-тест клиента Binance.
