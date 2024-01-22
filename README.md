# Homework HTTPS

## Key-pair Generation

Key-pair generation steps using `keytool`.

```shell
keytool -genkeypair -alias localhost -keyalg RSA -keysize 2048 -keystore localhost.jks -validity 3650
```
