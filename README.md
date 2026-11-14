<h1 align="center">Calculator App</h1>

<p align="center">
  A scientific calculator for Android, written in Kotlin with Jetpack Compose.<br />
  Build a whole expression, such as sin(30)+2^3, then press equals to get the answer.
</p>

You type the full expression on one line, including brackets, powers and functions, and the result appears when you press equals. Fraction output, a degrees or radians switch, memory keys, a unit converter, a constants library and a history of your last 100 calculations sit around that core.

It is one screen with three dialogs, and everything runs on the device. The app declares no permissions and makes no network calls. To build it you need Android Studio. It runs on Android 7.0 and newer.

## What you get

- **Whole expressions, evaluated in one go.** The input is a string that `calculateExpression` passes to exp4j when you press `=`. An operator typed after another operator replaces it. A bad expression shows `Error` and keeps your input so you can fix it. Results fade in over 300 ms. Long results step down from 48sp to 28sp so they stay on one line.
- **Degrees or radians for trig.** The DEG and RAD switch sets what `sin`, `cos` and `tan` expect. The hyperbolic functions, `ln`, `log` and `√` ignore it.
- **Fractions when the denominator stays small.** With the Fraction switch on, results go through Apache Commons Math's `Fraction`. You get `numerator/denominator` when the denominator is under 10000. Otherwise you get a decimal, rounded to fit 15 characters if it runs longer.
- **Memory keys on the current result.** `M+` and `M-` add or subtract the last result. `MR` puts the stored value into the expression, and `MC` clears it. A line above the display shows `M =` and the value while memory is not zero.
- **History you can reuse.** Every `=` stores the expression and its result, up to the last 100. Tap an entry to load it back. Clear All empties the list and resets the display.
- **Units and constants you can insert.** The Units dialog converts between length units from mm to mi, weight units from mg to st, and the temperature scales C, F and K. Use Result puts the answer into the expression. The Constants dialog searches ten values by name or symbol, from π and the golden ratio to Planck's constant and the speed of light, and inserts the full-precision value.
- **Light and dark themes, with wallpaper colors on Android 12 and newer.** The theme follows the system setting. On Android 12 and newer the app uses dynamic color, so the palette comes from your wallpaper.
- **No permissions, no network, no saved data.** The manifest declares no permissions. Memory and history live in the app's memory only, so they reset when the app restarts.

## Running it

Install Android Studio and the Android SDK platform for API 35. The app compiles against SDK 35 and runs on minSdk 24. The toolchain is Gradle 8.10.2, Android Gradle Plugin 8.8.2 and Kotlin 2.0.0. The wrapper pins Gradle, and `gradle/libs.versions.toml` pins the other two.

### In Android Studio

1. Open the project folder.
2. Let Gradle sync.
3. Pick a device or emulator and press Run.

### From the command line

```sh
./gradlew assembleDebug         # build a debug APK
./gradlew installDebug          # install it on a connected device or emulator
./gradlew test                  # run the local unit tests
./gradlew connectedAndroidTest  # run instrumented tests on a connected device or emulator
```

The wrapper script is committed without its executable bit. On Linux or macOS, run `chmod +x gradlew` once, or call it as `sh gradlew test`. On Windows, use `gradlew.bat`.

The unit and instrumented tests are still the Android Studio placeholders. They pass without checking the calculator logic.

## How it works

`MainActivity.kt` holds the keypad, the display and the evaluation. Before exp4j parses an expression, `calculateExpression` rewrites `×`, `÷`, `π`, `e` and `√(` into exp4j syntax. It also expands `n!` for integer literals. The trig functions are registered for the current angle mode. `formatResult` then picks integer, fraction or decimal output.

`model/CalculatorModel.kt` stores memory and the history. `utils/MathUtils.kt` holds the constants and the unit tables. It also holds `AdvancedMath`, which has permutations, combinations, gamma, base conversion, GCD and LCM. No screen calls `AdvancedMath` yet.

## License

MIT. See [LICENSE](LICENSE).
