package com.example.floattext

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. 请求通知权限（如果需要的话，虽然我们不用通知了）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
        }

        // 2. 检查悬浮窗权限
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, 0)
            return
        }

        // 3. 设置为无背景的悬浮 Activity
        setupFloatingWindow()
    }

    private fun setupFloatingWindow() {
        // 设置窗口属性
        window.setBackgroundDrawableResource(android.R.color.transparent)
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        
        val params = window.attributes
        params.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        params.y = 60
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                       WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                       WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        
        window.attributes = params

        // 创建文本视图
        val textView = TextView(this).apply {
            text = "等待内容..."
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 12f
            setPadding(20, 10, 20, 10)
            setOnLongClickListener {
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
                clipboard.setPrimaryClip(android.content.ClipData.newPlainText("float", text))
                true
            }
        }
        setContentView(textView)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (Settings.canDrawOverlays(this)) {
            setupFloatingWindow()
        } else {
            finish()
        }
    }
}
