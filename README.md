# light-sensor-android

[<ins>こちら</ins>](https://zenn.dev/joo_hashi/articles/decc1133f19c5e)&thinsp;を参考にした&thinsp;Android&thinsp;で明るさを表示するテストアプリ

- トグルスイッチで入切

- 更新間隔を&thinsp;1&thinsp;〜&thinsp;60&thinsp;秒でスライダーで設定

<img height=256 src="">ここに画像を挿入 [TODO]

<br>

### ビルド手順

- ソースを展開後、リポジトリトップに `local.properties` を作成して&thinsp;SDK&thinsp;のパスを指定。例えば下記のように

  ```
  sdk.dir=/Users/ユーザ名/Library/Android/sdk
  ```

- Android Studio&thinsp;以外にターミナルでもビルド・インストールできるよう [`Makefile`](Makefile) あり

  `make asm` アセンブル

  `make build` ビルド

  `make run` アプリ起動

  `make dev` ビルド＆アプリ起動

<br>

### ビルド確認環境

```
Android Studio Panda 4 | 2025.3.4
Build #AI-253.32098.37.2534.15232325, built on April 18, 2026
Runtime version: 21.0.10+-117844309-b1163.108 x86_64
VM: OpenJDK 64-Bit Server VM by JetBrains s.r.o.
Toolkit: sun.lwawt.macosx.LWCToolkit
macOS 26.4.1
```

<br>

### 動作確認環境（2026年5月）

- Garaxy Tab S8 Ultra (Android 16)

<br>
<hr>
