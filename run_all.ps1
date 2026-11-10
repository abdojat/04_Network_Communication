$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot
$serverProcesses = @()
$results = New-Object System.Collections.Generic.List[string]

function Run-Java([string]$ClassPath, [string]$MainClass, [string[]]$Arguments = @()) {
    Write-Host "`n==== $MainClass ====" -ForegroundColor Cyan
    $output = & java -cp $ClassPath $MainClass @Arguments 2>&1
    if ($LASTEXITCODE -ne 0) { throw "Java command failed: $MainClass`n$output" }
    foreach ($line in $output) { Write-Host $line; $results.Add([string]$line) }
}

try {
    if (-not (Get-Command java -ErrorAction SilentlyContinue)) { throw 'JDK is missing: java not found.' }
    if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) { throw 'Apache Maven is missing: mvn not found.' }
    Write-Host 'Building all original and lab modules...' -ForegroundColor Yellow
    & mvn -q clean package
    if ($LASTEXITCODE -ne 0) { throw 'Maven build failed.' }
    & mvn -q -pl lab dependency:copy-dependencies '-DoutputDirectory=target/dependency'
    if ($LASTEXITCODE -ne 0) { throw 'Cannot copy protobuf-java dependency.' }

    foreach ($port in @(8080, 8081)) {
        $p = Start-Process -FilePath 'java' -ArgumentList @('-cp', 'httpserver/target/classes', 'WebServer', [string]$port) -PassThru -NoNewWindow
        $serverProcesses += $p
    }
    $ready = $false
    for ($i = 0; $i -lt 30; $i++) {
        Start-Sleep -Milliseconds 300
        try {
            $one = Invoke-WebRequest 'http://localhost:8080/status' -TimeoutSec 2
            $two = Invoke-WebRequest 'http://localhost:8081/status' -TimeoutSec 2
            if ($one.StatusCode -eq 200 -and $two.StatusCode -eq 200) { $ready = $true; break }
        } catch { }
    }
    if (-not $ready) { throw 'HTTP servers did not start on ports 8080 and 8081.' }
    Run-Java 'httpclient/target/classes' 'Application'
    Run-Java 'lab/target/classes' 'JsonLab'
    Run-Java 'lab/target/classes' 'SchemaMismatchLab'
    Run-Java 'lab/target/classes' 'JavaSerializationLab'
    Run-Java 'lab/target/classes' 'JavaExceptionLab'
    $protoClassPath = 'lab/target/classes;lab/target/dependency/*'
    Run-Java $protoClassPath 'ProtobufLab'
    Run-Java $protoClassPath 'SchemaRenameLab'
    Run-Java $protoClassPath 'ComparisonLab'

    New-Item -ItemType Directory -Force results | Out-Null
    $results | Set-Content -LiteralPath 'results/run_results.txt' -Encoding UTF8
    Write-Host "`nAll checks PASSED. Results: results/run_results.txt" -ForegroundColor Green
} finally {
    foreach ($p in $serverProcesses) {
        if ($p -and -not $p.HasExited) { Stop-Process -Id $p.Id -Force -ErrorAction SilentlyContinue }
    }
}
