$folderToWatch = ".\app\src"

$watcher = New-Object System.IO.FileSystemWatcher
$watcher.Path = (Resolve-Path -Path $folderToWatch).Path
$watcher.IncludeSubdirectories = $true
$watcher.EnableRaisingEvents = $true

$global:lastBuildTime = [DateTime]::MinValue

$action = {
    $path = $Event.SourceEventArgs.FullPath
    $now = [DateTime]::Now
    
    # Debounce selama 3 detik agar tidak memicu build berkali-kali saat file di-save
    if (($now - $global:lastBuildTime).TotalSeconds -gt 3) {
        $global:lastBuildTime = $now
        
        Write-Host "`n=======================================================" -ForegroundColor Yellow
        Write-Host "[$(Get-Date -Format 'HH:mm:ss')] Perubahan terdeteksi: $path" -ForegroundColor Yellow
        Write-Host "Membangun ulang (Build) dan meng-install aplikasi..." -ForegroundColor Cyan
        
        # Menjalankan gradle installDebug
        $gradleCmd = ".\gradlew.bat"
        & $gradleCmd installDebug
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "Build berhasil! Membuka aplikasi di perangkat/emulator..." -ForegroundColor Green
            # Menjalankan activity utama melalui ADB
            & adb shell am start -n "com.mocare.app/.MainActivity" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER
        } else {
            Write-Host "Build gagal. Silakan periksa error di atas." -ForegroundColor Red
        }
        
        Write-Host "=======================================================" -ForegroundColor Yellow
        Write-Host "Menunggu perubahan file selanjutnya... (Tekan Ctrl+C untuk berhenti)" -ForegroundColor Green
    }
}

Register-ObjectEvent $watcher "Changed" -Action $action | Out-Null
Register-ObjectEvent $watcher "Created" -Action $action | Out-Null
Register-ObjectEvent $watcher "Renamed" -Action $action | Out-Null

Write-Host "=======================================================" -ForegroundColor Yellow
Write-Host "Menonton folder $folderToWatch untuk perubahan..." -ForegroundColor Green
Write-Host "Aplikasi akan otomatis di-build & direstart saat Anda melakukan Save (Ctrl+S)." -ForegroundColor Green
Write-Host "(Tekan Ctrl+C untuk berhenti)" -ForegroundColor Yellow
Write-Host "=======================================================" -ForegroundColor Yellow

# Loop agar script terus berjalan
while ($true) { Start-Sleep -Seconds 1 }
