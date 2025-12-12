
# **API SPECIFICATION**
# **OLYMPIC KHOA HỌC CƠ BẢN** 

---

> Author: Long Nguyen Hoang

> Email: nghlong3004@gmail.com

> Version: 1.0.0 

---
## **1. Quy ước chung** 

* **Base URL**: https://api.olympic.humg.edu.vn/api/v1 

* **Timezone**: UTC (Server). Frontend tự convert sang Local Time của user.  
* **Date Format**: ISO 8601 (yyyy-MM-dd'T'HH:mm:ss)  
* **Content-Type**: application/json (ngoại trừ các API upload file).  
* **Pagination**: Các API lấy danh sách sử dụng chuẩn query param sau:  
  * **Request**: ?page=0&size=10&sort=createdTime,desc  
  * **Response**:  
  ```json
    {  
      "content": [ ... ],  
      "page_number": 0,  
      "page_size": 10,  
      "total_elements": 50,  
      "total_pages": 5  
    }

### **Cơ chế Authentication (JWT + Refresh Token)**

1. **Access Token**: Gửi trong Header Authorization: Bearer <token>. Thời gian sống ngắn (ví dụ: 15-30 phút).  
2. **Refresh Token**: Lưu trong **HttpOnly Cookie**. Thời gian sống dài (ví dụ: 7-30 ngày). Frontend không thể đọc cookie này (chống XSS), nó tự động được browser gửi kèm request khi gọi API refresh.

## **2. Định dạng Phản hồi Lỗi**

Mọi API khi thất bại sẽ trả về JSON với cấu trúc:
```json
{  
  "status": 400,  
  "code": "VALIDATION_ERROR",  
  "message": "Email address is invalid."  
}
```
### **Bảng Lỗi Chung**

Các lỗi này có thể xuất hiện ở bất kỳ API nào và Frontend cần có cơ chế (Interceptor) để xử lý tập trung.

| HTTP Status | Code                      | Message                              | Mô tả                                                  |
|:------------|:--------------------------|:-------------------------------------|:-------------------------------------------------------|
| **400**     | VALIDATION_ERROR          | "Validation failed for input data."  | Dữ liệu đầu vào không hợp lệ.                          |
| **400**     | MISSING_PARAMETER         | "The required parameter is missing." | Thiếu tham số bắt buộc.                                |
| **400**     | HTTP_MESSAGE_NOT_READABLE | "Malformed JSON request."            | JSON gửi lên sai cú pháp hoặc sai kiểu dữ liệu.        |
| **401**     | UNAUTHORIZED              | "Full authentication is required."   | Chưa đăng nhập hoặc Token không hợp lệ.                |
| **401**     | TOKEN_EXPIRED             | "Access token has expired."          | Token hết hạn -> Frontend cần gọi API Refresh Token.   |
| **403**     | ACCESS_DENIED             | "You do not have permission."        | Có đăng nhập nhưng không đủ quyền hạn (Role) truy cập. |
| **404**     | RESOURCE_NOT_FOUND        | "Resources not found."               | ID không tồn tại hoặc sai đường dẫn URL.               |
| **405**     | METHOD_NOT_ALLOWED        | "Request method not supported."      | Gọi sai method (ví dụ API là POST nhưng gọi GET).      |
| **409**     | DATA_CONFLICT             | "Data conflict occurred."            | Xung đột dữ liệu chung.                                |
| **413**     | PAYLOAD_TOO_LARGE         | "The payload is too large."          | Request body vượt quá giới hạn.                        |
| **415**     | UNSUPPORTED_MEDIA_TYPE    | "Content type not supported."        | Sai Header Content-Type.                               |
| **429**     | RATE_LIMIT_EXCEEDED       | "Too many requests."                 | Gửi quá nhiều request trong thời gian ngắn (Spam).     |
| **500**     | INTERNAL_SERVER_ERROR     | "Unexpected server error."           | Lỗi hệ thống không xác định.                           |
| **503**     | SERVICE_UNAVAILABLE       | "Service is unavailable."            | Server đang bảo trì hoặc quá tải.                      |

## **3. Chi tiết API (Endpoints)**

### **I. Authentication (Xác thực)**

#### **1. Đăng ký**

**POST** /auth/register

* **Body**: 
 ```json
{
  "firstName": "Long",

  "lastName": "Nguyen",

  "email": "long@example.com",

  "password": "123456",

  "gender": "MALE",

  "birthday": "2004-03-30",

  "phone": "0987123456",

  "universityName": "HUMG",

  "facultyName": "Control Engineering"  
}
```

* **Errors**:  
  * 409 - EMAIL_ALREADY_EXISTS: "Email is already in use."

#### **2. Đăng nhập (Credentials)**

**POST** /auth/login

* **Body**:  
 ```json
{  
    "email": "vana@student.edu.vn",  
    "password": "Password123!"  
}
```
* **Success (200 OK)**:  
  * **Body**:  
```json
    {  
      "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",  
      "user": {  
        "id": 1,  
        "firstName": "Long",
        "lastName": "Nguyen",
        "email": "long@example.com",
        "password": "123456",
        "gender": "MALE",
        "birthday": "2004-03-30",
        "phone": "0987123456",
        "universityName": "HUMG",
        "facultyName": "Control Engineering",  
        "role": "STUDENT",  
        "avatarUrl": "https://example.com/default-avatar.png"  
  }  
}
```
* **Response Header (Set-Cookie)**: refresh_token=xyz123...; HttpOnly; Secure; SameSite=Strict; Path=/api/v1/auth/refresh-token; Max-Age=604800  
* **Errors**:  
  * 401 - BAD_CREDENTIALS: "Invalid email or password."  
  * 403 - ACCOUNT_LOCKED: "User account is locked."

#### 

#### **3. Đăng nhập Google (SSO)**

**POST** /auth/google

* **Body**: 
```json
  {  
    "id_token": "google_id_token_received_from_client_sdk"  
  }
```
* **Success (200 OK)**:  
  * **Body**:  
```json
    {  
      "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",  
      "user": {  
        "id": 1,  
        "firstName": "Long",

   	      "lastName": "Nguyen",

           "email": "long@example.com",

           "password": "123456",

           "gender": "MALE",

           "birthday": "2004-03-30",

           "phone": "0987123456",

           "universityName": "HUMG",

    "facultyName": "Control Engineering",  
    "role": "STUDENT",  
    "avatarUrl": "https://example.com/default-avatar.png"  
  }  
}
```

* **Response Header (Set-Cookie)**: refresh_token=xyz123...; HttpOnly; Secure; SameSite=Strict; Path=/api/v1/auth/refresh-token; Max-Age=604800  
* **Errors**:  
  * 401 - BAD_CREDENTIALS: "Invalid email or password."  
  * 403 - ACCOUNT_LOCKED: "User account is locked."


#### **4. Refresh Token (Lấy Access Token mới)**

**POST** /auth/refresh-token

* **Header**: Cookie: refresh_token=... (Browser tự động gửi, Frontend không cần can thiệp).  
* **Body**: {} (Empty)  
* **Success (200 OK)**:
```json
  {  
    "access_token": "new_access_token_eyJhb..."  
  }
```
* **Errors**:  
  * 401 - INVALID_REFRESH_TOKEN: "Refresh token is invalid." (Client phải logout).

#### **5. Đăng xuất**

**POST** /auth/logout

* **Body**: {}  
* **Success (200 OK)**:  
  * **Set-Cookie**: refresh_token=; HttpOnly; Path=/api/v1/auth/refresh-token; Max-Age=0 (Xóa cookie).  
  * **Body**:  
    {  
      "message": "Logged out successfully."  
    }

### 

### **II. Users (Người dùng)**

#### **1. Lấy Profile của mình hoặc của Profile của người khác** 

**GET** /users/profile | /users/{id}

* **Header**: Authorization: Bearer <token>  
* **Success**:  
```json
{
   "id": "1",  
   "firstName": "Long",
  "lastName": "Nguyen",
   "email": "long@example.com",
   "password": "123456",
   "gender": "MALE",
   "birthday": "2004-03-30",
   "phone": "0987123456",
   "universityName": "HUMG",
   "facultyName": "Control Engineering",  
   "role": "STUDENT",  
   "avatarUrl": "https://example.com/default-avatar.png",  
  "isActive": true  
}
```
#### **2. Cập nhật Profile**

**PUT** /users/profile

* **Header**: Authorization: Bearer <token> 
* **Body**:  
```json
  {  
    "firstName": "Nguyen Van A (New)",  
    "avatarUrl": "https://example.com/new-avatar.jpg"  
  }
```
### 

### **III. Documents (Tài liệu)**

#### **1. Lấy danh sách tài liệu**

**GET** /documents

* **Params**: ?page=0&size=10&keyword=Toan&uploader_id=1  
* **Success**:  
```json
  {  
    "content": [  
      {  
        "id": 101, 
        "title": "Đề thi thử Toán 2024",
        "description": "Mô tả...",  
        "fileUrl": "https://storage.googleapis.com/file.pdf",  
        "uploaderName": "Giang Vien B",  
        "created": "2024-05-20T10:00:00"  
    }  
  ],  
  "total_pages": 5,  
  "total_elements": 50  
}
```
#### **2. Upload tài liệu**

**POST** /documents

* **Header**: Authorization: Bearer <token>, Content-Type: multipart/form-data  
* **Body** :  
  * file: (Binary File)  
  * title: "Đề thi thử"  
  * description: "Mô tả..."  
  * isPublic: true  
* **Errors**:  
  * 413 - PAYLOAD_TOO_LARGE: "File size exceeds limit."  
  * 400 - INVALID_FILE_TYPE: "File type not supported."

#### **3. Xóa tài liệu**

**DELETE** /documents/{id}

* **Errors**:  
  * 403 - NOT_DOCUMENT_OWNER: "You do not have permission to delete this document."

### 

### **IV. Teams (Đội tuyển)**

#### **1. Lấy danh sách Đội tuyển**

**GET** /teams

* **Params**: ?subject=MATH  
* **Success**:  
```json
  [  
    {  
      "id": 1,  
      "name": "Đội Toán K65",  
      "subject": "MATH",  
      "badgeIconUrl": "https://example.com/badge.png",  
      "memberCount": 15  
    }  
  ]
```

#### **2. Tạo Team**

**POST** /teams

* **Auth**: TEACHER, ADMIN.  
* **Body**:  
```json
  {  
    "name": "Đội Toán",  
    "subject": "MATH",  
    "badgeIconUrl": "https://example.com/badge.png",  
    "description": "Đội tuyển toán..."  
  }
```
* **Errors**:  
  * 409 - TEAM_NAME_EXISTS: "Team name already exists."

#### **3. Xin vào Team**

**POST** /teams/{id}/join

* **Auth**: STUDENT.  
* **Errors**:  
  * 409 - ALREADY_MEMBER: "User is already a member or pending."

#### **4. Lấy danh sách thành viên (Để duyệt)**

**GET** /teams/{id}/members

* **Auth**: Leader, Teacher, Admin.  
* **Params**: ?status=PENDING (hoặc APPROVED)  
* **Success**:  
```json
  [  
    {  
      "userId": 10,  
      "firstName": "Nguyen Van",  
      "email": "c@student.edu.vn",  
      "status": "PENDING",  
      "joined": "2025-05-20T10:00:00"  
    }  
  ]
```
#### **5. Duyệt thành viên**

**PUT** /teams/{teamId}/members/{userId}/approve

* **Auth**: Leader, Teacher.  
* **Body**:  
```json
  {  
    "status": "APPROVED",  
    "isLeader": false  
  }
```
* **Errors**:  
  * 403 - NOT_LEADER: "Only team leader or creator can approve members."

### **V. Contests & Exercises (Cuộc thi & Bài tập)**

#### **1. Lấy danh sách Cuộc thi**

**GET** /contests

* **Params**: ?page=0&size=10  
* **Success**:  
```json
  {  
    "content": [  
      {  
        "id": 1,  
        "title": "Olympic Toán Cấp Trường 2025",  
        "start": "2025-05-01T08:00:00",  
        "end": "2025-05-01T11:00:00",  
        "status": "UPCOMING"  
      }  
    ],  
    "total_pages": 1  
  }
```
#### **2 . Tạo Cuộc thi**

**POST** /contests

* **Auth**: TEACHER, MANAGER.  
* **Body**:  
```json
{  
    "title": "Olympic Toán Cấp Trường 2025",  
    "description": "Cuộc thi thường niên...",  
    "start": "2025-05-01T08:00:00",  
    "end": "2025-05-01T11:00:00"  
 }
```

* **Errors**:  
  * 400  - INVALID_TIME_RANGE: "End time must be after start time."

#### **3. Tạo Bài tập**

**POST** /exercises

* **Body**:
```json
  {  
    "contest_id": 1,  
    "title": "Phần Trắc Nghiệm IQ",  
    "questions": [  
      {  
        "content": "1 + 1 = ?",  
        "questionType": "SINGLE_CHOICE",  
        "points": 1.0,  
        "options": [  
           { "content": "2", "is_correct": true },  
           { "content": "3", "is_correct": false }  
        ]  
      },  
      {  
         "content": "Hãy viết đoạn văn về...",  
         "questionType": "ESSAY",  
         "points": 5.0  
      }  
    ]  
  }
```
#### **4. Lấy đề bài (Làm bài)**

**GET** /exercises/{id}

* **Auth**: User tham gia.  
* **Response**:  
```json
  {  
    "id": 10,  
    "title": "Phần Trắc Nghiệm IQ",  
    "questions": [  
      {  
        "id": 101,  
        "content": "1 + 1 = ?",  
        "questionType": "SINGLE_CHOICE",  
        "options": [  
           { "id": 501, "content": "2" },  
           { "id": 502, "content": "3" }  
        ]  
      }  
    ]  
  }
```
#### **5. Nộp bài**

**POST** /exercises/{id}/submit

* **Body**:  
```json
  {  
    "answers": [  
      { "question_id": 101, "selected_option_id": 501 },  
      { "question_id": 102, "essay_answer": "Nội dung bài tự luận..." }  
    ]  
  }
```
* **Errors**:  
  * 400 - CONTEST_NOT_STARTED: "Contest has not started."  
  * 400 - CONTEST_ENDED: "Submission deadline passed."

### **VI. Forum (Diễn đàn)**

#### **1. Lấy danh sách Chủ đề**

**GET** /forum/topics

* **Params**: ?category_id=1&page=0  
* **Success**:  
```json
  {  
    "content": [  
      {  
        "id": 201,  
        "title": "[Thông báo] Lịch thi mới",  
        "authorName": "Admin",  
        "isPinned": true,  
        "viewCount": 150,  
        "commentCount": 12  
      }  
    ],  
    "total_pages": 2  
  }
```
#### **2. Tạo Chủ đề**

**POST** /forum/topics

* **Body**:  
```json
  {  
    "category_id": 1,  
    "title": "Hỏi về bài tập Lý",  
    "content": "Cho em hỏi bài 5 trang 20..."  
  }
```
#### **3. Lấy chi tiết Topic & Bình luận**

**GET** /forum/topics/{id}

* **Success**:  
```json
  {  
    "id": 201,  
    "title": "Hỏi về bài tập Lý",  
    "content": "Cho em hỏi...",  
    "author": { "id": 1, "full_name": "Student A", "avatar_url": "..." },  
    "comments": {  
      "content": [  
        {  
          "id": 501,  
          "content": "Bài này giải như sau...",  
          "author_name": "Teacher B",  
          "created_time": "2025-05-20T10:00:00"  
        }  
      ],  
      "page_number": 0,  
      "total_elements": 5  
    }  
  }
```
#### **4. Gửi Bình luận**

**POST** /forum/topics/{id}/comments

* **Body**: 
```json
  {  
    "content": "Bài này giải như sau...",  
    "parentCommentId": null  
  }
```
#### **5. Thả cảm xúc**

**POST** /reactions

* **Body**:  
```json
  {  
    "targetType": "TOPIC",  
    "targetId": 201,  
    "reactionType": "LIKE"  
  }
```
* **Errors**:  
  * 400 - INVALID_REACTION_TYPE: "Reaction type is invalid."

### **VII. Chat & Chatbot**

#### **1. Lấy lịch sử Chatbot**

**GET** /chatbot/history

* **Success**:
```json
  [  
    { "sender": "USER", "message": "Khi nào thi?", "time": "2025-05-20T10:00:00" },  
    { "sender": "BOT", "message": "Ngày 20/11...", "time": "2025-05-20T10:00:05" }  
  ]
```
#### **2. Gửi tin nhắn cho Bot**

**POST** /chatbot/message

* **Body**:  
```json
  {  
    "message": "Cho mình xin lịch thi Toán"  
  }
```
* **Success**:  
```json
  {  
    "reply": "Lịch thi Toán là ngày 20/11/2025 tại phòng A101.",  
    "suggested_actions": ["Xem chi tiết", "Đặt lịch nhắc"]  
  }
```
#### **3. Tạo nhóm Chat / Chat riêng**

**POST** /conversations

* **Body**:  
```json
  {  
    "type": "GROUP",  
    "name": "Nhóm Ôn Toán Cao Cấp",  
    "participantIds": [1, 2, 3]  
  }
```
#### **4. Lấy danh sách hội thoại**

**GET** /conversations

* **Success**:  
```json
  [  
    {  
      "id": 5,  
      "name": "Nhóm Ôn Toán Cao Cấp",  
      "lastMessage": "Mai học mấy giờ?",  
      "unreadCount": 2  
    }  
  ]
```
#### **5. Lấy lịch sử tin nhắn**

**GET** /conversations/{id}/messages

* **Params**: ?page=0&size=20  
* **Success**:  
```json
  {  
    "content": [  
      {  
        "id": 901,  
        "sender_id": 1,  
        "content": "Chào mọi người!",  
        "createdTime": "2025-05-20T10:00:00"  
      }  
    ],  
    "total_pages": 1  
  }
```
#### **6. Gửi tin nhắn (Chat)**

**POST** /conversations/{id}/messages

* **Body**: 
```json
  {  
    "content": "Chào mọi người!"  
  }
```

### **VIII. Administration (Quản trị hệ thống)**

#### **1. Nâng/Hạ quyền User (Upgrade/Downgrade Role)**

**PATCH** /users/{id}/role

* **Auth**: ADMIN Only.  
* **Body**: 
```json
  {  
    "role": "MANAGER"   
  }
```
  * *Valid Roles*: STUDENT, TEACHER, MANAGER, ADMIN.  
* **Success (200 OK)**:  
```json
  {  
    "message": "User role updated successfully.",  
    "userId": 10,  
    "newRole": "MANAGER"  
  }
```
* **Errors**:  
  * 404 - USER_NOT_FOUND: "User with ID {id} not found."  
  * 409 - CANNOT_MODIFY_ADMIN: "Cannot change role of another Administrator."  
  * 400 - INVALID_ROLE: "Role is not valid."

#### **2. Khóa/Mở khóa tài khoản (Lock/Unlock User)**

**PATCH** /users/{id}/status

* **Auth**: ADMIN Only.  
* **Body**:
```json
  {  
    "isActive": false  
  }
```
  * false: Khóa tài khoản (User không thể login).  
  * true: Mở khóa.  
* **Success (200 OK)**: 
```json
  {  
    "message": "User status updated.",  
    "isActive": false  
  }
```
* **Errors**:  
  * 409 - CANNOT_LOCK_ADMIN: "Cannot lock another Administrator account."