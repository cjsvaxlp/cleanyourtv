# 构建与电视

## 项目及产物

- 本仓库 `launcher/`，包名 `com.gathin.tvhome`，启动组件 `com.gathin.tvhome/.MainActivity`。
- JDK17、SDK34、项目自带 Gradle wrapper；release 开启 R8 和资源压缩，沿用 debug signingConfig。
- 标准构建：在 launcher 内执行 `./gradlew assembleRelease`。
- APK：`launcher/app/build/outputs/apk/release/app-release.apk`，2026-10-21 电视旧版391413字节；2026-10-22 v2.6合并版见实际产物，后续以实际产物为准，不含外置壁纸。
- 定制包无公开下载链接；从本仓库构建，或使用电脑上的 `Documents/Codex/2026-10-20/lian/outputs/港岛时光/港岛时光桌面.apk`。上游发布包不等于此定制包。

## 当前电脑的已验证环境

以下仅是原电脑本地路径，不是项目依赖；换机应重新定位 JDK/SDK/签名。原构建目录基础路径为 `/Users/chenjinsong/Documents/Codex/2026-10-20/lian/work`。

- JDK：`jdk-17.0.20.1+1/Contents/Home`
- SDK：`android-sdk`
- GRADLE_USER_HOME：`gradle`
- `-Dandroid.user.home=基础路径/android-user`
- `-Duser.home=基础路径/build-home`：现有覆盖安装的签名来源，必须保持稳定。不要复制密钥进仓库。

在本仓库根目录，替换为实际环境路径后构建：

```sh
JAVA_HOME=/实际JDK17路径 GRADLE_USER_HOME=/实际Gradle缓存路径 \
./launcher/gradlew --project-dir launcher assembleRelease --console=plain \
-Dandroid.user.home=/实际android-user路径 -Duser.home=/实际build-home路径
```

Android Studio 使用自己的 debug key 可能无法覆盖现有电视安装。安装前比对签名，不为解决冲突卸载。`local.properties`、签名和缓存不提交。

## 用户电视及数据

Sony BRAVIA 4K VH22，国行 Android12；最近地址 `192.168.68.56:5555`，每次先检查实际连接及授权，不假定永远不变。

- 主题：`/sdcard/Android/data/com.gathin.tvhome/files/config.json`
- 壁纸：上述 files 下的 `wallpapers/`
- 主题选择：SharedPreferences `appearance/mode`
- 固定应用：SharedPreferences `dock_choices/apps`，优先于主题默认值。
- 用户的固定应用最近为 YouTube、影视仓、File Manager+、Emby，以实时配置为准。
- 原电视系统高对比文字开启；不要全局更改辅助功能。顶部按钮由 CrispTextView 绘制字形路径避开系统文字描边，并禁用标签滚动；不要改回导致中间网络按钮空白的绘制方式。

修改电视配置前备份实际文件和可访问的应用选择。不要直接推送历史 config.json 覆盖用户的新设置。仅更新APK时使用同签名 `adb -s 地址 install -r APK路径`，再只重启本桌面；保留原默认HOME及系统组件。

临时预览可先force-stop，再 `am start -W -n com.gathin.tvhome/.MainActivity --es preview_theme light`（或dark）。结束后force-stop并不带extra启动；预览不应修改用户已保存模式。

验证主题设置实际往返切换、三个顶部按钮、应用库浅色、固定应用长按操作、HOME/输入源和壁纸。调试期间不关闭已获授权的网络/USB调试。不自动安装、提交或推送与当前用户请求无关的变更。

模拟器（API36）直接adb push创建的wallpapers目录可能对应用不可读；通过ThemeProvider写入文件。先推入/data/local/tmp，再content write导入，源和目标不能是同一文件。

模拟器时间：用户在上海，使用Asia/Shanghai，与程序Asia/Hong_Kong同为UTC+8。先用date核对日期及时间；恢复旧快照可能停在旧日期，即使auto_time=1也未校正。必要时通过cmd alarm set-time（当前UTC毫秒时间戳）和set-timezone校正模拟器，保留自动校时。校正后保存default_boot；勿据此更改实体电视时间。最新用户需求及实现细节见wallpaper.md和SKILL.md，170项测试通过。
