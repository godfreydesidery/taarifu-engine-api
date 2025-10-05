# Self-Registration API Documentation

## Overview
The self-registration API allows persons to register themselves in the system by providing their personal details. The system automatically creates a profile, user account, and citizen record, then returns login credentials.

## Endpoint
```
POST /mob/v1/profiles/register
```

## Request Body
```json
{
  "name": "John Doe",
  "displayName": "John",
  "email": "john.doe@example.com",
  "phoneNumber": "+1234567890",
  "dateOfBirth": "1990-01-15T00:00:00",
  "gender": "Male",
  "idType": "NATIONAL_ID",
  "idNumber": "1234567890",
  "address": "123 Main St, City, Country",
  "bio": "Software developer with 5 years experience"
}
```

### Required Fields
- `name`: Full name of the person
- `email`: Valid email address (will be used as login)
- `idType`: Type of ID document (NATIONAL_ID, PASSPORT, etc.)
- `idNumber`: ID number

### Optional Fields
- `displayName`: Display name for the profile
- `phoneNumber`: Contact phone number
- `dateOfBirth`: Date of birth
- `gender`: Gender
- `address`: Physical address
- `bio`: Personal biography

## Response
```json
{
  "message": "Registration successful! Please check your email for login credentials.",
  "profileUid": "01HZ9K8M7N6P5Q4R3S2T1U0V9W8X7Y6Z5",
  "username": "johndoe",
  "email": "john.doe@example.com",
  "registrationDate": "2024-01-15T10:30:00",
  "requirePasswordChange": true,
  "loginInstructions": "Please check your email for your temporary password and change it after first login for security."
}
```

## What Happens During Registration

1. **Username Generation**: Username is generated from email (part before @)
2. **Password Generation**: A secure temporary password is generated
3. **Profile Creation**: PERSON profile is created with provided details
4. **User Account**: User account is created with generated credentials
5. **Citizen Record**: Citizen record is automatically created (since profile type is PERSON)
6. **Email Notification**: Welcome email with temporary password is sent to user's email
7. **Response**: Registration confirmation with username is returned

## Security Features

- **Temporary Password**: Strong 12-character password with mixed case, numbers, and special characters
- **Password Change Required**: User must change password on first login
- **Secure Password Storage**: Passwords are hashed using BCrypt
- **No Password in Response**: Temporary password is not returned in API response
- **Email Delivery**: Password is sent via email (production) or stored in raw_password field (testing)
- **Unique Username**: If username exists, numbers are appended automatically
- **Email Validation**: Email format is validated
- **ID Validation**: ID type and number are validated for uniqueness

## Usage Example

```bash
curl -X POST http://localhost:8080/mob/v1/profiles/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Smith",
    "email": "jane.smith@example.com",
    "idType": "NATIONAL_ID",
    "idNumber": "9876543210",
    "phoneNumber": "+1234567890",
    "address": "456 Oak Ave, City, Country"
  }'
```

## Error Handling

The API returns appropriate HTTP status codes:
- `201 Created`: Registration successful
- `400 Bad Request`: Invalid input data or registration failed
- `409 Conflict`: Email or ID already exists

## Integration Notes

- **Public Access**: This endpoint is publicly accessible (no authentication required)
- **Security Configuration**: Endpoint is explicitly permitted in SecurityConfig
- **Automatic Integration**: Uses existing ProfileService and CitizenService
- **Transaction Safety**: Creates all necessary records in a single transaction
- **Secure Response**: No sensitive credentials returned in API response
- **Email Integration**: Welcome email with credentials sent via EmailService
- **Error Resilience**: Registration succeeds even if email fails (user can still login)
- **CORS Enabled**: Supports cross-origin requests for mobile/web applications

## Email Features

- **Welcome Email**: Professional HTML email template sent to new users
- **Credentials Included**: Username and temporary password included in email
- **Security Instructions**: Clear instructions for password change and security
- **Email Type**: Uses `WELCOME` email type for proper categorization
- **Async Processing**: Email sending doesn't block registration process

## Testing Notes

For testing purposes, the temporary password is stored in the `raw_password` field of the User entity. The email is also sent with the same credentials.

To retrieve the password for testing:
```sql
SELECT username, email, raw_password FROM users WHERE email = 'user@example.com';
```
