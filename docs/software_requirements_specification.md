# 

# 

# 

# 

# 

# **SOFTWARE REQUIREMENTS SPECIFICATION**

# **OLYMPIC KHOA HỌC CƠ BẢN** 

## 

## 

## **1\. Giới thiệu tổng quan**

### **1.1 Mục đích và Phạm vi tài liệu**

Tài liệu Đặc tả Yêu cầu Phần mềm này được xây dựng nhằm định nghĩa chi tiết về nội dung toàn diện các yêu cầu chức năng và phi chức năng cho Hệ thống Quản lý Olympic Khoa học Cơ bản. Hệ thống được thiết kế đặc biệt để phục vụ Khoa Khoa học Cơ bản, bao gồm các bộ môn Toán học, Vật lý, Hóa học và Ngoại ngữ (Anh, Trung,...), nhằm tạo ra một môi trường trực tuyến thống nhất cho việc ôn luyện, thi đấu và quản lý đội tuyển.1

Mục tiêu cốt lõi của dự án là chuyển đổi số quy trình bồi dưỡng nhân tài, từ phương thức truyền thống sang nền tảng web tích hợp. Tài liệu này đóng vai trò là văn bản pháp lý kỹ thuật giữa đội ngũ phát triển và các bên liên quan, bao gồm Ban chủ nhiệm khoa, Giảng viên và Quản trị viên hệ thống. Nó cung cấp một lộ trình rõ ràng từ thiết kế kiến trúc, cơ sở dữ liệu đến giao diện người dùng, đảm bảo sự đồng bộ trong quá trình triển khai và bảo trì.3

Phạm vi của hệ thống bao gồm 8 phân hệ chính: Quản lý người dùng và phân quyền , Hồ sơ cá nhân, Kho tài liệu số, Hệ thống bài tập và đánh giá, Diễn đàn thảo luận, Quản lý Đội tuyển và Gamification, Hệ thống Hỏi đáp, và Tổ chức Cuộc thi trực tuyến.4

### **1.2 Bối cảnh và Động lực phát triển**

Trong bối cảnh giáo dục hiện đại, việc ứng dụng công nghệ vào thi cử và quản lý học tập đã trở thành xu hướng tất yếu. Các hệ thống quản lý học tập truyền thống thường tập trung vào quản lý khóa học đại trà mà thiếu đi các tính năng đặc thù cho mô hình "Olympic" \- nơi đề cao tính cạnh tranh, sự vinh danh và tinh thần đội nhóm.5

Hệ thống được đề xuất để giải quyết các vấn đề cụ thể:

1. **Phân mảnh dữ liệu:** Tài liệu ôn thi và bài tập hiện nằm rải rác trên nhiều nền tảng.  
2. **Khó khăn trong quản lý đội tuyển:** Việc theo dõi thành tích và quá trình luyện tập của sinh viên giỏi chưa có công cụ chuyên biệt.  
3. **Hạn chế về tương tác:** Sinh viên cần sự hỗ trợ tức thời từ giảng viên hoặc AI thay vì chờ đợi các buổi họp mặt trực tiếp.

### **1.3 Đối tượng sử dụng**

Hệ thống phục vụ bốn nhóm đối tượng chính với các đặc quyền riêng biệt:

* **Student (Sinh viên):** User chính, tham gia học tập và thi đấu.  
* **Teacher (Giảng viên):** Cố vấn chuyên môn (Mentor) và quản lý đội tuyển.  
* **Manager (Quản lý):** Vai trò lai (Hybrid), hỗ trợ giảng viên trong công tác vận hành và cũng có thể tham gia học tập.  
* **Admin (Quản trị viên):** Người chịu trách nhiệm vận hành kỹ thuật và kiểm soát quyền.

**2\. Phân tích Yêu cầu Người dùng và Phân quyền** 

Hệ thống áp dụng mô hình kiểm soát truy cập dựa trên vai trò để đảm bảo tính bảo mật và toàn vẹn dữ liệu.

### **2.1 Ma trận Phân quyền Chi tiết**

Để đảm bảo tính minh bạch trong thiết kế, bảng dưới đây mô tả chi tiết quyền hạn của từng đối tượng đối với các tài nguyên chính của hệ thống.4

### **2.1.1 Quản trị hệ thống**

| Chức năng | ADMIN | TEACHER | MANAGER | STUDENT | Ghi Chú |
| :---: | :---: | :---: | :---: | :---: | ----- |
| Nâng/ hạ quyền |  |  |  |  | Không thể nâng/hạ Admin khác |
| Cấu hình hệ thống |  |  |  |  |    |

### **2.1.2 Tài liệu**

| Chức năng | ADMIN | TEACHER | MANAGER | STUDENT | Ghi Chú |
| :---: | :---: | :---: | :---: | :---: | ----- |
| Xem/tải xuống |  |  |  |  |  |
| Tải lên |  |  |  |  |    |
| Sửa/Xóa |  |  |  |  |  |

### **2.1.3 Bài tập**

| Chức năng | ADMIN | TEACHER | MANAGER | STUDENT | Ghi Chú |
| :---: | :---: | :---: | :---: | :---: | ----- |
| Tạo/Sửa/Xóa |  |  |  |  |  |
| Làm bài |  |  |  |  |    |

### **2.1.4 Đội tuyển**

| Chức năng | ADMIN | TEACHER | MANAGER | STUDENT | Ghi Chú |
| :---: | :---: | :---: | :---: | :---: | ----- |
| Tạo/Xóa |  |  |  |  |  |
| Quản lý thành viên |  |  |  |  |    |
| Bổ nhiệm đội trưởng |  |  |  |  |  |

### **2.1.5 Cuộc thi**

| Chức năng | ADMIN | TEACHER | MANAGER | STUDENT | Ghi Chú |
| :---: | :---: | :---: | :---: | :---: | ----- |
| Tạo/Sửa/Xóa |  |  |  |  |  |
| Tham gia |  |  |  |  |    |

### **2.1.6 Tương tác**

| Chức năng | ADMIN | TEACHER | MANAGER | STUDENT | Ghi Chú |
| :---: | :---: | :---: | :---: | :---: | ----- |
| Bài viết |  |  |  |  |  |
| Ghim Thông báo |  |  |  |  |    |
| Lịch sử chatbot |  |  |  |  | Để giảng viên có thể hiểu những khó khăn và nhu cầu của sinh viên |
| Chat group |  |  |  |  |  |

### **2.2 Phân tích Nghiệp vụ từng Vai trò**

#### **2.2.1 ADMIN** 

Quản trị viên đóng vai trò là "Superuser". Tuy nhiên, một yêu cầu nghiệp vụ quan trọng được đặt ra là cơ chế bảo vệ lẫn nhau: **Admin có quyền nâng/hạ quyền của tất cả user khác, nhưng không thể hạ quyền của một Admin khác**.

* **Logic nghiệp vụ:** Điều này ngăn chặn kịch bản phá hoại nội bộ, nơi một tài khoản Admin bị xâm nhập cố gắng xóa quyền của các Admin khác để chiếm đoạt hệ thống. Việc hạ quyền Admin chỉ có thể thực hiện qua truy cập trực tiếp vào cơ sở dữ liệu hoặc bởi một tài khoản "Root/System Owner" đặc biệt được thiết kế riêng.

#### **2.2.2 TEACHER** 

Giảng viên là trung tâm của tri thức và quản lý cộng đồng.

* **Quyền hạn mở rộng:** Ngoài việc quản lý nội dung (tài liệu, bài tập, cuộc thi), Teacher có quyền đặc biệt trong module **Đội tuyển** và **Chatbot**. Teacher có thể xem lịch sử chat của Chatbot để nắm bắt các vấn đề sinh viên thường gặp mà chưa dám hỏi trực tiếp.  
* **Cơ chế giới thiệu:** Khi Chatbot không thể giải quyết vấn đề của Student, hệ thống sẽ giới thiệu Teacher tương ứng (dựa trên môn học).

#### **2.2.3 MANAGER** 

Đây là vai trò mang tính chất "lai" (Hybrid Role).

* **Bối cảnh:** Manager thường là các Trợ giảng, cán bộ lớp hoặc sinh viên nòng cốt.  
* **Đặc thù:** Họ sở hữu quyền của Teacher (tạo đề, tạo cuộc thi, quản lý tài liệu) nhưng vẫn giữ thuộc tính của Student (có thể làm bài tập, tham gia cuộc thi).

#### **2.2.4 STUDENT** 

Là đối tượng phục vụ chính, Student có quyền truy cập rộng rãi vào tài nguyên nhưng bị hạn chế nghiêm ngặt về quyền chỉnh sửa hệ thống.8


## **3\. Đặc tả Yêu cầu Chức năng** 

### **3.1 Module Đăng ký và Xác thực** 

* **FR-01: Đăng nhập đa phương thức:** Hệ thống hỗ trợ đăng nhập truyền thống (Username/Password) và tích hợp SSO (Single Sign-On) qua Google và Email trường (Microsoft 365/Google Workspace). Việc tích hợp SSO giúp giảm thiểu rào cản gia nhập và tăng cường bảo mật danh tính.1  
* **FR-02: Đăng ký tự động:** Sinh viên có thể tự đăng ký tài khoản. Hệ thống tự động phân quyền "Student" mặc định. Đối với các vai trò cao hơn (Manager, Teacher), cần sự can thiệp nâng quyền từ Admin.

### **3.2 Module Hồ sơ và Gamification** 

Đây là tính năng tạo động lực (Motivation) quan trọng cho mô hình Olympic.

* **FR-03: Quản lý thông tin cá nhân:** Người dùng thay đổi Avatar, thông tin liên hệ.  
* **FR-04: Cơ chế Huy hiệu (Badges System):**  
  * **Logic cấp phát:** Huy hiệu được gắn tự động khi sinh viên trở thành thành viên chính thức của một Đội tuyển (Toán, Lý, Hóa, Ngoại ngữ).  
  * **Tính vĩnh viễn (Permanence):** Huy hiệu không mất đi ngay cả khi đội tuyển giải tán hoặc kết thúc mùa thi (trừ khi bị Teacher xóa thủ công khỏi danh sách thành viên với lý do kỷ luật). Điều này biến Profile thành một "Bảng vàng thành tích" lưu trữ lịch sử hoạt động suốt 4 năm học.  
  * **Hiển thị:** Huy hiệu (Icon nhỏ) hiển thị **bên cạnh tên người dùng** ở mọi nơi trên hệ thống (Forum, Comment, Chat, Leaderboard). Nếu tham gia nhiều đội (ví dụ vừa đội Toán vừa đội Lý), các huy hiệu sẽ xếp hàng ngang cạnh tên.9

### **3.3 Module Tài liệu**

* **FR-05: Thao tác CRUD:** Hỗ trợ đầy đủ Tải lên, Tải xuống, Xem (Preview trực tiếp PDF/Office), Sửa metadata và Xóa.  
* **FR-06: Phân loại:** Tài liệu được tag theo Môn học (Khoa học cơ bản: Toán, Lý, Hóa; Ngoại ngữ: Anh, Trung,...).

### **3.4 Module Bài tập** 

Module này yêu cầu sự linh hoạt cao để đáp ứng đặc thù của các môn Khoa học tự nhiên và Ngôn ngữ.

* **FR-07: Cấu trúc Đa dạng câu hỏi:**  
  * **Tự luận :** Cho phép nhập văn bản và đính kèm file (ảnh chụp bài làm). Đối với Toán/Lý/Hóa, tích hợp bộ soạn thảo công thức toán học (Equation Editor) ngay khung trả lời.  
  * **Trắc nghiệm:** Hỗ trợ:  
    * Chọn 1 đáp án đúng (Single Choice).  
    * Chọn nhiều đáp án đúng (Multiple Choice).  
    * Điền từ/số (Short Answer) \- quan trọng cho Ngoại ngữ và kết quả Toán học.  
* **FR-08: Thời gian:** Có thiết lập "Thời gian bắt đầu" và "Thời gian kết thúc" (Deadline). Tuy nhiên, trường này là không bắt buộc để hỗ trợ cả chế độ "Luyện tập tự do" và "Bài tập về nhà".

### **3.5 Module Cuộc thi** 

Đây là tính năng cốt lõi phân biệt hệ thống này với các LMS thông thường.

* **FR-09: Lập lịch thi đấu:** Bắt buộc phải có thời gian Bắt đầu và Kết thúc chính xác (Hẹn giờ). Hệ thống tự động mở đề khi đến giờ và đóng nộp bài khi hết giờ.  
* **FR-10: Môi trường làm bài:** Giao diện thi tập trung (Focus Mode), hiển thị đồng hồ đếm ngược.  
* **FR-11: Chống gian lận :**  
  * Cảnh báo khi rời khỏi tab thi.  
  * Vô hiệu hóa chức năng Copy/Paste trong trình làm bài.11  
  * Trộn thứ tự câu hỏi và đáp án cho từng sinh viên để giảm thiểu trao đổi bài.

### **3.6 Module Đội tuyển**

* **FR-12: Quản lý vòng đời đội tuyển:** Teacher/Admin tạo đội tuyển (VD: "Olympic Toán 2024").  
* **FR-13: Phân cấp nội bộ:**  
  * **Leader (Đội trưởng):** Teacher có quyền Bổ nhiệm/Bãi nhiệm Leader từ danh sách thành viên. Leader sẽ hỗ trợ Teacher duyệt thành viên mới hoặc quản lý hoạt động nội bộ.  
  * **Thành viên:** Teacher/Leader có quyền Thêm thành viên (Add directly) hoặc Duyệt đơn xin gia nhập (Approval workflow).

### **3.7 Module Diễn đàn**

* **FR-14: Thảo luận chuyên sâu:** Hỗ trợ tạo chủ đề, bài viết, bình luận.  
* **FR-15: Công cụ quản trị:** Tính năng **Ghim thông báo** cho Teacher/Admin để đưa các thông báo quan trọng (Lịch thi, Quy chế) lên đầu trang.  
* **FR-16: Rich Content:** Trình soạn thảo bài viết hỗ trợ chèn công thức Toán/Lý/Hóa (LaTeX support) và Media.

### **3.8 Module Hỏi đáp và Chatbot**

Hệ thống kết hợp giữa giao tiếp giữa người với người và người với máy.

* **FR-17: Hệ thống nhắn tin:**  
  * Nhắn tin riêng (Direct Message \- DM) giữa các User.  
  * Nhắn tin nhóm (Group Chat): Tạo nhóm chat tùy ý hoặc nhóm theo Đội tuyển.  
* **FR-18: Chatbot AI \- Trợ lý ảo:**  
  * **Giao diện (UI):** Thiết kế dạng **Floating Action Button (Icon nhỏ)** cố định ở góc màn hình. Người dùng có thể click để mở cửa sổ chat bất cứ lúc nào mà không gián đoạn việc đang làm.12  
  * **Cơ chế hiển thị tin nhắn:** Để tối ưu hiệu năng và tránh rối mắt, cửa sổ chat cố định hiển thị **10 tin nhắn gần nhất**. Khi người dùng cuộn lên, hệ thống sẽ tải thêm tin nhắn cũ (Lazy loading/Pagination).  
  * **Logic phản hồi:** Chatbot tự động trả lời các câu hỏi thường gặp (FAQ). Đặc biệt, Chatbot có tính năng **Giới thiệu chuyên gia**: Nếu sinh viên cần hỗ trợ chuyên sâu mà Bot không giải quyết được, Bot sẽ hiển thị thẻ thông tin (Card) của Teacher hoặc Admin phụ trách môn đó để sinh viên liên hệ trực tiếp.  
  * **Giám sát:** Teacher có quyền xem lịch sử chat của Chatbot để cải thiện nội dung hoặc can thiệp hỗ trợ kịp thời.

## **4\. Đặc tả Yêu cầu Phi chức năng**

### **4.1 Hiệu năng và Hiển thị Công thức** 

Đối với một hệ thống chuyên về Khoa học Cơ bản, khả năng hiển thị công thức Toán, Lý, Hóa là yếu tố sống còn.

* **NFR-01: Math Rendering Engine:** Hệ thống sử dụng thư viện **KaTeX** thay vì MathJax mặc định.  
  * *Lý do:* KaTeX có tốc độ render nhanh hơn vượt trội (synchronous rendering), không gây hiện tượng "giật" (reflow) trang khi tải các đề thi chứa hàng trăm công thức phức tạp.13 Điều này cực kỳ quan trọng trong các cuộc thi Olympic trực tuyến nơi tốc độ tải đề ảnh hưởng đến tâm lý thí sinh.  
  * *Dự phòng:* Có thể tích hợp MathJax như một phương án dự phòng (fallback) cho các ký hiệu đặc biệt mà KaTeX chưa hỗ trợ, đảm bảo tính tương thích toàn diện.14

### **4.2 Tính Bảo mật** 

* **NFR-02: Secure Authentication:** Mật khẩu người dùng (đăng ký thủ công) phải được băm (hashing) bằng thuật toán mạnh (BCrypt). Token phiên làm việc (JWT and Refresh Token) phải được quản lý chặt, có thời hạn hết hạn.  
* **NFR-03: Chống gian lận (Integrity):** Module Cuộc thi cần cơ chế phát hiện hành vi chuyển tab (blur event detection) và ghi log lại để Giám thị xử lý.

### **4.3 Khả năng mở rộng** 

* **NFR-04: Concurrency:** Hệ thống phải chịu tải được tối thiểu 500-1000 sinh viên làm bài thi cùng một thời điểm mà không bị trễ (latency) quá 2 giây cho mỗi thao tác lưu bài.15

## **5\. Kiến trúc Hệ thống** 

Sử dụng mô hình kiến trúc **Client-Server** với giao tiếp qua **RESTful API**, giúp tách biệt Frontend và Backend, thuận tiện cho việc phát triển ứng dụng Mobile sau này.

* **Frontend (Client):** Xây dựng bằng **React**.16  
* **Backend (Server):** Sử dụng **Java Spring Boot**.  
* **Database:** Sử dụng **PostgreSQL**.18


## **6\. Cơ bản Thiết kế Giao diện Người dùng (UI/UX)**

### **6.1 Widget Chatbot**

* **Vị trí:** Góc dưới bên phải, z-index cao nhất để trôi nổi trên mọi nội dung.  
* **Trạng thái đóng :** Hiển thị Icon tròn, có thể có thông báo đỏ nếu có tin mới.  
* **Trạng thái mở:**  
  * Click vào Icon \-\> Bung ra cửa sổ chat.  
  * **Header:** Tên Chatbot \+ Nút đóng \+ Nút "Liên hệ GV" (để thực hiện chức năng giới thiệu Teacher).  
  * **Body:** Hiển thị danh sách tin nhắn. Mặc định load 10 items mới nhất. Khi scroll lên đỉnh, hiện spinner loading và fetch tiếp 10 items cũ hơn.  
  * **Footer:** Ô nhập liệu \+ Nút gửi.

### **6.2 Giao diện Làm bài thi**

* **Bố cục:** Chia 2 cột.  
  * *Cột trái (70%):* Hiển thị câu hỏi. Sử dụng thư viện KaTeX để render các công thức Toán/Lý/Hóa.  
  * *Cột phải (30%):* Đồng hồ đếm ngược (Countdown Timer), Danh sách câu hỏi (Question Navigation) với trạng thái màu sắc (Đã làm/Chưa làm/Đang xem).  
* **Chống gian lận:** Khi phát hiện người dùng chuyển tab, hiển thị Modal cảnh báo "Hệ thống phát hiện bạn đã rời khỏi màn hình thi. Hành động này sẽ được ghi lại.".

### **6.3 Hiển thị Huy hiệu** 

* **Vị trí:** Luôn nằm ngay sau Display Name của người dùng.  
* **Style:** Icon dạng Vector (SVG) kích thước nhỏ (16x16px), có Tooltip khi hover chuột vào (VD: "Thành viên Đội tuyển Toán").  
* **Logic:** Frontend nhận mảng badges: \['Math', 'Physics'\] từ API User Profile và map ra các icon tương ứng xếp liền kề nhau.

## **7\. Kết luận và Khuyến nghị**

Hệ thống Olympic Khoa học Cơ bản được thiết kế trong tài liệu này không chỉ đáp ứng nhu cầu quản lý thi cử cơ bản mà còn hướng tới việc xây dựng một cộng đồng học thuật gắn kết thông qua các yếu tố Gamification (Huy hiệu, Đội tuyển) và hỗ trợ thông minh (Chatbot).

Sự phân quyền chi tiết giúp giảm tải cho Giảng viên bằng cách tận dụng nguồn lực từ các sinh viên xuất sắc/trợ giảng. Kiến trúc hệ thống đề xuất đảm bảo khả năng mở rộng và hiệu năng cao cho các kỳ thi quan trọng.

**Triển khai:**

1. **Giai đoạn 1:** Tập trung vào Core Features: Đăng nhập (SSO), Tài liệu, Bài tập trắc nghiệm cơ bản, và Quản lý Đội tuyển (để vận hành logic Huy hiệu).  
2. **Giai đoạn 2:** Phát triển Module Cuộc thi với tính năng hẹn giờ và chống gian lận. Tích hợp Chatbot cơ bản (Rule-based).  
3. **Giai đoạn 3:** Hoàn thiện Chatbot với AI, Mobile App và các tính năng thống kê nâng cao.

Tài liệu này đóng vai trò nền tảng cho đội ngũ phát triển và giới thiệu cho người đọc trước khi bắt tay vào xây dựng chi tiết.

#### **Nguồn trích dẫn**

1. Write an SRS document: How-tos, templates, and tips \- Canva, truy cập vào tháng 12 12, 2025, [https://www.canva.com/docs/srs-document/](https://www.canva.com/docs/srs-document/)  
2. Software Requirement Specification (SRS) Format \- GeeksforGeeks, truy cập vào tháng 12 12, 2025, [https://www.geeksforgeeks.org/software-engineering/software-requirement-specification-srs-format/](https://www.geeksforgeeks.org/software-engineering/software-requirement-specification-srs-format/)  
3. SRS Document Example: How to Write a Comprehensive SRS \- Ulam Labs, truy cập vào tháng 12 12, 2025, [https://www.ulam.io/blog/how-to-write-an-srs-document](https://www.ulam.io/blog/how-to-write-an-srs-document)  
4. Key 12 LMS Requirements For Your Checklist \- EducateMe, truy cập vào tháng 12 12, 2025, [https://www.educate-me.co/blog/lms-requirements](https://www.educate-me.co/blog/lms-requirements)  
5. Validating the impact of gamified technology-enhanced learning environments on motivation and academic performance: enhancing TELEs with digital badges \- Frontiers, truy cập vào tháng 12 12, 2025, [https://www.frontiersin.org/journals/education/articles/10.3389/feduc.2024.1429452/full](https://www.frontiersin.org/journals/education/articles/10.3389/feduc.2024.1429452/full)  
6. 15 Technical Requirements for Scalable Exam Security | Proctor360, truy cập vào tháng 12 12, 2025, [https://proctor360.com/blog/15-technical-requirements-for-scalable-exam-security](https://proctor360.com/blog/15-technical-requirements-for-scalable-exam-security)  
7. Software Requirements Specification (SRS) \- C\# Corner, truy cập vào tháng 12 12, 2025, [https://www.c-sharpcorner.com/article/software-requirements-specification/](https://www.c-sharpcorner.com/article/software-requirements-specification/)  
8. 17 LMS Requirements Checklist: Your Guide to Choosing the Right Platform \- Teachfloor, truy cập vào tháng 12 12, 2025, [https://www.teachfloor.com/blog/lms-requirements](https://www.teachfloor.com/blog/lms-requirements)  
9. Gamification in Education: A Systematic Mapping Study \- Winston-Salem State University, truy cập vào tháng 12 12, 2025, [https://www.wssu.edu/profiles/dichevc/gamification-in-education-systematic-mapping-study.pdf](https://www.wssu.edu/profiles/dichevc/gamification-in-education-systematic-mapping-study.pdf)  
10. 10 Examples of Badges Used in Gamification \- Trophy, truy cập vào tháng 12 12, 2025, [https://trophy.so/blog/badges-feature-gamification-examples](https://trophy.so/blog/badges-feature-gamification-examples)  
11. 9 Best Online Test Cheating Prevention Software for 2025 \- HackerEarth, truy cập vào tháng 12 12, 2025, [https://www.hackerearth.com/blog/online-test-cheating-prevention-tools](https://www.hackerearth.com/blog/online-test-cheating-prevention-tools)  
12. Browse thousands of Chatbot Floating Button images for design inspiration | Dribbble, truy cập vào tháng 12 12, 2025, [https://dribbble.com/search/chatbot-floating-button](https://dribbble.com/search/chatbot-floating-button)  
13. KaTeX vs. MathJax: The Battle for Web Math Rendering Supremacy \- BigGo News, truy cập vào tháng 12 12, 2025, [https://biggo.com/news/202511040733\_KaTeX\_MathJax\_Web\_Rendering\_Comparison](https://biggo.com/news/202511040733_KaTeX_MathJax_Web_Rendering_Comparison)  
14. mkdocs-material/docs/reference/math.md at master \- GitHub, truy cập vào tháng 12 12, 2025, [https://github.com/squidfunk/mkdocs-material/blob/master/docs/reference/math.md](https://github.com/squidfunk/mkdocs-material/blob/master/docs/reference/math.md)  
15. Srs On Online Quiz System | PDF | Graphical User Interfaces | Databases \- Scribd, truy cập vào tháng 12 12, 2025, [https://www.scribd.com/doc/53951908/srs-on-online-quiz-system](https://www.scribd.com/doc/53951908/srs-on-online-quiz-system)  
16. Chat Widget \- What is it and how does it work? \- GetStream.io, truy cập vào tháng 12 12, 2025, [https://getstream.io/glossary/chat-widget/](https://getstream.io/glossary/chat-widget/)  
17. How to build a live chat widget in React \- Ably Realtime, truy cập vào tháng 12 12, 2025, [https://ably.com/blog/how-to-build-a-live-chat-widget-in-react-creation](https://ably.com/blog/how-to-build-a-live-chat-widget-in-react-creation)  
18. Online-Examination-System/README.md at master \- GitHub, truy cập vào tháng 12 12, 2025, [https://github.com/MinaYossry/Online-Examination-System/blob/master/README.md](https://github.com/MinaYossry/Online-Examination-System/blob/master/README.md)  
19. Database design of online exam system \- Stack Overflow, truy cập vào tháng 12 12, 2025, [https://stackoverflow.com/questions/1763800/database-design-of-online-exam-system](https://stackoverflow.com/questions/1763800/database-design-of-online-exam-system)  
20. How to Design a Database for Online Learning Platform \- GeeksforGeeks, truy cập vào tháng 12 12, 2025, [https://www.geeksforgeeks.org/sql/how-to-design-a-database-for-online-learning-platform/](https://www.geeksforgeeks.org/sql/how-to-design-a-database-for-online-learning-platform/)

