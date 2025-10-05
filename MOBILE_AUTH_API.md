# Mobile Authentication API Documentation

## Overview
The Mobile Authentication API provides login, logout, and token refresh functionality for mobile users (citizens). Users can authenticate using their username/email and password received during registration.

## Endpoints

### 1. Login
```
POST /mob/v1/auth/login
```

#### Request Body
```json
{
  "usernameOrEmail": "janesmith",
  "password": "Kj8#mN2pQ9",
  "rememberMe": false
}
```

#### Request Fields
- `usernameOrEmail` (required): Username or email address
- `password` (required): User's password (minimum 8 characters)
- `rememberMe` (optional): Whether to extend session duration

#### Success Response (200 OK)
```json
{
  "uid": "01HZ9K8M7N6P5Q4R3S2T1U0V9W8X7Y6Z5",
  "username": "janesmith",
  "email": "jane.smith@example.com",
  "userType": "USER",
  "status": "ACTIVE",
  "passwordStrength": "STRONG",
  "requirePasswordChange": true,
  "lastLoginAt": "2024-01-15T10:35:00",
  "createdAt": "2024-01-15T10:30:00",
  "designations": ["CITIZEN"],
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600,
  "tokenType": "Bearer"
}
```

#### Error Responses
- `400 Bad Request`: Invalid request data
- `401 Unauthorized`: Invalid credentials
- `403 Forbidden`: Account inactive or wrong user type
- `500 Internal Server Error`: Server error

### 2. Logout
```
POST /mob/v1/auth/logout
```

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Success Response (200 OK)
```json
"Logout successful"
```

### 3. Refresh Token
```
POST /mob/v1/auth/refresh
```

#### Request Body
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### Success Response (200 OK)
```json
{
  "uid": "01HZ9K8M7N6P5Q4R3S2T1U0V9W8X7Y6Z5",
  "username": "janesmith",
  "email": "jane.smith@example.com",
  "userType": "USER",
  "status": "ACTIVE",
  "passwordStrength": "STRONG",
  "requirePasswordChange": true,
  "lastLoginAt": "2024-01-15T10:35:00",
  "createdAt": "2024-01-15T10:30:00",
  "designations": ["CITIZEN"],
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600,
  "tokenType": "Bearer"
}
```

## Usage Examples

### Complete Registration and Login Flow

#### 1. Register a new user
```bash
curl -X POST http://localhost:8080/mob/v1/profiles/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Smith",
    "email": "jane.smith@example.com",
    "idType": "NATIONAL_ID",
    "idNumber": "9876543210"
  }'
```

#### 2. Check email for credentials
The user receives an email with:
- Username: `janesmith`
- Temporary Password: `Kj8#mN2pQ9`

#### 3. Login with credentials
```bash
curl -X POST http://localhost:8080/mob/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "janesmith",
    "password": "Kj8#mN2pQ9"
  }'
```

#### 4. Use access token for authenticated requests
```bash
curl -X GET http://localhost:8080/mob/v1/profiles/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

#### 5. Refresh token when it expires
```bash
curl -X POST http://localhost:8080/mob/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }'
```

#### 6. Logout
```bash
curl -X POST http://localhost:8080/mob/v1/auth/logout \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

## Security Features

- **JWT Tokens**: Secure access and refresh tokens
- **User Type Validation**: Only USER type accounts can login via mobile
- **Account Status Check**: Inactive accounts cannot login
- **Password Change Required**: Users must change temporary passwords
- **Token Expiration**: Access tokens expire for security
- **Refresh Token**: Automatic token renewal without re-login

## Response Fields

### Designations Field
The `designations` field is an array of strings containing user's titles, roles, or designations:

- **Automatic Assignment**: Designations are determined based on user profile and associated records
- **CITIZEN Designation**: Automatically added for USER type users with PERSON profile who have a citizen record
- **Possible Values**: CITIZEN, MP, MP_ASSISTANT, ORGANIZATION, etc.
- **Future Enhancement**: Additional designation checks will be implemented for MP, MP_ASSISTANT, etc.

## Integration Notes

- **Public Access**: Login endpoint is publicly accessible (no authentication required)
- **User Type Restriction**: Only USER type accounts can login via mobile endpoints
- **Token Management**: Access tokens should be stored securely on client side
- **Error Handling**: Comprehensive error messages for different failure scenarios
- **CORS Enabled**: Supports cross-origin requests for mobile/web applications

## Testing Notes

For testing, you can retrieve user credentials from the database:
```sql
SELECT username, email, raw_password FROM users WHERE email = 'user@example.com';
```

The raw_password field contains the temporary password for testing purposes.
