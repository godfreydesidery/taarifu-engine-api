# Taarifu Engine API

A comprehensive Spring Boot REST API for managing political and administrative data in Tanzania. This system provides endpoints for managing locations, political parties, parliaments, and user administration.

## 🚀 Features

### Location Management
- **Hierarchical Location Structure**: Region → District → Ward → Village → Hamlet
- **Constituency Management**: Constituencies belong to Districts
- **Area System**: Generic area management with type-based categorization
- **CRUD Operations**: Full create, read, update, delete functionality
- **Search & Filtering**: Advanced search capabilities across all location types
- **Statistics**: Comprehensive statistics and analytics

### Political Management
- **Political Party Management**: Complete CRUD operations for political parties
- **Parliament Management**: Session-based parliament management
- **Status Tracking**: Active/inactive, registered/unregistered status management
- **Member Tracking**: Political party membership management

### User & Security
- **JWT Authentication**: Secure token-based authentication
- **Role-based Access Control**: Admin and user role management
- **User Management**: Complete user administration system
- **Password Security**: Strong password requirements and validation

## 🏗️ Architecture

### Technology Stack
- **Java 17+**
- **Spring Boot 3.x**
- **Spring Security**
- **Spring Data JPA**
- **MySQL Database**
- **Maven**
- **JWT Authentication**

### Project Structure
```
src/main/java/com/taarifu_engine_api/
├── api/
│   └── admin/                    # Admin REST controllers
├── config/                       # Configuration classes
├── controller/                   # General controllers
└── modules/
    ├── auth/                     # Authentication module
    ├── common/                   # Common utilities and DTOs
    ├── location/                 # Location management
    │   ├── area/                 # Generic area management
    │   ├── constituency/         # Constituency management
    │   ├── district/             # District management
    │   ├── hamlet/               # Hamlet management
    │   ├── region/               # Region management
    │   ├── village/              # Village management
    │   └── ward/                 # Ward management
    ├── parliament/               # Parliament management
    ├── politicalparty/           # Political party management
    └── userandrole/              # User and role management
```

## 📋 API Endpoints

### Location Management
- **Regions**: `/admin/v1/regions`
- **Districts**: `/admin/v1/districts`
- **Wards**: `/admin/v1/wards`
- **Villages**: `/admin/v1/villages`
- **Hamlets**: `/admin/v1/hamlets`
- **Constituencies**: `/admin/v1/constituencies`
- **Areas**: `/admin/v1/areas`

### Political Management
- **Political Parties**: `/admin/v1/political-parties`
- **Parliaments**: `/admin/v1/parliaments`

### Authentication
- **Login**: `/api/v1/auth/login`
- **Admin Auth**: `/admin/v1/auth/login`

## 🔧 Setup & Installation

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Git
- Environment variables configured (see [Configuration Setup](#-configuration-setup))

### Configuration Setup

This application uses environment variables for all sensitive credentials. This ensures:
- ✅ No credentials in version control
- ✅ Easy credential rotation
- ✅ Environment-specific configuration

**Quick Start:**
1. Copy `.env.example` to `.env` in the project root
2. Fill in required environment variables:
   ```bash
   # Required
   MAIL_USERNAME=your-email@smtp-brevo.com
   MAIL_PASSWORD=your-smtp-password
   JWT_SECRET=$(openssl rand -base64 32)
   
   # Optional (defaults provided for local development)
   DB_USERNAME=root
   DB_PASSWORD=rootroot
   ```

**For detailed configuration instructions, see [CONFIGURATION.md](CONFIGURATION.md)**

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/godfreydesidery/taarifu-engine-api.git
   cd taarifu-engine-api
   ```

2. **Configure Environment Variables**
   - Copy `.env.example` to `.env` in the project root
   - Fill in all required environment variables (see [CONFIGURATION.md](CONFIGURATION.md) for details)
   - **Required variables:**
     - `MAIL_USERNAME` - SMTP email username
     - `MAIL_PASSWORD` - SMTP email password
     - `JWT_SECRET` - JWT signing secret (generate with: `openssl rand -base64 32`)
   - **Optional variables** (have defaults for local development):
     - `DB_USERNAME` - Database username (default: `root`)
     - `DB_PASSWORD` - Database password (default: `rootroot`)
     - `MAIL_HOST` - SMTP host (default: `smtp-relay.brevo.com`)
     - `MAIL_PORT` - SMTP port (default: `587`)
   
   For detailed configuration instructions, see [CONFIGURATION.md](CONFIGURATION.md)

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the API**
   - API Base URL: `http://localhost:8080`
   - Health Check: `http://localhost:8080/api/v1/health`

### Database Migrations

Before deploying updates, check the `migrations/` directory for any required SQL scripts.

**Current Migration:**
- `migrations/001_fix_user_version_field.sql` - Fixes NULL version values in users table
  - **When to run**: Before deploying code that initializes User version field to 0L
  - **Command**: `mysql -u [username] -p [database_name] < migrations/001_fix_user_version_field.sql`
  - **What it does**: Updates all existing User records with NULL version values to 0

## 🔐 Authentication

### Default Admin User
The system automatically creates a root admin user on first startup (only if no users exist):
- **Username**: `rootadmin` (configurable via `ROOT_ADMIN_USERNAME`)
- **Email**: `root@email.com` (configurable via `ROOT_ADMIN_EMAIL`)
- **Password**: `RootAdmin@2024!Secure` (configurable via `ROOT_ADMIN_PASSWORD`)
- **⚠️ Important**: Change the root password immediately after first login!

### JWT Token
Include the JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

## 📊 Data Models

### Location Hierarchy
```
Region (RG000001)
├── District (DT000001)
│   ├── Ward (WD000001)
│   │   ├── Village (VL0000001)
│   │   │   └── Hamlet (HM0000001)
│   │   └── Village (VL0000002)
│   └── Constituency (CT000001)
└── District (DT000002)
```

### Code Formats
- **Region**: `RG000001` (6 digits)
- **District**: `DT000001` (6 digits)
- **Ward**: `WD000001` (6 digits)
- **Village**: `VL0000001` (7 digits)
- **Hamlet**: `HM0000001` (7 digits)
- **Constituency**: `CT000001` (6 digits)
- **Area**: `AR00000000001` (11 digits)
- **Political Party**: `PP000001` (6 digits)
- **Parliament**: `PT000001` (6 digits)

## 🧪 Testing

### API Testing
Use tools like Postman or curl to test the API endpoints:

```bash
# Health check
curl http://localhost:8080/api/v1/health

# Login
curl -X POST http://localhost:8080/admin/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"rootadmin","password":"admin123"}'
```

## 📝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **Godfrey Desidery** - *Initial work* - [godfreydesidery](https://github.com/godfreydesidery)

## 🙏 Acknowledgments

- Spring Boot community
- Tanzania administrative structure reference
- Open source contributors

## 📞 Support

For support, email support@taarifu.com or create an issue in this repository.

---

**Taarifu Engine API** - Empowering Tanzania's digital governance infrastructure.
