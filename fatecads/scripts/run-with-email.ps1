param(
    [string]$HostName = "smtp.gmail.com",
    [int]$Port = 587,
    [string]$Username,
    [string]$From
)

$ErrorActionPreference = "Stop"

if ([string]::IsNullOrWhiteSpace($Username)) {
    $Username = Read-Host "Email remetente"
}

if ([string]::IsNullOrWhiteSpace($From)) {
    $From = $Username
}

$securePassword = Read-Host "Senha de app do email" -AsSecureString
$passwordPtr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)

try {
    $plainPassword = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($passwordPtr)
    if ($HostName -eq "smtp.gmail.com") {
        $plainPassword = $plainPassword -replace "\s", ""
    }

    $env:FATECADS_MAIL_ENABLED = "true"
    $env:FATECADS_MAIL_FROM = $From
    $env:SPRING_MAIL_HOST = $HostName
    $env:SPRING_MAIL_PORT = $Port.ToString()
    $env:SPRING_MAIL_USERNAME = $Username
    $env:SPRING_MAIL_PASSWORD = $plainPassword
    $env:SPRING_MAIL_SMTP_AUTH = "true"
    $env:SPRING_MAIL_SMTP_STARTTLS = "true"

    Write-Host "Email habilitado para $Username via ${HostName}:$Port"

    if (Get-Command mvn -ErrorAction SilentlyContinue) {
        & mvn spring-boot:run
    } else {
        & .\mvnw.cmd spring-boot:run
    }
} finally {
    if ($passwordPtr -ne [IntPtr]::Zero) {
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordPtr)
    }

    Remove-Variable plainPassword -ErrorAction SilentlyContinue
    Remove-Item Env:\FATECADS_MAIL_ENABLED -ErrorAction SilentlyContinue
    Remove-Item Env:\FATECADS_MAIL_FROM -ErrorAction SilentlyContinue
    Remove-Item Env:\SPRING_MAIL_HOST -ErrorAction SilentlyContinue
    Remove-Item Env:\SPRING_MAIL_PORT -ErrorAction SilentlyContinue
    Remove-Item Env:\SPRING_MAIL_USERNAME -ErrorAction SilentlyContinue
    Remove-Item Env:\SPRING_MAIL_PASSWORD -ErrorAction SilentlyContinue
    Remove-Item Env:\SPRING_MAIL_SMTP_AUTH -ErrorAction SilentlyContinue
    Remove-Item Env:\SPRING_MAIL_SMTP_STARTTLS -ErrorAction SilentlyContinue
}
