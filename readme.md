# C2HTTP plugin

<p align="center">
<a href=https://t.me/lumintoch><img src=https://img.shields.io/badge/Sponsored%20by-Luminto-purple?style=for-the-badge&logo=githubsponsors&logoColor=white></a>
<img src="https://img.shields.io/badge/Paper-blue?style=for-the-badge&logo=spigotmc&logoColor=white&logoSize=auto" alt="Badge">
<img src="https://img.shields.io/badge/Java-orange?style=for-the-badge&logo=openjdk&logoSize=auto" alt="Badge">
</p>

## EN | **[Русская версия README](readme-ru.md)**

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

- `/c2h reload` - reload the config (requires `c2h.reload` permission).
- `/c2h send ip json` - send post (requires `c2h.send` permission).

### Permissions

- `c2h.reload` - access to reload.
- `c2h.send` - access to /c2h send

### Handling example

Uses `aiohttp`

```python
async def minecraft(request: aiohttp.web.Request):
    data = await request.post()
    if data.get("password") != config.tokens.chattohttp:
        logger.info("Invalid password")
        return aiohttp.web.Response(text="Password is not valid", status=401)
    nick = data.get("nick")
    message = data.get("message")
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
