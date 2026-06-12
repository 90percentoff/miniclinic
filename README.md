MiniClinic
==========

簡介
---
MiniClinic 是一個教學用的簡易診所管理系統，包含醫師、病患、掛號、看診完成與統計摘要 API。專案使用 Spring Profiles 區分環境：dev 以 SQLite 開發，prod 以 PostgreSQL 部署到 Render。

線上 Demo
---
https://miniclinic-<yourname>.onrender.com

技術棧
---
- Java 17
- Spring Boot 3
- Spring Data JPA (Hibernate)
- Thymeleaf
- Maven
- Docker (multi-stage build)

本機執行
---
先確保有 Java 17 與 Maven wrapper 可執行，然後在專案根目錄執行：

```bash
# 執行測試
./mvnw.cmd -U test

# 產生 jar
./mvnw.cmd -U package

# 直接執行
java -jar target/miniclinic-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

# 使用 Docker
docker build -t miniclinic:latest .
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=dev miniclinic:latest
```

預設帳號（測試用）
---
- 醫師: `doctor1` / 密碼: `password`（如有調整請以 `data.sql` 或 `data-prod.sql` 為準）

重要檔案
---
- `src/main/resources/templates/dashboard.html` — Dashboard 模板（已加入「完成」按鈕）
- `src/main/java/tw/edu/fju/miniclinic/controller/StatsController.java` — 新增 `/api/stats`
- `src/main/java/tw/edu/fju/miniclinic/model/AppointmentRepository.java` — 新增 `countByStatus`
- `Dockerfile`, `.dockerignore`, `.gitignore`

提交與 AI 使用紀錄
---
請在 commit message 以 `[NO-AI]` 或 `[AI-USED]` 開頭；`[AI-USED]` commit 的 body 請包含三欄：問AI / AI建議 / 我的修改。
