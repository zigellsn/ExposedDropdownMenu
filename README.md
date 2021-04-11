# Exposed Dropdown Menu for Jetpack Compose

[![Release](https://jitpack.io/v/zigellsn/ExposedDropdownMenu.svg)](https://jitpack.io/#zigellsn/ExposedDropdownMenu)

An exposed dropdown menu for Jetpack Compose

## Usage

Add it to your build.gradle with:
```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
and:
```gradle
dependencies {
    implementation 'com.github.zigellsn:exposeddropdownmenu:{latest version}'
}
```

and use it like this:

```Kotlin
@Composable
fun ShowExposedDropdownMenu() {
    // ...
    ExposedDropdownMenu(
        items = listOf("A", "B", "C"),
        label = { Text(text = "Letters") },
        selectedItem = { id, text ->
            // Do something
        }
    ) { text, _ ->
        Text(text = text)
    }
    // ...
}
```