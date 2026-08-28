Add-Type -AssemblyName System.Drawing

function New-IconBitmap {
    param([int]$Size)

    $bmp = New-Object System.Drawing.Bitmap $Size, $Size
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
    $g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $g.Clear([System.Drawing.Color]::Transparent)

    $s = $Size / 512.0

    # --- background: rounded square, purple gradient ---
    $radius = 112 * $s
    $rect = New-Object System.Drawing.RectangleF 0, 0, $Size, $Size
    $path = New-Object System.Drawing.Drawing2D.GraphicsPath
    $d = $radius * 2
    $path.AddArc($rect.X, $rect.Y, $d, $d, 180, 90)
    $path.AddArc($rect.Right - $d, $rect.Y, $d, $d, 270, 90)
    $path.AddArc($rect.Right - $d, $rect.Bottom - $d, $d, $d, 0, 90)
    $path.AddArc($rect.X, $rect.Bottom - $d, $d, $d, 90, 90)
    $path.CloseFigure()

    $c1 = [System.Drawing.Color]::FromArgb(255, 168, 123, 245)
    $c2 = [System.Drawing.Color]::FromArgb(255, 109, 40, 217)
    $brush = New-Object System.Drawing.Drawing2D.LinearGradientBrush(
        (New-Object System.Drawing.Point 0, 0),
        (New-Object System.Drawing.Point $Size, $Size),
        $c1, $c2)
    $g.FillPath($brush, $path)

    # --- magnifying glass ring ---
    $cx = 205 * $s
    $cy = 205 * $s
    $lensR = 128 * $s
    $ringWidth = 30 * $s
    $ringPen = New-Object System.Drawing.Pen ([System.Drawing.Color]::White), $ringWidth
    $ringPen.StartCap = [System.Drawing.Drawing2D.LineCap]::Round
    $ringPen.EndCap = [System.Drawing.Drawing2D.LineCap]::Round
    $g.DrawEllipse($ringPen, $cx - $lensR, $cy - $lensR, $lensR * 2, $lensR * 2)

    # subtle glass tint inside the lens
    $glassBrush = New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::FromArgb(28, 255, 255, 255))
    $innerR = $lensR - ($ringWidth / 2)
    $g.FillEllipse($glassBrush, $cx - $innerR, $cy - $innerR, $innerR * 2, $innerR * 2)

    # --- handle ---
    $handlePen = New-Object System.Drawing.Pen ([System.Drawing.Color]::White), (34 * $s)
    $handlePen.StartCap = [System.Drawing.Drawing2D.LineCap]::Round
    $handlePen.EndCap = [System.Drawing.Drawing2D.LineCap]::Round
    $ang = [Math]::PI / 4
    $startX = $cx + ($lensR - 6 * $s) * [Math]::Cos($ang)
    $startY = $cy + ($lensR - 6 * $s) * [Math]::Sin($ang)
    $endX = 452 * $s
    $endY = 452 * $s
    $g.DrawLine($handlePen, $startX, $startY, $endX, $endY)

    # --- git branch glyph inside the lens ---
    $nodeR = 19 * $s
    $topN = New-Object System.Drawing.PointF (170 * $s), (145 * $s)
    $botN = New-Object System.Drawing.PointF (170 * $s), (268 * $s)
    $branchN = New-Object System.Drawing.PointF (262 * $s), (160 * $s)

    $linePen = New-Object System.Drawing.Pen ([System.Drawing.Color]::White), (14 * $s)
    $linePen.StartCap = [System.Drawing.Drawing2D.LineCap]::Round
    $linePen.EndCap = [System.Drawing.Drawing2D.LineCap]::Round

    # trunk line
    $g.DrawLine($linePen, $topN.X, $topN.Y, $botN.X, $botN.Y)

    # branch curve from mid-trunk to branch node
    $midTrunk = New-Object System.Drawing.PointF $topN.X, (($topN.Y + $botN.Y) / 2)
    $c1pt = New-Object System.Drawing.PointF (($midTrunk.X + $branchN.X) / 2), $midTrunk.Y
    $c2pt = New-Object System.Drawing.PointF $branchN.X, (($midTrunk.Y + $branchN.Y) / 2)
    $curvePts = @($midTrunk, $c1pt, $c2pt, $branchN)
    $g.DrawBezier($linePen, $curvePts[0], $curvePts[1], $curvePts[2], $curvePts[3])

    $nodeBrush = New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::White)
    foreach ($n in @($topN, $botN, $branchN)) {
        $g.FillEllipse($nodeBrush, $n.X - $nodeR, $n.Y - $nodeR, $nodeR * 2, $nodeR * 2)
    }

    $g.Dispose()
    return $bmp
}

$outDir = "src\main\resources\icons"
New-Item -ItemType Directory -Force -Path $outDir | Out-Null

$sizes = @(16, 32, 48, 64, 128, 256, 512)
foreach ($sz in $sizes) {
    $bmp = New-IconBitmap -Size $sz
    $bmp.Save("$outDir\gitlens-$sz.png", [System.Drawing.Imaging.ImageFormat]::Png)
    if ($sz -eq 512) {
        $bmp.Save("docs\icon-preview.png", [System.Drawing.Imaging.ImageFormat]::Png)
    }
    $bmp.Dispose()
}

Write-Output "icons written to $outDir"
