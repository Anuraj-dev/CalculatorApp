# CalculatorApp

A scientific calculator for Android built with Kotlin and Jetpack Compose, supporting expression-based input, fractions, unit conversion, and a scientific constants library.

## Features

- **Expression-based input and evaluation** using [exp4j](https://github.com/fasterxml/exp4j) — type a full expression (e.g. `sin(30)+2^3`) and evaluate it in one go
- **Basic and scientific modes**, toggleable from the UI, with a full keypad (`+ - × ÷ ^ √ %`, parentheses, backspace, clear)
- **Scientific functions**: `sin`, `cos`, `tan`, `sinh`, `cosh`, `tanh`, `ln`, `log`, factorial (`!`), square root, with a Degrees/Radians toggle
- **Fraction display mode** — results can be shown as a fraction (via Apache Commons Math `Fraction`) instead of a decimal
- **Memory functions**: `MC` (clear), `MR` (recall), `M+` (add), `M-` (subtract), with a memory indicator shown when memory is non-zero
- **Calculation history** — past expressions and results are stored, browsable in a history dialog, reusable by tapping an entry, and clearable
- **Unit converter** — convert between length (mm, cm, m, km, in, ft, yd, mi), weight (mg, g, kg, t, oz, lb, st), and temperature (C, F, K) units, with the result insertable directly into the calculator input
- **Scientific constants library** — searchable list of mathematical constants (π, e, golden ratio, Euler–Mascheroni constant, √2) and physical constants (Planck constant, Avogadro's number, speed of light, gravitational constant, Boltzmann constant), insertable into the calculator input
- **Advanced math utilities** (`AdvancedMath`): permutations (nPr), combinations (nCr), gamma function (Lanczos approximation), base conversion, GCD/LCM
- Animated UI transitions (button press feedback, result fade/slide animations, adaptive result font sizing for long numbers)
- Light/dark theme support via Jetpack Compose Material 3 theming

## Tech stack

- Kotlin
- Jetpack Compose (Material 3)
- [exp4j](https://github.com/fasterxml/exp4j) for mathematical expression parsing
- [Apache Commons Math](https://commons.apache.org/proper/commons-math/) for fraction representation
- Gradle (Kotlin DSL) build system

## Requirements

- Android Studio (Koala or newer recommended)
- Android SDK with `compileSdk`/`targetSdk` 35 installed
- Minimum supported Android version: SDK 24 (Android 7.0)

## Build & run

### Using Android Studio

1. Open the project folder in Android Studio.
2. Let Gradle sync and download dependencies.
3. Select a device or emulator and click **Run**.

### Using the command line

```bash
# Build a debug APK
./gradlew assembleDebug

# Install on a connected device/emulator
./gradlew installDebug

# Run unit tests
./gradlew test

# Run instrumented tests (requires a connected device/emulator)
./gradlew connectedAndroidTest
```

On Windows, use `gradlew.bat` instead of `./gradlew`.
