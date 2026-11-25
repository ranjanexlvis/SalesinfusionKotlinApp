package com.example.kmmexlvistech.presentation.theme

import androidx.compose.ui.graphics.Color


val PrimaryDeep  = Color(0xFF0B3A82)
val Primary      = Color(0xFF1D4ED8)
val PrimaryMid   = Color(0xFF2563EB)
val PrimaryLight = Color(0xFFE6EFFE)

// Surfaces
val PaperBg   = Color(0xFFF4F6FB)
val CardWhite = Color(0xFFFFFFFF)

// Text
val TextDark = Color(0xFF1B2333)
val TextGrey = Color(0xFF6B7280)

// Status colors
val Saffron      = Color(0xFFF5A623)
val SaffronLight = Color(0xFFFFF3DC)
val SuccessGreen = Color(0xFF2E9E5B)
val SuccessBg    = Color(0xFFE3F6EA)
val DangerRed    = Color(0xFFE5484D)
val DangerBg     = Color(0xFFFDE7E8)
val PrimaryColor = Color(0xFF339FFE)
val PrimaryVariantColor = Color(0xFF00396B)
val AccentColor = Color(0xFFC62F79)
val BackgroundContent = Color(0xFFF6F6F6)
val lightSurface = Color(0xFFF5F5F5)

val Grey = Color(0xFF404649)

val PagerDotColor = Color(0xFFC4CDD3)
val IconColorGrey = Grey

val TextFieldColor = Color(0xFFF6F6F6)
val BorderColor = Color(0xFFDBDBDC)

val splashBackground = Color(0xFF14232F)
val drawerSelectedColor = Color(0xFFC9E3FA)

val otp_background = Color(0xFFF7F7FCA) // Use with black text
val otp_border = Color(0xFFD9DBE9) // Use with white text

val orange_400 = Color(0xFFFFA726) // Use with black text


val PrimaryColorDark = Color(0xFF8FC7FF)
val BackgroundDark = Color(0xFF121417)
val SurfaceDark = Color(0xFF1D2024)
val OnBackgroundDark = Color(0xFFE7E9EC)

val StatusOpenBg = PrimaryVariantColor          // "Open" status pill background
val StatusOpenText = Color(0xFFFFFFFF)
val TaskAccentBar = orange_400                  // colored left strip on each task card
val PhoneNumberColor = PrimaryColor             // clickable phone number text
val CardBorderColor = Color(0xFFEAEAEA)

val BannerErrorBg = Color(0xFF1A1A1A)
val BannerErrorText = Color(0xFFFFFFFF)

val ProgressDimOverlay = Color(0x88000000)
val LoginBackground = Color(0xFF14232F)
val LegacySplashBackground = Color(0xFF14232F)
val SplashAccent = Color(0xFFE8A33D)
val SplashSubtitle = Color(0xFF9AA3AA)
val SplashLogoBackground = Color(0x14FFFFFF)

val LoginButtonBackground = Color(0xFFE8A33D)
val LoginButtonTextColor = Color(0xFF14232F)

// ---------- Home Dashboard v2 tokens (matches latest product design) ----------
val DashLeadsAccent = Color(0xFF7C3AED)      // purple - Leads
val DashTasksAccent = Color(0xFF16A34A)      // green - Tasks
val DashCallsAccent = Color(0xFF2563EB)      // blue - Calls
val DashMeetingsAccent = Color(0xFFF59E0B)   // orange - Meetings
val DashDueAccent = Color(0xFFDC2626)        // red - Due tasks
val DashPositiveTrend = Color(0xFF16A34A)    // rising sparkline
val DashNegativeTrend = Color(0xFFDC2626)    // falling sparkline
val NotificationBadgeColor = Color(0xFFDC2626)

val BottomNavSelected = PrimaryColor
val BottomNavUnselected = Grey.copy(alpha = 0.55f)
val BottomNavIndicator = drawerSelectedColor


// ---------- Dashboard v2 background + drawer tokens ----------
val DashboardBackground = Color(0xFFF4F3FB)   // light lavender page bg from the design
val DrawerHeaderBg = PrimaryVariantColor
val DrawerItemSelectedBg = drawerSelectedColor

val LoginCardBorder = CardBorderColor


// Donut chart / legend colors (Tasks, Calls, Meetings by Status)
val ChartPalette = listOf(
    Color(0xFF2E9E5B), // green  - Completed
    Color(0xFFF5A623), // orange - Pending / Scheduled
    Color(0xFF8B6FE0), // purple - In Progress / No Answer / Rescheduled
    Color(0xFFE5484D), // red    - Cancelled
    Color(0xFF3B82F6)  // blue   - spare
)

// Bar chart colors (by Priority: Low, Medium, High, Urgent)
val PriorityColors = listOf(
    Color(0xFF4F9DF7), // Low
    Color(0xFF5CC98F), // Medium
    Color(0xFFFFC145), // High
    Color(0xFFEF5B5B)  // Urgent
)