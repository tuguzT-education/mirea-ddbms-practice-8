package io.github.tuguzt.ddbms.practice8.view.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import com.github.tkuenneth.nativeparameterstoreaccess.Dconf
import com.github.tkuenneth.nativeparameterstoreaccess.Dconf.HAS_DCONF
import com.github.tkuenneth.nativeparameterstoreaccess.MacOSDefaults
import com.github.tkuenneth.nativeparameterstoreaccess.NativeParameterStoreAccess.IS_MACOS
import com.github.tkuenneth.nativeparameterstoreaccess.NativeParameterStoreAccess.IS_WINDOWS
import com.github.tkuenneth.nativeparameterstoreaccess.WindowsRegistry

/**
 * This function exists because Compose [`isSystemInDarkTheme`][androidx.compose.foundation.isSystemInDarkTheme]
 * is broken for now.
 */
fun isSystemInDarkTheme(): Boolean = when {
    IS_WINDOWS -> {
        val result = WindowsRegistry.getWindowsRegistryEntry(
            "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize",
            "AppsUseLightTheme",
        )
        result == 0x0
    }
    IS_MACOS -> {
        val result = MacOSDefaults.getDefaultsEntry("AppleInterfaceStyle")
        result == "Dark"
    }
    HAS_DCONF -> {
        val result = Dconf.getDconfEntry("/org/gnome/desktop/interface/gtk-theme")
        result.lowercase().contains("dark")
    }
    else -> false
}

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200,
)

private val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200,

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

/**
 * Main [Material Theme][MaterialTheme] of the application.
 */
@Composable
fun Practice8Theme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}
