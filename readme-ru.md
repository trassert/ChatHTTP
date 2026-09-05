# C2HTTP plugin

<p align="center">
<a href=https://t.me/lumintoch><img src=https://img.shields.io/badge/Sponsored%20by-Luminto-purple?style=for-the-badge&logo=githubsponsors&logoColor=white></a>
<img src="https://img.shields.io/badge/Paper-blue?style=for-the-badge&logo=spigotmc&logoColor=white&logoSize=auto" alt="Badge">
<img src="https://img.shields.io/badge/Java-orange?style=for-the-badge&logo=openjdk&logoSize=auto" alt="Badge">
</p>

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
- `/c2h send айпи json` - отправить POST (требуется `c2h.send`)

### Права

- `c2h.reload` — доступ к перезагрузке.
- `c2h.send` - доступ к `/c2h send`

### Пример обработки

Используется `aiohttp`

```python
async def minecraft(request: aiohttp.web.Request):
    data = await request.post()
    if data.get("password") != config.tokens.chattohttp:
        logger.info("Неверный пароль")
        return aiohttp.web.Response(text="Password is not valid", status=401)
    nick = data.get("nick")
    message = data.get("message")
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
