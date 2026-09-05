@echo off
cd /d "%~dp0fincommerce\backend"
echo [FinCommerce] Starting Backend Server on http://localhost:8085...
call .\mvnw.cmd spring-boot:run
