# MongoDB Atlas Database Transfer Script
# Transfers 100FragenGame from source cluster to target cluster using mongodump/mongorestore

# Configuration
$sourceUri = "mongodb+srv://springConnect:angewandteProgrammierung@webcluster.fy8tk7i.mongodb.net/?retryWrites=true&w=majority"
$sourceDb = "100FragenGame"

$targetUri = "mongodb+srv://cedric_db_user:lSvZVpWZaOYCj9Uh@app.z6k3bts.mongodb.net/?appName=app"
$targetDb = "100FragenGame"  # Change this if target DB name is different

$archiveFile = "C:\temp\100FragenGame-backup.archive.gz"
$mongoToolsPath = "C:\Program Files\MongoDB\Tools\100\bin"
$mongodump = "$mongoToolsPath\mongodump.exe"
$mongorestore = "$mongoToolsPath\mongorestore.exe"

# Colors for output
$Green = @{ ForegroundColor = "Green" }
$Yellow = @{ ForegroundColor = "Yellow" }
$Red = @{ ForegroundColor = "Red" }
$Cyan = @{ ForegroundColor = "Cyan" }

Write-Host "========================================" @Cyan
Write-Host "MongoDB Atlas Database Transfer Script" @Cyan
Write-Host "========================================" @Cyan

# 1. Verify MongoDB tools exist
Write-Host "`n[1/5] Checking MongoDB Database Tools..." @Yellow
if (-not (Test-Path $mongodump)) {
    Write-Host "ERROR: mongodump not found at $mongodump" @Red
    exit 1
}
if (-not (Test-Path $mongorestore)) {
    Write-Host "ERROR: mongorestore not found at $mongorestore" @Red
    exit 1
}
Write-Host "✓ MongoDB tools found" @Green

# 2. Create temp directory
Write-Host "`n[2/5] Creating backup directory..." @Yellow
if (-not (Test-Path C:\temp)) {
    New-Item -ItemType Directory -Path C:\temp -Force | Out-Null
}
Write-Host "✓ Temp directory ready: C:\temp" @Green

# 3. Display transfer plan
Write-Host "`n[3/5] Transfer Plan:" @Yellow
Write-Host "  Source URI: $sourceUri"
Write-Host "  Source DB:  $sourceDb"
Write-Host "  Target URI: $targetUri"
Write-Host "  Target DB:  $targetDb"
Write-Host "  Archive:    $archiveFile"

# Confirmation
Write-Host "`n⚠️  IMPORTANT:" @Yellow
Write-Host "  - Ensure your public IP is whitelisted in BOTH Atlas projects"
Write-Host "  - Source cluster: Network Access → IP Whitelist"
Write-Host "  - Target cluster: Network Access → IP Whitelist"
Write-Host "`nProceed? (yes/no)"
$confirm = Read-Host

if ($confirm -ne "yes") {
    Write-Host "Cancelled." @Red
    exit 0
}

# 4. Create dump from source
Write-Host "`n[4/5] Creating dump from source cluster..." @Yellow
Write-Host "This may take a few minutes depending on database size..." @Cyan

try {
    & $mongodump `
        --uri="$sourceUri" `
        --db="$sourceDb" `
        --archive="$archiveFile" `
        --gzip `
        --verbose

    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: mongodump failed with exit code $LASTEXITCODE" @Red
        Write-Host "Common issues:" @Yellow
        Write-Host "  1. Public IP not whitelisted in source cluster" @Yellow
        Write-Host "  2. Authentication failed (check username/password)" @Yellow
        Write-Host "  3. Network connectivity issue" @Yellow
        exit 1
    }

    $fileSize = (Get-Item $archiveFile).Length / 1MB
    Write-Host "✓ Dump created successfully" @Green
    Write-Host "  File size: $([Math]::Round($fileSize, 2)) MB" @Green
}
catch {
    Write-Host "ERROR: Failed to create dump: $_" @Red
    exit 1
}

# 5. Restore to target
Write-Host "`n[5/5] Restoring to target cluster..." @Yellow
Write-Host "This may take a few minutes depending on database size..." @Cyan

$restoreConfirm = Read-Host "Restore now? (yes/no)"
if ($restoreConfirm.ToLower() -ne "yes") {
    Write-Host "Restore cancelled. Archive saved at: $archiveFile" @Yellow
    exit 0
}

try {
    & $mongorestore `
        --uri="$targetUri" `
        --archive="$archiveFile" `
        --gzip `
        --verbose

    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: mongorestore failed with exit code $LASTEXITCODE" @Red
        Write-Host "Common issues:" @Yellow
        Write-Host "  1. Public IP not whitelisted in target cluster" @Yellow
        Write-Host "  2. Authentication failed (check username/password)" @Yellow
        Write-Host "  3. Network connectivity issue" @Yellow
        Write-Host "  4. Insufficient permissions on target database" @Yellow
        exit 1
    }

    Write-Host "✓ Restore completed successfully" @Green
}
catch {
    Write-Host "ERROR: Failed to restore: $_" @Red
    exit 1
}

# 6. Verification instructions
Write-Host "`n========================================" @Green
Write-Host "TRANSFER COMPLETE!" @Green
Write-Host "========================================" @Green

Write-Host "`nNext steps:" @Yellow
Write-Host "1. Verify the restore in target cluster:" @Yellow
Write-Host "   mongosh '$targetUri'" @Cyan
Write-Host "   > use $targetDb" @Cyan
Write-Host "   > db.getCollectionNames()" @Cyan
Write-Host "   > db.questions.countDocuments()" @Cyan
Write-Host "   > db.answers.countDocuments()" @Cyan

Write-Host "`n2. (Optional) Update application.properties if switching to new DB:" @Yellow
Write-Host "   Edit src/main/resources/application.properties" @Cyan
Write-Host "   Update spring.data.mongodb.uri to target URI" @Cyan

Write-Host "`n3. Clean up:" @Yellow
Write-Host "   Delete archive: Remove-Item '$archiveFile'" @Cyan

Write-Host "`nArchive saved at: $archiveFile" @Cyan
