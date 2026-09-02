package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ── Primary Orange Brand Colors ──────────────────────────────────────────────
val OrangePrimary        = Color(0xFFF97316) // Orange 500 — main dark-mode primary
val OrangePrimaryLight   = Color(0xFFEA580C) // Orange 600 — main light-mode primary
val OrangePrimaryDark    = Color(0xFFFF6B00) // Slightly brighter for pressed/hover states
val OrangePrimaryContainer     = Color(0xFF3D1A00) // Dark orange-tinted surface (dark mode)
val OrangePrimaryContainerLight= Color(0xFFFEF3EE) // Soft warm tint (light mode)
val OrangePrimaryContainerOn   = Color(0xFFFFD0B5) // Text inside dark container
val OrangePrimaryContainerOnLight = Color(0xFF9A3412) // Text inside light container

// ── Secondary Amber Colors ────────────────────────────────────────────────────
val AmberSecondary       = Color(0xFFFB923C) // Orange 400 — secondary accents (dark)
val AmberSecondaryLight  = Color(0xFFF97316) // Orange 500 — secondary accents (light)
val AmberSecondaryContainer     = Color(0xFF4A1E00)
val AmberSecondaryContainerLight= Color(0xFFFFF7ED)

// ── Backward-compatible alias (used in some screens) ─────────────────────────
// Kept so existing EmeraldPrimary references compile after replacement
val EmeraldPrimary = OrangePrimary

// ── Dark Theme — Charcoal Backgrounds ────────────────────────────────────────
val DarkBackground       = Color(0xFF111318) // Very dark charcoal
val DarkSurface          = Color(0xFF1C1C1E) // Card surface
val DarkSurfaceVariant   = Color(0xFF252529) // Progress tracks / secondary surfaces
val DarkOnSurface        = Color(0xFFF2F2F7) // Primary text
val DarkOnSurfaceVariant = Color(0xFF8E8E93) // Secondary / muted text
val DarkOutline          = Color(0xFF2C2C2E) // Card borders

// ── Light Theme — Clean Neutral Backgrounds ────────────────────────────────────
val LightBackground       = Color(0xFFF9F9FB) // Near-white with slight warm tint
val LightSurface          = Color(0xFFFFFFFF) // Cards
val LightSurfaceVariant   = Color(0xFFF3F4F6) // Input tracks / secondary bg
val LightOnSurface        = Color(0xFF111318) // Primary text
val LightOnSurfaceVariant = Color(0xFF6B7280) // Secondary text
val LightOutline          = Color(0xFFE5E7EB) // Borders

// ── Semantic Status Colors ────────────────────────────────────────────────────
val RiskLow    = Color(0xFF22C55E) // Green  — low waste risk
val RiskMedium = Color(0xFFF59E0B) // Amber  — medium waste risk
val RiskHigh   = Color(0xFFEF4444) // Red    — high waste risk

// ── Chart Palette ─────────────────────────────────────────────────────────────
val ChartOrange  = Color(0xFFF97316) // Primary chart color (matches brand)
val ChartGreen   = Color(0xFF22C55E)
val ChartTeal    = Color(0xFF06B6D4)
val ChartPurple  = Color(0xFF8B5CF6)
val ChartBlue    = Color(0xFF3B82F6)
val ChartAmber   = Color(0xFFF59E0B)
