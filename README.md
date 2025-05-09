# aOS Github App

This app uses a test token to access Github REST apis and buids a very simple Github client based on these apis. The features include:

- Popular Repos
- My Repos
- Search Repos
- Login
- Logout
- Raise Issue

## Source Code

- entry: `app/src/main/java/com/example/aos/MainActivity.kt`
- routes: `com/example/aos/navigation/AppNavigation.kt`
- service: `com/example/aos/service/github.kt`
- viewmodel: `com/example/aos/viewmodel`

## Build & Test

Build Apks

```bash
./gradlew app:assembleRelease  app:assembleDebug
```

Run Tests (WIP)

```bash
./gradlew app:test
```

## Architecture

The project is three-layered. 

![](./screenshots/GitHubAppCoreClasses.png)

The login and raise issue flow:

![](./screenshots/GitHubAppLoginAndIssueFlow.png)

## Screenshots

### Home
Popular eepos
![Home](./screenshots/ScreenHome.png)

My Profile and repos
![Profile](./screenshots/ScreenProfile.png)

### Repo
Repo detail
![RepoDetail](./screenshots/ScreenRepoDetail.png)

Raise an Issue
![RaiseIssue](./screenshots/ScreenRaiseIssue.png)

Search repos
![Search](./screenshots/ScreenSearch.png)

## Login

![Login](./screenshots/ScreenLogin.png)
