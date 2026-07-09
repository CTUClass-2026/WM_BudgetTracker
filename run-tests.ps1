# run-tests.ps1 - Compile sources and run JUnit Platform standalone console
# Usage: Powershell: .\run-tests.ps1

Set-StrictMode -Version Latest
$base = Split-Path -Parent $MyInvocation.MyCommand.Path
Push-Location $base

$libDir = "$base\lib"
$targetDir = "$base\target\tmp-classes"
$junitJar = "$libDir\junit-platform-console-standalone-1.10.0.jar"

if (-not (Test-Path $junitJar)) {
    Write-Host 'JUnit standalone jar not found in lib/. Please download junit-platform-console-standalone-1.10.0.jar into lib/'
    Pop-Location
    exit 1
}

if (-not (Test-Path $targetDir)) {
    New-Item -ItemType Directory -Path $targetDir -Force | Out-Null
}

$javac = 'javac'
$java = 'java'

function Compile-Sources([bool]$includeAssertJSwingTests = $false) {
    Write-Host "Compiling sources (includeAssertJSwingTests=$includeAssertJSwingTests)..."
    $mainFiles = Get-ChildItem -Path 'src/main/java/com/mycompany/personalfinancetrackerctu/*.java' -File | ForEach-Object { $_.FullName }
    if ($includeAssertJSwingTests) {
        $testFiles = Get-ChildItem -Path 'src/test/java/com/mycompany/personalfinancetrackerctu/*.java' -File | ForEach-Object { $_.FullName }
    } else {
        $testFiles = Get-ChildItem -Path 'src/test/java/com/mycompany/personalfinancetrackerctu/*.java' -File | Where-Object { $_.Name -notlike '*AssertJSwing*' } | ForEach-Object { $_.FullName }
    }

        $all = $mainFiles + $testFiles
        # include lib/* on classpath if any jars exist
        $libGlob = "$libDir\*"
        $cpForCompile = "$junitJar;$libGlob"
        & $javac -d $targetDir -cp $cpForCompile $all
    return $LASTEXITCODE
}

# initial compile without AssertJ tests
$assertjPresent = (Get-ChildItem -Path $libDir -Filter 'assertj-swing*.jar' -ErrorAction SilentlyContinue | Measure-Object).Count
if ($assertjPresent -gt 0) {
    $rc = Compile-Sources $true
} else {
    $rc = Compile-Sources $false
}

if ($rc -ne 0) {
    Write-Error 'Compilation failed'
    Pop-Location
    exit $rc
}

# If AssertJ not present, try to download minimal jars from Maven Central
if ($assertjPresent -eq 0) {
    Write-Host 'AssertJ-Swing not detected; attempting to download required jars from Maven Central...'
    if (-not (Test-Path $libDir)) { New-Item -ItemType Directory -Path $libDir | Out-Null }

    $toDownload = @(
        @{ url = 'https://repo1.maven.org/maven2/org/assertj/assertj-swing-junit/3.17.1/assertj-swing-junit-3.17.1.jar'; dest = Join-Path $libDir 'assertj-swing-junit-3.17.1.jar' },
        @{ url = 'https://repo1.maven.org/maven2/org/assertj/assertj-swing/3.17.1/assertj-swing-3.17.1.jar'; dest = Join-Path $libDir 'assertj-swing-3.17.1.jar' },
        @{ url = 'https://repo1.maven.org/maven2/org/assertj/assertj-core/3.24.2/assertj-core-3.24.2.jar'; dest = Join-Path $libDir 'assertj-core-3.24.2.jar' }
    )

    foreach ($item in $toDownload) {
        if (-not (Test-Path $item.dest)) {
            try {
                Write-Host "Downloading $($item.url) -> $($item.dest)"
                Invoke-WebRequest -Uri $item.url -OutFile $item.dest -ErrorAction Stop
            } catch {
                Write-Warning "Failed to download $($item.url): $_"
            }
        } else {
            Write-Host "File $($item.dest) already exists; skipping."
        }
    }

    $assertjPresent = (Get-ChildItem -Path $libDir -Filter 'assertj-swing*.jar' -ErrorAction SilentlyContinue | Measure-Object).Count
    if ($assertjPresent -gt 0) {
        Write-Host 'Recompiling including AssertJ tests...'
        $rc2 = Compile-Sources $true
        if ($rc2 -ne 0) {
            Write-Error 'Compilation including AssertJ tests failed'
            Pop-Location
            exit $rc2
        }
    } else {
        Write-Host 'AssertJ jars not available; continuing without AssertJ tests.'
    }
}

    $runArgs = @()
    if ((Get-ChildItem -Path $libDir -Filter 'assertj-swing*.jar' -ErrorAction SilentlyContinue).Count -gt 0) {
        $combined = "$targetDir;$libDir\*"
        $runArgs += "--class-path"; $runArgs += $combined
    } else {
        $runArgs += "-cp"; $runArgs += $targetDir
    }
    $runArgs += "--scan-class-path"
    Write-Host 'Running tests...'
    # build classpath for launcher
    if ((Get-ChildItem -Path $libDir -Filter 'assertj-swing*.jar' -ErrorAction SilentlyContinue).Count -gt 0) {
        $cpRun = "$junitJar;$targetDir;$libDir\*"
    } else {
        $cpRun = "$junitJar;$targetDir"
    }
    & $java -cp $cpRun org.junit.platform.console.ConsoleLauncher --scan-class-path
    $exitCode = $LASTEXITCODE

Pop-Location
exit $exitCode
