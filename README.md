Đọc kỹ hướng dẫn trong file này: https://docs.docker.com/desktop/setup/install/windows-install/ để tải Docker Compose về Win

Sau đó clone dự án về, mở terminal, cd PblManagement, đảm bảo dòng trong terminal như này ~/Desktop/repos/PBL3_Manage-PBL-Library/pblManagement

Gõ lệnh: ./mvnw clean package để đảm bảo build hoàn chỉnh không lỗi
Gõ lệnh docker compose up, không bấm nút chạy chương trình vì gõ lệnh đồng nghĩa với chạy luôn rồi

Sau đó mở Google và nhập link http://localhost:8080/swagger-ui/index.html để truy cập Swagger UI
Ở dưới chữ OpenAPI definition ở góc trái màn hình có dòng chữ nhỏ: /v3/api_docs. Nhấp chuột vào dòng đó. Chọn pretty-print
Những gì trong trang này là documentation cho các REST API endpoint, dùng nó để đưa vào cho AI nhanh chóng build frontend

Để dừng chạy Docker thì nhấn Ctrl + C
