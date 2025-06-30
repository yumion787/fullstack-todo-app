# 🚀 Trello風タスク管理アプリ (フルスタック版)

Spring Boot + React/Next.js で構築された、本格的なタスク管理アプリケーションです。
![スクリーンショット 2025-06-30 21 25 34](https://github.com/user-attachments/assets/f9852f0d-9372-4894-94f4-cbad01d35cc1)

## 📋 プロジェクト概要

### 🎯 機能
- **リスト管理**: タスクを整理するリスト（カラム）の作成・編集・削除
- **タスク管理**: タスクの作成・編集・削除・完了状態管理
- **ドラッグ&ドロップ**: 直感的なタスク・リストの並び替え

- **日本語完全対応**: 漢字・ひらがな・カタカナ・絵文字すべてサポート
- **リアルタイム更新**: データベース連携によるデータ永続化

### 🏗️ アーキテクチャ
```
┌─────────────────┐    HTTP API    ┌─────────────────┐    JDBC    ┌─────────────┐
│   Frontend      │ ────────────── │    Backend      │ ────────── │   Database  │
│                 │                │                 │            │             │
│ Next.js 15      │                │ Spring Boot 3.x │            │ MySQL 8.0   │
│ React 19        │                │ Java 21         │            │             │
│ TypeScript      │                │ JPA/Hibernate   │            │             │
│ TailwindCSS     │                │ REST API        │            │             │
│ shadcn/ui       │                │                 │            │             │
└─────────────────┘                └─────────────────┘            └─────────────┘
```

## 🛠️ 技術スタック

### Frontend
- **Framework**: Next.js 15.3.2 (App Router)
- **Language**: TypeScript
- **UI Library**: React 19, shadcn/ui
- **Styling**: Tailwind CSS
- **State Management**: TanStack Query v5
- **HTTP Client**: Axios
- **Drag & Drop**: @hello-pangea/dnd

### Backend
- **Framework**: Spring Boot 3.5.3
- **Language**: Java 21
- **Database**: MySQL 8.0
- **ORM**: JPA/Hibernate
- **Build Tool**: Gradle 8.14.2
- **Architecture**: REST API

### Infrastructure
- **Containerization**: Docker & Docker Compose
- **Database**: MySQL with UTF-8MB4 encoding
- **Development**: Hot reload (Spring Boot DevTools + Next.js Turbopack)

## 🚀 セットアップ & 起動

### 1. 前提条件
- Docker & Docker Compose
- Node.js 18+ & npm
- Java 21+ (開発時のみ)

### 2. プロジェクトクローン
```bash
git clone <repository-url>
cd dev_taskapp
```

### 3. バックエンド起動 (Docker Compose)
```bash
# MySQL + Spring Boot API を一括起動
docker-compose up -d

# ログ確認
docker-compose logs -f todo-api
```

### 4. フロントエンド起動
```bash
cd frontend/todo-app
npm install
npm run dev
```

### 5. アクセス
- **フロントエンド**: http://localhost:3000
- **API**: http://localhost:8080/api
- **データベース**: localhost:3306

## 📁 プロジェクト構造

```
dev_taskapp/
├── backend/todo-api/          # Spring Boot API
│   ├── src/main/java/         # Javaソースコード
│   ├── src/main/resources/    # 設定ファイル・SQL
│   ├── build.gradle           # Gradle設定
│   └── Dockerfile             # Docker設定
├── frontend/todo-app/         # Next.js アプリ
│   ├── src/app/              # App Router (Next.js 13+)
│   ├── src/components/       # UIコンポーネント
│   ├── src/hooks/           # カスタムフック
│   ├── src/services/        # API通信
│   └── src/types/           # TypeScript型定義
├── mysql/                    # MySQL設定
│   └── my.cnf               # 文字エンコーディング設定
├── docker-compose.yml       # Docker Compose設定
└── README.md               # このファイル
```

## 🔧 開発

### バックエンド開発
```bash
cd backend/todo-api
./gradlew bootRun  # ローカル起動 (MySQLコンテナ必要)
./gradlew test     # テスト実行
```

### フロントエンド開発
```bash
cd frontend/todo-app
npm run dev        # 開発サーバー起動
npm run build      # 本番ビルド
npm run lint       # ESLint実行
```

### データベース操作
```bash
# MySQL接続
docker exec -it todo-mysql mysql -u todouser -ptodopassword todoapp

# データベースリセット
docker-compose down
docker volume rm dev_taskapp_mysql_data
docker-compose up -d
```

## 🌟 主要機能

### ✅ 実装済み
- [x] リスト（カラム）のCRUD操作
- [x] タスクのCRUD操作  
- [x] ドラッグ&ドロップによる並び替え
- [x] 日本語完全対応
- [x] レスポンシブデザイン
- [x] リアルタイムデータ同期
- [x] エラーハンドリング
- [x] デッドロック回避機能

### 🚧 今後の予定
- [ ] ユーザー認証・認可
- [ ] リアルタイム通知
- [ ] タスクの期限管理
- [ ] ファイル添付機能
- [ ] チーム機能

## 🛡️ セキュリティ・品質

### セキュリティ対策
- CORS設定による適切なオリジン制御
- SQL injection対策 (JPA使用)
- XSS対策 (React標準のエスケープ)

### パフォーマンス最適化
- TanStack Queryによるキャッシュ管理
- データベースインデックス最適化
- 並行処理デッドロック回避

### コード品質
- TypeScript型安全性
- ESLint + Prettier
- コンポーネント設計パターン

## 📄 ライセンス

MIT License

## 🤝 コントリビューション

1. Fork this repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

**作成者**: yumion
**更新日**: 2025年6月30日 
