package com.example.floattext

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 1. 请求通知栏权限（Android 13及以上需要）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
        }

        // 2. 检查并申请悬浮窗权限
        if (Settings.canDrawOverlays(this)) {
            startFloatService()
            finish() // 权限有了就直接启动并关闭界面
        } else {
            // 去系统设置页面申请悬浮窗权限
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, 0)
        }
    }

    private fun startFloatService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, FloatService::class.java))
        } else {
            startService(Intent(this, FloatService::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (Settings.canDrawOverlays(this)) {
            startFloatService()
        }
        finish() // 无论是否授权，处理后都关闭这个透明界面
    }
}
