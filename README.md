# 校园非机动车管理系统 - 后端API文档

本文档为校园非机动车管理系统的后端提供完整的API接口规范和使用说明，旨在帮助前端开发人员快速理解后端服务并进行高效对接。

## 1. 项目概述

本项目是一个基于Spring Boot的校园非机动车智能管理系统。它旨在通过数字化的方式解决校园内自行车、电动车等非机动车的管理问题，核心功能包括：用户注册登录、车辆信息绑定与审核、动态二维码通行证生成与扫码核验。

## 2. 技术栈

- **核心框架**: Spring Boot 3.3.2
- **语言**: Java 17
- **持久层**: Spring Data JPA (Hibernate)
- **数据库**: MySQL 8.0+
- **安全与认证**: Spring Security, JSON Web Tokens (JWT)
- **模板引擎**: Thymeleaf (用于渲染扫码验证页面)
- **构建工具**: Maven

## 3. 快速启动

### 3.1. 前置要求

- Java 17
- Maven 3.8+
- MySQL 8.0+ 数据库服务

### 3.2. 数据库设置

1.  在您的MySQL实例中，执行 `mysql/init.sql` 脚本。这将创建名为 `schoolpass` 的数据库以及所有必需的数据表。

#### 3.2.1. 创建管理员账户（可选，用于测试）

系统默认注册的用户均为普通学生/教职工角色。如果您需要测试管理员功能，请遵循以下步骤：

1.  通过 `POST /api/auth/register` 接口注册一个新用户。
2.  使用数据库客户端连接到您的 `schoolpass` 数据库。
3.  在 `users` 表中找到您刚刚创建的用户记录，记下其 `id`。
4.  在 `user_roles` 表中，将该用户 `user_id` 对应的 `roles` 字段值从 `ROLE_STUDENT` 修改为 `ROLE_ADMIN`。

### 3.3. 应用配置

1.  打开 `src/main/resources/application.properties` 文件。
2.  修改以下数据库连接配置，替换为您自己的数据库信息：
    ```properties
    spring.datasource.url=jdbc:mysql://[你的数据库主机地址]:3306/schoolpass?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    spring.datasource.username=[你的数据库用户名]
    spring.datasource.password=[你的数据库密码]
    ```

### 3.4. 运行应用

在项目根目录下，执行以下命令：

```bash
mvn spring-boot:run
```

当您在控制台看到 `Tomcat started on port(s): 8080 (http)` 时，表示后端服务已成功启动。

## 4. 全局约定

### 4.1. Base URL

所有API的根路径为 `http://[你的服务器地址]:8080`。

### 4.2. 认证 (Authentication)

-   本系统的受保护接口采用基于JWT的Bearer Token认证。
-   用户通过登录接口获取`token`后，在后续请求中必须在 **HTTP Header** 中携带 `Authorization` 字段。
-   格式为: `Authorization: Bearer <Your-JWT-Token>`

### 4.3. 通用响应格式

#### 成功响应

-   对于返回数据的请求，通常响应体为包含数据的JSON对象或数组。
-   对于操作成功的请求（如注册、提交申请），响应体为一个`MessageResponse`对象。
    ```json
    {
        "message": "操作成功的提示信息"
    }
    ```

#### 错误响应

-   **400 Bad Request**: 请求参数无效或业务逻辑错误（如手机号已注册）。
    ```json
    {
        "message": "具体的错误原因"
    }
    ```
-   **401 Unauthorized**: 未提供Token或Token无效/过期。
    ```json
    {
        "status": 401,
        "error": "Unauthorized",
        "message": "认证失败: Full authentication is required to access this resource",
        "path": "/api/vehicles/my-vehicle"
    }
    ```
-   **403 Forbidden**: 已认证，但角色权限不足（如普通用户尝试访问管理员接口）。
-   **404 Not Found**: 请求的资源不存在。

---

## 5. API接口文档

### 5.1. 认证模块 (`/api/auth`)

#### 5.1.1. 用户注册

-   **Endpoint**: `POST /api/auth/register`
-   **描述**: 创建一个新的用户账户（默认为学生/教职工角色）。
-   **认证**: 无 (公开接口)
-   **Request Body**:
    ```json
    {
        "phone": "13800138002",
        "studentId": "20240002",
        "password": "password123"
    }
    ```
-   **Success Response** (`200 OK`):
    ```json
    {
        "message": "用户注册成功！"
    }
    ```

#### 5.1.2. 用户登录

-   **Endpoint**: `POST /api/auth/login`
-   **描述**: 使用手机号和密码登录，获取JWT。
-   **认证**: 无 (公开接口)
-   **Request Body**:
    ```json
    {
        "phone": "13800138002",
        "password": "password123"
    }
    ```
-   **Success Response** (`200 OK`):
    ```json
    {
        "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMzgwMDEzODAwMiIsImlhdCI6MTY3OTk4OTg5NywiZXhwIjoxNjc5OTk3MDk3fQ.abc...",
        "type": "Bearer",
        "id": 2,
        "phone": "13800138002",
        "studentId": "20240002"
    }
    ```
-   **Error Responses**:
    -   `401 Unauthorized` (用户不存在):
        ```json
        {
            "message": "该手机号未注册"
        }
        ```
    -   `401 Unauthorized` (密码错误):
        ```json
        {
            "message": "密码错误"
        }
        ```

---

### 5.2. 车辆管理模块 (用户) (`/api/vehicles`)

#### 5.2.1. 申请车辆通行证

-   **Endpoint**: `POST /api/vehicles/apply`
-   **描述**: 已登录用户为自己的车辆提交通行证申请。每位用户只能绑定一辆车。
-   **认证**: **需要** (Bearer Token)
-   **Request Body**:
    ```json
    {
        "licensePlate": "沪A-E12345",
        "photoUrl": "https://example.com/my-bike.png"
    }
    ```
-   **Success Response** (`200 OK`):
    ```json
    {
        "message": "车辆申请已提交，请等待审核。"
    }
    ```

#### 5.2.2. 查询我的车辆信息

-   **Endpoint**: `GET /api/vehicles/my-vehicle`
-   **描述**: 获取当前登录用户所绑定的车辆信息及其状态。
-   **认证**: **需要** (Bearer Token)
-   **Success Response** (`200 OK`):
    -   **特别说明**: 
        - 仅当`status`为`APPROVED`时，才会包含`passToken`字段。前端应使用此`passToken`的内容来生成二维码。该令牌有效期为60秒，每次请求此接口都会生成新的令牌。
        - 仅当`status`为`REJECTED`时，才会包含`rejectionReason`字段。
    -   **示例 (审核通过)**:
        ```json
        {
            "id": 1,
            "licensePlate": "沪A-E12345",
            "photoUrl": "https://example.com/my-bike.png",
            "status": "APPROVED",
            "rejectionReason": null,
            "ownerId": 2,
            "ownerPhone": "13800138002",
            "passToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjc5OTkwMTU5LCJleHAiOjE2Nzk5OTAxNzl9.def..."
        }
        ```
    -   **示例 (审核被驳回)**:
        ```json
        {
            "id": 1,
            "licensePlate": "沪A-E12345",
            "photoUrl": "https://example.com/my-bike.png",
            "status": "REJECTED",
            "rejectionReason": "照片不清晰，请重新上传。",
            "ownerId": 2,
            "ownerPhone": "13800138002",
            "passToken": null
        }
        ```
-   **Error Response** (`404 Not Found`): 如果用户还未申请车辆，则返回404。

---

### 5.3. 车辆管理模块 (管理员) (`/api/admin`)

#### 5.3.1. 获取待审核列表

-   **Endpoint**: `GET /api/admin/vehicles/pending`
-   **描述**: 获取所有状态为`PENDING_APPROVAL`的车辆申请列表。
-   **认证**: **需要** (管理员角色的Bearer Token)
-   **Success Response** (`200 OK`):
    ```json
    [
        {
            "id": 1,
            "licensePlate": "沪A-E12345",
            "photoUrl": "https://example.com/my-bike.png",
            "status": "PENDING_APPROVAL",
            "rejectionReason": null,
            "ownerId": 2,
            "ownerPhone": "13800138002",
            "passToken": null
        }
    ]
    ```

#### 5.3.2. 审核车辆申请

-   **Endpoint**: `POST /api/admin/vehicles/{id}/review`
-   **描述**: 审核指定ID的车辆申请，可批准或驳回。
-   **认证**: **需要** (管理员角色的Bearer Token)
-   **URL参数**: `{id}` - 要审核的车辆ID。
-   **Request Body**:
    -   批准:
    ```json
    {
        "status": "APPROVED"
    }
    ```
    -   驳回:
    ```json
    {
        "status": "REJECTED",
        "rejectionReason": "照片不清晰，请重新上传。"
    }
    ```
-   **Success Response** (`200 OK`): 返回更新后的车辆信息。
    -   批准后的响应示例:
    ```json
    {
        "id": 1,
        "licensePlate": "沪A-E12345",
        "photoUrl": "https://example.com/my-bike.png",
        "status": "APPROVED",
        "rejectionReason": null,
        "ownerId": 2,
        "ownerPhone": "13800138002",
        "passToken": null
    }
    ```
    -   驳回后的响应示例:
    ```json
    {
        "id": 1,
        "licensePlate": "沪A-E12345",
        "photoUrl": "https://example.com/my-bike.png",
        "status": "REJECTED",
        "rejectionReason": "照片不清晰，请重新上传。",
        "ownerId": 2,
        "ownerPhone": "13800138002",
        "passToken": null
    }
    ```

---

### 5.4. 通行证扫码验证模块 (`/pass`)

#### 5.4.1. 验证通行证

-   **Endpoint**: `GET /pass/{token}`
-   **描述**: 公开接口，用于安保人员扫码后验证。此接口不返回JSON，而是直接返回一个渲染好的HTML页面。移动端App可通过WebView来加载此URL。
-   **认证**: 无 (公开接口)
-   **URL参数**: `{token}` - 从`GET /api/vehicles/my-vehicle`接口获取的`passToken`。
-   **Success Response** (`200 OK`):
    -   返回一个包含车辆信息、车主信息和醒目通行状态的HTML页面。
-   **Error Response** (`200 OK`):
    -   如果令牌无效或过期，同样返回一个HTML页面，但内容是"验证失败"的错误提示。
