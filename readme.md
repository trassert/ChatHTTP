### C2HTTP plugin

![Issues](https://img.shields.io/github/issues-raw/trassert/ChatHTTP?color=c78aff&label=issues&style=for-the-badge)
![Contributors](https://img.shields.io/github/contributors/trassert/ChatHTTP?color=c78aff&label=contributors&style=for-the-badge)
![Lines](https://img.shields.io/endpoint?url=https://ghloc.vercel.app/api/trassert/ChatHTTP/badge?style=flat&logoColor=white&color=c78aff&style=for-the-badge)
![Commit Activity](https://img.shields.io/github/commit-activity/m/trassert/ChatHTTP?color=c78aff&label=commits&style=for-the-badge)
![Last Commit](https://img.shields.io/github/last-commit/trassert/ChatHTTP?color=c78aff&label=last%20commit&style=for-the-badge)

**[Русская версия README](readme-ru.md)**

## EN

### What is this plugin?

Intercepts player messages and sends them to the specified webhook URL via a POST request (parameters: `nick`, `message`, `password`).

### How does it work?

Intercepts player messages and sends them to the specified webhook URL via a POST request (parameters: nick, message, password).

### Configuration `config.yml`

```yaml
webhook-url: "https://your-server/endpoint"
password: "your_password"
# optional: no-permission-message, config-reloaded, main-text
```

### Commands

- `/c2h reload` — reload the config (requires `c2h.reload` permission).

### Permissions

- `c2h.reload` — access to reload.

### Handling example

Uses `aiohttp`

```python
async def minecraft(request: aiohttp.web.Request):
    data = await request.post()
    if data.get("password") != config.tokens.chattohttp:
        logger.info("Invalid password")
        return aiohttp.web.Response(text="Password is not valid", status=401)
    nick = data.get("nick")
    if not formatter.is_valid_mc_nick(nick):
        return aiohttp.web.Response(text="Nick is not valid", status=406)
    logger.info(f"{nick} said: {message}")
    return aiohttp.web.Response(text="ok")
```

### Building

Install Maven and run  
`mvn clean package`

### Versioning

The plugin uses SemVer - `<major>.<minor>.<patch>`
- **major** - incompatible changes
- **minor** - compatible feature changes
- **patch** - non-functional changes (bugfixes)
