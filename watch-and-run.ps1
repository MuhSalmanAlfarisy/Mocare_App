$folderToWatch = ".\app\src"
$projectRoot = (Resolve-Path -Path ".").Path

$watcher = New-Object System.IO.FileSystemWatcher
$watcher.Path = (Resolve-Path -Path $folderToWatch).Path
$watcher.IncludeSubdirectories = $true
$watcher.EnableRaisingEvents = $true

$global:lastBuildTime = [DateTime]::MinValue
$global:selectedDevice = $null

function Get-ConnectedDevices {
    $adbOutput = adb devices 2>$null
    $devices = @()
    foreach ($line in $adbOutput) {
        if ($line -match "^\s*(\S+)\s+device\s*$") {
            $devices += $matches[1]
        }
    }
    return $devices
}

function Format-RelativePath($fullPath) {
    if ($fullPath.StartsWith($projectRoot)) {
        return $fullPath.Substring($projectRoot.Length).TrimStart("\", "/")
    }
    return $fullPath
}

$action = {
    $path = $Event.SourceEventArgs.FullPath
    $now = [DateTime]::Now
    $timeStr = Get-Date -Format 'HH:mm:ss'
    
    # Debounce 3 detik
    if (($now - $global:lastBuildTime).TotalSeconds -gt 3) {
        $global:lastBuildTime = $now
        $relativePath = Format-RelativePath $path
        
        Write-Host "`n=======================================================" -ForegroundColor Cyan
        Write-Host "MOCARE - AUTO BUILD AND RUN" -ForegroundColor Cyan
        Write-Host "=======================================================" -ForegroundColor Cyan
        
        Write-Host "`n[$timeStr] CHANGE DETECTED" -ForegroundColor Yellow
        Write-Host "File: $relativePath" -ForegroundColor White
        
        # 1. Cek Device / Emulator
        $devices = Get-ConnectedDevices
        
        if ($devices.Count -eq 0) {
            Write-Host "`n[$timeStr] DEVICE ERROR" -ForegroundColor Red
            Write-Host "Tidak ada perangkat/emulator yang terhubung via ADB." -ForegroundColor Red
            Write-Host "Silakan sambungkan HP (USB Debugging) atau nyalakan Emulator." -ForegroundColor Yellow
            Write-Host "`n=======================================================" -ForegroundColor DarkGray
            Write-Host "READY - WATCHING FOR CHANGES (Tekan Ctrl+C untuk berhenti)" -ForegroundColor Green
            Write-Host "=======================================================" -ForegroundColor DarkGray
            return
        }
        
        $targetDevice = $null
        if ($devices.Count -eq 1) {
            $targetDevice = $devices[0]
            Write-Host "`n[$timeStr] TARGET DEVICE" -ForegroundColor Green
            Write-Host "Device: $targetDevice" -ForegroundColor White
        } else {
            Write-Host "`n[$timeStr] MULTIPLE DEVICES DETECTED ($($devices.Count))" -ForegroundColor Cyan
            for ($i = 0; $i -lt $devices.Count; $i++) {
                Write-Host "   [$($i+1)] $($devices[$i])" -ForegroundColor White
            }
            if ($global:selectedDevice -and ($devices -contains $global:selectedDevice)) {
                $targetDevice = $global:selectedDevice
            } else {
                $targetDevice = $devices[0]
                $global:selectedDevice = $targetDevice
            }
            Write-Host "Target Selected: $targetDevice" -ForegroundColor Green
        }
        
        $env:ANDROID_SERIAL = $targetDevice
        
        # 2. Build Process
        $buildStartTime = Get-Date
        Write-Host "`n[$(Get-Date -Format 'HH:mm:ss')] BUILD STARTED" -ForegroundColor Cyan
        Write-Host "Running: Gradle installDebug" -ForegroundColor DarkGray
        Write-Host "-------------------------------------------------------" -ForegroundColor DarkGray
        
        # Eksekusi gradlew via cmd.exe dan tampilkan output Gradle secara real-time
        cmd.exe /c ".\gradlew.bat installDebug"
        $buildExitCode = $LASTEXITCODE
        $buildDuration = [math]::Round(((Get-Date) - $buildStartTime).TotalSeconds, 1)
        
        Write-Host "-------------------------------------------------------" -ForegroundColor DarkGray
        
        if ($buildExitCode -eq 0) {
            Write-Host "`n[$(Get-Date -Format 'HH:mm:ss')] BUILD SUCCESSFUL (in ${buildDuration}s)" -ForegroundColor Green
            
            # 3. Installing & Launching
            Write-Host "`n[$(Get-Date -Format 'HH:mm:ss')] INSTALLING AND LAUNCHING APK" -ForegroundColor Cyan
            Write-Host "Target:  $targetDevice" -ForegroundColor White
            Write-Host "Package: com.mocare.app/.MainActivity" -ForegroundColor White
            
            $launchOutput = adb -s $targetDevice shell am start -n "com.mocare.app/.MainActivity" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER 2>&1
            Write-Host $launchOutput -ForegroundColor DarkGray
            
            Write-Host "`n[$(Get-Date -Format 'HH:mm:ss')] APPLICATION RUNNING" -ForegroundColor Green
        } else {
            Write-Host "`n[$(Get-Date -Format 'HH:mm:ss')] BUILD FAILED (exit code $buildExitCode)" -ForegroundColor Red
            Write-Host "Silakan periksa pesan error Gradle di atas untuk detailnya." -ForegroundColor Yellow
        }
        
        Write-Host "`n=======================================================" -ForegroundColor DarkGray
        Write-Host "READY - WATCHING FOR CHANGES (Tekan Ctrl+C untuk berhenti)" -ForegroundColor Green
        Write-Host "=======================================================" -ForegroundColor DarkGray
    }
}

# Bersihkan event watcher lama jika ada (agar tidak bentrok jika dijalankan ulang)
Get-EventSubscriber | Where-Object { $_.SourceObject -is [System.IO.FileSystemWatcher] } | Unregister-Event -Force

Register-ObjectEvent $watcher "Changed" -Action $action | Out-Null
Register-ObjectEvent $watcher "Created" -Action $action | Out-Null
Register-ObjectEvent $watcher "Renamed" -Action $action | Out-Null

Write-Host "=======================================================" -ForegroundColor Cyan
Write-Host "MOCARE - AUTO BUILD AND RUN WATCHER" -ForegroundColor Cyan
Write-Host "=======================================================" -ForegroundColor Cyan
Write-Host "Menonton folder $folderToWatch untuk perubahan file..." -ForegroundColor Green
Write-Host "Aplikasi akan otomatis di-build, di-install dan di-restart saat Anda Save (Ctrl+S)." -ForegroundColor Green
Write-Host "=======================================================" -ForegroundColor DarkGray
Write-Host "READY - WATCHING FOR CHANGES (Tekan Ctrl+C untuk berhenti)" -ForegroundColor Green
Write-Host "=======================================================" -ForegroundColor DarkGray

try {
    # Wait-Event lebih direkomendasikan daripada Start-Sleep untuk loop event watcher di PowerShell
    while ($true) {
        Wait-Event -Timeout 1
    }
} finally {
    Get-EventSubscriber | Where-Object { $_.SourceObject -is [System.IO.FileSystemWatcher] } | Unregister-Event -Force
}
