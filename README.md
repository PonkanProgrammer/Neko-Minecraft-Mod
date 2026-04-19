# Neko Cat Customizer (Minecraft Java / Fabric)

Minecraft Java版で猫をカスタマイズするための軽量Modです。

## 機能
- `Cat Styler` アイテムで猫の模様を順番に切り替え
- しゃがみながら `Cat Styler` を使うと、猫の模様（+ 手懐け済みなら首輪色）をランダム変更
- 各色の染料を手懐け済み猫に使うと首輪の色を直接変更

## 動作環境
- Minecraft 1.20.1
- Fabric Loader 0.15.11+
- Java 17

## 導入
1. Fabric をセットアップ
2. このModをビルドして `build/libs` のjarを `mods` フォルダへ配置

## 操作方法
1. コマンドで `cat_styler` を取得:
   - `/give @p nekocatcustomizer:cat_styler`
2. 猫に右クリック:
   - 通常右クリック: 模様変更
   - しゃがみ右クリック: ランダム変更
3. 手懐け済み猫に染料右クリック: 首輪色変更
