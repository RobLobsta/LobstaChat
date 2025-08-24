/*
 * Copyright (C) 2024 Rob Lobsta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.roblobsta.lobstachat.llm

import android.app.ActivityManager
import android.content.Context
import android.os.Environment
import android.os.StatFs
import org.koin.core.annotation.Single

@Single
class DeviceSpecProvider(private val context: Context) {

    fun getAvailableRam(): Long {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return memoryInfo.availMem / 1024 / 1024
    }

    fun getTotalRam(): Long {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return memoryInfo.totalMem / 1024 / 1024
    }

    fun getAvailableDiskSpace(): Long {
        val stat = StatFs(Environment.getExternalStorageDirectory().path)
        return stat.availableBytes / 1024 / 1024 / 1024
    }

    fun hasNpu(): Boolean {
        // There is no standard way to detect an NPU.
        // We can check for the presence of the NNAPI, but this is not a guarantee.
        // For now, we will return false.
        return false
    }

    fun hasGpu(): Boolean {
        // There is no standard way to detect a GPU.
        // We can check for the presence of a hardware-accelerated EGL context,
        // but this is not a guarantee. For now, we will return true as most modern
        // devices have a GPU.
        return true
    }
}
