# BÀI 4: Tích hợp - Thiết kế ChatMemory bền vững (Persistent Memory) cho Booking Agent

## 1. Giải pháp thiết kế và phân tách phiên chat
Môi trường Cloud phân tán yêu cầu hệ thống phải là **Stateless**. Lịch sử hội thoại không thể nằm ở RAM của từng Pod (InMemoryChatMemory) mà phải được chuyển xuống một kho lưu trữ dùng chung như MySQL. Ta sẽ sử dụng một lớp custom implement `ChatMemory` (thường được gọi là `JdbcChatMemory` hoặc dùng các thư viện mở rộng của Spring AI hỗ trợ Vector DB/SQL) nhận vào một `conversationId` làm định danh phiên làm việc.
Tại Controller, khi request đầu tiên tới chưa có mã Session, ta sinh ra mã UUID để làm `conversationId`. Ở các request sau, Frontend truyền lại UUID này lên, hệ thống sẽ chèn nó vào `ChatMemoryAdvisor` để trích xuất đúng lịch sử cũ của khách hàng đó từ MySQL trước khi gửi lên LLM.

## 2. Mã nguồn Java cấu hình và Controller (đã tích hợp JdbcChatMemory)
Xem các file Java đính kèm: `DatabaseChatMemoryConfig.java` và `BookingController.java`.

## 3. Thuyết minh kiến trúc đồng bộ dữ liệu
Giải pháp này tuân thủ nguyên lý **12-Factor App**. Dữ liệu trạng thái (State) đã được đẩy ra khỏi vùng nhớ cục bộ (RAM) của ứng dụng và đưa vào một Database ACID tập trung. Khi hệ thống scale-out lên 10 hay 100 Pods, Load Balancer có thể gửi request tới bất kỳ Pod nào. Pod đó chỉ cần dùng tham số `conversationId` do Client gửi lên, thực hiện 1 câu lệnh `SELECT` xuống MySQL là lấy được toàn bộ context lịch sử hội thoại, ghép chung vào Prompt để gửi tới OpenAI/Gemini. Khởi động lại Server để deploy code mới cũng không làm ảnh hưởng tới các phiên chat đang dang dở.
