# EarthCanvas

GPSの位置情報を利用し、現実空間で絵を描くことができるAndroidアプリです。  
描いた絵はAIが判定し、指定されたお題と一致すればクリアとなります。

---

## 🎮 アプリ概要

このアプリでは、ユーザーの現在位置をGPSで取得し、Google Map 上に移動の軌跡を表示します。  
現在の座標と前回の座標を線で結び、これにより「歩きながら地面に絵を描く」ような体験ができます。

- お題となる絵（例：ねこ、さかな など）を指定
- ユーザーが移動して絵を描く
- 描き終わると AI がその軌跡を判定
- お題と一致していると判断された場合、ゲーム終了

---

## 🧭 今後の改善予定

- 現在地の向きを示すピンを追加し、移動方向をよりわかりやすくする
- 描いた絵の履歴を保存し、後から再表示できる機能の追加

---

## 🧪 使用技術・構成

| 項目 | 内容 |
|------|------|
| GPS位置取得 | Androidの位置情報サービス |
| 地図表示・軌跡描画 | Google Maps SDK |
| AIモデル | TensorFlow Lite |
| モデル変換 | Google Colab で `.npy` → `.tflite` に変換 |
| 学習データ | Google提供の [Quick, Draw! Dataset](https://quickdraw.withgoogle.com/data) |
| 開発環境 | Android Studio / Java / Python（Colab上） |

---

## 👤 担当部分（個人の貢献）

- 公開されているTensorFlow Liteの画像分類モデルを参考にし、アプリ内で動作するように組み込みを担当
- .npy → .tflite モデル変換（Google Colabを利用）
- 推論処理の流れや処理分担の理解を深め、結合作業にも参加
- モデルの中身の実装を行ったわけではありませんが、組み込みや呼び出し処理を理解し、動作検証を通して精度向上やエラー対応を行いました

---

## 📖 参考にした記事・コード

AIによる絵の判定機能の実装においては、以下の公開情報を参考にしました：

- [CodeCamp公式ブログ：「AI×Androidアプリ開発」](https://blog.codecamp.jp/programming-android-app-development-ai)  
- [GitHub - oshimamasara/DRAW](https://github.com/oshimamasara/DRAW)

上記リソースの内容を読み解きながら、AIモデルの組み込みや、TFLite形式への変換、推論処理の呼び出しなどを実装しました。  
当初はAIの内部処理を深く理解していたわけではありませんが、**その後コードを読み返し、自分のアプリで正しく動作させるまでの過程で処理の構造や役割について理解を深めました。**

コードの多くは既存のものをベースとしていますが、**アプリに統合し、自分で動作確認・デバッグ・拡張を行った経験が大きな学びとなりました。**

---

## 📷 スクリーンショット

<img src="https://github.com/user-attachments/assets/31fb6d09-ba14-4380-a2ec-01b5f0a8d721" width="200">

アプリ起動後、ユーザーの移動を地図上に描画していく様子を示しています。

---

## 📌 注意事項

このアプリは学習目的で開発されたものであり、商用利用は想定していません。

---

## 🔧 実機確認・ビルドの前に（Google Maps APIキーの設定）

このアプリでは Google Maps SDK を使用しています。実機で動作確認を行うには、Google Cloud Console から取得した APIキーを `local.properties` に設定してください。

### 1. Google Maps APIキーの取得

Google Cloud Console にログインし、[Maps SDK for Android](https://console.cloud.google.com/) を有効化して、APIキーを取得してください。

### 2. `local.properties` に以下を追記

MAPS\_API\_KEY=あなたのAPIキー

以上で、APIキーがアプリに埋め込まれ、地図機能が有効になります。


