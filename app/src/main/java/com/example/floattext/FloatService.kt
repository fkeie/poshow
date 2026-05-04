package com.example.floattext

import android.app.*
import android.content.*
import android.graphics.*
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.text.ClipboardManager
import android.view.*
import android.widget.TextView
import androidx.core.app.NotificationCompat

class FloatService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var floatView: TextView
    private val channelId = "float_channel_01"

    // 用于接收更新文本的广播（预留的API接口）
    private val updateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val newText = intent?.getStringExtra("text")
            if (!newText.isNullOrEmpty()) {
                floatView.text = newText
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(1, createNotification())
        
        // 注册广播接收器，监听更新指令
        registerReceiver(updateReceiver, IntentFilter("com.float.text.UPDATE_ACTION"))

        setupFloatingWindow()
    }

    private fun setupFloatingWindow() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        // 创建文本视图
        floatView = TextView(this).apply {
            text = "等待内容..." // 初始占位文字
            setTextColor(Color.WHITE)
            textSize = 12f // 小巧的字号
            setBackgroundColor(Color.TRANSPARENT)
            setPadding(20, 10, 20, 10)
            
            // 长按复制功能
            setOnLongClickListener {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = ClipData.newPlainText("float_text", text)
                clipboard.setPrimaryClip(clip)
                true
            }
        }

        // 设置悬浮窗参数
        val params = WindowManager.LayoutParams().apply {
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            }
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            format = PixelFormat.TRANSLUCENT
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            y = 60 // 距离屏幕顶部60像素
        }

        try {
            windowManager.addView(floatView, params)
        } catch (e: Exception) {
            // 权限丢失时停止自身
            stopSelf()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "悬浮窗服务通道", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("悬浮文本服务运行中")
            .setContentText("点击返回应用")
            .setSmallIcon(android.R.drawable.sym_def_app_icon)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(updateReceiver)
        if (::windowManager.isInitialized && ::floatView.isInitialized) {
            windowManager.removeView(floatView)
        }
    }
}