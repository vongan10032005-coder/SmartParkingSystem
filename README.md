# 🚗 Smart Parking System

Hệ thống quản lý và nhận diện biển số xe thông minh sử dụng AI On-device

![Android](https://img.shields.io/badge/Android-API26+-green)
![Laravel](https://img.shields.io/badge/Laravel-10-red)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0-blue)
![CI/CD](https://img.shields.io/badge/CI%2FCD-GitHub_Actions-black)

---

## 👥 Thành viên nhóm

| Thành viên | MSSV | Phụ trách |
|---|---|---|
| [Võ Thị Ngân] | [3120223126] | Android App, Camera AI, Clean Architecture |
| [Nguyễn Thục Linh Nhi] | [3120223142] | Unit Testing, Documentation, Backend Support |

---

## 📱 Tính năng chính

- **Quét biển số tự động** — Camera + ML Kit OCR nhận diện biển số ngay trên thiết bị
- **Ghi nhận xe vào/ra** — Lưu thời gian vào/ra chính xác
- **Tính phí tự động** — 5.000đ/giờ, làm tròn lên
- **Hoạt động offline** — Room DB lưu dữ liệu khi mất mạng
- **Tự động đồng bộ** — WorkManager sync dữ liệu khi có mạng
- **Lịch sử xe ra/vào** — Xem toàn bộ lịch sử với trạng thái sync

---

## 🛠 Công nghệ sử dụng

### Android
| Công nghệ | Mục đích |
|---|---|
| Kotlin + Jetpack Compose | Ngôn ngữ + UI hiện đại |
| Clean Architecture + MVVM | Kiến trúc phân tầng rõ ràng |
| CameraX | Xử lý camera mượt mà |
| ML Kit Text Recognition | AI nhận diện biển số on-device |
| Hilt | Dependency Injection |
| Room Database | Lưu trữ offline |
| Retrofit + OkHttp | Giao tiếp REST API |
| WorkManager | Đồng bộ dữ liệu offline |
| Coroutines + StateFlow | Xử lý bất đồng bộ |
| DataStore | Lưu JWT token |

### Backend
| Công nghệ | Mục đích |
|---|---|
| Laravel 10 + PHP 8 | Framework backend |
| MySQL | Cơ sở dữ liệu |
| Laravel Sanctum | Xác thực JWT token |

---

## 🗄 Cấu trúc Database

### Bảng `users`
| Column | Type | Mô tả |
|---|---|---|
| id | bigint PK | ID người dùng |
| name | varchar(255) | Tên nhân viên |
| email | varchar(255) UNIQUE | Email đăng nhập |
| password | varchar(255) | Mật khẩu bcrypt |
| created_at | timestamp | Thời gian tạo |
| updated_at | timestamp | Thời gian cập nhật |

### Bảng `vehicle_records`
| Column | Type | Mô tả |
|---|---|---|
| id | bigint PK | ID bản ghi |
| license_plate | varchar(20) | Biển số xe |
| entry_time | bigint | Thời gian vào (Unix ms) |
| exit_time | bigint NULL | Thời gian ra (Unix ms) |
| fee | double NULL | Phí gửi xe (đồng) |
| user_id | bigint FK | ID nhân viên ghi nhận |
| is_synced | boolean | Đã sync lên server chưa |
| created_at | timestamp | Thời gian tạo |
| updated_at | timestamp | Thời gian cập nhật |

---

## 🔌 API Endpoints

Base URL: `http://[IP_MÁY_TÍNH]:8000`

### Authentication
| Method | Endpoint | Mô tả | Auth |
|---|---|---|---|
| POST | /api/auth/login | Đăng nhập, nhận token | ❌ |
| POST | /api/auth/logout | Đăng xuất | ✅ Bearer |

### Parking Management
| Method | Endpoint | Mô tả | Auth |
|---|---|---|---|
| POST | /api/vehicles/entry | Ghi nhận xe vào | ✅ Bearer |
| PUT | /api/vehicles/{id}/exit | Ghi nhận xe ra + tính phí | ✅ Bearer |
| GET | /api/vehicles/history | Lấy lịch sử xe | ✅ Bearer |
| POST | /api/vehicles/sync | Sync dữ liệu offline | ✅ Bearer |

### Ví dụ Request/Response

**POST /api/auth/login**
```json
Request:
{
  "email": "admin@parking.com",
  "password": "password123"
}

Response 200:
{
  "token": "1|abc123xyz...",
  "user": {
    "id": 1,
    "name": "Admin Parking",
    "email": "admin@parking.com"
  }
}
```

**POST /api/vehicles/entry**
```json
Request:
{
  "license_plate": "51A-12345",
  "entry_time": 1711900800000
}

Response 201:
{
  "id": 1,
  "license_plate": "51A-12345",
  "entry_time": 1711900800000,
  "exit_time": null,
  "fee": null
}
```

**PUT /api/vehicles/1/exit**
```json
Request:
{
  "exit_time": 1711904400000
}

Response 200:
{
  "id": 1,
  "license_plate": "51A-12345",
  "entry_time": 1711900800000,
  "exit_time": 1711904400000,
  "fee": 5000.0
}
```

---

## 🔄 Luồng dữ liệu
```
[Camera] 
    → [ML Kit OCR] nhận diện văn bản
    → [Regex Filter] lọc biển số Việt Nam (\d{2}[A-Z]\d?[-]\d{4,5})
    → [CameraViewModel] xử lý state
    → [RecordEntryUseCase / RecordExitUseCase] business logic
    → [ParkingRepositoryImpl]
        ├── [Room DB] lưu local ngay lập tức (offline-first)
        └── [Retrofit API] gọi server nếu có mạng
              └── Nếu offline → đánh dấu isSynced = false
                      → [WorkManager] tự động sync sau 15 phút khi có mạng
```

---

## 🏗 Kiến trúc dự án
```
com.nhom.smartparking/
│
├── data/
│   ├── local/
│   │   ├── dao/          ← VehicleRecordDao
│   │   ├── database/     ← AppDatabase
│   │   └── entity/       ← VehicleRecordEntity
│   ├── remote/
│   │   ├── api/          ← ParkingApiService
│   │   ├── dto/          ← Request/Response models
│   │   └── interceptor/  ← AuthInterceptor
│   └── repository/       ← ParkingRepositoryImpl, AuthRepositoryImpl
│
├── domain/
│   ├── model/            ← VehicleRecord
│   ├── repository/       ← ParkingRepository, AuthRepository (interfaces)
│   └── usecase/          ← RecordEntry, RecordExit, Sync, Login
│
├── presentation/
│   ├── ui/
│   │   ├── login/        ← LoginScreen
│   │   ├── dashboard/    ← DashboardScreen
│   │   ├── camera/       ← CameraScreen
│   │   ├── history/      ← HistoryScreen
│   │   └── components/   ← AppNavigation
│   └── viewmodel/        ← LoginVM, CameraVM, HistoryVM
│
├── di/                   ← Hilt Modules
├── worker/               ← SyncWorker
└── util/                 ← Extensions, Constants
```

---

## 🚀 Hướng dẫn chạy dự án

### Yêu cầu
- Android Studio Hedgehog+
- JDK 17
- XAMPP (Apache + MySQL)
- PHP 8.x + Composer

### Backend
```bash
# 1. Vào thư mục backend
cd SmartParkingBackend

# 2. Cài dependencies
composer install

# 3. Tạo file .env
cp .env.example .env
php artisan key:generate

# 4. Cấu hình database trong .env
DB_DATABASE=smart_parking
DB_USERNAME=root
DB_PASSWORD=

# 5. Tạo database và chạy migration
php artisan migrate
php artisan db:seed --class=UserSeeder

# 6. Chạy server
php artisan serve --host=0.0.0.0 --port=8000
```

Tài khoản mặc định:
- Email: `admin@parking.com`
- Password: `password123`

### Android
```bash
# 1. Mở Android Studio
# 2. Tìm IP máy tính: ipconfig (Windows)
# 3. Sửa BASE_URL trong NetworkModule.kt
private const val BASE_URL = "http://[IP_CUA_BAN]:8000/"

# 4. Kết nối điện thoại qua USB, bật USB Debugging
# 5. Nhấn Run (▶️)
```

---

## 🌿 Git Flow
```
main        ← Production ready (protected)
develop     ← Integration branch (protected)  
feature/*   ← Feature branches
```

Quy tắc:
- Không push thẳng lên `main` hoặc `develop`
- Mọi tính năng tạo nhánh `feature/[tên-chức-năng]-[tên-sv]`
- Bắt buộc có PR + 1 người review trước khi merge

---

## ⚙️ CI/CD

GitHub Actions tự động chạy khi push lên `main` hoặc `develop`:
1. Setup JDK 17
2. Cache Gradle
3. Chạy Unit Tests
4. Build Debug APK
5. Upload APK artifact

---

## 🧪 Unit Tests
```bash
# Chạy tất cả tests
./gradlew test

# Xem kết quả
app/build/reports/tests/testDebugUnitTest/index.html
```

Test classes:
- `RecordEntryUseCaseTest` — Test logic ghi nhận xe vào
- `RecordExitUseCaseTest` — Test logic ghi nhận xe ra
- `VehicleRecordTest` — Test domain model