# Configuration Guide

This guide explains how to configure the Taarifu Engine API using environment variables for secure credential management.

## Overview

All sensitive credentials (database, email, JWT secrets, etc.) are externalized to environment variables. This ensures:
- ✅ No credentials in version control
- ✅ Easy credential rotation
- ✅ Environment-specific configuration
- ✅ Secure production deployments

## Required Environment Variables

### Database Configuration

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `DB_URL` | Full JDBC connection URL | `jdbc:mysql://localhost:3306/taarifu_engine_db_test?...` | No |
| `DB_USERNAME` | Database username | `root` | No (for local) |
| `DB_PASSWORD` | Database password | `rootroot` | No (for local) |

**Example:**
```bash
DB_URL=jdbc:mysql://localhost:3306/taarifu_engine_db_test?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true
DB_USERNAME=root
DB_PASSWORD=your-database-password
```

### Email Configuration (SMTP)

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `MAIL_HOST` | SMTP server hostname | `smtp-relay.brevo.com` | No |
| `MAIL_PORT` | SMTP server port | `587` | No |
| `MAIL_USERNAME` | SMTP username | - | **Yes** |
| `MAIL_PASSWORD` | SMTP password | - | **Yes** |
| `MAIL_FROM` | From email address | `no-reply@otapp.live` | No |

**Example:**
```bash
MAIL_HOST=smtp-relay.brevo.com
MAIL_PORT=587
MAIL_USERNAME=your-email@smtp-brevo.com
MAIL_PASSWORD=your-smtp-password
MAIL_FROM=no-reply@otapp.live
```

### JWT Configuration

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `JWT_SECRET` | Base64-encoded JWT signing secret | - | **Yes** |
| `JWT_ACCESS_TOKEN_EXPIRATION` | Access token expiration (seconds) | `36000` (10 hours) | No |
| `JWT_REFRESH_TOKEN_EXPIRATION` | Refresh token expiration (seconds) | `604800` (7 days) | No |

**Generating JWT_SECRET:**
```bash
# Using OpenSSL
openssl rand -base64 32

# Using Java
java -cp . -c "import java.util.Base64; import java.security.SecureRandom; byte[] bytes = new byte[32]; new SecureRandom().nextBytes(bytes); System.out.println(Base64.getEncoder().encodeToString(bytes));"
```

**Example:**
```bash
JWT_SECRET=VGhhcmlmdUVuZ2luZUFQSjIwMjRTZWN1cmVKV1RTZWNyZXRLZXlGb3JBdXRoZW50aWNhdGlvblB1cnBvc2VzT25seQ==
JWT_ACCESS_TOKEN_EXPIRATION=36000
JWT_REFRESH_TOKEN_EXPIRATION=604800
```

### Root Admin Configuration (Local Development)

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `ROOT_ADMIN_USERNAME` | Root admin username | `rootadmin` | No |
| `ROOT_ADMIN_PASSWORD` | Root admin password | `RootAdmin@2024!Secure` | No |
| `ROOT_ADMIN_EMAIL` | Root admin email | `root@email.com` | No |

**Note:** These are only used when initializing the first admin user. In production, set strong values.

**Example:**
```bash
ROOT_ADMIN_USERNAME=rootadmin
ROOT_ADMIN_PASSWORD=YourStrongPassword@2024!
ROOT_ADMIN_EMAIL=admin@yourdomain.com
```

### SMS Configuration (Optional)

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `SMS_PROVIDER` | SMS provider name | `beem` | No |
| `SMS_USERNAME` | SMS provider API key/username | - | Yes (if SMS enabled) |
| `SMS_PASSWORD` | SMS provider API secret/password | - | Yes (if SMS enabled) |
| `SMS_SENDER_ID` | SMS sender ID | `OTAPP` | No |
| `SMS_ENABLED` | Enable/disable SMS | `false` | No |

**Beem Africa Setup:**
1. Sign up for a Beem Africa account at https://beem.africa
2. Get your API Key and Secret Key from the dashboard
3. Register a sender ID (short code or alphanumeric)
4. Configure the environment variables

**Example:**
```bash
SMS_PROVIDER=beem
SMS_USERNAME=your-beem-api-key
SMS_PASSWORD=your-beem-api-secret
SMS_SENDER_ID=OTAPP
SMS_ENABLED=true
```

**Note:** 
- SMS is disabled by default (`SMS_ENABLED=false`)
- When SMS is disabled, SMS sending is skipped but user creation continues normally
- Phone numbers are automatically formatted to international format (+255XXXXXXXXX for Tanzania)
- SMS failures do not break user creation flows

## Setting Environment Variables

### Windows

#### Command Prompt
```cmd
set MAIL_USERNAME=your-email@smtp-brevo.com
set MAIL_PASSWORD=your-password
set JWT_SECRET=your-jwt-secret
```

#### PowerShell
```powershell
$env:MAIL_USERNAME="your-email@smtp-brevo.com"
$env:MAIL_PASSWORD="your-password"
$env:JWT_SECRET="your-jwt-secret"
```

#### Using .env File (Recommended)
1. Copy `.env.example` to `.env` in the project root
2. Fill in your actual values
3. Use a tool like `dotenv` or load manually in your IDE

### Linux / macOS

#### Bash/Zsh
```bash
export MAIL_USERNAME=your-email@smtp-brevo.com
export MAIL_PASSWORD=your-password
export JWT_SECRET=your-jwt-secret
```

#### Using .env File (Recommended)
1. Copy `.env.example` to `.env` in the project root
2. Fill in your actual values
3. Source the file: `source .env`
4. Or use `export $(cat .env | xargs)`

### IntelliJ IDEA

1. Go to **Run** → **Edit Configurations**
2. Select your Spring Boot configuration
3. Under **Environment variables**, click the folder icon
4. Add each variable with its value
5. Or use **Environment file** option to load from `.env`

### Eclipse

1. Right-click your project → **Run As** → **Run Configurations**
2. Select your Spring Boot configuration
3. Go to **Environment** tab
4. Add each variable with its value

### VS Code

1. Install the "DotENV" extension
2. Create `.env` file in project root
3. The extension will automatically load variables
4. Or use launch.json:
```json
{
  "configurations": [{
    "envFile": "${workspaceFolder}/.env"
  }]
}
```

## Docker / Container Setup

### Docker Compose

```yaml
version: '3.8'
services:
  taarifu-api:
    image: taarifu-engine-api:latest
    environment:
      - DB_URL=jdbc:mysql://db:3306/taarifu_engine_db
      - DB_USERNAME=taarifu_user
      - DB_PASSWORD=${DB_PASSWORD}
      - MAIL_USERNAME=${MAIL_USERNAME}
      - MAIL_PASSWORD=${MAIL_PASSWORD}
      - JWT_SECRET=${JWT_SECRET}
    env_file:
      - .env
```

### Kubernetes

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: taarifu-secrets
type: Opaque
stringData:
  DB_PASSWORD: your-db-password
  MAIL_USERNAME: your-email
  MAIL_PASSWORD: your-smtp-password
  JWT_SECRET: your-jwt-secret
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: taarifu-api
spec:
  template:
    spec:
      containers:
      - name: taarifu-api
        envFrom:
        - secretRef:
            name: taarifu-secrets
```

## Production Deployment

### Best Practices

1. **Never commit `.env` files** - They are already in `.gitignore`
2. **Use secrets management services:**
   - AWS Secrets Manager
   - HashiCorp Vault
   - Azure Key Vault
   - Kubernetes Secrets
3. **Rotate credentials regularly**
4. **Use different credentials per environment**
5. **Restrict access to production credentials**

### Environment-Specific Configuration

Create separate `.env` files for each environment:
- `.env.local` - Local development
- `.env.dev` - Development environment
- `.env.staging` - Staging environment
- `.env.prod` - Production environment

**Note:** All `.env*` files (except `.env.example`) are ignored by git.

## Verification

After setting environment variables, verify they are loaded:

1. **Check application logs** - Look for configuration loading messages
2. **Use Spring Boot Actuator** - Access `/actuator/env` (if enabled)
3. **Test email sending** - Try the forgot password endpoint
4. **Test database connection** - Check application startup logs

## Troubleshooting

### Variables Not Loading

1. **Check variable names** - Must match exactly (case-sensitive)
2. **Check .env file location** - Should be in project root
3. **Restart application** - Environment variables are loaded at startup
4. **Check IDE settings** - Ensure IDE is configured to load variables

### Missing Required Variables

If required variables are missing, the application will fail to start with clear error messages:
- `MAIL_USERNAME` and `MAIL_PASSWORD` are required
- `JWT_SECRET` is required

### Default Values

Some variables have defaults for local development:
- Database: `root/rootroot` (local only)
- Root admin: `rootadmin/RootAdmin@2024!Secure` (local only)

**Never use defaults in production!**

## Security Notes

1. **JWT_SECRET** - Must be at least 32 bytes, base64-encoded
2. **Database passwords** - Use strong, unique passwords
3. **SMTP passwords** - Use app-specific passwords when available
4. **Root admin password** - Change immediately after first login
5. **Credential rotation** - Rotate all credentials regularly

## Additional Resources

- [Spring Boot Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [12-Factor App: Config](https://12factor.net/config)
- [Environment Variables Best Practices](https://www.twilio.com/blog/environment-variables-java)

