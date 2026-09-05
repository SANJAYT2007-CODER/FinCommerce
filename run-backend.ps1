Set-Location -Path "$PSScriptRoot\fincommerce\backend"
Write-Host "[FinCommerce] Starting Backend Server on http://localhost:8085..." -ForegroundColor Green
.\mvnw.cmd spring-boot:run
