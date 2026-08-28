# Builds a macOS .icns from the PNGs in src/main/resources/icons.
# icns container format: 'icns' magic + UInt32BE total length, then repeated
# { 4-byte OSType tag + UInt32BE chunk length (header included) + raw PNG bytes }.

$srcDir = Join-Path $PSScriptRoot "..\src\main\resources\icons"
$outPath = Join-Path $srcDir "gitlens.icns"

# tag -> source PNG size
$entries = [ordered]@{
    "icp4" = 16
    "icp5" = 32
    "icp6" = 64
    "ic07" = 128
    "ic08" = 256
    "ic09" = 512
}

function Write-UInt32BE([System.IO.BinaryWriter]$writer, [uint32]$value) {
    $bytes = [BitConverter]::GetBytes($value)
    if ([BitConverter]::IsLittleEndian) { [Array]::Reverse($bytes) }
    $writer.Write($bytes)
}

$chunks = @()
foreach ($tag in $entries.Keys) {
    $size = $entries[$tag]
    $pngBytes = [System.IO.File]::ReadAllBytes("$srcDir\gitlens-$size.png")
    $chunks += , @{ Tag = $tag; Bytes = $pngBytes }
}

$totalLength = 8 # 'icns' + length field
foreach ($c in $chunks) { $totalLength += 8 + $c.Bytes.Length }

$stream = New-Object System.IO.MemoryStream
$writer = New-Object System.IO.BinaryWriter $stream

$writer.Write([System.Text.Encoding]::ASCII.GetBytes("icns"))
Write-UInt32BE $writer ([uint32]$totalLength)

foreach ($c in $chunks) {
    $writer.Write([System.Text.Encoding]::ASCII.GetBytes($c.Tag))
    Write-UInt32BE $writer ([uint32](8 + $c.Bytes.Length))
    $writer.Write($c.Bytes)
}

$writer.Flush()
[System.IO.File]::WriteAllBytes($outPath, $stream.ToArray())
$writer.Dispose()
$stream.Dispose()

Write-Output "wrote $outPath"
