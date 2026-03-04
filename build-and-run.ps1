# RICB Backend Application - PowerShell Build & Run Script
# This script provides an easy way to build and run the Spring Boot application

param(
    [ValidateSet('build', 'clean', 'run', 'clean-build-run', 'run-jar')]
    [string]$Action = $null
)

# Color codes for output
$ColorSuccess = "Green"
$ColorError = "Red"
$ColorInfo = "Cyan"
$ColorWarning = "Yellow"

function Write-Success {
    param([string]$Message)
    Write-Host $Message -ForegroundColor $ColorSuccess
}

function Write-Error-Custom {
    param([string]$Message)
    Write-Host $Message -ForegroundColor $ColorError
}

function Write-Info {
    param([string]$Message)
    Write-Host $Message -ForegroundColor $ColorInfo
}

function Write-Warning-Custom {
    param([string]$Message)
    Write-Host $Message -ForegroundColor $ColorWarning
}

function Check-Java {
    try {
        $version = java -version 2>&1
        Write-Success "Java found: $version"
        return $true
    } catch {
        Write-Error-Custom "ERROR: Java not found. Please install Java 21 and add it to PATH"
        Write-Info "Download: https://www.oracle.com/java/technologies/downloads/#java21"
        return $false
    }
}

function Build-Project {
    Write-Info "Building project (downloading dependencies)..."
    & .\mvnw.cmd clean package -DskipTests
    if ($LASTEXITCODE -eq 0) {
        Write-Success "BUILD SUCCESSFUL!"
        return $true
    } else {
        Write-Error-Custom "BUILD FAILED! Check errors above."
        return $false
    }
}

function Run-Application {
    Write-Info "Starting Spring Boot application..."
    Write-Info "Application will be available at: http://localhost:8080"
    Write-Info "Press Ctrl+C to stop the application"
    Write-Info ""
    & .\mvnw.cmd spring-boot:run
}

function Run-JAR {
    if (-not (Test-Path "target/ricb_api-0.0.1-SNAPSHOT.jar")) {
        Write-Error-Custom "ERROR: JAR file not found. Please build first."
        return $false
    }
    Write-Info "Starting application from JAR..."
    Write-Info "Application will be available at: http://localhost:8080"
    Write-Info "Press Ctrl+C to stop the application"
    Write-Info ""
    java -jar target/ricb_api-0.0.1-SNAPSHOT.jar
    return $true
}

function Show-Menu {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "RICB Backend - Spring Boot 3.0" -ForegroundColor Cyan
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Options:" -ForegroundColor Yellow
    Write-Host "  1. Clean and Build (downloads dependencies)"
    Write-Host "  2. Build only (uses cached dependencies)"
    Write-Host "  3. Build and Run"
    Write-Host "  4. Run application (from cached JAR)"
    Write-Host "  5. Show help"
    Write-Host "  6. Exit"
    Write-Host ""
}

function Show-Help {
    Write-Host ""
    Write-Host "RICB Backend Application - Usage Guide" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Usage: .\build-and-run.ps1 [-Action <action>]" -ForegroundColor Green
    Write-Host ""
    Write-Host "Actions:" -ForegroundColor Yellow
    Write-Host "  clean         Clean and build project"
    Write-Host "  build         Build project (cached dependencies)"
    Write-Host "  clean-build-run  Clean, build, and run"
    Write-Host "  run           Start application"
    Write-Host "  run-jar       Run from JAR file"
    Write-Host ""
    Write-Host "Examples:" -ForegroundColor Green
    Write-Host "  .\build-and-run.ps1 clean"
    Write-Host "  .\build-and-run.ps1 clean-build-run"
    Write-Host "  .\build-and-run.ps1 run-jar"
    Write-Host ""
}

# Main script logic

Write-Info "RICB Backend Application - Build and Run Script"
Write-Info ""

# Check Java first
if (-not (Check-Java)) {
    exit 1
}

# Determine action
if ($Action -eq $null) {
    # Interactive menu
    do {
        Show-Menu
        $choice = Read-Host "Enter your choice (1-6)"

        switch ($choice) {
            "1" {
                if (Build-Project) {
                    Read-Host "Press Enter to continue"
                }
            }
            "2" {
                Write-Info "Building project..."
                & .\mvnw.cmd package -DskipTests
                if ($LASTEXITCODE -eq 0) {
                    Write-Success "BUILD SUCCESSFUL!"
                }
                Read-Host "Press Enter to continue"
            }
            "3" {
                if (Build-Project) {
                    Run-Application
                }
            }
            "4" {
                Run-JAR
            }
            "5" {
                Show-Help
                Read-Host "Press Enter to continue"
            }
            "6" {
                Write-Info "Exiting..."
                exit 0
            }
            default {
                Write-Warning-Custom "Invalid choice. Please try again."
                Read-Host "Press Enter to continue"
            }
        }
    } while ($true)
} else {
    # Direct action mode
    switch ($Action) {
        "clean" {
            if (Build-Project) {
                exit 0
            } else {
                exit 1
            }
        }
        "build" {
            Write-Info "Building project..."
            & .\mvnw.cmd package -DskipTests
            exit $LASTEXITCODE
        }
        "clean-build-run" {
            if (Build-Project) {
                Run-Application
            } else {
                exit 1
            }
        }
        "run" {
            Run-Application
        }
        "run-jar" {
            if (Run-JAR) {
                exit 0
            } else {
                exit 1
            }
        }
    }
}

