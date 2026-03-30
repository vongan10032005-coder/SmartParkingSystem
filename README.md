# 🚗 Smart Parking System

Hệ thống quản lý và nhận diện biển số xe thông minh

## Công nghệ sử dụng

### Android
- Kotlin + Jetpack Compose
- Clean Architecture + MVVM
- CameraX + ML Kit Text Recognition (AI On-device)
- Hilt (Dependency Injection)
- Room Database (Offline cache)
- Retrofit + OkHttp (REST API)
- WorkManager (Offline sync)
- Coroutines + StateFlow

### Backend
- Laravel 10 + PHP 8
- MySQL
- Laravel Sanctum (JWT Auth)

## Cấu trúc Database

### Bảng `users`
| Column | Type | Mô tả |
|--------|------|-------|
| id | bigint PK | ID người dùng |
| name | varchar | Tên |
| email | varchar | Email đăng nhập |
| password | varchar | Mật khẩu đã hash |

### Bảng `vehicle_records`
| Column | Type | Mô tả |
|--------|------|-------|
| id | bigint PK | ID bản ghi |
| license_plate | varchar(20) | Biển số xe |
| entry_time | bigint | Thời gian vào (Unix ms) |
| exit_time | bigint NULL | Thời gian ra (Unix ms) |
| fee | double NULL | Phí gửi xe |
| user_id | bigint FK | ID nhân viên |
| is_synced | boolean | Đã sync lên server |

## API Endpoints

| Method | Endpoint | Mô tả | Auth |
|--------|----------|-------|------|
| POST | /api/auth/login | Đăng nhập | ❌ |
| POST | /api/auth/logout | Đăng xuất | ✅ |
| POST | /api/vehicles/entry | Ghi nhận xe vào | ✅ |
| PUT | /api/vehicles/{id}/exit | Ghi nhận xe ra | ✅ |
| GET | /api/vehicles/history | Lịch sử xe | ✅ |
| POST | /api/vehicles/sync | Sync offline data | ✅ |

## Luồng dữ liệu
```
Camera → ML Kit OCR → Regex Filter → ViewModel
→ UseCase → Repository → Room DB (local)
                      → Retrofit API (remote)
WorkManager → Sync offline data khi có mạng
```

## Cách chạy

### Backend
```bash
cd SmartParkingBackend
php artisan serve --host=0.0.0.0 --port=8000
```

### Android
1. Mở Android Studio
2. Đổi IP trong `NetworkModule.kt`
3. Nhấn Run

## Thành viên nhóm

| Thành viên           | MSSV       | Phụ trách |
|----------------------|------------|-----------|
| Võ Thị Ngân          | 3120223126 | Android App |
| Nguyễn Thục Linh Nhi | 3120223142 | Backend Laravel |