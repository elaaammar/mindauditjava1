# Script pour tester les APIs REST
Write-Host "=== TEST DES APIs REST ===" -ForegroundColor Green

# Test 1: Ping
Write-Host "`n1. Test PING..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:9000/api/ping" -Method Get
    Write-Host "✓ PING fonctionne!" -ForegroundColor Green
    Write-Host ($response | ConvertTo-Json)
} catch {
    Write-Host "✗ PING échoué: $_" -ForegroundColor Red
}

# Test 2: Liste des utilisateurs
Write-Host "`n2. Test GET /api/users..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:9000/api/users" -Method Get
    Write-Host "✓ GET users fonctionne!" -ForegroundColor Green
    Write-Host ($response | ConvertTo-Json -Depth 5)
} catch {
    Write-Host "✗ GET users échoué: $_" -ForegroundColor Red
}

# Test 3: Statistiques
Write-Host "`n3. Test GET /api/stats/all..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:9000/api/stats/all" -Method Get
    Write-Host "✓ GET stats fonctionne!" -ForegroundColor Green
    Write-Host ($response | ConvertTo-Json -Depth 5)
} catch {
    Write-Host "✗ GET stats échoué: $_" -ForegroundColor Red
}

# Test 4: Login
Write-Host "`n4. Test POST /api/auth/login..." -ForegroundColor Yellow
try {
    $body = @{
        email = "admin@example.com"
        password = "admin123"
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "http://localhost:9000/api/auth/login" -Method Post -Body $body -ContentType "application/json"
    Write-Host "✓ LOGIN fonctionne!" -ForegroundColor Green
    Write-Host ($response | ConvertTo-Json -Depth 5)
    
    # Sauvegarder le token pour les tests suivants
    $global:token = $response.data.token
    Write-Host "`nToken sauvegardé: $global:token" -ForegroundColor Cyan
} catch {
    Write-Host "✗ LOGIN échoué: $_" -ForegroundColor Red
}

# Test 5: Current User (avec token)
if ($global:token) {
    Write-Host "`n5. Test GET /api/auth/current (avec token)..." -ForegroundColor Yellow
    try {
        $headers = @{
            "Authorization" = $global:token
        }
        $response = Invoke-RestMethod -Uri "http://localhost:9000/api/auth/current" -Method Get -Headers $headers
        Write-Host "✓ GET current user fonctionne!" -ForegroundColor Green
        Write-Host ($response | ConvertTo-Json -Depth 5)
    } catch {
        Write-Host "✗ GET current user échoué: $_" -ForegroundColor Red
    }
}

Write-Host "`n=== FIN DES TESTS ===" -ForegroundColor Green
