# 壁纸时间算法

## 用户目标

时区 Asia/Hong_Kong；默认浅色06:00–19:00，深色19:00–次日06:00。以分钟计算，文件日夜分组并按文件名字典序排列。未来采用 light-/dark- 前缀时应同时处理旧文件兼容和迁移，不能只改说明。

```text
M = hour*60 + minute
L = 360; D = 1140
mod(x) = (x%1440+1440)%1440
TL = mod(D-L); TD = 1440-TL
isLight = mod(M-L) < TL
S = isLight ? L : D
T = isLight ? TL : TD
N = 当前主题可用图片数
E = mod(M-S)
index = min(N-1, floor(E*N/T))
```

N=1 整段一张；N=0 使用纯背景，不借用另一组。损坏图片应剔除后重算。第 i 张起点为 S+ceil(i*T/N)，不要先将T/N取整再累加。夜间必须跨午夜计算。5张日景各156分钟，2张夜景各330分钟。每分钟检查只用于切换边界，不代表每分钟换图。

手动选择深浅主题与当前钟点不匹配时，必须明确图片分组与时段进度规则后实现；目前不要擅自改已有个人时间表。

## 当前个人源码实际行为

`HongKongWallpaper.java` 固定文件名及数组：

| 时段 | 图片 |
|---|---|
| 06:00–10:00 | hk-dawn.png |
| 10:00–12:20 | hk-day.png |
| 12:20–14:40 | hk-day-kennedy.jpg |
| 14:40–17:00 | hk-day-disney.jpg |
| 17:00–19:00 | hk-dusk.png |
| 19:00–00:30 | hk-night.png |
| 00:30–06:00 | hk-night-peak.jpg |

主题由 `GlassPalette.java` 控制，壁纸由 `WallpaperPlayer.java` 调用上述时间表；当前手动主题与壁纸分组不是同一选择器。`schedule.properties`、`ThemeSchedule.java` 属于旧分享版，不存在于当前个人项目的算法中。

电视图片位于 `/sdcard/Android/data/com.gathin.tvhome/files/wallpapers/`。新增摄影作品时核对来源许可及 `WallpaperPlayer` 的署名；不要把现有署名误用于新图片。
