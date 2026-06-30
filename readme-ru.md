### C2HTTP plugin

![Issues](https://img.shields.io/github/issues-raw/trassert/ChatHTTP?color=c78aff&label=issues&style=for-the-badge)
![Contributors](https://img.shields.io/github/contributors/trassert/ChatHTTP?color=c78aff&label=contributors&style=for-the-badge)
![Lines](https://img.shields.io/endpoint?url=https://ghloc.vercel.app/api/trassert/ChatHTTP/badge?style=flat&logoColor=white&color=c78aff&style=for-the-badge)
![Commit Activity](https://img.shields.io/github/commit-activity/m/trassert/ChatHTTP?color=c78aff&label=commits&style=for-the-badge)
![Last Commit](https://img.shields.io/github/last-commit/trassert/ChatHTTP?color=c78aff&label=last%20commit&style=for-the-badge)

## RU

### Что это за плагин?

Перехватывает сообщения игроков и отправляет их на указанный webhook‑URL через POST‑запрос (параметры: `nick`, `message`, `password`).

### Как это работает?

Перехватывает сообщения игроков и отправляет их на указанный webhook‑URL через POST‑запрос (параметры: nick, message, password).

### Настройка `config.yml`

```yaml
webhook-url: "https://ваш-сервер/endpoint"
password: "ваш_пароль"
# опционально: no-permission-message, config-reloaded, main-text
```

### Команды

- `/c2h reload` — перезагрузить конфиг (требуется право `c2h.reload`).

### Права

- `c2h.reload` — доступ к перезагрузке.

### Пример обработки

Используется `aiohttp`

```python
async def minecraft(request: aiohttp.web.Request):
    data = await request.post()
    if data.get("password") != config.tokens.chattohttp:
        logger.info("Неверный пароль")
        return aiohttp.web.Response(text="Password is not valid", status=401)
    nick = data.get("nick")
    if not formatter.is_valid_mc_nick(nick):
        return aiohttp.web.Response(text="Nick is not valid", status=406)
    logger.info(f"{nick} сказал: {message}")
    return aiohttp.web.Response(text="ok")
```

### Сборка

Установите Maven и выполните  
`mvn clean package`

### Версионирование

Плагин использует SemVer - `<major>.<minor>.<patch>`
- **major** - несовместимые изменения
- **minor** - совместимые изменения функционала
- **patch** - изменения без функционала (багфиксы)