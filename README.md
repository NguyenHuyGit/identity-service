# Identity Service

## Giới thiệu

Identity Service là một dự án Spring Boot cung cấp các API xác thực và quản lý người dùng. Dự án sử dụng JWT để bảo vệ các endpoint, hỗ trợ đăng ký, đăng nhập, kiểm tra token, và phân quyền người dùng.

## Tính năng chính

- Đăng ký tài khoản người dùng mới
- Đăng nhập và sinh JWT token
- Kiểm tra tính hợp lệ của token (introspect)
- Phân quyền truy cập dựa trên vai trò (role)
- Bảo vệ API bằng Spring Security và JWT
- Lưu trữ dữ liệu người dùng với MySQL

## Công nghệ sử dụng

- Java 17+
- Spring Boot 3
- Spring Security
- JWT (Nimbus JOSE + JWT)
- MapStruct
- Lombok
- MySQL

## Cấu hình

Thêm vào file `application.yaml`:
```
spring.datasource.url=jdbc:mysql://localhost:3306/identity_db
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password

jwt.signerKey=your_secret_key
```

## Khởi động dự án

1. Cài đặt MySQL và tạo database.
2. Cấu hình thông tin kết nối trong `application.xaml`.
3. Chạy lệnh:
   ```
   mvn spring-boot:run
   ```
4. Truy cập các API qua địa chỉ: `http://localhost:8080`
