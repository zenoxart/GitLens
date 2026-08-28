$sizes = @(16, 32, 48, 256)
$srcDir = "src\main\resources\icons"
$outPath = "src\main\resources\icons\gitlens.ico"

$pngBytesList = @()
foreach ($sz in $sizes) {
    $pngBytesList += , [System.IO.File]::ReadAllBytes("$srcDir\gitlens-$sz.png")
}

$stream = New-Object System.IO.MemoryStream
$writer = New-Object System.IO.BinaryWriter $stream

# ICONDIR
$writer.Write([UInt16]0)      # reserved
$writer.Write([UInt16]1)      # type = icon
$writer.Write([UInt16]$sizes.Count)

$headerSize = 6 + (16 * $sizes.Count)
$offset = $headerSize

for ($i = 0; $i -lt $sizes.Count; $i++) {
    $sz = $sizes[$i]
    $pngBytes = $pngBytesList[$i]
    $dim = if ($sz -eq 256) { 0 } else { $sz }  # 256 encodes as 0 in ICONDIRENTRY
    $writer.Write([Byte]$dim)     # width
    $writer.Write([Byte]$dim)     # height
    $writer.Write([Byte]0)        # color count
    $writer.Write([Byte]0)        # reserved
    $writer.Write([UInt16]1)      # color planes
    $writer.Write([UInt16]32)     # bits per pixel
    $writer.Write([UInt32]$pngBytes.Length)
    $writer.Write([UInt32]$offset)
    $offset += $pngBytes.Length
}

foreach ($pngBytes in $pngBytesList) {
    $writer.Write($pngBytes)
}

$writer.Flush()
[System.IO.File]::WriteAllBytes($outPath, $stream.ToArray())
$writer.Dispose()
$stream.Dispose()

Write-Output "wrote $outPath"
