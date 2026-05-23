param(
    [string]$ImageName = "md-searcher",
    [string]$Tag = "local",
    [int]$HostPort = 8081,
    [switch]$NoMaven,
    [switch]$Push,
    [string]$DockerHubUsername = $env:DOCKERHUB_USERNAME,
    [string]$DockerHubToken = $env:DOCKERHUB_TOKEN
)

$ErrorActionPreference = "Stop"

function Invoke-Step {
    param(
        [string]$Name,
        [scriptblock]$Script
    )

    Write-Host ""
    Write-Host "==> $Name"
    & $Script
}

$localImage = "${ImageName}:${Tag}"
$containerName = ($ImageName + "-local-test").ToLowerInvariant()

Invoke-Step "Checking Docker" {
    docker info | Out-Null
    docker --version
}

if (-not $NoMaven) {
    Invoke-Step "Building WAR with Maven" {
        mvn clean package -DskipTests
    }
}

Invoke-Step "Building Docker image $localImage" {
    docker build -t $localImage .
}

try {
    Invoke-Step "Removing previous test container if present" {
        $existing = docker ps -aq --filter "name=^/${containerName}$"
        if ($existing) {
            docker rm -f $containerName | Out-Null
        }
    }

    Invoke-Step "Starting test container on http://localhost:$HostPort" {
        docker run --name $containerName -d -p "${HostPort}:8080" $localImage | Out-Null
    }

    Invoke-Step "Running HTTP smoke test" {
        $deadline = (Get-Date).AddSeconds(30)
        $lastError = $null

        do {
            try {
                $response = Invoke-WebRequest -UseBasicParsing "http://localhost:$HostPort/" -TimeoutSec 5
                if ($response.StatusCode -eq 200) {
                    Write-Host "HTTP status: 200"
                    return
                }

                $lastError = "Unexpected HTTP status: $($response.StatusCode)"
            } catch {
                $lastError = $_.Exception.Message
                Start-Sleep -Seconds 1
            }
        } while ((Get-Date) -lt $deadline)

        throw "Smoke test failed: $lastError"
    }

    if ($Push) {
        Invoke-Step "Pushing image to Docker Hub" {
            if ([string]::IsNullOrWhiteSpace($DockerHubUsername)) {
                throw "DOCKERHUB_USERNAME is missing."
            }
            if ([string]::IsNullOrWhiteSpace($DockerHubToken)) {
                throw "DOCKERHUB_TOKEN is missing."
            }

            $remoteTag = "${DockerHubUsername}/${ImageName}:${Tag}"
            $remoteLatest = "${DockerHubUsername}/${ImageName}:latest"

            $DockerHubToken | docker login -u $DockerHubUsername --password-stdin
            docker tag $localImage $remoteTag
            docker tag $localImage $remoteLatest
            docker push $remoteTag
            docker push $remoteLatest
        }
    }
} finally {
    Invoke-Step "Cleaning up test container" {
        $existing = docker ps -aq --filter "name=^/${containerName}$"
        if ($existing) {
            docker rm -f $containerName | Out-Null
        }
    }
}

Write-Host ""
Write-Host "Docker local test completed successfully."
