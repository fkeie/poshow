package com.example.poshow

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. 检查是否有悬浮窗权限
        if (!Settings.canDrawOverlays(this)) {
            // 没有权限，弹个提示并跳转到授权页面
            Toast.makeText(this, "请授予悬浮窗权限", Toast.LENGTH_SHORT).show()
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, 0)
            return
        }

        // 2. 设置 Activity 窗口属性，使其悬停在桌面
        val params = window.attributes
        params.width = WindowManager.LayoutParams.WRAP_CONTENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        params.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        params.y = 100 // 距离屏幕顶部 100 像素
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                       WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        
        window.attributes = params
        window.setBackgroundDrawableResource(android.R.color.transparent) // 背景透明

        // 3. 放一个最简单的文本到界面上
        val textView = TextView(this)
        textView.text = "Hello 悬浮!"
        textView.setTextColor(android.graphics.Color.WHITE)
        textView.textSize = 18f
        textView.setBackgroundColor(0x88000000) // 半透明黑色背景
        textView.setPadding(30, 15, 30, 15)

        setContentView(textView)
    }

    // 用户从权限设置页面返回后的回调
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (Settings.canDrawOverlays(this)) {
            // 授权成功，重新执行悬浮逻辑
            recreate() 
        } else {
            Toast.makeText(this, "权限被拒绝，无法显示悬浮窗", Toast.LENGTH_SHORT).show()
            finish() // 关闭应用
        }
    }
}
